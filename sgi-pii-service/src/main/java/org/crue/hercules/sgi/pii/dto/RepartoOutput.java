package org.crue.hercules.sgi.pii.dto;

import java.math.BigDecimal;
import java.time.Instant;

import org.crue.hercules.sgi.pii.model.Reparto.Estado;

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
public class RepartoOutput {
  private Long id;
  private Long invencionId;
  private Instant fecha;
  private BigDecimal importeUniversidad;
  private BigDecimal importeEquipoInventor;
  private Estado estado;
}
