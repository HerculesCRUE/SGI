package org.crue.hercules.sgi.csp.repository.specification;

import java.time.Instant;
import java.util.List;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConceptoGasto_;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc_;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConvocatoriaConceptoGastoCodigoEcSpecifications {

  /**
   * {@link ConvocatoriaConceptoGastoCodigoEc} conceptoGastos activos.
   * 
   * @return specification para obtener los {@link ConceptoGasto} activos.
   */
  public static Specification<ConvocatoriaConceptoGastoCodigoEc> byConceptoGastoActivo() {
    return (root, query, cb) -> cb.equal(root.get(ConvocatoriaConceptoGastoCodigoEc_.convocatoriaConceptoGasto)
        .get(ConvocatoriaConceptoGasto_.conceptoGasto).get(ConceptoGasto_.activo), Boolean.TRUE);
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
    return (root, query, cb) -> cb.equal(root.get(ConvocatoriaConceptoGastoCodigoEc_.convocatoriaConceptoGasto)
        .get(ConvocatoriaConceptoGasto_.permitido), permitido);
  }

  /**
   * Se obtienen los {@link ConvocatoriaConceptoGastoCodigoEc} por convocatoria
   * 
   * @param idConvocatoria identificador de la {@link Convocatoria}
   * @return specification para obtener los
   *         {@link ConvocatoriaConceptoGastoCodigoEc} por convocatoria
   */
  public static Specification<ConvocatoriaConceptoGastoCodigoEc> byConvocatoria(Long idConvocatoria) {
    return (root, query, cb) -> cb.equal(root.get(ConvocatoriaConceptoGastoCodigoEc_.convocatoriaConceptoGasto)
        .get(ConvocatoriaConceptoGasto_.convocatoria).get(Convocatoria_.id), idConvocatoria);
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
    return (root, query, cb) -> cb.equal(
        root.get(ConvocatoriaConceptoGastoCodigoEc_.convocatoriaConceptoGasto).get(ConvocatoriaConceptoGasto_.id),
        idConvocatoriaConceptoGasto);
  }

  /**
   * Se obtienen los {@link ConvocatoriaConceptoGastoCodigoEc} por
   * convocatoriaConceptosGastoIds
   * 
   * @param convocatoriaConceptosGastoIds identificadores de
   *                                      {@link ConvocatoriaConceptoGasto}
   * @return specification para obtener los
   *         {@link ConvocatoriaConceptoGastoCodigoEc}
   *         por convocatoriaConceptosGastoIds
   */
  public static Specification<ConvocatoriaConceptoGastoCodigoEc> byConvocatoriaConceptosGastoIds(
      List<Long> convocatoriaConceptosGastoIds) {
    return (root, query, cb) -> {
      if (convocatoriaConceptosGastoIds == null || convocatoriaConceptosGastoIds.isEmpty()) {
        return cb.isTrue(cb.literal(false));
      }

      return root.get(ConvocatoriaConceptoGastoCodigoEc_.convocatoriaConceptoGastoId)
          .in(convocatoriaConceptosGastoIds);
    };
  }

  /**
   * Se obtienen los {@link ConvocatoriaConceptoGastoCodigoEc} por
   * {@link ConceptoGasto}
   * 
   * @param convocatoriaConceptoGastoId identificador de la
   *                                    {@link ConvocatoriaConceptoGasto}
   * @return specification para obtener los
   *         {@link ConvocatoriaConceptoGastoCodigoEc} por {@link ConceptoGasto}
   */
  public static Specification<ConvocatoriaConceptoGastoCodigoEc> byConceptoGasto(
      Long convocatoriaConceptoGastoId) {
    return (root, query, cb) -> {
      Subquery<Long> queryConvocatoriaConceptoGasto = query.subquery(Long.class);
      Root<ConvocatoriaConceptoGasto> subqRoot = queryConvocatoriaConceptoGasto.from(ConvocatoriaConceptoGasto.class);
      queryConvocatoriaConceptoGasto
          .select(subqRoot.get(ConvocatoriaConceptoGasto_.conceptoGasto).get(ConceptoGasto_.id))
          .where(cb.equal(subqRoot.get(ConvocatoriaConceptoGasto_.id), convocatoriaConceptoGastoId));

      return root.get(ConvocatoriaConceptoGastoCodigoEc_.convocatoriaConceptoGasto)
          .get(ConvocatoriaConceptoGasto_.conceptoGasto).get(ConceptoGasto_.id).in(queryConvocatoriaConceptoGasto);
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
    return (root, query, cb) -> cb.and(cb.isNotNull(root.get(ConvocatoriaConceptoGastoCodigoEc_.fechaInicio)),
        cb.isNotNull(root.get(ConvocatoriaConceptoGastoCodigoEc_.fechaFin)));
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
    return (root, query, cb) -> cb.and(
        cb.or(cb.isNull(root.get(ConvocatoriaConceptoGastoCodigoEc_.fechaInicio)),
            cb.lessThanOrEqualTo(root.get(ConvocatoriaConceptoGastoCodigoEc_.fechaInicio),
                fechaFin != null ? fechaFin : Instant.parse("2500-01-01T23:59:59Z"))),
        cb.or(cb.isNull(root.get(ConvocatoriaConceptoGastoCodigoEc_.fechaFin)),
            cb.greaterThanOrEqualTo(root.get(ConvocatoriaConceptoGastoCodigoEc_.fechaFin),
                fechaInicio != null ? fechaInicio : Instant.parse("1900-01-01T00:00:00Z"))));
  }

  /**
   * {@link ConvocatoriaConceptoGasto} de la {@link Convocatoria} con meses
   * solapados
   * 
   * @param mesInicial mes inicio de {@link ConvocatoriaConceptoGasto}
   * @param mesFinal   mes fin de la {@link ConvocatoriaConceptoGasto}.
   * @return specification para obtener los {@link ConvocatoriaConceptoGasto} con
   *         rango de meses solapados
   */
  public static Specification<ConvocatoriaConceptoGastoCodigoEc> byRangoMesesConceptoGastoSolapados(Integer mesInicial,
      Integer mesFinal) {
    return (root, query, cb) -> cb
        .and(
            cb.or(
                cb.isNull(root.get(ConvocatoriaConceptoGastoCodigoEc_.convocatoriaConceptoGasto)
                    .get(ConvocatoriaConceptoGasto_.mesInicial)),
                cb.lessThanOrEqualTo(
                    root.get(ConvocatoriaConceptoGastoCodigoEc_.convocatoriaConceptoGasto)
                        .get(ConvocatoriaConceptoGasto_.mesInicial),
                    mesFinal != null ? mesFinal : 9999)),
            cb.or(cb.isNull(root.get(
                ConvocatoriaConceptoGastoCodigoEc_.convocatoriaConceptoGasto)
                .get(ConvocatoriaConceptoGasto_.mesFinal)), cb.greaterThanOrEqualTo(
                    root.get(
                        ConvocatoriaConceptoGastoCodigoEc_.convocatoriaConceptoGasto)
                        .get(ConvocatoriaConceptoGasto_.mesFinal),
                    mesInicial != null ? mesInicial : 1)));
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
    return (root, query, cb) -> root.get(ConvocatoriaConceptoGastoCodigoEc_.id).in(excluidos).not();
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
    return (root, query, cb) -> cb.equal(root.get(ConvocatoriaConceptoGastoCodigoEc_.codigoEconomicoRef),
        codigoEconomicoRef);
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