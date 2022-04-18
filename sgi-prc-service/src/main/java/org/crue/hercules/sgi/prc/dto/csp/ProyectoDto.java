package org.crue.hercules.sgi.prc.dto.csp;

import java.io.Serializable;
import java.math.BigDecimal;
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
public class ProyectoDto implements Serializable {

  public enum ClasificacionCVN {
    /** Proyectos competitivos */
    COMPETITIVOS,
    /** Contratos, Convenios, Proyectos no competitivos */
    NO_COMPETITIVOS;
  }

  private Long id;
  private String titulo;
  private Instant fechaInicio;
  private Instant fechaFin;
  private Instant fechaFinDefinitiva;
  private ClasificacionCVN clasificacionCVN;
  private BigDecimal totalImporteConcedido;
  private BigDecimal importeConcedido;
  private Long ambitoGeograficoId;
  private Boolean convocatoriaExcelencia;

}
