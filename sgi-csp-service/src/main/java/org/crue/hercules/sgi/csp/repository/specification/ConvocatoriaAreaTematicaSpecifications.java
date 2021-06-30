package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.springframework.data.jpa.domain.Specification;

public class ConvocatoriaAreaTematicaSpecifications {

  /**
   * {@link ConvocatoriaAreaTematica} de la {@link Convocatoria} con el id
   * indicado.
   * 
   * @param id identificador de la {@link Convocatoria}.
   * @return specification para obtener los {@link ConvocatoriaAreaTematica} de la
   *         {@link Convocatoria} con el id indicado.
   */
  public static Specification<ConvocatoriaAreaTematica> byConvocatoriaId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaAreaTematica_.convocatoria).get(Convocatoria_.id), id);
    };
  }

}