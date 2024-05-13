package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CreateSubscriberSuccessResponseSubscriber
 */

@JsonTypeName("create_subscriber_success_response_subscriber")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class CreateSubscriberSuccessResponseSubscriber {

  private String subscriberId;

  public CreateSubscriberSuccessResponseSubscriber subscriberId(String subscriberId) {
    this.subscriberId = subscriberId;
    return this;
  }

  /**
   * A unique identifier for the subscriber.
   * @return subscriberId
  */
  
  @Schema(name = "subscriber_id", description = "A unique identifier for the subscriber.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("subscriber_id")
  public String getSubscriberId() {
    return subscriberId;
  }

  public void setSubscriberId(String subscriberId) {
    this.subscriberId = subscriberId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateSubscriberSuccessResponseSubscriber createSubscriberSuccessResponseSubscriber = (CreateSubscriberSuccessResponseSubscriber) o;
    return Objects.equals(this.subscriberId, createSubscriberSuccessResponseSubscriber.subscriberId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(subscriberId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateSubscriberSuccessResponseSubscriber {\n");
    sb.append("    subscriberId: ").append(toIndentedString(subscriberId)).append("\n");
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

