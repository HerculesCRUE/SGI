package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.BaseEntity;
import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ProduccionCientificaInput implements Serializable {

  @NotEmpty
  @Size(max = BaseEntity.ID_REF_LENGTH)
  private String produccionCientificaRef;

  @NotEmpty
  @Size(max = BaseEntity.EPIGRAFE_LENGTH)
  private String epigrafe;

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
