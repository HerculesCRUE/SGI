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

import org.crue.hercules.sgi.rep.dto.BaseRestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RespuestaDto extends BaseRestDto {

  private MemoriaDto memoria;

  private ApartadoDto apartado;

  private TipoDocumentoRespuestaDto tipoDocumento;

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

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoDocumentoRespuestaDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nombre;
    private FormularioDto formulario;
    private Boolean activo;
  }
}