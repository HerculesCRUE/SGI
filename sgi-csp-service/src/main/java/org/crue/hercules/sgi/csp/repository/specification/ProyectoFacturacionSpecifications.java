package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.EstadoValidacionIP;
import org.crue.hercules.sgi.csp.model.EstadoValidacionIP_;
import org.crue.hercules.sgi.csp.model.ProyectoFacturacion;
import org.crue.hercules.sgi.csp.model.ProyectoFacturacion_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoFacturacionSpecifications {

  /**
   * {@link ProyectoFacturacion} cuya estado validacion ip es validada.
   *
   * @return specification para obtener los {@link ProyectoFacturacion} cuya
   *         estado validacion ip es validada.
   */
  public static Specification<ProyectoFacturacion> validado() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoFacturacion_.estadoValidacionIP).get(EstadoValidacionIP_.estado),
          EstadoValidacionIP.TipoEstadoValidacion.VALIDADA);
    };
  }

  public static Specification<ProyectoFacturacion> byFechaConformidadNotNull() {
    return (root, query, cb) -> {
      return cb.isNotNull(root.get(ProyectoFacturacion_.fechaConformidad));
    };
  }
}