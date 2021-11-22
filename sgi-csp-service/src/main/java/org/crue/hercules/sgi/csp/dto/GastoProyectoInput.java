package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.EstadoGastoProyecto.TipoEstadoGasto;
import org.crue.hercules.sgi.csp.model.GastoProyecto;

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
public class GastoProyectoInput implements Serializable {

  private Long proyectoId;

  @NotBlank
  @Size(max = GastoProyecto.GASTO_REF_LENGTH)
  private String gastoRef;

  private Long conceptoGastoId;

  private Instant fechaCongreso;

  @Min(GastoProyecto.IMPORTE_INSCRIPCION_MIN)
  private BigDecimal importeInscripcion;

  @Size(max = GastoProyecto.OBSERVACIONES_LENGTH)
  private String observaciones;

  private EstadoGastoProyecto estado;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class EstadoGastoProyecto {
    private Long id;
    private TipoEstadoGasto estado;
    private Instant fechaEstado;
    private String comentario;
  }

}
