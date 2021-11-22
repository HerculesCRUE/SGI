package org.crue.hercules.sgi.framework.problem.message;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility class used to build
 * {@link org.crue.hercules.sgi.framework.problem.Problem} messages.
 */
public class ProblemMessage {

  /**
   * Creates a new {@link ProblemMessage}.
   */
  private ProblemMessage() {
  }

  /**
   * Gets the builder class for
   * {@link org.crue.hercules.sgi.framework.problem.Problem} messages.
   * 
   * @return the builder
   */
  public static ProblemMessageBuilder builder() {
    return new ProblemMessageBuilder();
  }

  /**
   * Builder class for {@link org.crue.hercules.sgi.framework.problem.Problem}
   * messages.
   */
  public static class ProblemMessageBuilder {
    private String message;

    private final Map<String, Object> parameters = new LinkedHashMap<>();

    /**
     * Set the message key for building a
     * {@link org.crue.hercules.sgi.framework.problem.Problem} detail message.
     * 
     * @param key the key
     * @return self reference
     */
    public ProblemMessageBuilder key(final String key) {
      this.message = "{" + key + "}";
      return this;
    }

    /**
     * Set the class to get the key for building a
     * {@link org.crue.hercules.sgi.framework.problem.Problem} detail message.
     * 
     * @param key the class
     * @return self reference
     */
    public ProblemMessageBuilder key(final Class<?> key) {
      this.message = "{" + key.getName() + ".message}";
      return this;
    }

    /**
     * Set the class and property to get the key for building a
     * {@link org.crue.hercules.sgi.framework.problem.Problem} detail message.
     * 
     * @param key      the class
     * @param property the property
     * @return self reference
     */
    public ProblemMessageBuilder key(final Class<?> key, final String property) {
      this.message = "{" + key.getName() + "." + property + ".message}";
      return this;
    }

    /**
     * Set the {@link org.crue.hercules.sgi.framework.problem.Problem} detail
     * message.
     * 
     * @param message the message
     * @return self reference
     */
    public ProblemMessageBuilder message(final String message) {
      this.message = message;
      return this;
    }

    /**
     * Add a parameter to be used for building a
     * {@link org.crue.hercules.sgi.framework.problem.Problem} detail message.
     * 
     * @param name  the parameter name
     * @param value the paramenter value
     * @return self reference
     */
    public ProblemMessageBuilder parameter(final String name, final Object value) throws IllegalArgumentException {
      parameters.put(name, value);
      return this;
    }

    /**
     * Builds a {@link org.crue.hercules.sgi.framework.problem.Problem} detail
     * message using the {@link MessageInterpolator} and the provided configuration.
     * 
     * @return {@link org.crue.hercules.sgi.framework.problem.Problem} detail
     *         message
     */
    public String build() {
      return MessageInterpolator.interpolateMessage(message, parameters);
    }

  }
}
