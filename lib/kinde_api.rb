require "kinde_api/version"
require "kinde_api/configuration"
require "kinde_api/client"

require 'securerandom'
require 'oauth2'
require 'pkce_challenge'
require 'faraday/follow_redirects'

module KindeApi
  class << self
    attr_accessor :config

    def configure
      if block_given?
        yield(Configuration.default)
      else
        Configuration.default
      end

      @config = Configuration.default
    end

    # receive url for authorization in Kinde itself
    #
    # @return [Hash]
    def auth_url(**kwargs)
      params = {
        redirect_uri: @config.callback_url,
        state: SecureRandom.hex,
        scope: @config.scope
      }.merge(**kwargs)
      return { url: @config.oauth_client.auth_code.authorize_url(params) } unless @config.pkce_enabled

      pkce_challenge = PkceChallenge.challenge(char_length: 128)
      params.merge!(code_challenge_method: 'S256', code_challenge: pkce_challenge.code_challenge)
      {
        url: @config.oauth_client.auth_code.authorize_url(params),
        code_verifier: pkce_challenge.code_verifier
      }
    end

    # when callback processor receives code, it needs to be used for fetching bearer token
    #
    # @return [Hash]
    def fetch_tokens(params_or_code, code_verifier = nil)
      code = params_or_code.kind_of?(Hash) ? params.fetch("code") : params_or_code
      params = { redirect_uri: @config.callback_url }
      params[:code_verifier] = code_verifier if code_verifier
      @config.oauth_client.auth_code.get_token(code.to_s, params).to_hash
    end

    # @return [KindeApi::Client]
    def client(bearer_token)
      sdk_api_client = api_client(bearer_token)
      KindeApi::Client.new(sdk_api_client, bearer_token)
    end

    def logout(bearer_token, sdk_api_client = nil)
      (sdk_api_client || api_client(bearer_token))
        .call_api(
          :get, '/logout',
          query_params: { 'redirect' => @config.logout_url },
          header_params: { 'Authorization' => "Bearer #{bearer_token}" }
        )
    end

    def client_credentials_access(
      client_id: @config.client_id,
      client_secret: @config.client_secret,
      audience: "#{@config.domain}/api"
    )
      Faraday.new(url: @config.domain) do |faraday|
        faraday.response :json
        faraday.use Faraday::FollowRedirects::Middleware
      end
        .post(@config.token_url) do |req|
        req.headers[:content_type] = 'application/x-www-form-urlencoded'
        req.body =
          "grant_type=client_credentials&client_id=#{client_id}&client_secret=#{client_secret}&audience=#{audience}"
      end.body
    end

    def token_expired?(hash)
      OAuth2::AccessToken.from_hash(@config.oauth_client, hash).expired?
    end

    # @return [Hash]
    def refresh_token(hash)
      OAuth2::AccessToken.from_hash(@config.oauth_client, hash).refresh.to_hash
    end

    # init sdk api client by bearer token
    #
    # @return [KindeSdk::ApiClient]
    def api_client(bearer_token)
      config = KindeSdk::Configuration.default
      config.configure do |c|
        c.access_token = bearer_token
        c.server_variables = { businessName: @config.business_name || @config.domain.split("//")[1].split(".")[0] }
        c.host = @config.domain
        c.debugging = @config.debugging
        c.logger = @config.logger
      end

      KindeSdk::ApiClient.new(config)
    end
  end
end
