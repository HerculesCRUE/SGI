package org.crue.hercules.sgi.rep.dto.eti;

import java.io.IOException;
import java.io.StringWriter;

import org.crue.hercules.sgi.rep.dto.BaseRestDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RespuestaInput extends BaseRestDto {

  private Long apartadoId;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private String valor;

  @JsonRawValue
  public String getValor() {
    return valor;
  }

  public void setValor(final String valor) {
    this.valor = valor;
  }

  @JsonProperty(value = "valor")
  public void setEsquemaRaw(JsonNode jsonNode) throws IOException {
    // this leads to non-standard json:

    if (jsonNode.isNull()) {
      setValor(null);
    } else {
      StringWriter stringWriter = new StringWriter();
      ObjectMapper objectMapper = new ObjectMapper();
      try (JsonGenerator generator = new JsonFactory(objectMapper).createGenerator(stringWriter)) {
        generator.writeTree(jsonNode);
        setValor(stringWriter.toString());
      }
    }
  }

}