package org.crue.hercules.sgi.rep.dto.eti;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ApartadoDefinicionDto implements Serializable {
  private static final long serialVersionUID = 1L;

  private String lang;
  private String nombre;
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
