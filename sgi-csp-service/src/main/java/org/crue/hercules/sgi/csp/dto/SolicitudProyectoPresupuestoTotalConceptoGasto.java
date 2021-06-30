package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import org.crue.hercules.sgi.csp.model.ConceptoGasto;

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
public class SolicitudProyectoPresupuestoTotalConceptoGasto implements Serializable {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  private ConceptoGasto conceptoGasto;
  private BigDecimal importeTotal;

}
