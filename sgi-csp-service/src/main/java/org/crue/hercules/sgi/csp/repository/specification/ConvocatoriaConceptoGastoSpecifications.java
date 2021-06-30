package org.crue.hercules.sgi.csp.repository.specification;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConceptoGasto_;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.springframework.data.jpa.domain.Specification;

public class ConvocatoriaConceptoGastoSpecifications {

  /**
   * {@link ConvocatoriaConceptoGasto} convocatoria activas.
   * 
   * @return specification para obtener las {@link Convocatoria} activas.
   */
  public static Specification<ConvocatoriaConceptoGasto> byConvocatoriaActiva() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaConceptoGasto_.convocatoria).get(Convocatoria_.activo), Boolean.TRUE);
    };
  }

  /**
   * {@link ConvocatoriaConceptoGasto} ConvocatoriaConceptoGasto permitido.
   * 
   * @param permitido boolean indica si el gasto es permitido o no
   * @return specification para obtener los {@link ConvocatoriaConceptoGasto}
   *         permitidos o no.
   */
  public static Specification<ConvocatoriaConceptoGasto> byPermitido(Boolean permitido) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaConceptoGasto_.permitido), permitido);
    };
  }

  /**
   * Se obtienen los {@link ConvocatoriaConceptoGasto} por convocatoria
   * 
   * @param idConvocatoria identificador de la {@link Convocatoria}
   * @return specification para obtener los {@link ConvocatoriaConceptoGasto} por
   *         convocatoria
   */
  public static Specification<ConvocatoriaConceptoGasto> byConvocatoria(Long idConvocatoria) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaConceptoGasto_.convocatoria).get(Convocatoria_.id), idConvocatoria);
    };
  }

  /**
   * Se obtienen los {@link ConvocatoriaConceptoGasto} con valores para los meses
   * de inicio y fin
   * 
   * @return specification para obtener los {@link ConvocatoriaConceptoGasto} con
   *         valor en los meses
   */
  public static Specification<ConvocatoriaConceptoGasto> withMeses() {
    return (root, query, cb) -> {

      return cb.and(cb.isNotNull(root.get(ConvocatoriaConceptoGasto_.mesInicial)),
          cb.isNotNull(root.get(ConvocatoriaConceptoGasto_.mesFinal)));
    };
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
  public static Specification<ConvocatoriaConceptoGasto> byRangoMesesSolapados(Integer mesInicial, Integer mesFinal) {
    return (root, query, cb) -> {
      return cb
          .and(
              cb.or(cb.isNull(root.get(ConvocatoriaConceptoGasto_.mesInicial)),
                  cb.lessThanOrEqualTo(root.get(ConvocatoriaConceptoGasto_.mesInicial),
                      mesFinal != null ? mesFinal : 12)),
              cb.or(cb.isNull(root.get(ConvocatoriaConceptoGasto_.mesFinal)), cb.greaterThanOrEqualTo(
                  root.get(ConvocatoriaConceptoGasto_.mesFinal), mesInicial != null ? mesInicial : 1)));
    };
  }

  /**
   * {@link ConvocatoriaConceptoGasto} excluido en la lista.
   * 
   * @param excluidos lista de ids excluidos
   * @return specification para obtener los {@link ConvocatoriaConceptoGasto} cuyo
   *         id no se encuentre entre los recibidos.
   */
  public static Specification<ConvocatoriaConceptoGasto> notIn(List<Long> excluidos) {
    return (root, query, cb) -> {
      return root.get(ConvocatoriaConceptoGasto_.id).in(excluidos).not();
    };
  }

  /**
   * {@link ConvocatoriaConceptoGasto} por Concepto de gasto.
   * 
   * @param conceptoGasto el {@link ConceptoGasto} de
   *                      {@link ConvocatoriaConceptoGasto}
   * @return specification para obtener los {@link ConvocatoriaConceptoGasto} con
   *         el {@link ConceptoGasto} indicado.
   */
  public static Specification<ConvocatoriaConceptoGasto> byConceptoGasto(ConceptoGasto conceptoGasto) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaConceptoGasto_.conceptoGasto).get(ConceptoGasto_.id), conceptoGasto.getId());
    };
  }

  /**
   * {@link ConvocatoriaConceptoGasto} id diferente de
   * {@link ConvocatoriaConceptoGasto} con el indicado.
   * 
   * @param id identificador de la {@link ConvocatoriaConceptoGasto}.
   * @return specification para comprobar si el id es diferentes del
   *         {@link ConvocatoriaConceptoGasto} indicado.
   */
  public static Specification<ConvocatoriaConceptoGasto> byIdNotEqual(Long id) {
    return (root, query, cb) -> {
      if (id == null) {
        return cb.isTrue(cb.literal(true)); // always true = no filtering
      }
      return cb.equal(root.get(ConvocatoriaConceptoGasto_.id), id).not();
    };
  }
}