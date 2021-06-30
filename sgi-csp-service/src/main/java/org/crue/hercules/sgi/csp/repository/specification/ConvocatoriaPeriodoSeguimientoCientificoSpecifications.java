package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.springframework.data.jpa.domain.Specification;

public class ConvocatoriaPeriodoSeguimientoCientificoSpecifications {

  /**
   * {@link ConvocatoriaPeriodoSeguimientoCientifico} de la {@link Convocatoria}
   * con el id indicado.
   * 
   * @param id identificador de la {@link Convocatoria}.
   * @return specification para obtener los
   *         {@link ConvocatoriaPeriodoSeguimientoCientifico} de la
   *         {@link Convocatoria} con el id indicado.
   */
  public static Specification<ConvocatoriaPeriodoSeguimientoCientifico> byConvocatoriaId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaPeriodoSeguimientoCientifico_.convocatoria).get(Convocatoria_.id), id);
    };
  }

}