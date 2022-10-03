package org.crue.hercules.sgi.eti.dto;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representación de salida del API REST de un Formly.
 * <p>
 * La entidad Formly representa un formulario configurable.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormlyOutput implements Serializable {
  private Long id;
  private String nombre;
  private Integer version;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private String esquema;

  @JsonRawValue
  public String getEsquema() {
    return esquema;
  }

  public void setEsquema(final String esquema) {
    this.esquema = esquema;
  }

  @JsonProperty(value = "esquema")
  public void setEsquemaRaw(JsonNode jsonNode) throws IOException {
    // this leads to non-standard json:

    if (jsonNode.isNull()) {
      setEsquema(null);
    } else {
      StringWriter stringWriter = new StringWriter();
      ObjectMapper objectMapper = new ObjectMapper();
      try (JsonGenerator generator = new JsonFactory(objectMapper).createGenerator(stringWriter)) {
        generator.writeTree(jsonNode);
        setEsquema(stringWriter.toString());
      }
    }
  }
}
