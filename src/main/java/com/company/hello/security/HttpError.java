package com.company.hello.security;

import com.company.hello.util.Arguments;
import com.google.common.base.Objects;

public class HttpError {
  private final int httpStatus;

  private final String errorMessage;

  public HttpError(final int httpStatus, final String errorMessage) {
    Arguments.greaterThan("httpStatus", httpStatus, 0);

    this.httpStatus = httpStatus;
    this.errorMessage = errorMessage;
  }

  public int getHttpStatus() {
    return httpStatus;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
      .add("httpStatus", httpStatus)
      .add("errorMessage", errorMessage)
      .toString();
  }
}
