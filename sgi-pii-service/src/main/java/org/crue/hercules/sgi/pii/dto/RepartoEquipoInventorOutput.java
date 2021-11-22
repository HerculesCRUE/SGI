package org.crue.hercules.sgi.pii.dto;

import java.math.BigDecimal;

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
public class RepartoEquipoInventorOutput {
  private Long id;
  private Long repartoId;
  private Long invencionInventorId;
  private String proyectoRef;
  private BigDecimal importeNomina;
  private BigDecimal importeProyecto;
  private BigDecimal importeOtros;
}
