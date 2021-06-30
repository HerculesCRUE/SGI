package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoFase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoFaseRepository
    extends JpaRepository<ProyectoFase, Long>, JpaSpecificationExecutor<ProyectoFase> {

  /**
   * Comprueba si existe algun {@link ProyectoFase} asociado a algún
   * {@link Proyecto}.
   * 
   * @param proyectoId id del {@link Proyecto}.
   * @return true si existe, false si no existe.
   */
  boolean existsByProyectoId(Long proyectoId);

}
