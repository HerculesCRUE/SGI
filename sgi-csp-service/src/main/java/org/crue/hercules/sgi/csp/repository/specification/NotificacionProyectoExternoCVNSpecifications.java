package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN;
import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN_;
import org.springframework.data.jpa.domain.Specification;

public class NotificacionProyectoExternoCVNSpecifications {

  /**
   * Devuelve todos los {@link NotificacionProyectoExternoCVN} con el asociados a
   * un
   * {@link Autorizacion}
   * 
   * @param autorizacionId identificador de la autorizacion
   * @return specification para obtener los {@link NotificacionProyectoExternoCVN}
   *         asociados a un {@link Autorizacion}
   */
  public static Specification<NotificacionProyectoExternoCVN> byAutorizacionId(Long autorizacionId) {
    return (root, query, cb) -> {
      return cb.equal(root.get(NotificacionProyectoExternoCVN_.autorizacionId), autorizacionId);
    };
  }

}
