package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.Autorizacion_;
import org.springframework.data.jpa.domain.Specification;

public class AutorizacionSpecifications {

  /**
   * {@link Autorizacion} por id
   * 
   * @param id identificador de la {@link Autorizacion}
   * @return specification para obtener las {@link Autorizacion} por id.
   */
  public static Specification<Autorizacion> byId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(Autorizacion_.id), id);
    };
  }

  /**
   * {@link Autorizacion} en las que la persona es el solicitante.
   * 
   * @param personaRef referencia de la persona
   * @return specification para obtener las {@link Autorizacion} en las que la
   *         persona es el solicitante.
   */
  public static Specification<Autorizacion> bySolicitante(String personaRef) {
    return (root, query, cb) -> {
      return cb.equal(root.get(Autorizacion_.solicitanteRef), personaRef);
    };
  }

}
