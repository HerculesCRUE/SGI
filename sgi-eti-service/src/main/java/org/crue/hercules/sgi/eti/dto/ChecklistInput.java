package org.crue.hercules.sgi.eti.dto;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
 * Representación de entrada del API REST de un Checklist.
 * <p>
 * La entidad Checklist representa las respuestas al formulario preliminar para
 * la solicitud de una PeticionEvaluacion.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistInput implements Serializable {
  @NotBlank
  private String personaRef;

  @NotNull
  private Long formlyId;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @NotBlank
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
    // setJson(jsonNode.toString());

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
}
