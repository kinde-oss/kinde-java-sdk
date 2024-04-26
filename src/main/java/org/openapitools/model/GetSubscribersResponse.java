package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.model.SubscribersSubscriber;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * GetSubscribersResponse
 */

@JsonTypeName("get_subscribers_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class GetSubscribersResponse {

  private String code;

  private String message;

  @Valid
  private List<@Valid SubscribersSubscriber> subscribers;

  private String nextToken;

  public GetSubscribersResponse code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Response code.
   * @return code
  */
  
  @Schema(name = "code", description = "Response code.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public GetSubscribersResponse message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Response message.
   * @return message
  */
  
  @Schema(name = "message", description = "Response message.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public GetSubscribersResponse subscribers(List<@Valid SubscribersSubscriber> subscribers) {
    this.subscribers = subscribers;
    return this;
  }

  public GetSubscribersResponse addSubscribersItem(SubscribersSubscriber subscribersItem) {
    if (this.subscribers == null) {
      this.subscribers = new ArrayList<>();
    }
    this.subscribers.add(subscribersItem);
    return this;
  }

  /**
   * Get subscribers
   * @return subscribers
  */
  @Valid 
  @Schema(name = "subscribers", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("subscribers")
  public List<@Valid SubscribersSubscriber> getSubscribers() {
    return subscribers;
  }

  public void setSubscribers(List<@Valid SubscribersSubscriber> subscribers) {
    this.subscribers = subscribers;
  }

  public GetSubscribersResponse nextToken(String nextToken) {
    this.nextToken = nextToken;
    return this;
  }

  /**
   * Pagination token.
   * @return nextToken
  */
  
  @Schema(name = "next_token", description = "Pagination token.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("next_token")
  public String getNextToken() {
    return nextToken;
  }

  public void setNextToken(String nextToken) {
    this.nextToken = nextToken;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GetSubscribersResponse getSubscribersResponse = (GetSubscribersResponse) o;
    return Objects.equals(this.code, getSubscribersResponse.code) &&
        Objects.equals(this.message, getSubscribersResponse.message) &&
        Objects.equals(this.subscribers, getSubscribersResponse.subscribers) &&
        Objects.equals(this.nextToken, getSubscribersResponse.nextToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, message, subscribers, nextToken);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetSubscribersResponse {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    subscribers: ").append(toIndentedString(subscribers)).append("\n");
    sb.append("    nextToken: ").append(toIndentedString(nextToken)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

