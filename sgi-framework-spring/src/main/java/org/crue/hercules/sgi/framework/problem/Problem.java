package org.crue.hercules.sgi.framework.problem;

import java.io.Serializable;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;

/**
 * Object representing a Problem.
 * 
 * @see <a href="https://tools.ietf.org/html/rfc7807">RFC 7807</a>
 */
public class Problem implements Serializable {
  /**
   * The default {@link Problem} type {@link URI} ("urn:problem-type:unknown")
   */
  public static final URI UNKNOWN_PROBLEM_TYPE = URI.create("urn:problem-type:unknown");
  /**
   * The default {@link Problem} HTTP status code (500 - INTERNAL_SERVER_ERROR)
   */
  public static final int INTERAL_SERVER_ERROR_STATUS = HttpStatus.INTERNAL_SERVER_ERROR.value();

  /** Type {@link URI} */
  private final URI type;
  /** Title */
  private final String title;
  /** HTTP status code */
  private final int status;
  /** Detail message */
  private final String detail;
  /** Unique identifier {@link URI} */
  private final URI instance;
  /** {@link Extension}s */
  private final Map<String, Serializable> extensions;

  /**
   * Gets the builder class to build {@link Problem} object.
   * 
   * @return the {@link ProblemBuilder}
   */
  public static ProblemBuilder builder() {
    return new ProblemBuilder();
  }

  /**
   * Builds a new {@link Problem}.
   * 
   * @param type       the {@link Problem} type {@link URI}
   * @param title      the {@link Problem} title
   * @param status     the {@link Problem} HTTP status code
   * @param detail     the {@link Problem} detail message
   * @param instance   the {@link Problem} unique identifier {@link URI}
   * @param extensions the {@link Problem} {@link Extension}s
   */
  public Problem(URI type, String title, int status, String detail, URI instance,
      Map<String, Serializable> extensions) {
    this.type = type;
    this.title = title;
    this.status = status;
    this.detail = detail;
    this.instance = instance;
    this.extensions = Collections.unmodifiableMap(new HashMap<>(extensions));
  }

  /**
   * Builds a new {@link Problem}.
   * 
   * @param type       the {@link Problem} type {@link URI}
   * @param title      the {@link Problem} title
   * @param status     the {@link Problem} HTTP status code
   * @param detail     the {@link Problem} detail message
   * @param instance   the {@link Problem} unique identifier {@link URI}
   * @param extensions the {@link Problem} {@link Extension}s
   */
  public Problem(URI type, String title, int status, String detail, URI instance, Set<Extension> extensions) {
    this(type, title, status, detail, instance, buildMapFromExtensions(extensions));
  }

  /**
   * Builds a new {@link Problem}.
   * 
   * @param type       the {@link Problem} type {@link URI}
   * @param title      the {@link Problem} title
   * @param status     the {@link Problem} HTTP status code
   * @param detail     the {@link Problem} detail message
   * @param instance   the {@link Problem} unique identifier {@link URI}
   * @param extensions the {@link Problem} {@link Extension}s
   */
  public Problem(URI type, String title, int status, String detail, URI instance, Extension... extensions) {
    this(type, title, status, detail, instance, buildMapFromExtensions(extensions));
  }

  private static Map<String, Serializable> buildMapFromExtensions(Set<Extension> extensions) {
    Map<String, Serializable> map = new HashMap<>(extensions.size());
    extensions.forEach(e -> map.put(e.getKey(), e.getValue()));
    return map;
  }

  private static Map<String, Serializable> buildMapFromExtensions(Extension[] extensions) {
    Map<String, Serializable> map = new HashMap<>(extensions.length);
    for (Problem.Extension e : extensions) {
      map.put(e.getKey(), e.getValue());
    }
    return map;
  }

  /**
   * Gets the {@link Problem} type {@link URI}.
   * 
   * @return the type
   */
  public URI getType() {
    return this.type;
  }

  /**
   * Gets the {@link Problem} title.
   * 
   * @return the title
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Gets the {@link Problem} HTTP status code.
   * 
   * @return the HTTP status code
   */
  public int getStatus() {
    return this.status;
  }

  /**
   * Gets the {@link Problem} detail message.
   * 
   * @return the detail message
   */
  public String getDetail() {
    return this.detail;
  }

  /**
   * Gets the {@link Problem} unique identifier {@link URI}.
   * 
   * @return the unique identifier {@link URI}.
   */
  public URI getInstance() {
    return this.instance;
  }

  /**
   * Gets the {@link Problem} {@link Extension} names.
   * 
   * @return the {@link Extension} names
   */
  public Set<String> getExtensions() {
    return Collections.unmodifiableSet(extensions.keySet());
  }

  /**
   * Gets the {@link Extension} value for the given {@link Extension} name.
   * 
   * @param name the {@link Extension} name
   * @return the {@link Extension} value
   */
  public Object getExtensionValue(String name) {
    return extensions.get(name);
  }

  /**
   * Cheks if the {@link Problem} has an {@link Extension} with the provided name.
   * 
   * @param extension the {@link Extension} name
   * @return true if found
   */
  public boolean hasExtension(String extension) {
    return extensions.containsKey(extension);
  }

  /**
   * A {@link Problem} extension.
   */
  public static final class Extension implements Map.Entry<String, Serializable> {

    private final String key;
    private Serializable value;

    /**
     * New {@link Extension} with the provided key and value.
     * 
     * @param key   the {@link Extension} key
     * @param value the {@link Extension} value
     */
    public Extension(String key, Serializable value) {
      this.key = key;
      this.value = value;
    }

    @Override
    public String getKey() {
      return key;
    }

    @Override
    public Serializable getValue() {
      return value;
    }

    @Override
    public Serializable setValue(Serializable value) {
      this.value = value;
      return value;
    }
  }

  /**
   * Builder class to build {@link Problem} object.
   */
  public static class ProblemBuilder {
    private URI type = UNKNOWN_PROBLEM_TYPE;
    private String title;
    private int status = INTERAL_SERVER_ERROR_STATUS;
    private String detail;
    private URI instance = URI.create("urn:uuid:" + UUID.randomUUID());
    private Map<String, Serializable> extensions = new LinkedHashMap<>();

    /**
     * Sets the {@link Problem} type {@link URI}.
     * 
     * @param type the type
     * @return self reference
     */
    public ProblemBuilder type(URI type) {
      this.type = type;
      return this;
    }

    /**
     * Sets the {@link Problem} title.
     * 
     * @param title the title
     * @return self reference
     */
    public ProblemBuilder title(String title) {
      this.title = title;
      return this;
    }

    /**
     * Sets the {@link Problem} HTTP status code.
     * 
     * @param status the status
     * @return self reference
     */
    public ProblemBuilder status(int status) {
      this.status = status;
      return this;
    }

    /**
     * Sets the {@link Problem} detail message.
     * 
     * @param detail the detail
     * @return self reference
     */
    public ProblemBuilder detail(String detail) {
      this.detail = detail;
      return this;
    }

    /**
     * Sets the {@link Problem} unique identifier {@link URI}.
     * 
     * @param instance the instance identifier
     * @return self reference
     */
    public ProblemBuilder instance(URI instance) {
      this.instance = instance;
      return this;
    }

    /**
     * Adds an {@link Problem} to the Problem.
     * 
     * @param name  the extension name
     * @param value the extension value
     * @return self reference
     */
    public ProblemBuilder extension(String name, Serializable value) {
      extensions.put(name, value);
      return this;
    }

    /**
     * Builds a {@link Problem} object with the given information.
     * 
     * @return the {@link Problem} object
     */
    public Problem build() {
      return new Problem(type, title, status, detail, instance, extensions);
    }
  }
}
