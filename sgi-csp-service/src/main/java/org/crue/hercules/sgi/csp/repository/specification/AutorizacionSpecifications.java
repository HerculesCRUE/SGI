package org.crue.hercules.sgi.csp.repository.specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.Autorizacion_;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion.Estado;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion_;
import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN;
import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN_;
import org.springframework.data.jpa.domain.Specification;

public class AutorizacionSpecifications {

  private AutorizacionSpecifications() {
  }

  /**
   * {@link Autorizacion} por id
   * 
   * @param id identificador de la {@link Autorizacion}
   * @return specification para obtener las {@link Autorizacion} por id.
   */
  public static Specification<Autorizacion> byId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(Autorizacion_.id), id);
  }

  /**
   * {@link Autorizacion} en las que la persona es el solicitante.
   * 
   * @param personaRef referencia de la persona
   * @return specification para obtener las {@link Autorizacion} en las que la
   *         persona es el solicitante.
   */
  public static Specification<Autorizacion> bySolicitante(String personaRef) {
    return (root, query, cb) -> cb.equal(root.get(Autorizacion_.solicitanteRef), personaRef);
  }

  /**
   * {@link Autorizacion} en las que la el estado coincide el parametro
   * 
   * @param estado estado según el cual hay que filtrar
   * @return specification para obtener las {@link Autorizacion} en las que la
   *         persona es el solicitante.
   */
  public static Specification<Autorizacion> byEstado(Estado estado) {
    return (root, query, cb) -> cb.equal(root.get(Autorizacion_.estado).get(EstadoAutorizacion_.estado), estado);
  }

  /**
   * {@link Autorizacion} sin {@link NotificacionProyectoExternoCVN} asociada
   * 
   * @return specification para obtener las {@link Autorizacion} sin
   *         {@link NotificacionProyectoExternoCVN}
   */
  public static Specification<Autorizacion> withoutNotificacionProyectoExternoCVN() {
    return (root, query, cb) -> {
      Join<Autorizacion, NotificacionProyectoExternoCVN> joinRequisito = root
          .join(Autorizacion_.notificacionProyectoExternoCvn, JoinType.LEFT);

      return cb.isNull(joinRequisito.get(NotificacionProyectoExternoCVN_.id));
    };
  }

}
