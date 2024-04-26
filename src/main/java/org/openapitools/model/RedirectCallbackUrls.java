package org.openapitools.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RedirectCallbackUrls
 */

@JsonTypeName("redirect_callback_urls")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-08-08T18:38:44.963952+05:00[Asia/Karachi]")
public class RedirectCallbackUrls {

  @Valid
  private List<String> redirectUrls;

  public RedirectCallbackUrls redirectUrls(List<String> redirectUrls) {
    this.redirectUrls = redirectUrls;
    return this;
  }

  public RedirectCallbackUrls addRedirectUrlsItem(String redirectUrlsItem) {
    if (this.redirectUrls == null) {
      this.redirectUrls = new ArrayList<>();
    }
    this.redirectUrls.add(redirectUrlsItem);
    return this;
  }

  /**
   * An application's redirect URLs.
   * @return redirectUrls
  */
  
  @Schema(name = "redirect_urls", description = "An application's redirect URLs.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("redirect_urls")
  public List<String> getRedirectUrls() {
    return redirectUrls;
  }

  public void setRedirectUrls(List<String> redirectUrls) {
    this.redirectUrls = redirectUrls;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RedirectCallbackUrls redirectCallbackUrls = (RedirectCallbackUrls) o;
    return Objects.equals(this.redirectUrls, redirectCallbackUrls.redirectUrls);
  }

  @Override
  public int hashCode() {
    return Objects.hash(redirectUrls);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RedirectCallbackUrls {\n");
    sb.append("    redirectUrls: ").append(toIndentedString(redirectUrls)).append("\n");
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

