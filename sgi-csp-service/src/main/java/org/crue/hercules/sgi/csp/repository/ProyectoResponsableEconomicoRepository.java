package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoResponsableEconomico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ProyectoResponsableEconomico}.
 */
@Repository
public interface ProyectoResponsableEconomicoRepository
    extends JpaRepository<ProyectoResponsableEconomico, Long>, JpaSpecificationExecutor<ProyectoResponsableEconomico> {

  /**
   * Obtiene los {@link ProyectoResponsableEconomico} asociadas a un
   * {@link Proyecto}
   * 
   * @param proyectoId Identificador del {@link Proyecto}
   * @return Listado de {@link ProyectoResponsableEconomico} del {@link Proyecto}
   */
  List<ProyectoResponsableEconomico> findByProyectoId(Long proyectoId);

}
