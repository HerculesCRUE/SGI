package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoSocioEquipoRepository
    extends JpaRepository<ProyectoSocioEquipo, Long>, JpaSpecificationExecutor<ProyectoSocioEquipo> {

  /**
   * Devuelve el listado de todos los {@link ProyectoSocioEquipo} asociados a un
   * {@link ProyectoSocio}
   * 
   * @param proyectoSocioId Identificador del {@link ProyectoSocio}
   * @return listado de {@link ProyectoSocioEquipo}
   */
  List<ProyectoSocioEquipo> findAllByProyectoSocioId(Long proyectoSocioId);

  /**
   * Elimina todos los {@link ProyectoSocioEquipo} asociados a un
   * {@link ProyectoSocio}.
   * 
   * @param id Identificador de {@link ProyectoSocio}.
   */
  void deleteByProyectoSocioId(Long id);
}
