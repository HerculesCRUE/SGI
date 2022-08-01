package org.crue.hercules.sgi.csp.repository.custom;

import org.crue.hercules.sgi.csp.dto.ProyectoSeguimientoEjecucionEconomica;
import org.crue.hercules.sgi.csp.dto.RelacionEjecucionEconomica;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link ProyectoProyectoSge}.
 */
@Component
public interface CustomProyectoProyectoSgeRepository {

  /**
   * Obtiene datos economicos de los {@link ProyectoProyectoSge}
   * 
   * @param specification condiciones que deben cumplir.
   * @param pageable      paginación.
   * @return el listado de entidades {@link RelacionEjecucionEconomica} paginadas
   *         y filtradas.
   */
  Page<RelacionEjecucionEconomica> findRelacionesEjecucionEconomica(Specification<ProyectoProyectoSge> specification,
      Pageable pageable);

  /**
   * Obtiene datos economicos de los {@link ProyectoProyectoSge}
   * 
   * @param specification condiciones que deben cumplir.
   * @param pageable      paginación.
   * @return el listado de entidades {@link ProyectoSeguimientoEjecucionEconomica}
   *         paginadas
   *         y filtradas.
   */
  Page<ProyectoSeguimientoEjecucionEconomica> findProyectosSeguimientoEjecucionEconomica(
      Specification<ProyectoProyectoSge> specification, Pageable pageable);
}
