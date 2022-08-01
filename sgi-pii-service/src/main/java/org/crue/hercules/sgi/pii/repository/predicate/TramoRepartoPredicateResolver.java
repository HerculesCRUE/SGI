package org.crue.hercules.sgi.pii.repository.predicate;

import java.util.regex.Pattern;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;
import org.crue.hercules.sgi.pii.model.TramoReparto;
import org.crue.hercules.sgi.pii.model.TramoReparto.Tipo;
import org.crue.hercules.sgi.pii.model.TramoReparto_;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

public class TramoRepartoPredicateResolver implements SgiRSQLPredicateResolver<TramoReparto> {
  private static final Pattern POSITIVE_NUMBER_PATTERN = Pattern.compile("^\\d+$");

  private enum Property {
    /* Máximo valor Hasta */
    MAX_HASTA("maxHasta"),
    /* Tramo que contiene al valor recibido entre sus campos Desde y Hasta */
    BETWEEN_DESDE_HASTA("betweenDesdeHasta");

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

  private TramoRepartoPredicateResolver() {
  }

  public static TramoRepartoPredicateResolver getInstance() {
    return new TramoRepartoPredicateResolver();
  }

  private Predicate buildByMaxHasta(ComparisonNode node, Root<TramoReparto> root, CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!operator.equals(RSQLOperators.EQUAL)) {
      // Unsupported Operator
      throw new IllegalArgumentException("Unsupported operator: " + operator + " for " + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException("Bad number of arguments for " + node.getSelector());
    }
    String selectedId = node.getArguments().get(0);
    Subquery<Integer> subquery = query.subquery(Integer.class);
    Root<TramoReparto> rootSubquery = subquery.from(TramoReparto.class);
    subquery.select(cb.max(rootSubquery.get(TramoReparto_.hasta)));
    // Si el argumento es un id (entero positivo) se excluye dicho id para obtener
    // el hasta máximo
    if (isPositiveNumber(selectedId)) {
      subquery.where(cb.notEqual(rootSubquery.get(TramoReparto_.id), Long.parseLong(selectedId)));
    }

    return cb.equal(root.get(TramoReparto_.hasta), subquery);
  }

  private Predicate buildByBetweenDesdeHasta(ComparisonNode node, Root<TramoReparto> root, CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!operator.equals(RSQLOperators.EQUAL)) {
      // Unsupported Operator
      throw new IllegalArgumentException("Unsupported operator: " + operator + " for " + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException("Bad number of arguments for " + node.getSelector());
    }
    String argument = node.getArguments().get(0);
    if (!isPositiveNumber(argument)) {
      throw new IllegalArgumentException("Argument should be a positive integer for " + node.getSelector());
    }

    Integer value = Integer.parseInt(argument);

    return cb.or(
        cb.and(cb.lessThanOrEqualTo(root.get(TramoReparto_.desde), value),
            cb.greaterThanOrEqualTo(root.get(TramoReparto_.hasta), value)),
        cb.and(cb.equal(root.get(TramoReparto_.tipo), Tipo.FINAL),
            cb.lessThanOrEqualTo(root.get(TramoReparto_.desde), value)));
  }

  public boolean isPositiveNumber(String value) {
    if (value == null) {
      return false;
    }
    return POSITIVE_NUMBER_PATTERN.matcher(value).matches();
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<TramoReparto> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {

    final Property property = Property.fromCode(node.getSelector());

    if (property == null) {
      return null;
    }

    switch (property) {
      case MAX_HASTA:
        return buildByMaxHasta(node, root, query, criteriaBuilder);
      case BETWEEN_DESDE_HASTA:
        return buildByBetweenDesdeHasta(node, root, criteriaBuilder);
      default:
        return null;
    }
  }

}
