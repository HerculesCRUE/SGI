package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;

import javax.persistence.Convert;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.converter.CodigoCVNConverter;
import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class AliasEnumeradoOutput implements Serializable {
  private Long id;
  private String codigo;
  private String prefijoEnumerado;

  @JsonIgnore
  @Convert(converter = CodigoCVNConverter.class)
  private CodigoCVN codigoCVN;

  public void setCodigo(String codigo) {
    this.codigo = codigo;
    if (StringUtils.hasText(codigo)) {
      this.codigoCVN = CodigoCVN.getByCode(codigo);
    }
  }

  public void setCodigoCVN(CodigoCVN codigoCVN) {
    if (null != codigoCVN) {
      this.codigo = codigoCVN.getCode();
    }
  }
}
