package org.crue.hercules.sgi.pii.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class ProcedimientoOutput {

  private Long id;
  private Instant fecha;
  private TipoProcedimiento tipoProcedimiento;
  private Long solicitudProteccionId;
  private String accionATomar;
  private Instant fechaLimiteAccion;
  private Boolean generarAviso;
  private String comentarios;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoProcedimiento {
    private Long id;
    private String nombre;
    private String descripcion;
  }

}
