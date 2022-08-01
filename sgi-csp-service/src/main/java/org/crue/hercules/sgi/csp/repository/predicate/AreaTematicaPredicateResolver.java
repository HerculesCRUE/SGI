package org.crue.hercules.sgi.csp.repository.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.crue.hercules.sgi.csp.model.AreaTematica_;
import org.crue.hercules.sgi.csp.util.PredicateResolverUtil;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import io.github.perplexhub.rsql.RSQLOperators;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AreaTematicaPredicateResolver implements SgiRSQLPredicateResolver<AreaTematica> {

  public enum Property {
    /* Padre */
    PADRE_ID("padreId");

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

  public static AreaTematicaPredicateResolver getInstance() {
    return new AreaTematicaPredicateResolver();
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<AreaTematica> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    Property property = Property.fromCode(node.getSelector());
    if (property == null) {
      return null;
    }

    if (property.equals(Property.PADRE_ID)) {
      return buildByPadreId(node, root, criteriaBuilder);
    }

    return null;
  }

  private Predicate buildByPadreId(ComparisonNode node, Root<AreaTematica> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL, RSQLOperators.IS_NULL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    if (node.getOperator().equals(RSQLOperators.EQUAL)) {
      Long padreId = Long.parseLong(node.getArguments().get(0));
      return cb.equal(root.get(AreaTematica_.padre).get(AreaTematica_.id), padreId);
    } else {
      return cb.isNull(root.get(AreaTematica_.padre));
    }
  }

}
