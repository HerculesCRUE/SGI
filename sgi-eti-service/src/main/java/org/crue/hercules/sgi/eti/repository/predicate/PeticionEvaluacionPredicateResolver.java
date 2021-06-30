package org.crue.hercules.sgi.eti.repository.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion_;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import io.github.perplexhub.rsql.RSQLOperators;

public class PeticionEvaluacionPredicateResolver implements SgiRSQLPredicateResolver<PeticionEvaluacion> {
  private enum Property {
    TITULO("peticionEvaluacion.titulo"), COMITE("comite.id"), PERSONA("peticionEvaluacion.personaRef"),
    ESTADO("estadoActual.id"), CODIGO("peticionEvaluacion.codigo");

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

  private static Predicate buildFilterTitulo(ComparisonNode node, Root<PeticionEvaluacion> root, CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!operator.equals(RSQLOperators.IGNORE_CASE_LIKE) && !operator.equals(RSQLOperators.LIKE)) {
      // Unsupported Operator
      throw new IllegalArgumentException("Unsupported operator: " + operator + " for " + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException("Bad number of arguments for " + node.getSelector());
    }
    String tituloFilter = node.getArguments().get(0);
    Predicate titulo = cb.like(root.get(PeticionEvaluacion_.titulo), tituloFilter);
    return cb.and(titulo);
  }

  private static Predicate buildFilterPersona(ComparisonNode node, Root<PeticionEvaluacion> root,
      CriteriaQuery<?> query, CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!operator.equals(RSQLOperators.EQUAL)) {
      // Unsupported Operator
      throw new IllegalArgumentException("Unsupported operator: " + operator + " for " + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException("Bad number of arguments for " + node.getSelector());
    }
    String personaFilter = node.getArguments().get(0);
    Predicate persona = cb.equal(root.get(PeticionEvaluacion_.personaRef), personaFilter);
    return cb.and(persona);
  }

  private static Predicate buildFilterCodigo(ComparisonNode node, Root<PeticionEvaluacion> root, CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!operator.equals(RSQLOperators.IGNORE_CASE_LIKE)) {
      // Unsupported Operator
      throw new IllegalArgumentException("Unsupported operator: " + operator + " for " + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException("Bad number of arguments for " + node.getSelector());
    }
    String codigoFilter = node.getArguments().get(0);
    Predicate codigo = cb.like(root.get(PeticionEvaluacion_.codigo), codigoFilter);
    return cb.and(codigo);
  }

  private static Predicate buildNonFilter(ComparisonNode node, Root<PeticionEvaluacion> root, CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    // esta comprobación se hace para que la consulta no devuelva resultado
    Predicate titulo = cb.notEqual(root.get(PeticionEvaluacion_.titulo), root.get(PeticionEvaluacion_.titulo));
    return cb.and(titulo);
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<PeticionEvaluacion> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    switch (Property.fromCode(node.getSelector())) {
    case TITULO:
      return buildFilterTitulo(node, root, query, criteriaBuilder);
    case PERSONA:
      return buildFilterPersona(node, root, query, criteriaBuilder);
    case CODIGO:
      return buildFilterCodigo(node, root, query, criteriaBuilder);
    case ESTADO:
      return buildNonFilter(node, root, query, criteriaBuilder);
    case COMITE:
      return buildNonFilter(node, root, query, criteriaBuilder);
    default:
      return null;
    }
  }
}
