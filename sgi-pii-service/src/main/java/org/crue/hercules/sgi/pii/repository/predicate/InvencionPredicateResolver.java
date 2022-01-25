package org.crue.hercules.sgi.pii.repository.predicate;

import java.util.regex.Pattern;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.Invencion_;
import org.crue.hercules.sgi.pii.model.TipoProteccion;
import org.crue.hercules.sgi.pii.model.TipoProteccion_;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

public class InvencionPredicateResolver implements SgiRSQLPredicateResolver<Invencion> {
  private static final Pattern POSITIVE_NUMBER_PATTERN = Pattern.compile("^\\d+$");

  private enum Property {
    /* Tipo proteccion */
    TIPO_PROTECCION("tipoProteccion");

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

  private InvencionPredicateResolver() {
  }

  public static InvencionPredicateResolver getInstance() {
    return new InvencionPredicateResolver();
  }

  private Predicate buildByTipoProteccion(ComparisonNode node, Root<Invencion> root, CriteriaQuery<?> query,
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
    String argument = node.getArguments().get(0);
    if (!isPositiveNumber(argument)) {
      throw new IllegalArgumentException("Argument should be a positive integer for " + node.getSelector());
    }
    Long tipoProteccionId = Long.parseLong(argument);

    Join<Invencion, TipoProteccion> joinTipoProteccion = root.join(Invencion_.tipoProteccion);
    Join<TipoProteccion, TipoProteccion> joinSubtipoProteccion = joinTipoProteccion.join(TipoProteccion_.padre,
        JoinType.LEFT);

    return cb.and(
        cb.or(
            cb.equal(joinTipoProteccion.get(TipoProteccion_.id), tipoProteccionId),
            cb.equal(joinSubtipoProteccion.get(TipoProteccion_.id), tipoProteccionId)));
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
  public Predicate toPredicate(ComparisonNode node, Root<Invencion> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    switch (Property.fromCode(node.getSelector())) {
      case TIPO_PROTECCION:
        return buildByTipoProteccion(node, root, query, criteriaBuilder);
      default:
        return null;
    }
  }
}
