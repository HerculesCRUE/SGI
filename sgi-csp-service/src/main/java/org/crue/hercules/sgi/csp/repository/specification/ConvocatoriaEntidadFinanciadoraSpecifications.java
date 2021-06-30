package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.springframework.data.jpa.domain.Specification;

public class ConvocatoriaEntidadFinanciadoraSpecifications {

  /**
   * {@link ConvocatoriaEntidadFinanciadora} de la {@link Convocatoria} con el id
   * indicado.
   * 
   * @param id identificador de la {@link Convocatoria}.
   * @return specification para obtener los
   *         {@link ConvocatoriaEntidadFinanciadora} de la {@link Convocatoria}
   *         con el id indicado.
   */
  public static Specification<ConvocatoriaEntidadFinanciadora> byConvocatoriaId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaEntidadFinanciadora_.convocatoria).get(Convocatoria_.id), id);
    };
  }

}