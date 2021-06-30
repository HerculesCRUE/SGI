package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto_;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto_;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.springframework.data.jpa.domain.Specification;

public class SolicitudProyectoPresupuestoSpecifications {

  /**
   * {@link SolicitudProyectoPresupuesto} del {@link Solicitud} con el id
   * indicado.
   * 
   * @param id identificador de la {@link Solicitud}.
   * @return specification para obtener los {@link SolicitudProyectoPresupuesto}
   *         de la {@link Solicitud} con el id indicado.
   */
  public static Specification<SolicitudProyectoPresupuesto> bySolicitudId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(SolicitudProyectoPresupuesto_.solicitudProyecto).get(SolicitudProyecto_.solicitud)
          .get(Solicitud_.id), id);
    };
  }

  /**
   * {@link SolicitudProyectoPresupuesto} de la entidad con la referencia
   * indicada.
   * 
   * @param entidadRef identificador de la entidad.
   * @return specification para obtener los {@link SolicitudProyectoPresupuesto}
   *         de la entidad con la referencia indicada.
   */
  public static Specification<SolicitudProyectoPresupuesto> byEntidadRef(String entidadRef) {
    return (root, query, cb) -> {
      return cb.equal(root.get(SolicitudProyectoPresupuesto_.entidadRef), entidadRef);
    };
  }

  /**
   * {@link SolicitudProyectoPresupuesto} con financiacionAjena igual a la
   * indicada.
   * 
   * @param financiacionAjena flag financiacionAjena.
   * @return specification para obtener los {@link SolicitudProyectoPresupuesto}
   *         de la entidad con la referencia indicada.
   */
  public static Specification<SolicitudProyectoPresupuesto> byFinanciacionAjena(Boolean financiacionAjena) {
    return (root, query, cb) -> {
      return cb.equal(root.get(SolicitudProyectoPresupuesto_.financiacionAjena), financiacionAjena);
    };
  }

}
