package org.crue.hercules.sgi.csp.repository.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.ProyectoFacturacion;
import org.crue.hercules.sgi.csp.model.ProyectoFacturacion_;
import org.crue.hercules.sgi.csp.util.PredicateResolverUtil;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import io.github.perplexhub.rsql.RSQLOperators;

public class ProyectoFacturacionPredicateResolver implements SgiRSQLPredicateResolver<ProyectoFacturacion> {
  private enum Property {

    PROYECTO_ID_SGI("proyectoIdSGI");

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
  };

  private ProyectoFacturacionPredicateResolver() {

  }

  public static ProyectoFacturacionPredicateResolver getInstance() {
    return new ProyectoFacturacionPredicateResolver();
  }

  private Predicate buildByProyectoIdSGI(ComparisonNode node, Root<ProyectoFacturacion> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    Long proyectoId = Long.valueOf(node.getArguments().get(0));

    return cb.equal(root.get(ProyectoFacturacion_.proyectoId), proyectoId);
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<ProyectoFacturacion> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    Property property = Property.fromCode(node.getSelector());
    if (property == null) {
      return null;
    }

    switch (property) {
      case PROYECTO_ID_SGI:
        return buildByProyectoIdSGI(node, root, criteriaBuilder);
      default:
        return null;
    }
  }
}
