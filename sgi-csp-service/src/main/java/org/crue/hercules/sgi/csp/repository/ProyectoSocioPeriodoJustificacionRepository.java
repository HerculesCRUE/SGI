package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoSocioPeriodoJustificacionRepository
    extends JpaRepository<ProyectoSocioPeriodoJustificacion, Long>,
    JpaSpecificationExecutor<ProyectoSocioPeriodoJustificacion> {

  /**
   * Recupera todos los {@link ProyectoSocioPeriodoJustificacion} asociados a un
   * {@link ProyectoSocio}.
   * 
   * @param proyectoSocioId Identificador de {@link ProyectoSocio}.
   * @return listado de {@link ProyectoSocioPeriodoJustificacion}
   */
  List<ProyectoSocioPeriodoJustificacion> findAllByProyectoSocioId(Long proyectoSocioId);

  /**
   * Elimina todos los {@link ProyectoSocioPeriodoJustificacion} asociados a un
   * {@link ProyectoSocio}.
   * 
   * @param id Identificador de {@link ProyectoSocio}.
   */
  void deleteByProyectoSocioId(Long id);
}
