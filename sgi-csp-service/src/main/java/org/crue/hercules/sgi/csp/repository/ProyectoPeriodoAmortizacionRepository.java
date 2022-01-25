package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ProyectoPeriodoAmortizacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ProyectoPeriodoAmortizacion}.
 */
@Repository
public interface ProyectoPeriodoAmortizacionRepository
    extends JpaRepository<ProyectoPeriodoAmortizacion, Long>, JpaSpecificationExecutor<ProyectoPeriodoAmortizacion> {

  /**
   * Obtiene las {@link ProyectoPeriodoAmortizacion} con el proyectoSGERef
   * indicado.
   *
   * @param proyectoSGERef la referencia.
   * @param paging         la información de la paginación.
   * @return la lista de entidades {@link ProyectoPeriodoAmortizacion} del
   *         proyectoSGERef paginadas.
   */
  Page<ProyectoPeriodoAmortizacion> findAllByProyectoSGERef(String proyectoSGERef,
      Pageable paging);

}
