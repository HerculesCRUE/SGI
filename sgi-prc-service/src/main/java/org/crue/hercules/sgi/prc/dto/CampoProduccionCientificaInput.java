package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.crue.hercules.sgi.prc.model.BaseEntity;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;
import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class CampoProduccionCientificaInput implements Serializable {

  @NotNull
  private Long produccionCientificaId;

  @NotEmpty
  @Size(max = BaseEntity.CAMPO_CVN_LENGTH)
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
