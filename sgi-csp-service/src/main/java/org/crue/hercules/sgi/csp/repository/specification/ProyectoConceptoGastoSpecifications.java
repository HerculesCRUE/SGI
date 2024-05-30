package org.crue.hercules.sgi.csp.repository.specification;

import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConceptoGasto_;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProyectoConceptoGastoSpecifications {

  /**
   * {@link ProyectoConceptoGasto} proyecto activos.
   * 
   * @return specification para obtener los {@link ProyectoConceptoGasto} con un
   *         {@link Proyecto} activo.
   */
  public static Specification<ProyectoConceptoGasto> byProyectoActivo() {
    return (root, query, cb) -> cb.equal(root.get(ProyectoConceptoGasto_.proyecto).get(Proyecto_.activo), Boolean.TRUE);
  }

  /**
   * {@link ProyectoConceptoGasto} ProyectoConceptoGasto permitido.
   * 
   * @param permitido boolean indica si el gasto es permitido o no
   * @return specification para obtener los {@link ProyectoConceptoGasto}
   *         permitidos o no.
   */
  public static Specification<ProyectoConceptoGasto> byPermitido(Boolean permitido) {
    return (root, query, cb) -> cb.equal(root.get(ProyectoConceptoGasto_.permitido), permitido);
  }

  /**
   * Se obtienen los {@link ProyectoConceptoGasto} por proyecto
   * 
   * @param idProyecto identificador del {@link Proyecto}
   * @return specification para obtener los {@link ProyectoConceptoGasto} por
   *         proyecto
   */
  public static Specification<ProyectoConceptoGasto> byProyecto(Long idProyecto) {
    return (root, query, cb) -> cb.equal(root.get(ProyectoConceptoGasto_.proyecto).get(Proyecto_.id), idProyecto);
  }

  /**
   * Se obtienen los {@link ProyectoConceptoGasto} con valores para las fechas de
   * inicio y fin
   * 
   * @return specification para obtener los {@link ProyectoConceptoGasto} con
   *         valor en las fechas
   */
  public static Specification<ProyectoConceptoGasto> withFechas() {
    return (root, query, cb) -> cb.and(cb.isNotNull(root.get(ProyectoConceptoGasto_.fechaInicio)),
        cb.isNotNull(root.get(ProyectoConceptoGasto_.fechaFin)));
  }

  /**
   * Se obtienen los {@link ProyectoConceptoGasto} con valores para las fechas de
   * inicio y/o fin
   * 
   * @return specification para obtener los {@link ProyectoConceptoGasto} con
   *         valor en alguna de sus fechas
   */
  public static Specification<ProyectoConceptoGasto> withFechaInicioOrFechaFin() {
    return (root, query, cb) -> cb.or(cb.isNotNull(root.get(ProyectoConceptoGasto_.fechaInicio)),
        cb.isNotNull(root.get(ProyectoConceptoGasto_.fechaFin)));
  }

  /**
   * {@link ProyectoConceptoGasto} de la {@link Proyecto} con meses solapados
   * 
   * @param fechaInicio fecha inicio de {@link ProyectoConceptoGasto}
   * @param fechaFin    fecha fin de la {@link ProyectoConceptoGasto}.
   * @return specification para obtener los {@link ProyectoConceptoGasto} con
   *         rango de meses solapados
   */
  public static Specification<ProyectoConceptoGasto> byRangoFechasSolapados(Instant fechaInicio, Instant fechaFin) {
    return (root, query, cb) -> cb.and(
        cb.or(cb.isNull(root.get(ProyectoConceptoGasto_.fechaInicio)),
            cb.lessThanOrEqualTo(root.get(ProyectoConceptoGasto_.fechaInicio),
                fechaFin != null ? fechaFin : Instant.parse("2500-01-01T23:59:59Z"))),
        cb.or(cb.isNull(root.get(ProyectoConceptoGasto_.fechaFin)),
            cb.greaterThanOrEqualTo(root.get(ProyectoConceptoGasto_.fechaFin),
                fechaInicio != null ? fechaInicio : Instant.parse("1900-01-01T00:00:00Z"))));
  }

  /**
   * {@link ProyectoConceptoGasto} excluido en la lista.
   * 
   * @param excluidos lista de ids excluidos
   * @return specification para obtener los {@link ProyectoConceptoGasto} cuyo id
   *         no se encuentre entre los recibidos.
   */
  public static Specification<ProyectoConceptoGasto> notIn(List<Long> excluidos) {
    return (root, query, cb) -> root.get(ProyectoConceptoGasto_.id).in(excluidos).not();
  }

  /**
   * {@link ProyectoConceptoGasto} por Concepto de gasto.
   * 
   * @param conceptoGasto el {@link ConceptoGasto} de
   *                      {@link ProyectoConceptoGasto}
   * @return specification para obtener los {@link ProyectoConceptoGasto} con el
   *         {@link ConceptoGasto} indicado.
   */
  public static Specification<ProyectoConceptoGasto> byConceptoGasto(ConceptoGasto conceptoGasto) {
    return (root, query, cb) -> cb.equal(root.get(ProyectoConceptoGasto_.conceptoGasto).get(ConceptoGasto_.id),
        conceptoGasto.getId());
  }

  /**
   * {@link ProyectoConceptoGasto} id diferente de {@link ProyectoConceptoGasto}
   * con el indicado.
   * 
   * @param id identificador de la {@link ProyectoConceptoGasto}.
   * @return specification para comprobar si el id es diferentes del
   *         {@link ProyectoConceptoGasto} indicado.
   */
  public static Specification<ProyectoConceptoGasto> byIdNotEqual(Long id) {
    return (root, query, cb) -> {
      if (id == null) {
        return cb.isTrue(cb.literal(true)); // always true = no filtering
      }
      return cb.equal(root.get(ProyectoConceptoGasto_.id), id).not();
    };
  }

}