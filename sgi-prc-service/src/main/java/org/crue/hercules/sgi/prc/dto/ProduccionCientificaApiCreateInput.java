package org.crue.hercules.sgi.prc.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.prc.model.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProduccionCientificaApiCreateInput extends ProduccionCientificaApiInput {

  public enum TipoEstadoProduccionCientifica {
    VALIDADO,
    PENDIENTE;
  }

  @NotEmpty
  @Size(max = BaseEntity.ID_REF_LENGTH)
  private String idRef;

  @NotEmpty
  @Size(max = BaseEntity.EPIGRAFE_LENGTH)
  private String epigrafeCVN;

  private TipoEstadoProduccionCientifica estado;

}
