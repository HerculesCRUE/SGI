package org.crue.hercules.sgi.prc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoFuente;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoNodo;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoPuntos;
import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ConfiguracionBaremoOutput {
  private Long id;
  private String nombre;
  private Integer prioridad;
  private TipoBaremo tipoBaremo;
  private TipoFuente tipoFuente;
  private TipoPuntos tipoPuntos;
  private TipoNodo tipoNodo;
  private String epigrafe;
  private Long padreId;

  @JsonIgnore
  private EpigrafeCVN epigrafeCVN;

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
}
