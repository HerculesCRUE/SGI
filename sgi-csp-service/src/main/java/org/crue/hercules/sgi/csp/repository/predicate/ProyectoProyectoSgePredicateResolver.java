package org.crue.hercules.sgi.csp.repository.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo_;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.crue.hercules.sgi.csp.model.RolProyecto_;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import io.github.perplexhub.rsql.RSQLOperators;

public class ProyectoProyectoSgePredicateResolver implements SgiRSQLPredicateResolver<ProyectoProyectoSge> {
  private enum Property {
    /* Responsable proyecto */
    RESPONSABLE_PROYECTO("responsableProyecto");

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

  private ProyectoProyectoSgePredicateResolver() {

  }

  public static ProyectoProyectoSgePredicateResolver getInstance() {
    return new ProyectoProyectoSgePredicateResolver();
  }

  private Predicate buildByResponsableEquipo(ComparisonNode node, Root<ProyectoProyectoSge> root,
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

    String personaRef = node.getArguments().get(0);
    Join<ProyectoProyectoSge, Proyecto> joinProyecto = root.join(ProyectoProyectoSge_.proyecto, JoinType.INNER);
    ListJoin<Proyecto, ProyectoEquipo> joinEquipos = joinProyecto.join(Proyecto_.equipo, JoinType.LEFT);

    return cb.and(cb.equal(joinEquipos.get(ProyectoEquipo_.personaRef), personaRef),
        cb.equal(joinEquipos.get(ProyectoEquipo_.rolProyecto).get(RolProyecto_.rolPrincipal), true));
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<ProyectoProyectoSge> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    switch (Property.fromCode(node.getSelector())) {
      case RESPONSABLE_PROYECTO:
        return buildByResponsableEquipo(node, root, query, criteriaBuilder);
      default:
        return null;
    }
  }
}
