package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoConceptoGastoRepository
    extends JpaRepository<ProyectoConceptoGasto, Long>, JpaSpecificationExecutor<ProyectoConceptoGasto> {

  /**
   * Busca un {@link ProyectoConceptoGasto} por su {@link Proyecto} y
   * {@link ConceptoGasto}.
   * 
   * @param proyectoId      Id del {@link Proyecto}
   * @param conceptoGastoId Id del {@link ConceptoGasto}
   * @param permitido       indica si el concepto de gasto es permitido o no
   * @return un {@link ProyectoConceptoGasto}
   */
  List<ProyectoConceptoGasto> findByProyectoIdAndConceptoGastoActivoTrueAndConceptoGastoIdAndPermitidoIs(
      Long proyectoId, Long conceptoGastoId, Boolean permitido);

  /**
   * Obtiene los {@link ProyectoConceptoGasto} permitidos para un
   * {@link Proyecto}.
   *
   * @param proyectoId el id del {@link Proyecto}.
   * @param paging     la información de la paginación.
   * @return la lista de entidades {@link ProyectoConceptoGasto} del
   *         {@link Proyecto} paginadas.
   */
  Page<ProyectoConceptoGasto> findAllByProyectoIdAndConceptoGastoActivoTrueAndPermitidoTrue(Long proyectoId,
      Pageable paging);

  /**
   * Obtiene los {@link ProyectoConceptoGasto} NO permitidos para un
   * {@link Proyecto}.
   *
   * @param proyectoId el id del {@link Proyecto}.
   * @param paging     la información de la paginación.
   * @return la lista de entidades {@link ProyectoConceptoGasto} del
   *         {@link Proyecto} paginadas.
   */
  Page<ProyectoConceptoGasto> findAllByProyectoIdAndConceptoGastoActivoTrueAndPermitidoFalse(Long proyectoId,
      Pageable paging);

  /**
   * Obtiene los {@link ProyectoConceptoGasto} para un {@link Proyecto}.
   *
   * @param proyectoId el id del {@link Proyecto}.
   * @return la lista de entidades {@link ProyectoConceptoGasto} del
   *         {@link Proyecto} paginadas.
   */
  List<ProyectoConceptoGasto> findAllByProyectoIdAndConceptoGastoActivoTrue(Long proyectoId);

}
