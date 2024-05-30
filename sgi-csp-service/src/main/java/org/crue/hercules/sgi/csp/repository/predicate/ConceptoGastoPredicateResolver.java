package org.crue.hercules.sgi.csp.repository.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.repository.specification.ConceptoGastoSpecifications;
import org.crue.hercules.sgi.csp.util.PredicateResolverUtil;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import io.github.perplexhub.rsql.RSQLOperators;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConceptoGastoPredicateResolver implements SgiRSQLPredicateResolver<ConceptoGasto> {

  public static final String SPLIT_DELIMITER = ",";

  public enum Property {
    /* Proyecto */
    PROYECTO("proyectoId");

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

  public static ConceptoGastoPredicateResolver getInstance() {
    return new ConceptoGastoPredicateResolver();
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<ConceptoGasto> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    Property property = Property.fromCode(node.getSelector());
    if (property == null) {
      return null;
    }

    switch (property) {
      case PROYECTO:
        return buildByProyectoId(node, root, query, criteriaBuilder);
      default:
        return null;
    }
  }

  private Predicate buildByProyectoId(ComparisonNode node, Root<ConceptoGasto> root, CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    Long proyectoId = Long.parseLong(node.getArguments().get(0));
    return ConceptoGastoSpecifications.byPermitidosInProyecto(proyectoId).toPredicate(root, query, cb);
  }

}
