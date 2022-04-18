package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionCampo.TipoFormato;
import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ConfiguracionCampoOutput implements Serializable {
  private Long id;
  private TipoFormato tipoFormato;
  private Boolean fechaReferenciaInicio;
  private Boolean fechaReferenciaFin;
  private Boolean validacionAdicional;
  private String epigrafe;
  private String codigo;

  @JsonIgnore
  private EpigrafeCVN epigrafeCVN;
  @JsonIgnore
  private CodigoCVN codigoCVN;

  public void setEpigrafe(String epigrafe) {
    this.epigrafe = epigrafe;
    if (StringUtils.hasText(epigrafe)) {
      this.epigrafeCVN = EpigrafeCVN.getByCode(epigrafe);
    }
  }

  public void setEpigrafeCVN(EpigrafeCVN epigrafeCVN) {
    if (null != epigrafeCVN) {
      this.epigrafe = epigrafeCVN.getCode();
    }
  }

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
