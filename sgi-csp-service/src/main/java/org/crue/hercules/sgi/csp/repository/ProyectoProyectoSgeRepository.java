package org.crue.hercules.sgi.csp.repository;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ProyectoProyectoSge}.
 */
@Repository
public interface ProyectoProyectoSgeRepository
    extends JpaRepository<ProyectoProyectoSge, Long>, JpaSpecificationExecutor<ProyectoProyectoSge> {

  /**
   * Indica si existen {@link ProyectoProyectoSge} de un {@link Proyecto}
   * 
   * @param proyectoId identificador de la {@link Proyecto}
   * @return si existe la entidad {@link ProyectoProyectoSge}
   */
  boolean existsByProyectoId(Long proyectoId);

  Optional<ProyectoProyectoSge> findByIdAndProyectoUnidadGestionRefIn(Long id, List<String> unidadGestionRefs);

}
