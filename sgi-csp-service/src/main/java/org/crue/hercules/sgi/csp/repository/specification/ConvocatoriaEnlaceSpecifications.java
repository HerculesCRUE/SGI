package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.springframework.data.jpa.domain.Specification;

public class ConvocatoriaEnlaceSpecifications {

  /**
   * {@link ConvocatoriaEnlace} de la {@link Convocatoria} con el id indicado.
   * 
   * @param id identificador de la {@link Convocatoria}.
   * @return specification para obtener los {@link ConvocatoriaEnlace} de la
   *         {@link Convocatoria} con el id indicado.
   */
  public static Specification<ConvocatoriaEnlace> byConvocatoriaId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaEnlace_.convocatoria).get(Convocatoria_.id), id);
    };
  }

}
