package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.crue.hercules.sgi.csp.repository.custom.CustomSolicitudProyectoPresupuestoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudProyectoPresupuestoRepository extends JpaRepository<SolicitudProyectoPresupuesto, Long>,
    JpaSpecificationExecutor<SolicitudProyectoPresupuesto>, CustomSolicitudProyectoPresupuestoRepository {

  /**
   * Recupera todos las {@link SolicitudProyectoPresupuesto} asociados a una
   * {@link SolicitudProyecto}.
   * 
   * @param solicitudProyectoId Identificador de
   *                            {@link SolicitudProyectoPresupuesto}
   * @return listado de {@link SolicitudProyectoPresupuesto}
   */
  List<SolicitudProyectoPresupuesto> findBySolicitudProyectoId(Long solicitudProyectoId);

  /**
   * Recupera todos las {@link SolicitudProyectoPresupuesto} asociados a una
   * {@link Solicitud}.
   * 
   * @param solicitudId Identificador de {@link Solicitud}
   * @return número de entidades {@link SolicitudProyectoPresupuesto}
   */
  int countBySolicitudProyectoSolicitudId(Long solicitudId);

  /**
   * Comprueba si hay {@link SolicitudProyectoPresupuesto} asociados a la
   * {@link Solicitud}.
   * 
   * @param solicitudId Identificador de {@link Solicitud}
   * @return <code>true</code> si existen, <code>false</code> en otro caso
   */
  boolean existsBySolicitudProyectoSolicitudId(Long solicitudId);

}
