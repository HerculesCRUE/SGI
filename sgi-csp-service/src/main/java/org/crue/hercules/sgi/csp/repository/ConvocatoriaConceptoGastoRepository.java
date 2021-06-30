package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConvocatoriaConceptoGastoRepository
    extends JpaRepository<ConvocatoriaConceptoGasto, Long>, JpaSpecificationExecutor<ConvocatoriaConceptoGasto> {

  /**
   * Busca un {@link ConvocatoriaConceptoGasto} por su {@link Convocatoria} y
   * {@link ConceptoGasto}.
   * 
   * @param convocatoriaId  Id de la {@link Convocatoria}
   * @param conceptoGastoId Id del {@link ConceptoGasto}
   * @param permitido       indica si el concepto de gasto es permitido o no
   * @return una {@link ConvocatoriaConceptoGasto}
   */
  List<ConvocatoriaConceptoGasto> findByConvocatoriaIdAndConceptoGastoActivoTrueAndConceptoGastoIdAndPermitidoIs(
      Long convocatoriaId, Long conceptoGastoId, Boolean permitido);

  /**
   * Obtiene las {@link ConvocatoriaConceptoGasto} permitidos para una
   * {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param paging         la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaConceptoGasto} de la
   *         {@link Convocatoria} paginadas.
   */
  Page<ConvocatoriaConceptoGasto> findAllByConvocatoriaIdAndConceptoGastoActivoTrueAndPermitidoTrue(Long convocatoriaId,
      Pageable paging);

  /**
   * Obtiene las {@link ConvocatoriaConceptoGasto} NO permitidos para una
   * {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param paging         la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaConceptoGasto} de la
   *         {@link Convocatoria} paginadas.
   */
  Page<ConvocatoriaConceptoGasto> findAllByConvocatoriaIdAndConceptoGastoActivoTrueAndPermitidoFalse(
      Long convocatoriaId, Pageable paging);

  /**
   * Obtiene las {@link ConvocatoriaConceptoGasto} para una {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @return la lista de entidades {@link ConvocatoriaConceptoGasto} de la
   *         {@link Convocatoria} paginadas.
   */
  List<ConvocatoriaConceptoGasto> findAllByConvocatoriaIdAndConceptoGastoActivoTrue(Long convocatoriaId);

}
