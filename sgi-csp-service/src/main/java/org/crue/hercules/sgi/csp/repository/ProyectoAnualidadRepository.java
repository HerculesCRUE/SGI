package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad;
import org.crue.hercules.sgi.csp.repository.custom.CustomProyectoAnualidadRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ProyectoAnualidad}.
 */
@Repository
public interface ProyectoAnualidadRepository
    extends JpaRepository<ProyectoAnualidad, Long>, JpaSpecificationExecutor<ProyectoAnualidad>, CustomProyectoAnualidadRepository {

  /**
   * Recupera el {@link ProyectoAnualidad} deu un {@link Proyecto} cuyo año sea el
   * recibido por parámetro.
   * 
   * @param anio       Año del {@link ProyectoAnualidad}.
   * @param proyectoId Identificador del {@link Proyecto}.
   * @return {@link ProyectoAnualidad}.
   */
  Optional<ProyectoAnualidad> findByAnioAndProyectoId(Integer anio, Long proyectoId);

}
