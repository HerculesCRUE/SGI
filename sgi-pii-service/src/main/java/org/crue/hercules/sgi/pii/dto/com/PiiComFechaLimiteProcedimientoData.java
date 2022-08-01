package org.crue.hercules.sgi.pii.dto.com;

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
public class PiiComFechaLimiteProcedimientoData {
  private Instant fechaLimite;
  private String tipoProcedimiento;
  private String accionATomar;
}
