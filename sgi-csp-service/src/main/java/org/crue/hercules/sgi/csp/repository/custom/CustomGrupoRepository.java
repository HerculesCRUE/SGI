package org.crue.hercules.sgi.csp.repository.custom;

import java.time.Instant;
import java.util.List;

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
   * Obtiene los ids de {@link Grupo} que cumplen con la specification recibida.
   * 
   * @param specification condiciones que deben cumplir.
   * @return lista de ids de {@link Grupo}.
   */
  List<Long> findIds(Specification<Grupo> specification);

  /**
   * Devuelve una lista paginada y filtrada {@link Grupo} sin duplicados y
   * ordenable por el nombre.
   * 
   * @param specs    condiciones que deben cumplir.
   * @param pageable la información de la paginación.
   * @return la lista de {@link Grupo} paginadas y/o filtradas.
   */
  Page<Grupo> findAllDistinct(Specification<Grupo> specs, Pageable pageable);

}
