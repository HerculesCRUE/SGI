package org.crue.hercules.sgi.prc.repository.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica_;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import io.github.perplexhub.rsql.RSQLOperators;

public class DireccionTesisPredicateResolver extends ProduccionCientificaPredicateResolver {

  public enum Property {
    /* Fecha defensa */
    FECHA_DEFENSA("fechaDefensa"),
    /* Fecha defensa desde */
    FECHA_DEFENSA_DESDE("fechaDefensaDesde"),
    /* Fecha defensa hasta */
    FECHA_DEFENSA_HASTA("fechaDefensaHasta"),
    /* Grupo investigación */
    GRUPO_INVESTIGACION("grupoInvestigacion"),
    /* Investigador */
    INVESTIGADOR("investigador"),
    /* Tipo de proyecto */
    TIPO_PROYECTO("tipoProyecto"),
    /* Título del trabajo */
    TITULO_TRABAJO("tituloTrabajo");

    private String code;

    private Property(String code) {
      this.code = code;
    }

    public String getCode() {
      return code;
    }

    public static Property fromCode(String code) {
      for (Property property : Property.values()) {
        if (property.code.equals(code)) {
          return property;
        }
      }
      return null;
    }
  }

  private DireccionTesisPredicateResolver() {
  }

  public static DireccionTesisPredicateResolver getInstance() {
    return new DireccionTesisPredicateResolver();
  }

  private Predicate buildByFechaDefensaDesde(ComparisonNode node, Root<ProduccionCientifica> root,
      CriteriaQuery<?> query, CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!(operator.equals(RSQLOperators.GREATER_THAN_OR_EQUAL))) {

      // Unsupported Operator
      throw new IllegalArgumentException(UNSUPPORTED_OPERATOR + operator + FOR + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException(BAD_NUMBER_OF_ARGUMENTS_FOR + node.getSelector());
    }

    String fechaDefensaDesde = node.getArguments().get(0);

    Subquery<String> queryFechaDefensaDesde = buildSubqueryValorCampoProduccionCientifica(
        cb, query, root.get(ProduccionCientifica_.id), CodigoCVN.E030_040_000_140, operator, fechaDefensaDesde);
    return cb.and(cb.exists(queryFechaDefensaDesde));
  }

  private Predicate buildByFechaDefensaHasta(ComparisonNode node, Root<ProduccionCientifica> root,
      CriteriaQuery<?> query, CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!(operator.equals(RSQLOperators.LESS_THAN_OR_EQUAL))) {
      // Unsupported Operator
      throw new IllegalArgumentException(UNSUPPORTED_OPERATOR + operator + FOR + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException(BAD_NUMBER_OF_ARGUMENTS_FOR + node.getSelector());
    }

    String fechaDefensaHasta = node.getArguments().get(0);

    Subquery<String> queryFechaDefensaHasta = buildSubqueryValorCampoProduccionCientifica(
        cb, query, root.get(ProduccionCientifica_.id), CodigoCVN.E030_040_000_140, operator, fechaDefensaHasta);
    return cb.and(cb.exists(queryFechaDefensaHasta));
  }

  private Predicate buildByGrupoInvestigacion(ComparisonNode node, Root<ProduccionCientifica> root,
      CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!operator.equals(RSQLOperators.EQUAL)) {
      // Unsupported Operator
      throw new IllegalArgumentException(UNSUPPORTED_OPERATOR + operator + FOR + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException(BAD_NUMBER_OF_ARGUMENTS_FOR + node.getSelector());
    }

    String grupoRef = node.getArguments().get(0);

    Subquery<Long> queryGrupoInvestigador = getSubqueryAutorGrupo(cb, query, root.get(ProduccionCientifica_.id),
        grupoRef);
    return cb.and(cb.exists(queryGrupoInvestigador));
  }

  private Predicate buildByInvestigador(ComparisonNode node, Root<ProduccionCientifica> root,
      CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!(operator.equals(RSQLOperators.IGNORE_CASE_LIKE))) {
      // Unsupported Operator
      throw new IllegalArgumentException(UNSUPPORTED_OPERATOR + operator + FOR + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException(BAD_NUMBER_OF_ARGUMENTS_FOR + node.getSelector());
    }

    String investigador = "%" + node.getArguments().get(0).toLowerCase() + "%";

    Subquery<Long> queryInvestigador = getSubqueryAutor(cb, query, root.get(ProduccionCientifica_.id), investigador);
    return cb.and(cb.exists(queryInvestigador));
  }

  private Predicate buildByTipoProyecto(ComparisonNode node, Root<ProduccionCientifica> root,
      CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!(operator.equals(RSQLOperators.IGNORE_CASE_LIKE))) {
      // Unsupported Operator
      throw new IllegalArgumentException(UNSUPPORTED_OPERATOR + operator + FOR + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException(BAD_NUMBER_OF_ARGUMENTS_FOR + node.getSelector());
    }

    String tipoProyecto = "%" + node.getArguments().get(0).toLowerCase() + "%";

    Subquery<String> queryTipoProyecto = buildSubqueryValorCampoProduccionCientifica(cb,
        query, root.get(ProduccionCientifica_.id), CodigoCVN.E030_040_000_010, operator, tipoProyecto);
    return cb.and(cb.exists(queryTipoProyecto));
  }

  private Predicate buildByTituloTrabajo(ComparisonNode node, Root<ProduccionCientifica> root,
      CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!(operator.equals(RSQLOperators.IGNORE_CASE_LIKE))) {
      // Unsupported Operator
      throw new IllegalArgumentException(UNSUPPORTED_OPERATOR + operator + FOR + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException(BAD_NUMBER_OF_ARGUMENTS_FOR + node.getSelector());
    }

    String tituloTrabajo = "%" + node.getArguments().get(0).toLowerCase() + "%";

    Subquery<String> queryTituloTrabajo = buildSubqueryValorCampoProduccionCientifica(cb, query,
        root.get(ProduccionCientifica_.id), CodigoCVN.E030_040_000_030, operator, tituloTrabajo);
    return cb.and(cb.exists(queryTituloTrabajo));
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<ProduccionCientifica> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    Property property = Property.fromCode(node.getSelector());
    if (null != property) {
      switch (property) {
        case FECHA_DEFENSA_DESDE:
          return buildByFechaDefensaDesde(node, root, query, criteriaBuilder);
        case FECHA_DEFENSA_HASTA:
          return buildByFechaDefensaHasta(node, root, query, criteriaBuilder);
        case GRUPO_INVESTIGACION:
          return buildByGrupoInvestigacion(node, root, query, criteriaBuilder);
        case INVESTIGADOR:
          return buildByInvestigador(node, root, query, criteriaBuilder);
        case TIPO_PROYECTO:
          return buildByTipoProyecto(node, root, query, criteriaBuilder);
        case TITULO_TRABAJO:
          return buildByTituloTrabajo(node, root, query, criteriaBuilder);
        default:
          return null;
      }
    } else {
      return null;
    }
  }
}
