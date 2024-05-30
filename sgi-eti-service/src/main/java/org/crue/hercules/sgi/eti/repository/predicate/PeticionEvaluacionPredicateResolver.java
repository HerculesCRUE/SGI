package org.crue.hercules.sgi.eti.repository.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion_;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import io.github.perplexhub.rsql.RSQLOperators;

public class PeticionEvaluacionPredicateResolver implements SgiRSQLPredicateResolver<PeticionEvaluacion> {
  private static final String BAD_NUMBER_OF_ARGUMENTS_FOR = "Bad number of arguments for ";
  private static final String FOR = " for ";
  private static final String UNSUPPORTED_OPERATOR = "Unsupported operator: ";
  private static final String LIKE_WILDCARD_PERCENT = "%";

  private enum Property {
    TITULO("peticionEvaluacion.titulo"),
    COMITE("comite.id"),
    PERSONA("peticionEvaluacion.personaRef"),
    ESTADO("estadoActual.id"),
    CODIGO("peticionEvaluacion.codigo"),
    NUM_REFERENCIA("numReferencia");

    private String code;

    private Property(String code) {
      this.code = code;
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

  private static PeticionEvaluacionPredicateResolver instance;

  private PeticionEvaluacionPredicateResolver() {
    // Do nothing. Hide external instanciation
  }

  public static PeticionEvaluacionPredicateResolver getInstance() {
    if (instance == null) {
      instance = new PeticionEvaluacionPredicateResolver();
    }
    return instance;
  }

  private static Predicate buildFilterTitulo(ComparisonNode node, Root<PeticionEvaluacion> root, CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!operator.equals(RSQLOperators.IGNORE_CASE_LIKE) && !operator.equals(RSQLOperators.LIKE)) {
      // Unsupported Operator
      throw new IllegalArgumentException(UNSUPPORTED_OPERATOR + operator + FOR + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException(BAD_NUMBER_OF_ARGUMENTS_FOR + node.getSelector());
    }
    String tituloFilter = node.getArguments().get(0);
    Predicate titulo = cb.like(root.get(PeticionEvaluacion_.titulo), tituloFilter);
    return cb.and(titulo);
  }

  private static Predicate buildFilterPersona(ComparisonNode node, Root<PeticionEvaluacion> root,
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
    String personaFilter = node.getArguments().get(0);
    Predicate persona = cb.equal(root.get(PeticionEvaluacion_.personaRef), personaFilter);
    return cb.and(persona);
  }

  private static Predicate buildFilterCodigo(ComparisonNode node, Root<PeticionEvaluacion> root,
      CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!operator.equals(RSQLOperators.IGNORE_CASE_LIKE)) {
      // Unsupported Operator
      throw new IllegalArgumentException(UNSUPPORTED_OPERATOR + operator + FOR + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException(BAD_NUMBER_OF_ARGUMENTS_FOR + node.getSelector());
    }
    String codigoFilter = node.getArguments().get(0);
    Predicate codigo = cb.like(root.get(PeticionEvaluacion_.codigo), codigoFilter);
    return cb.and(codigo);
  }

  private static Predicate buildNonFilter(Root<PeticionEvaluacion> root,
      CriteriaBuilder cb) {
    // esta comprobación se hace para que la consulta no devuelva resultado
    Predicate titulo = cb.notEqual(root.get(PeticionEvaluacion_.titulo), root.get(PeticionEvaluacion_.titulo));
    return cb.and(titulo);
  }

  private static Predicate buildFilterNumReferencia(ComparisonNode node, Root<PeticionEvaluacion> root,
      CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!operator.equals(RSQLOperators.IGNORE_CASE_LIKE)) {
      // Unsupported Operator
      throw new IllegalArgumentException(UNSUPPORTED_OPERATOR + operator + FOR + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException(BAD_NUMBER_OF_ARGUMENTS_FOR + node.getSelector());
    }
    String numReferencia = node.getArguments().get(0);

    Subquery<Long> subquery = query.subquery(Long.class);
    Root<Memoria> rootSubquery = subquery.from(Memoria.class);

    subquery.select(rootSubquery.get(Memoria_.peticionEvaluacionId)).where(
        cb.like(rootSubquery.get(Memoria_.numReferencia),
            LIKE_WILDCARD_PERCENT + numReferencia + LIKE_WILDCARD_PERCENT));

    return root.get(PeticionEvaluacion_.id).in(subquery);
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<PeticionEvaluacion> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {

    Property property = Property.fromCode(node.getSelector());

    if (property == null) {
      return null;
    }

    switch (property) {
      case TITULO:
        return buildFilterTitulo(node, root, criteriaBuilder);
      case PERSONA:
        return buildFilterPersona(node, root, criteriaBuilder);
      case CODIGO:
        return buildFilterCodigo(node, root, criteriaBuilder);
      case ESTADO:
        return buildNonFilter(root, criteriaBuilder);
      case COMITE:
        return buildNonFilter(root, criteriaBuilder);
      case NUM_REFERENCIA:
        return buildFilterNumReferencia(node, root, query, criteriaBuilder);
      default:
        return null;
    }
  }
}
