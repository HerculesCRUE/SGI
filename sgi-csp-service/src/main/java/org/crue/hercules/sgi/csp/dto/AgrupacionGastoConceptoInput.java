package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
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
public class AgrupacionGastoConceptoInput implements Serializable {
  public static final int NOMBRE_LENGTH = 50;
  @NotNull
  private Long agrupacionId;

  @NotNull
  private Long conceptoGastoId;

}
