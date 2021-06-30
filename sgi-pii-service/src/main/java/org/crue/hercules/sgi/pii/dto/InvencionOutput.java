package org.crue.hercules.sgi.pii.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvencionOutput {
  private Long id;
  private String titulo;
  private Instant fechaComunicacion;
  private String descripcion;
  private String comentarios;
  private TipoProteccion tipoProteccion;
  private Boolean activo;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoProteccion {
    private Long id;
    private String nombre;
  }
}
