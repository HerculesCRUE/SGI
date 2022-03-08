package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ValorCampoOutput implements Serializable {
  private Long id;
  private String valor;
  private Integer orden;
  private Long campoProduccionCientificaId;
}
