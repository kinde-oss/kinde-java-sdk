# KindeApi::UsersApi

All URIs are relative to *https://app.kinde.com*

| Method | HTTP request | Description |
| ------ | ------------ | ----------- |
| [**create_user**](UsersApi.md#create_user) | **POST** /api/v1/user | Create User |
| [**delete_user**](UsersApi.md#delete_user) | **DELETE** /api/v1/user | Delete User |
| [**get_user_data**](UsersApi.md#get_user_data) | **GET** /api/v1/user | Get User |
| [**get_users**](UsersApi.md#get_users) | **GET** /api/v1/users | List Users |
| [**update_user**](UsersApi.md#update_user) | **PATCH** /api/v1/user | Update User |


## create_user

> <CreateUserResponse> create_user(opts)

Create User

Creates a user record and optionally zero or more identities for the user. An example identity could be the email address of the user. 

### Examples

```ruby
require 'time'
require 'kinde_api'
# setup authorization
KindeApi.configure do |config|
  # Configure Bearer authorization (JWT): kindeBearerAuth
  config.access_token = 'YOUR_BEARER_TOKEN'
end

api_instance = KindeApi::UsersApi.new
opts = {
  create_user_request: KindeApi::CreateUserRequest.new # CreateUserRequest | The details of the user to create.
}

begin
  # Create User
  result = api_instance.create_user(opts)
  p result
rescue KindeApi::ApiError => e
  puts "Error when calling UsersApi->create_user: #{e}"
end
```

#### Using the create_user_with_http_info variant

This returns an Array which contains the response data, status code and headers.

> <Array(<CreateUserResponse>, Integer, Hash)> create_user_with_http_info(opts)

```ruby
begin
  # Create User
  data, status_code, headers = api_instance.create_user_with_http_info(opts)
  p status_code # => 2xx
  p headers # => { ... }
  p data # => <CreateUserResponse>
rescue KindeApi::ApiError => e
  puts "Error when calling UsersApi->create_user_with_http_info: #{e}"
end
```

### Parameters

| Name | Type | Description | Notes |
| ---- | ---- | ----------- | ----- |
| **create_user_request** | [**CreateUserRequest**](CreateUserRequest.md) | The details of the user to create. | [optional] |

### Return type

[**CreateUserResponse**](CreateUserResponse.md)

### Authorization

[kindeBearerAuth](../README.md#kindeBearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/json; charset=utf-8


## delete_user

> <SuccessResponse> delete_user(opts)

Delete User

Delete a user record. 

### Examples

```ruby
require 'time'
require 'kinde_api'
# setup authorization
KindeApi.configure do |config|
  # Configure Bearer authorization (JWT): kindeBearerAuth
  config.access_token = 'YOUR_BEARER_TOKEN'
end

api_instance = KindeApi::UsersApi.new
opts = {
  id: 'id_example' # String | The user's id.
}

begin
  # Delete User
  result = api_instance.delete_user(opts)
  p result
rescue KindeApi::ApiError => e
  puts "Error when calling UsersApi->delete_user: #{e}"
end
```

#### Using the delete_user_with_http_info variant

This returns an Array which contains the response data, status code and headers.

> <Array(<SuccessResponse>, Integer, Hash)> delete_user_with_http_info(opts)

```ruby
begin
  # Delete User
  data, status_code, headers = api_instance.delete_user_with_http_info(opts)
  p status_code # => 2xx
  p headers # => { ... }
  p data # => <SuccessResponse>
rescue KindeApi::ApiError => e
  puts "Error when calling UsersApi->delete_user_with_http_info: #{e}"
end
```

### Parameters

| Name | Type | Description | Notes |
| ---- | ---- | ----------- | ----- |
| **id** | **String** | The user&#39;s id. | [optional] |

### Return type

[**SuccessResponse**](SuccessResponse.md)

### Authorization

[kindeBearerAuth](../README.md#kindeBearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/json; charset=utf-8


## get_user_data

> <User> get_user_data(id)

Get User

Retrieve a user record. 

### Examples

```ruby
require 'time'
require 'kinde_api'
# setup authorization
KindeApi.configure do |config|
  # Configure Bearer authorization (JWT): kindeBearerAuth
  config.access_token = 'YOUR_BEARER_TOKEN'
end

api_instance = KindeApi::UsersApi.new
id = 'id_example' # String | The user's id.

begin
  # Get User
  result = api_instance.get_user_data(id)
  p result
rescue KindeApi::ApiError => e
  puts "Error when calling UsersApi->get_user_data: #{e}"
end
```

#### Using the get_user_data_with_http_info variant

This returns an Array which contains the response data, status code and headers.

> <Array(<User>, Integer, Hash)> get_user_data_with_http_info(id)

```ruby
begin
  # Get User
  data, status_code, headers = api_instance.get_user_data_with_http_info(id)
  p status_code # => 2xx
  p headers # => { ... }
  p data # => <User>
rescue KindeApi::ApiError => e
  puts "Error when calling UsersApi->get_user_data_with_http_info: #{e}"
end
```

### Parameters

| Name | Type | Description | Notes |
| ---- | ---- | ----------- | ----- |
| **id** | **String** | The user&#39;s id. |  |

### Return type

[**User**](User.md)

### Authorization

[kindeBearerAuth](../README.md#kindeBearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/json; charset=utf-8


## get_users

> <UsersResponse> get_users(opts)

List Users

The returned list can be sorted by full name or email address in ascending or descending order. The number of records to return at a time can also be controlled using the `page_size` query string parameter. 

### Examples

```ruby
require 'time'
require 'kinde_api'
# setup authorization
KindeApi.configure do |config|
  # Configure Bearer authorization (JWT): kindeBearerAuth
  config.access_token = 'YOUR_BEARER_TOKEN'
end

api_instance = KindeApi::UsersApi.new
opts = {
  sort: 'name_asc', # String | Field and order to sort the result by.
  page_size: 56, # Integer | Number of results per page. Defaults to 10 if parameter not sent.
  user_id: 'user_id_example', # String | ID of the user to filter by.
  next_token: 'next_token_example', # String | A string to get the next page of results if there are more results.
  email: 'email_example' # String | Filter the results by email address. The query string should be comma separated and url encoded.
}

begin
  # List Users
  result = api_instance.get_users(opts)
  p result
rescue KindeApi::ApiError => e
  puts "Error when calling UsersApi->get_users: #{e}"
end
```

#### Using the get_users_with_http_info variant

This returns an Array which contains the response data, status code and headers.

> <Array(<UsersResponse>, Integer, Hash)> get_users_with_http_info(opts)

```ruby
begin
  # List Users
  data, status_code, headers = api_instance.get_users_with_http_info(opts)
  p status_code # => 2xx
  p headers # => { ... }
  p data # => <UsersResponse>
rescue KindeApi::ApiError => e
  puts "Error when calling UsersApi->get_users_with_http_info: #{e}"
end
```

### Parameters

| Name | Type | Description | Notes |
| ---- | ---- | ----------- | ----- |
| **sort** | **String** | Field and order to sort the result by. | [optional] |
| **page_size** | **Integer** | Number of results per page. Defaults to 10 if parameter not sent. | [optional] |
| **user_id** | **String** | ID of the user to filter by. | [optional] |
| **next_token** | **String** | A string to get the next page of results if there are more results. | [optional] |
| **email** | **String** | Filter the results by email address. The query string should be comma separated and url encoded. | [optional] |

### Return type

[**UsersResponse**](UsersResponse.md)

### Authorization

[kindeBearerAuth](../README.md#kindeBearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/json; charset=utf-8


## update_user

> <User> update_user(update_user_request, opts)

Update User

Update a user record. 

### Examples

```ruby
require 'time'
require 'kinde_api'
# setup authorization
KindeApi.configure do |config|
  # Configure Bearer authorization (JWT): kindeBearerAuth
  config.access_token = 'YOUR_BEARER_TOKEN'
end

api_instance = KindeApi::UsersApi.new
update_user_request = KindeApi::UpdateUserRequest.new # UpdateUserRequest | The user to update.
opts = {
  id: 'id_example' # String | The user's id.
}

begin
  # Update User
  result = api_instance.update_user(update_user_request, opts)
  p result
rescue KindeApi::ApiError => e
  puts "Error when calling UsersApi->update_user: #{e}"
end
```

#### Using the update_user_with_http_info variant

This returns an Array which contains the response data, status code and headers.

> <Array(<User>, Integer, Hash)> update_user_with_http_info(update_user_request, opts)

```ruby
begin
  # Update User
  data, status_code, headers = api_instance.update_user_with_http_info(update_user_request, opts)
  p status_code # => 2xx
  p headers # => { ... }
  p data # => <User>
rescue KindeApi::ApiError => e
  puts "Error when calling UsersApi->update_user_with_http_info: #{e}"
end
```

### Parameters

| Name | Type | Description | Notes |
| ---- | ---- | ----------- | ----- |
| **update_user_request** | [**UpdateUserRequest**](UpdateUserRequest.md) | The user to update. |  |
| **id** | **String** | The user&#39;s id. | [optional] |

### Return type

[**User**](User.md)

### Authorization

[kindeBearerAuth](../README.md#kindeBearerAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/json; charset=utf-8
