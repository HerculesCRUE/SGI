package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.crue.hercules.sgi.eti.model.TipoDocumento_;
import org.springframework.data.jpa.domain.Specification;

public class TipoDocumentoSpecifications {

  public static Specification<TipoDocumento> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoDocumento_.activo), Boolean.TRUE);
    };
  }
}
