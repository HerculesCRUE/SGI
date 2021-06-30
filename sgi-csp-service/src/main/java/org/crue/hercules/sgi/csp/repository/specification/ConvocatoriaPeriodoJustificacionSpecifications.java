package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.springframework.data.jpa.domain.Specification;

public class ConvocatoriaPeriodoJustificacionSpecifications {

  /**
   * {@link ConvocatoriaPeriodoJustificacion} de la {@link Convocatoria} con el id
   * indicado.
   * 
   * @param id identificador de la {@link Convocatoria}.
   * @return specification para obtener los
   *         {@link ConvocatoriaPeriodoJustificacion} de la {@link Convocatoria}
   *         con el id indicado.
   */
  public static Specification<ConvocatoriaPeriodoJustificacion> byConvocatoriaId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaPeriodoJustificacion_.convocatoria).get(Convocatoria_.id), id);
    };
  }

}
