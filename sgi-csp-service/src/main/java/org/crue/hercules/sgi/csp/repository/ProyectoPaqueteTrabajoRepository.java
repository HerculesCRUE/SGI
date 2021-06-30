package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.crue.hercules.sgi.csp.repository.custom.CustomProyectoPaqueteTrabajoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoPaqueteTrabajoRepository extends JpaRepository<ProyectoPaqueteTrabajo, Long>,
    JpaSpecificationExecutor<ProyectoPaqueteTrabajo>, CustomProyectoPaqueteTrabajoRepository {

  /**
   * Comprueba la existencia de {@link ProyectoPaqueteTrabajo} en el
   * {@link Proyecto} con el nombre indicado.
   * 
   * @param proyectoId id del {@link Proyecto}.
   * @param nombre     Nombre del {@link ProyectoPaqueteTrabajo}.
   * @return true en el {@link Proyecto} existe un {@link ProyectoPaqueteTrabajo}
   *         con el mismo nombre.
   */
  boolean existsProyectoPaqueteTrabajoByProyectoIdAndNombre(Long proyectoId, String nombre);

  /**
   * Comprueba la existencia de {@link ProyectoPaqueteTrabajo} en el
   * {@link Proyecto} con el nombre indicado excluyendo de la búsqueda un
   * determinado {@link ProyectoPaqueteTrabajo}
   * 
   * @param id         id del {@link ProyectoPaqueteTrabajo}.
   * @param proyectoId id del {@link Proyecto}.
   * @param nombre     Nombre del {@link ProyectoPaqueteTrabajo}.
   * @return true en el {@link Proyecto} existe un {@link ProyectoPaqueteTrabajo}
   *         con el mismo nombre.
   */
  boolean existsProyectoPaqueteTrabajoByIdNotAndProyectoIdAndNombre(Long id, Long proyectoId, String nombre);

}
