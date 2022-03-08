package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;
import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class CampoProduccionCientificaOutput implements Serializable {
  private Long id;

  private Long produccionCientificaId;

  private String codigo;

  @JsonIgnore
  private CodigoCVN codigoCVN;

  public void setCodigo(String codigo) {
    this.codigo = codigo;
    if (StringUtils.hasText(codigo)) {
      this.codigoCVN = CodigoCVN.getByInternValue(codigo);
    }
  }

  public void setCodigoCVN(CodigoCVN codigoCVN) {
    if (null != codigoCVN) {
      this.codigo = codigoCVN.getInternValue();
    }
  }
}
