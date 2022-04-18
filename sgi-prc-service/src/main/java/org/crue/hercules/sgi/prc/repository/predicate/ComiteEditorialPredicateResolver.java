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

public class ComiteEditorialPredicateResolver extends ProduccionCientificaPredicateResolver {

  public enum Property {
    /* Fecha inicio */
    FECHA_INICIO("fechaInicio"),
    /* Fecha publicación desde */
    FECHA_INICIO_DESDE("fechaInicioDesde"),
    /* Fecha publicación hasta */
    FECHA_INICIO_HASTA("fechaInicioHasta"),
    /* Grupo investigación */
    GRUPO_INVESTIGACION("grupoInvestigacion"),
    /* Investigador */
    INVESTIGADOR("investigador"),
    /* ISSN */
    ISSN("issn"),
    /* Nombre */
    NOMBRE("nombre");

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

  private ComiteEditorialPredicateResolver() {
  }

  public static ComiteEditorialPredicateResolver getInstance() {
    return new ComiteEditorialPredicateResolver();
  }

  private Predicate buildByFechaInicioDesde(ComparisonNode node, Root<ProduccionCientifica> root,
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

    String fechaInicioDesde = node.getArguments().get(0);

    Subquery<String> queryFechaInicioDesde = buildSubqueryValorCampoProduccionCientifica(
        cb, query, root.get(ProduccionCientifica_.id), CodigoCVN.E060_030_030_140, operator, fechaInicioDesde);
    return cb.and(cb.exists(queryFechaInicioDesde));
  }

  private Predicate buildByFechaInicioHasta(ComparisonNode node, Root<ProduccionCientifica> root,
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

    String fechaInicioHasta = node.getArguments().get(0);

    Subquery<String> queryFechaInicioHasta = buildSubqueryValorCampoProduccionCientifica(
        cb, query, root.get(ProduccionCientifica_.id), CodigoCVN.E060_030_030_140, operator, fechaInicioHasta);
    return cb.and(cb.exists(queryFechaInicioHasta));
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

    String investigador = "%" + node.getArguments().get(0) + "%";

    Subquery<Long> queryInvestigador = getSubqueryAutor(cb, query, root.get(ProduccionCientifica_.id), investigador);
    return cb.and(cb.exists(queryInvestigador));
  }

  private Predicate buildByISSN(ComparisonNode node, Root<ProduccionCientifica> root, CriteriaQuery<?> query,
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

    String issn = "%" + node.getArguments().get(0) + "%";

    Subquery<String> queryISSN = buildSubqueryValorCampoProduccionCientifica(cb, query,
        root.get(ProduccionCientifica_.id), CodigoCVN.ISSN, operator, issn);
    return cb.and(cb.exists(queryISSN));
  }

  private Predicate buildByNombre(ComparisonNode node, Root<ProduccionCientifica> root,
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

    String nombre = "%" + node.getArguments().get(0) + "%";

    Subquery<String> queryNombre = buildSubqueryValorCampoProduccionCientifica(cb,
        query, root.get(ProduccionCientifica_.id), CodigoCVN.E060_030_030_010, operator, nombre);
    return cb.and(cb.exists(queryNombre));
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
        case FECHA_INICIO_DESDE:
          return buildByFechaInicioDesde(node, root, query, criteriaBuilder);
        case FECHA_INICIO_HASTA:
          return buildByFechaInicioHasta(node, root, query, criteriaBuilder);
        case GRUPO_INVESTIGACION:
          return buildByGrupoInvestigacion(node, root, query, criteriaBuilder);
        case INVESTIGADOR:
          return buildByInvestigador(node, root, query, criteriaBuilder);
        case ISSN:
          return buildByISSN(node, root, query, criteriaBuilder);
        case NOMBRE:
          return buildByNombre(node, root, query, criteriaBuilder);
        default:
          return null;
      }
    } else {
      return null;
    }
  }
}
