package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.springframework.data.jpa.domain.Specification;

public class ConvocatoriaEntidadConvocanteSpecifications {

  /**
   * {@link ConvocatoriaEntidadConvocante} de la {@link Convocatoria} con el id
   * indicado.
   * 
   * @param id identificador de la {@link Convocatoria}.
   * @return specification para obtener los {@link ConvocatoriaEntidadConvocante}
   *         de la {@link Convocatoria} con el id indicado.
   */
  public static Specification<ConvocatoriaEntidadConvocante> byConvocatoriaId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaEntidadConvocante_.convocatoria).get(Convocatoria_.id), id);
    };
  }

}