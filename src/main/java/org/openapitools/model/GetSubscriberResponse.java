package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.model.Subscriber;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * GetSubscriberResponse
 */

@JsonTypeName("get_subscriber_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class GetSubscriberResponse {

  private String code;

  private String message;

  @Valid
  private List<@Valid Subscriber> subscribers;

  public GetSubscriberResponse code(String code) {
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

  public GetSubscriberResponse message(String message) {
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

  public GetSubscriberResponse subscribers(List<@Valid Subscriber> subscribers) {
    this.subscribers = subscribers;
    return this;
  }

  public GetSubscriberResponse addSubscribersItem(Subscriber subscribersItem) {
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
  public List<@Valid Subscriber> getSubscribers() {
    return subscribers;
  }

  public void setSubscribers(List<@Valid Subscriber> subscribers) {
    this.subscribers = subscribers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GetSubscriberResponse getSubscriberResponse = (GetSubscriberResponse) o;
    return Objects.equals(this.code, getSubscriberResponse.code) &&
        Objects.equals(this.message, getSubscriberResponse.message) &&
        Objects.equals(this.subscribers, getSubscriberResponse.subscribers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, message, subscribers);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetSubscriberResponse {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    subscribers: ").append(toIndentedString(subscribers)).append("\n");
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

