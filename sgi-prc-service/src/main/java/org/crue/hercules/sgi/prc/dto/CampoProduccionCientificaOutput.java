package org.crue.hercules.sgi.prc.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class CampoProduccionCientificaOutput extends CampoProduccionCientificaInput {

  private Long id;
}
