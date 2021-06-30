package org.crue.hercules.sgi.csp.repository.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import io.github.perplexhub.rsql.RSQLOperators;

public class SolicitudPredicateResolver implements SgiRSQLPredicateResolver<Solicitud> {
  private enum Property {
    REFERENCIA_CONVOCATORIA("referenciaConvocatoria");

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

  private static SolicitudPredicateResolver instance;

  private SolicitudPredicateResolver() {
    // Do nothing. Hide external instanciation
  }

  public static SolicitudPredicateResolver getInstance() {
    if (instance == null) {
      instance = new SolicitudPredicateResolver();
    }
    return instance;
  }

  private static Predicate buildByReferenciaConvocatoria(ComparisonNode node, Root<Solicitud> root,
      CriteriaQuery<?> query, CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!operator.equals(RSQLOperators.IGNORE_CASE_LIKE)) {
      // Unsupported Operator
      throw new IllegalArgumentException("Unsupported operator: " + operator + " for " + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException("Bad number of arguments for " + node.getSelector());
    }

    String referenciaConvocatoria = "%" + node.getArguments().get(0) + "%";

    Join<Solicitud, Convocatoria> joinConvocatoria = root.join(Solicitud_.convocatoria, JoinType.LEFT);

    return cb.or(
        cb.and(cb.isNotNull(joinConvocatoria),
            cb.like(joinConvocatoria.get(Convocatoria_.codigo), referenciaConvocatoria)),
        cb.and(cb.and(cb.isNull(joinConvocatoria),
            cb.like(root.get(Solicitud_.convocatoriaExterna), referenciaConvocatoria))));
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<Solicitud> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    switch (Property.fromCode(node.getSelector())) {
      case REFERENCIA_CONVOCATORIA:
        return buildByReferenciaConvocatoria(node, root, query, criteriaBuilder);
      default:
        return null;
    }
  }
}
