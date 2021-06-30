package org.crue.hercules.sgi.csp.repository.specification;

import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConceptoGasto_;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGastoCodigoEc_;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoConceptoGastoCodigoEcSpecifications {

  /**
   * {@link ProyectoConceptoGastoCodigoEc} conceptoGastos activos.
   * 
   * @return specification para obtener los {@link ConceptoGasto} activos.
   */
  public static Specification<ProyectoConceptoGastoCodigoEc> byConceptoGastoActivo() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoConceptoGastoCodigoEc_.proyectoConceptoGasto)
          .get(ProyectoConceptoGasto_.conceptoGasto).get(ConceptoGasto_.activo), Boolean.TRUE);
    };
  }

  /**
   * {@link ProyectoConceptoGastoCodigoEc} ProyectoConceptoGasto permitido.
   * 
   * @param permitido boolean indica si el gasto es permitido o no
   * @return specification para obtener los {@link ProyectoConceptoGasto}
   *         permitidos o no.
   */
  public static Specification<ProyectoConceptoGastoCodigoEc> byProyectoConceptoGastoPermitido(Boolean permitido) {
    return (root, query, cb) -> {
      return cb.equal(
          root.get(ProyectoConceptoGastoCodigoEc_.proyectoConceptoGasto).get(ProyectoConceptoGasto_.permitido),
          permitido);
    };
  }

  /**
   * Se obtienen los {@link ProyectoConceptoGastoCodigoEc} por proyecto
   * 
   * @param idProyecto identificador del {@link Proyecto}
   * @return specification para obtener los {@link ProyectoConceptoGastoCodigoEc}
   *         por proyecto
   */
  public static Specification<ProyectoConceptoGastoCodigoEc> byProyecto(Long idProyecto) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoConceptoGastoCodigoEc_.proyectoConceptoGasto)
          .get(ProyectoConceptoGasto_.proyecto).get(Proyecto_.id), idProyecto);
    };
  }

  /**
   * Se obtienen los {@link ProyectoConceptoGastoCodigoEc} por proyecto
   * 
   * @param idProyectoConceptoGasto identificador del
   *                                {@link ProyectoConceptoGasto}
   * @return specification para obtener los {@link ProyectoConceptoGastoCodigoEc}
   *         por proyecto
   */
  public static Specification<ProyectoConceptoGastoCodigoEc> byProyectoConceptoGasto(Long idProyectoConceptoGasto) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoConceptoGastoCodigoEc_.proyectoConceptoGasto).get(ProyectoConceptoGasto_.id),
          idProyectoConceptoGasto);
    };
  }

  /**
   * Se obtienen los {@link ProyectoConceptoGastoCodigoEc} con valores para las
   * fechas de inicio y fin
   * 
   * @return specification para obtener los {@link ProyectoConceptoGastoCodigoEc}
   *         con valor en las fechas
   */
  public static Specification<ProyectoConceptoGastoCodigoEc> withFechas() {
    return (root, query, cb) -> {

      return cb.and(cb.isNotNull(root.get(ProyectoConceptoGastoCodigoEc_.fechaInicio)),
          cb.isNotNull(root.get(ProyectoConceptoGastoCodigoEc_.fechaFin)));
    };
  }

  /**
   * {@link ProyectoConceptoGastoCodigoEc} del {@link Proyecto} con fechas
   * solapadas
   * 
   * @param fechaInicio fecha inicio de {@link ProyectoConceptoGastoCodigoEc}
   * @param fechaFin    fecha fin de la {@link ProyectoConceptoGastoCodigoEc}.
   * @return specification para obtener los {@link ProyectoConceptoGastoCodigoEc}
   *         con rango de fechas solapadas
   */
  public static Specification<ProyectoConceptoGastoCodigoEc> byRangoFechaSolapados(Instant fechaInicio,
      Instant fechaFin) {
    return (root, query, cb) -> {
      return cb.and(
          cb.or(cb.isNull(root.get(ProyectoConceptoGastoCodigoEc_.fechaInicio)),
              cb.lessThanOrEqualTo(root.get(ProyectoConceptoGastoCodigoEc_.fechaInicio),
                  fechaFin != null ? fechaFin : Instant.parse("2500-01-01T23:59:59Z"))),
          cb.or(cb.isNull(root.get(ProyectoConceptoGastoCodigoEc_.fechaFin)),
              cb.greaterThanOrEqualTo(root.get(ProyectoConceptoGastoCodigoEc_.fechaFin),
                  fechaInicio != null ? fechaInicio : Instant.parse("1900-01-01T00:00:00Z"))));
    };
  }

  /**
   * {@link ProyectoConceptoGastoCodigoEc} excluido en la lista.
   * 
   * @param excluidos lista de ids excluidos
   * @return specification para obtener los {@link ProyectoConceptoGastoCodigoEc}
   *         cuyo id no se encuentre entre los recibidos.
   */
  public static Specification<ProyectoConceptoGastoCodigoEc> notIn(List<Long> excluidos) {
    return (root, query, cb) -> {
      return root.get(ProyectoConceptoGastoCodigoEc_.id).in(excluidos).not();
    };
  }

  /**
   * {@link ProyectoConceptoGastoCodigoEc} por Código económico.
   * 
   * @param codigoEconomicoRef código económico de
   *                           {@link ProyectoConceptoGastoCodigoEc}
   * @return specification para obtener los {@link ProyectoConceptoGastoCodigoEc}
   *         con el codigoEconomicoRef indicado.
   */
  public static Specification<ProyectoConceptoGastoCodigoEc> byCodigoEconomicoRef(String codigoEconomicoRef) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoConceptoGastoCodigoEc_.codigoEconomicoRef), codigoEconomicoRef);
    };
  }

  /**
   * {@link ProyectoConceptoGastoCodigoEc} id diferente de
   * {@link ProyectoConceptoGastoCodigoEc} con el indicado.
   * 
   * @param id identificador de la {@link ProyectoConceptoGastoCodigoEc}.
   * @return specification para comprobar si el id es diferentes del
   *         {@link ProyectoConceptoGastoCodigoEc} indicado.
   */
  public static Specification<ProyectoConceptoGastoCodigoEc> byIdNotEqual(Long id) {
    return (root, query, cb) -> {
      if (id == null) {
        return cb.isTrue(cb.literal(true)); // always true = no filtering
      }
      return cb.equal(root.get(ProyectoConceptoGastoCodigoEc_.id), id).not();
    };
  }
}