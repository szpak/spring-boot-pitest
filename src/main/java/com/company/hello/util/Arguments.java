package com.company.hello.util;

import com.google.common.base.Strings;

import java.util.Objects;

public final class Arguments {
  private Arguments() {
  }

  public static void notNull(final String argumentName, final Object argument) {
    notEmpty("argumentName", argumentName);

    if (Objects.isNull(argument)) {
      throw new IllegalArgumentException(
        String.format("The argument '%s' must not be null.", argumentName));
    }
  }

  public static void greaterThan(final String argumentName, final long value,
    final long comparator) {
    notEmpty("argumentName", argumentName);

    if (value <= comparator) {
      throw new IllegalArgumentException(
        String.format("The value of argument '%s' has to be greater than '%d'.", argumentName,
          value)
      );
    }
  }

  public static void notEmpty(final String argumentName, final String value) {
    if (Strings.isNullOrEmpty(argumentName)) {
      throw new IllegalArgumentException("The 'argumentName' must not be null or empty.");
    }

    if (Strings.isNullOrEmpty(value)) {
      throw new IllegalArgumentException(
        String.format("The argument '%s' must not be null or empty.", argumentName));
    }
  }
}
