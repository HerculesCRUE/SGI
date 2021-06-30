package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.ConvocatoriaPartida;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPartida_;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.springframework.data.jpa.domain.Specification;

public class ConvocatoriaPartidaSpecifications {

  /**
   * {@link ConvocatoriaPartida} de la {@link Convocatoria} con el id indicado.
   * 
   * @param id identificador de la {@link Convocatoria}.
   * @return specification para obtener los {@link ConvocatoriaPartida} de la
   *         {@link Convocatoria} con el id indicado.
   */
  public static Specification<ConvocatoriaPartida> byConvocatoriaId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaPartida_.convocatoria).get(Convocatoria_.id), id);
    };
  }

}