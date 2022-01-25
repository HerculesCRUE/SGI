package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPalabraClave;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPalabraClave_;
import org.springframework.data.jpa.domain.Specification;

public class ConvocatoriaPalabraClaveSpecifications {

  /**
   * {@link ConvocatoriaPalabraClave} de la entidad {@link Convocatoria} con el id
   * indicado.
   * 
   * @param id identificador de la entidad {@link Convocatoria}.
   * @return specification para obtener las {@link ConvocatoriaPalabraClave} de
   *         la entidad {@link Convocatoria} con el id indicado.
   */
  public static Specification<ConvocatoriaPalabraClave> byConvocatoriaId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaPalabraClave_.convocatoriaId), id);
    };
  }
}
