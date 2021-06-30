package org.crue.hercules.sgi.csp.repository.specification;

import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConceptoGasto_;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc_;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.springframework.data.jpa.domain.Specification;

public class ConvocatoriaConceptoGastoCodigoEcSpecifications {

  /**
   * {@link ConvocatoriaConceptoGastoCodigoEc} conceptoGastos activos.
   * 
   * @return specification para obtener los {@link ConceptoGasto} activos.
   */
  public static Specification<ConvocatoriaConceptoGastoCodigoEc> byConceptoGastoActivo() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaConceptoGastoCodigoEc_.convocatoriaConceptoGasto)
          .get(ConvocatoriaConceptoGasto_.conceptoGasto).get(ConceptoGasto_.activo), Boolean.TRUE);
    };
  }

  /**
   * {@link ConvocatoriaConceptoGastoCodigoEc} ConvocatoriaConceptoGasto
   * permitido.
   * 
   * @param permitido boolean indica si el gasto es permitido o no
   * @return specification para obtener los {@link ConvocatoriaConceptoGasto}
   *         permitidos o no.
   */
  public static Specification<ConvocatoriaConceptoGastoCodigoEc> byConvocatoriaConceptoGastoPermitido(
      Boolean permitido) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaConceptoGastoCodigoEc_.convocatoriaConceptoGasto)
          .get(ConvocatoriaConceptoGasto_.permitido), permitido);
    };
  }

  /**
   * Se obtienen los {@link ConvocatoriaConceptoGastoCodigoEc} por convocatoria
   * 
   * @param idConvocatoria identificador de la {@link Convocatoria}
   * @return specification para obtener los
   *         {@link ConvocatoriaConceptoGastoCodigoEc} por convocatoria
   */
  public static Specification<ConvocatoriaConceptoGastoCodigoEc> byConvocatoria(Long idConvocatoria) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaConceptoGastoCodigoEc_.convocatoriaConceptoGasto)
          .get(ConvocatoriaConceptoGasto_.convocatoria).get(Convocatoria_.id), idConvocatoria);
    };
  }

  /**
   * Se obtienen los {@link ConvocatoriaConceptoGastoCodigoEc} por convocatoria
   * 
   * @param idConvocatoriaConceptoGasto identificador de la
   *                                    {@link ConvocatoriaConceptoGasto}
   * @return specification para obtener los
   *         {@link ConvocatoriaConceptoGastoCodigoEc} por convocatoria
   */
  public static Specification<ConvocatoriaConceptoGastoCodigoEc> byConvocatoriaConceptoGasto(
      Long idConvocatoriaConceptoGasto) {
    return (root, query, cb) -> {
      return cb.equal(
          root.get(ConvocatoriaConceptoGastoCodigoEc_.convocatoriaConceptoGasto).get(ConvocatoriaConceptoGasto_.id),
          idConvocatoriaConceptoGasto);
    };
  }

  /**
   * Se obtienen los {@link ConvocatoriaConceptoGastoCodigoEc} con valores para
   * las fechas de inicio y fin
   * 
   * @return specification para obtener los
   *         {@link ConvocatoriaConceptoGastoCodigoEc} con valor en las fechas
   */
  public static Specification<ConvocatoriaConceptoGastoCodigoEc> withFechas() {
    return (root, query, cb) -> {

      return cb.and(cb.isNotNull(root.get(ConvocatoriaConceptoGastoCodigoEc_.fechaInicio)),
          cb.isNotNull(root.get(ConvocatoriaConceptoGastoCodigoEc_.fechaFin)));
    };
  }

  /**
   * {@link ConvocatoriaConceptoGastoCodigoEc} de la {@link Convocatoria} con
   * fechas solapadas
   * 
   * @param fechaInicio fecha inicio de {@link ConvocatoriaConceptoGastoCodigoEc}
   * @param fechaFin    fecha fin de la {@link ConvocatoriaConceptoGastoCodigoEc}.
   * @return specification para obtener los
   *         {@link ConvocatoriaConceptoGastoCodigoEc} con rango de fechas
   *         solapadas
   */
  public static Specification<ConvocatoriaConceptoGastoCodigoEc> byRangoFechaSolapados(Instant fechaInicio,
      Instant fechaFin) {
    return (root, query, cb) -> {
      return cb.and(
          cb.or(cb.isNull(root.get(ConvocatoriaConceptoGastoCodigoEc_.fechaInicio)),
              cb.lessThanOrEqualTo(root.get(ConvocatoriaConceptoGastoCodigoEc_.fechaInicio),
                  fechaFin != null ? fechaFin : Instant.parse("2500-01-01T23:59:59Z"))),
          cb.or(cb.isNull(root.get(ConvocatoriaConceptoGastoCodigoEc_.fechaFin)),
              cb.greaterThanOrEqualTo(root.get(ConvocatoriaConceptoGastoCodigoEc_.fechaFin),
                  fechaInicio != null ? fechaInicio : Instant.parse("1900-01-01T00:00:00Z"))));
    };
  }

  /**
   * {@link ConvocatoriaConceptoGastoCodigoEc} excluido en la lista.
   * 
   * @param excluidos lista de ids excluidos
   * @return specification para obtener los
   *         {@link ConvocatoriaConceptoGastoCodigoEc} cuyo id no se encuentre
   *         entre los recibidos.
   */
  public static Specification<ConvocatoriaConceptoGastoCodigoEc> notIn(List<Long> excluidos) {
    return (root, query, cb) -> {
      return root.get(ConvocatoriaConceptoGastoCodigoEc_.id).in(excluidos).not();
    };
  }

  /**
   * {@link ConvocatoriaConceptoGastoCodigoEc} por Código económico.
   * 
   * @param codigoEconomicoRef código económico de
   *                           {@link ConvocatoriaConceptoGastoCodigoEc}
   * @return specification para obtener los
   *         {@link ConvocatoriaConceptoGastoCodigoEc} con el codigoEconomicoRef
   *         indicado.
   */
  public static Specification<ConvocatoriaConceptoGastoCodigoEc> byCodigoEconomicoRef(String codigoEconomicoRef) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaConceptoGastoCodigoEc_.codigoEconomicoRef), codigoEconomicoRef);
    };
  }

  /**
   * {@link ConvocatoriaConceptoGastoCodigoEc} id diferente de
   * {@link ConvocatoriaConceptoGastoCodigoEc} con el indicado.
   * 
   * @param id identificador de la {@link ConvocatoriaConceptoGastoCodigoEc}.
   * @return specification para comprobar si el id es diferentes del
   *         {@link ConvocatoriaConceptoGastoCodigoEc} indicado.
   */
  public static Specification<ConvocatoriaConceptoGastoCodigoEc> byIdNotEqual(Long id) {
    return (root, query, cb) -> {
      if (id == null) {
        return cb.isTrue(cb.literal(true)); // always true = no filtering
      }
      return cb.equal(root.get(ConvocatoriaConceptoGastoCodigoEc_.id), id).not();
    };
  }
}