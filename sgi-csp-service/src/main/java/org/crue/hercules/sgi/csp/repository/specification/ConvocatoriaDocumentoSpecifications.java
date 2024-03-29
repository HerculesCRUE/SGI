package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaDocumento;
import org.crue.hercules.sgi.csp.model.ConvocatoriaDocumento_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.springframework.data.jpa.domain.Specification;

public class ConvocatoriaDocumentoSpecifications {

  /**
   * {@link ConvocatoriaDocumento} de la {@link Convocatoria} con el id indicado.
   * 
   * @param id identificador de la {@link Convocatoria}.
   * @return specification para obtener los {@link ConvocatoriaDocumento} de la
   *         {@link Convocatoria} con el id indicado.
   */
  public static Specification<ConvocatoriaDocumento> byConvocatoriaId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaDocumento_.convocatoria).get(Convocatoria_.id), id);
    };
  }

  /**
   * {@link ConvocatoriaDocumento} publicos.
   * 
   * @return specification para obtener los {@link ConvocatoriaDocumento}
   *         publicos.
   */
  public static Specification<ConvocatoriaDocumento> onlyPublicos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaDocumento_.publico), Boolean.TRUE);
    };
  }
}
