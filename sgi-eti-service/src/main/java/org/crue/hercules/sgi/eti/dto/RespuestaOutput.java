package org.crue.hercules.sgi.eti.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespuestaOutput {
  private Long id;

  private TipoDocumentoDto tipoDocumento;

  private String valor;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoDocumentoDto {
    private Long id;

    private String nombre;

  }
}
