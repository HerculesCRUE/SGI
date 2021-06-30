package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadGestora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoEntidadGestoraRepository
    extends JpaRepository<ProyectoEntidadGestora, Long>, JpaSpecificationExecutor<ProyectoEntidadGestora> {

  /**
   * Comprueba la existencia de la relación entre el {@link Proyecto} y una
   * entidad gestora.
   * 
   * @param proyectoId id del {@link Proyecto}.
   * @param entidadRef EntidadRef del {@link ProyectoEntidadGestora}.
   * @return true en el {@link Proyecto} existe un {@link ProyectoEntidadGestora}
   *         con el mismo nombre.
   */
  boolean existsProyectoEntidadGestoraByProyectoIdAndEntidadRef(Long proyectoId, String entidadRef);

  /**
   * Comprueba la existencia de la relación entre el {@link Proyecto} y una
   * entidad gestora excluyendo de la búsqueda un determinado
   * {@link ProyectoEntidadGestora}
   * 
   * @param id         id del {@link ProyectoEntidadGestora}.
   * @param proyectoId id del {@link Proyecto}.
   * @param entidadRef EntidadRef del {@link ProyectoEntidadGestora}.
   * @return true en el {@link Proyecto} existe un {@link ProyectoEntidadGestora}
   *         con el mismo nombre.
   */
  boolean existsProyectoEntidadGestoraByIdNotAndProyectoIdAndEntidadRef(Long id, Long proyectoId, String entidadRef);

}
