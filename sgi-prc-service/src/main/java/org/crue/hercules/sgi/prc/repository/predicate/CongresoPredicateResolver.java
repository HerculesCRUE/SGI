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

public class CongresoPredicateResolver extends ProduccionCientificaPredicateResolver {

  public enum Property {
    /* Fecha celebración */
    FECHA_CELEBRACION("fechaCelebracion"),
    /* Fecha celebración desde */
    FECHA_CELEBRACION_DESDE("fechaCelebracionDesde"),
    /* Fecha celebración hasta */
    FECHA_CELEBRACION_HASTA("fechaCelebracionHasta"),
    /* Grupo investigación */
    GRUPO_INVESTIGACION("grupoInvestigacion"),
    /* Investigador */
    INVESTIGADOR("investigador"),
    /* Nombre del congreso */
    NOMBRE_CONGRESO("nombreCongreso"),
    /* Tipo de evento */
    TIPO_EVENTO("tipoEvento"),
    /* Título de trabajo */
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

  private CongresoPredicateResolver() {
  }

  public static CongresoPredicateResolver getInstance() {
    return new CongresoPredicateResolver();
  }

  private Predicate buildByFechaCelebracionDesde(ComparisonNode node, Root<ProduccionCientifica> root,
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

    String fechaCelebracionDesde = node.getArguments().get(0);

    Subquery<String> queryFechaCelebracionDesde = buildSubqueryValorCampoProduccionCientifica(
        cb, query, root.get(ProduccionCientifica_.id), CodigoCVN.E060_010_020_190, operator, fechaCelebracionDesde);
    return cb.and(cb.exists(queryFechaCelebracionDesde));
  }

  private Predicate buildByFechaCelebracionHasta(ComparisonNode node, Root<ProduccionCientifica> root,
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

    String fechaCelebracionHasta = node.getArguments().get(0);

    Subquery<String> queryFechaCelebracionHasta = buildSubqueryValorCampoProduccionCientifica(
        cb, query, root.get(ProduccionCientifica_.id), CodigoCVN.E060_010_020_190, operator, fechaCelebracionHasta);
    return cb.and(cb.exists(queryFechaCelebracionHasta));
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

  private Predicate buildByNombreCongreso(ComparisonNode node, Root<ProduccionCientifica> root, CriteriaQuery<?> query,
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

    String nombreCongreso = "%" + node.getArguments().get(0).toLowerCase() + "%";

    Subquery<String> queryNombreCongreso = buildSubqueryValorCampoProduccionCientifica(cb, query,
        root.get(ProduccionCientifica_.id), CodigoCVN.E060_010_020_100, operator, nombreCongreso);
    return cb.and(cb.exists(queryNombreCongreso));
  }

  private Predicate buildByTipoEvento(ComparisonNode node, Root<ProduccionCientifica> root,
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

    String tipoEvento = "%" + node.getArguments().get(0).toLowerCase() + "%";

    Subquery<String> queryTipoEvento = buildSubqueryValorCampoProduccionCientifica(cb,
        query, root.get(ProduccionCientifica_.id), CodigoCVN.E060_010_020_010, operator, tipoEvento);
    return cb.and(cb.exists(queryTipoEvento));
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

    Subquery<String> queryTituloTrabajo = buildSubqueryValorCampoProduccionCientifica(cb,
        query, root.get(ProduccionCientifica_.id), CodigoCVN.E060_010_020_030, operator, tituloTrabajo);
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
        case FECHA_CELEBRACION_DESDE:
          return buildByFechaCelebracionDesde(node, root, query, criteriaBuilder);
        case FECHA_CELEBRACION_HASTA:
          return buildByFechaCelebracionHasta(node, root, query, criteriaBuilder);
        case GRUPO_INVESTIGACION:
          return buildByGrupoInvestigacion(node, root, query, criteriaBuilder);
        case INVESTIGADOR:
          return buildByInvestigador(node, root, query, criteriaBuilder);
        case NOMBRE_CONGRESO:
          return buildByNombreCongreso(node, root, query, criteriaBuilder);
        case TIPO_EVENTO:
          return buildByTipoEvento(node, root, query, criteriaBuilder);
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
