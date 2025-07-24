package org.crue.hercules.sgi.pii.dto;

import java.time.Instant;
import java.util.Collection;

import org.crue.hercules.sgi.pii.model.ProcedimientoAccionATomar;
import org.crue.hercules.sgi.pii.model.ProcedimientoComentarios;
import org.crue.hercules.sgi.pii.model.TipoProcedimientoDescripcion;
import org.crue.hercules.sgi.pii.model.TipoProcedimientoNombre;

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
  private Collection<ProcedimientoAccionATomar> accionATomar;
  private Instant fechaLimiteAccion;
  private Boolean generarAviso;
  private Collection<ProcedimientoComentarios> comentarios;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoProcedimiento {
    private Long id;
    private Collection<TipoProcedimientoNombre> nombre;
    private Collection<TipoProcedimientoDescripcion> descripcion;
  }

}
