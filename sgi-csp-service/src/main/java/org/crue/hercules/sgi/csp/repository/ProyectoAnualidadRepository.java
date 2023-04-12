package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad;
import org.crue.hercules.sgi.csp.repository.custom.CustomProyectoAnualidadRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ProyectoAnualidad}.
 */
@Repository
public interface ProyectoAnualidadRepository extends JpaRepository<ProyectoAnualidad, Long>,
    JpaSpecificationExecutor<ProyectoAnualidad>, CustomProyectoAnualidadRepository {

  /**
   * Comprueba si existe algun {@link ProyectoAnualidad} del {@link Proyecto} cuyo
   * año sea el recibido por parámetro.
   * 
   * @param anio       Año del {@link ProyectoAnualidad}.
   * @param proyectoId Identificador del {@link Proyecto}.
   * @return {@link ProyectoAnualidad}.
   */
  boolean existsByAnioAndProyectoId(Integer anio, Long proyectoId);

  /**
   * Comprueba si existe algun {@link ProyectoAnualidad} del {@link Proyecto} cuyo
   * año sea el
   * recibido por parámetro.
   * 
   * @param anio       Año del {@link ProyectoAnualidad}.
   * @param proyectoId Identificador del {@link Proyecto}.
   * @param id         Identificador del {@link ProyectoAnualidad} excluido de la
   *                   busqueda.
   * @return {@link ProyectoAnualidad}.
   */
  boolean existsByAnioAndProyectoIdAndIdNot(Integer anio, Long proyectoId, Long id);

  /**
   * Recupera el {@link ProyectoAnualidad} del un {@link Proyecto}
   * 
   * @param proyectoId Identificador del {@link Proyecto}.
   * @param pageable   datos paginación.
   * @return {@link ProyectoAnualidad}.
   */
  Page<ProyectoAnualidad> findAllByProyectoId(Long proyectoId, Pageable pageable);

  /**
   * Recupera una lista de objetos {@link ProyectoAnualidad} de un
   * {@link Proyecto}
   * 
   * @param proyectoId Identificador del {@link Proyecto}
   * @return lista de {@link ProyectoAnualidad}
   */
  List<ProyectoAnualidad> findByProyectoId(Long proyectoId);

}
