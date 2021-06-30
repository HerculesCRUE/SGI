package org.crue.hercules.sgi.framework.problem;

import java.io.Serializable;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Problem implements Serializable {
  public static final URI UNKNOWN_PROBLEM_TYPE = URI.create("urn:problem-type:unknown");

  private final URI type;
  private final String title;
  private final int status;
  private final String detail;
  private final URI instance;
  private final Map<String, Object> extensions;

  public static ProblemBuilder builder() {
    return new ProblemBuilder();
  }

  public Problem(URI type, String title, int status, String detail, URI instance, Map<String, Object> extensions) {
    this.type = type;
    this.title = title;
    this.status = status;
    this.detail = detail;
    this.instance = instance;
    this.extensions = Collections.unmodifiableMap(new HashMap<>(extensions));
  }

  public Problem(URI type, String title, int status, String detail, URI instance, Set<Extension> extensions) {
    this(type, title, status, detail, instance, buildMapFromExtensions(extensions));
  }

  public Problem(URI type, String title, int status, String detail, URI instance, Extension... extensions) {
    this(type, title, status, detail, instance, buildMapFromExtensions(extensions));
  }

  private static Map<String, Object> buildMapFromExtensions(Set<Extension> extensions) {
    Map<String, Object> map = new HashMap<>(extensions.size());
    extensions.forEach(e -> map.put(e.getKey(), e.getValue()));
    return map;
  }

  private static Map<String, Object> buildMapFromExtensions(Extension[] extensions) {
    Map<String, Object> map = new HashMap<>(extensions.length);
    for (Problem.Extension e : extensions) {
      map.put(e.getKey(), e.getValue());
    }
    return map;
  }

  public URI getType() {
    return this.type;
  }

  public String getTitle() {
    return this.title;
  }

  public int getStatus() {
    return this.status;
  }

  public String getDetail() {
    return this.detail;
  }

  public URI getInstance() {
    return this.instance;
  }

  public Set<String> getExtensions() {
    return Collections.unmodifiableSet(extensions.keySet());
  }

  public Object getExtensionValue(String name) {
    return extensions.get(name);
  }

  public boolean hasExtension(String extension) {
    return extensions.containsKey(extension);
  }

  public static final class Extension implements Map.Entry<String, Object> {

    private final String key;
    private Object value;

    private Extension(String key, Object value) {
      this.key = key;
      this.value = value;
    }

    @Override
    public String getKey() {
      return key;
    }

    @Override
    public Object getValue() {
      return value;
    }

    @Override
    public Object setValue(Object value) {
      this.value = value;
      return value;
    }
  }

  public static class ProblemBuilder {
    private URI type = UNKNOWN_PROBLEM_TYPE;
    private String title;
    private int status = 0;
    private String detail;
    private URI instance = URI.create("urn:uuid:" + UUID.randomUUID());
    private Map<String, Object> extensions = new LinkedHashMap<>();

    public ProblemBuilder() {
    }

    public ProblemBuilder type(URI type) {
      this.type = type;
      return this;
    }

    public ProblemBuilder title(String title) {
      this.title = title;
      return this;
    }

    public ProblemBuilder status(int status) {
      this.status = status;
      return this;
    }

    public ProblemBuilder detail(String detail) {
      this.detail = detail;
      return this;
    }

    public ProblemBuilder instance(URI instance) {
      this.instance = instance;
      return this;
    }

    public ProblemBuilder extension(String name, Object value) {
      extensions.put(name, value);
      return this;
    }

    public Problem build() {
      return new Problem(type, title, status, detail, instance, extensions);
    }
  }
}
