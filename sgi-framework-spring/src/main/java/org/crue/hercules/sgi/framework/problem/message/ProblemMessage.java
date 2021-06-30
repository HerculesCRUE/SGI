package org.crue.hercules.sgi.framework.problem.message;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProblemMessage {
  public static ProblemMessageBuilder builder() {
    return new ProblemMessageBuilder();
  }

  public static class ProblemMessageBuilder {
    private String message;

    private final Map<String, Object> parameters = new LinkedHashMap<>();

    public ProblemMessageBuilder() {
    }

    public ProblemMessageBuilder key(final String key) {
      this.message = "{" + key + "}";
      return this;
    }

    public ProblemMessageBuilder key(final Class<?> key) {
      this.message = "{" + key.getName() + ".message}";
      return this;
    }

    public ProblemMessageBuilder key(final Class<?> key, final String property) {
      this.message = "{" + key.getName() + "." + property + ".message}";
      return this;
    }

    public ProblemMessageBuilder message(final String message) {
      this.message = message;
      return this;
    }

    public ProblemMessageBuilder parameter(final String name, final Object value) throws IllegalArgumentException {
      parameters.put(name, value);
      return this;
    }

    public String build() {
      return MessageInterpolator.interpolateMessage(message, parameters);
    }

  }
}
