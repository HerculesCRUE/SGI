package org.crue.hercules.sgi.prc.dto.sgp;

import java.io.Serializable;

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
public class SexenioDto implements Serializable {

  private String numero;
  private String personaRef;
  private String paisRef;
}
