package org.crue.hercules.sgi.prc.dto;

import java.time.Instant;

import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class PublicacionResumen extends ProduccionCientificaResumen {

  private String tituloPublicacion;
  private String tipoProduccion;
  private Instant fechaPublicacion;

  public PublicacionResumen(Long id, String produccionCientificaRef, TipoEstadoProduccion estado,
      EpigrafeCVN epigrafeCVN, String tituloPublicacion, String tipoProduccion, String fechaPublicacion) {
    super(id, produccionCientificaRef, estado, epigrafeCVN);
    this.tituloPublicacion = tituloPublicacion;
    this.tipoProduccion = tipoProduccion;
    this.fechaPublicacion = StringUtils.hasText(fechaPublicacion) ? Instant.parse(fechaPublicacion) : null;
  }

}
