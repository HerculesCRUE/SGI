package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudProyectoSocioEquipoRepository
    extends JpaRepository<SolicitudProyectoSocioEquipo, Long>, JpaSpecificationExecutor<SolicitudProyectoSocioEquipo> {

  /**
   * Recupera los {@link SolicitudProyectoSocioEquipo} asociados al id de
   * {@link SolicitudProyectoSocio} recibido por parámetro
   * 
   * @param solicitudProyectoSocioId Identificador de
   *                                 {@link SolicitudProyectoSocio}
   * @return listado {@link SolicitudProyectoSocioEquipo}
   */
  List<SolicitudProyectoSocioEquipo> findAllBySolicitudProyectoSocioId(Long solicitudProyectoSocioId);

  /**
   * Se eliminan todos los {@link SolicitudProyectoSocioEquipo} asociadosal id de
   * {@link SolicitudProyectoSocio} recibido por parámetro.
   * 
   * @param solicitudProyectoSocioId Identificador de
   *                                 {@link SolicitudProyectoSocio}
   */
  void deleteBySolicitudProyectoSocioId(Long solicitudProyectoSocioId);

}
