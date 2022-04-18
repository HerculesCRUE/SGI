package org.crue.hercules.sgi.prc.dto.sgepii;

import java.io.Serializable;

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
public class IngresoColumnaDefDto implements Serializable {
  private String id;
  private String nombre;
  private boolean acumulable;
  private boolean importeReparto;
}
