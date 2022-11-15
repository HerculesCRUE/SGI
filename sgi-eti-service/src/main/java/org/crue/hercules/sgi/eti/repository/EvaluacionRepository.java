package org.crue.hercules.sgi.eti.repository;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.repository.custom.CustomEvaluacionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Evaluacion}.
 */
@Repository
public interface EvaluacionRepository
    extends JpaRepository<Evaluacion, Long>, JpaSpecificationExecutor<Evaluacion>, CustomEvaluacionRepository {

  /**
   * Obtener todas las entidades paginadas {@link Evaluacion} activas para una
   * determinada {@link ConvocatoriaReunion} que no sean de revisión mínima.
   *
   * @param id       Id de {@link ConvocatoriaReunion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  Page<Evaluacion> findAllByActivoTrueAndConvocatoriaReunionIdAndEsRevMinimaFalse(Long id, Pageable pageable);

  /**
   * Recupera aquellas evaluaciones activas que sean o no revisión mínima según el
   * parámetro recibido.
   * 
   * @param esRevMinima           indicador de revisión mínima.
   * @param idTipoEvaluacion      identificador del tipo de evaluación a
   *                              recuperar.
   * @param idConvocatoriaReunion identificador de la convocatoria a recuperar.
   * @return listado de evaluaciones.
   */
  List<Evaluacion> findByActivoTrueAndTipoEvaluacionIdAndEsRevMinimaAndConvocatoriaReunionId(Long idTipoEvaluacion,
      Boolean esRevMinima, Long idConvocatoriaReunion);

  /**
   * Recupera la última evaluación de una memoria.
   * 
   * @param idMemoria identificador de la memoria.
   * @return evaluación.
   */
  Optional<Evaluacion> findFirstByMemoriaIdAndActivoTrueOrderByVersionDesc(Long idMemoria);

  /**
   * Recupera la última evaluación de una memoria y su tipo de evaluación
   * 
   * @param idMemoria        identificador de la memoria.
   * @param idTipoEvaluacion identificador del tipo de evaluación.
   * @return evaluación.
   */
  Optional<Evaluacion> findFirstByMemoriaIdAndTipoEvaluacionIdAndActivoTrueOrderByVersionDesc(Long idMemoria,
      Long idTipoEvaluacion);

  /**
   * Recupera la evaluación de la memoria con la última versión
   * 
   * @param idMemoria el identificador del objeto {@link Memoria}
   * @param version   el número de versión
   * @return el objeto {@link Evaluacion}
   */
  Evaluacion findByMemoriaIdAndVersionAndActivoTrue(Long idMemoria, Integer version);

  /**
   * Comprueba si existen {@link Evaluacion} asociadas a una
   * {@link ConvocatoriaReunion}
   *
   * @param convocatoriaReunionId Id de {@link ConvocatoriaReunion}.
   * @return si hay acta asociado a la convocatoria de reunión
   */
  boolean existsByConvocatoriaReunionIdAndActivoIsTrue(Long convocatoriaReunionId);
}