package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoPago_;
import org.crue.hercules.sgi.csp.model.ProyectoSocio_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoSocioPeriodoPagoSpecifications {

  /**
   * {@link ProyectoSocioPeriodoPago} de la {@link ProyectoSocio} con el id
   * indicado.
   * 
   * @param id identificador de la {@link ProyectoSocio}.
   * @return specification para obtener los {@link ProyectoSocioPeriodoPago} de la
   *         {@link ProyectoSocio} con el id indicado.
   */
  public static Specification<ProyectoSocioPeriodoPago> byProyectoSocioId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoSocioPeriodoPago_.proyectoSocio).get(ProyectoSocio_.id), id);
    };
  }

}
