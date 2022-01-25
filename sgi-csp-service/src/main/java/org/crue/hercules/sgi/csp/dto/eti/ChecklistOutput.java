package org.crue.hercules.sgi.csp.dto.eti;

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

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.time.Instant;

/**
 * Representación de salida del API REST de un Checklist.
 * <p>
 * La entidad Checklist representa las respuestas al formulario preliminar para
 * la solicitud de una PeticionEvaluacion.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistOutput implements Serializable {
  private Long id;
  private String personaRef;
  private Formly formly;
  private Instant fechaCreacion;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private String respuesta;

  @JsonRawValue
  public String getRespuesta() {
    return respuesta;
  }

  public void setRespuesta(final String respuesta) {
    this.respuesta = respuesta;
  }

  @JsonProperty(value = "respuesta")
  public void setRespuestaRaw(JsonNode jsonNode) throws IOException {
    // this leads to non-standard json:

    if (jsonNode.isNull()) {
      setRespuesta(null);
    } else {
      StringWriter stringWriter = new StringWriter();
      ObjectMapper objectMapper = new ObjectMapper();
      try (JsonGenerator generator = new JsonFactory(objectMapper).createGenerator(stringWriter)) {
        generator.writeTree(jsonNode);
        setRespuesta(stringWriter.toString());
      }
    }
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Formly implements Serializable {
    private Long id;
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
        }
        setEsquema(stringWriter.toString());
      }
    }
  }
}
