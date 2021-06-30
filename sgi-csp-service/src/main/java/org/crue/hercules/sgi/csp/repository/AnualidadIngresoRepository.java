package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.AnualidadIngreso;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link AnualidadIngreso}.
 */
@Repository
public interface AnualidadIngresoRepository
    extends JpaRepository<AnualidadIngreso, Long>, JpaSpecificationExecutor<AnualidadIngreso> {

  /**
   * Elimina los {@link AnualidadIngreso} por el identificador de
   * {@link ProyectoAnualidad}.
   * 
   * @param id de {@link ProyectoAnualidad}.
   */
  void deleteByProyectoAnualidadId(Long id);

}
