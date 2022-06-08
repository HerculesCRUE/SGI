package org.crue.hercules.sgi.eti.repository.specification;

import java.time.Instant;

import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion_;
import org.crue.hercules.sgi.eti.model.Dictamen_;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluacion_;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications de {@link Evaluacion}.
 */
public class EvaluacionSpecifications {

  /**
   * Evaluaciones activas.
   * 
   * @return specification para obtener las evaluaciones activas.
   */
  public static Specification<Evaluacion> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Evaluacion_.activo), Boolean.TRUE);
    };
  }

  /**
   * Evaluaciones inactivas.
   * 
   * @return specification para obtener las evaluaciones inactivas.
   */
  public static Specification<Evaluacion> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Evaluacion_.activo), Boolean.FALSE);
    };
  }

  /**
   * Evaluaciones de una {@link ConvocatoriaReunion}.
   * 
   * @param idConvocatoriaReunion identificador de la {@link ConvocatoriaReunion}.
   * @return specification para obtener las evaluaciones asociadas a la
   *         convocatoria.
   */
  public static Specification<Evaluacion> byConvocatoriaReunionId(Long idConvocatoriaReunion) {
    return (root, query, cb) -> {
      return cb.equal(root.get(Evaluacion_.convocatoriaReunion).get(ConvocatoriaReunion_.id), idConvocatoriaReunion);
    };
  }

  /**
   * Evaluaciones que son o no son revisión mínima.
   * 
   * @param esRevMinima true para obtener las evaluaciones que son revisión mínima
   *                    o false para las que no lo son.
   * @return specification para obtener las evaluaciones que son o no son revisión
   *         mínima.
   */
  public static Specification<Evaluacion> byEsRevMinima(boolean esRevMinima) {
    return (root, query, cb) -> {
      return cb.equal(root.get(Evaluacion_.esRevMinima), esRevMinima);
    };
  }

  public static Specification<Evaluacion> memoriaId(Long idMemoria) {
    return (root, query, cb) -> {
      return cb.equal(root.get(Evaluacion_.memoria).get(Memoria_.id), idMemoria);
    };
  }

  public static Specification<Evaluacion> byFechaDictamenBetween(Instant fechaInicio, Instant fechaFin) {
    return (root, query, cb) -> {
      return cb.between(root.get(Evaluacion_.fechaDictamen), fechaInicio, fechaFin);
    };
  }

  public static Specification<Evaluacion> byMemoriaEstado(Long tipoEstadoMemoria) {
    return (root, query, cb) -> {
      return cb.equal(root.get(Evaluacion_.memoria).get(Memoria_.ESTADO_ACTUAL), tipoEstadoMemoria);
    };
  }

  public static Specification<Evaluacion> byDictamenEstado(Long tipoEstadoDictamen) {
    return (root, query, cb) -> {
      return cb.equal(root.get(Evaluacion_.dictamen).get(Dictamen_.ID), tipoEstadoDictamen);
    };
  }

}
