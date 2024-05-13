package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.openapitools.model.CreateSubscriberSuccessResponseSubscriber;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CreateSubscriberSuccessResponse
 */

@JsonTypeName("create_subscriber_success_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class CreateSubscriberSuccessResponse {

  private CreateSubscriberSuccessResponseSubscriber subscriber;

  public CreateSubscriberSuccessResponse subscriber(CreateSubscriberSuccessResponseSubscriber subscriber) {
    this.subscriber = subscriber;
    return this;
  }

  /**
   * Get subscriber
   * @return subscriber
  */
  @Valid 
  @Schema(name = "subscriber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("subscriber")
  public CreateSubscriberSuccessResponseSubscriber getSubscriber() {
    return subscriber;
  }

  public void setSubscriber(CreateSubscriberSuccessResponseSubscriber subscriber) {
    this.subscriber = subscriber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateSubscriberSuccessResponse createSubscriberSuccessResponse = (CreateSubscriberSuccessResponse) o;
    return Objects.equals(this.subscriber, createSubscriberSuccessResponse.subscriber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(subscriber);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateSubscriberSuccessResponse {\n");
    sb.append("    subscriber: ").append(toIndentedString(subscriber)).append("\n");
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

