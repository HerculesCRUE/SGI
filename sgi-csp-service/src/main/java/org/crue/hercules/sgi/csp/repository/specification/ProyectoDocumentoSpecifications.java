package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoDocumento;
import org.crue.hercules.sgi.csp.model.ProyectoDocumento_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoDocumentoSpecifications {

  /**
   * {@link ProyectoDocumento} de la {@link Proyecto} con el id indicado.
   * 
   * @param id identificador de la {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoDocumento} de la
   *         {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoDocumento> byProyectoId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoDocumento_.proyecto).get(Proyecto_.id), id);
    };
  }

}
