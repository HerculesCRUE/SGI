package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;

import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;

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
public class ProduccionCientificaApiOutput implements Serializable {
  private String idRef;
  private String epigrafeCVN;
  private TipoEstadoProduccion estado;
}
