package org.crue.hercules.sgi.csp.repository.custom;

import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.csp.dto.GrupoDto;
import org.crue.hercules.sgi.csp.dto.RelacionEjecucionEconomica;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link Grupo}.
 */
@Component
public interface CustomGrupoRepository {

  /**
   * Devuelve si grupoRef pertenece a un grupo de investigación con el campo
   * "Grupo especial de investigación" a "No" el 31 de diciembre del
   * año que se esta baremando
   *
   * @param grupoRef        grupoRef
   * @param fechaBaremacion fecha de baremación
   * @return true/false
   */
  boolean isGrupoBaremable(Long grupoRef, Instant fechaBaremacion);

  /**
   * Devuelve una lista de {@link GrupoDto} pertenecientes a un determinado
   * grupo y que estén a 31 de diciembre del año de baremación
   *
   * @param fechaBaremacion fecha de baremación
   * 
   * @return Lista de {@link GrupoDto}
   */
  List<GrupoDto> findAllByAnio(Instant fechaBaremacion);

  /**
   * Obtiene datos economicos de los {@link Grupo}
   * 
   * @param specification condiciones que deben cumplir.
   * @param pageable      paginación.
   * @return el listado de entidades {@link RelacionEjecucionEconomica} paginadas
   *         y
   *         filtradas.
   */
  Page<RelacionEjecucionEconomica> findRelacionesEjecucionEconomica(Specification<Grupo> specification,
      Pageable pageable);

  /**
   * Obtiene los ids de {@link Grupo} que cumplen con la specification recibida.
   * 
   * @param specification condiciones que deben cumplir.
   * @return lista de ids de {@link Grupo}.
   */
  List<Long> findIds(Specification<Grupo> specification);

}
