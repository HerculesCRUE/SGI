package org.crue.hercules.sgi.rel.repository.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;
import org.crue.hercules.sgi.rel.model.Relacion;
import org.crue.hercules.sgi.rel.model.Relacion_;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import io.github.perplexhub.rsql.RSQLOperators;

public class RelacionPredicateResolver implements SgiRSQLPredicateResolver<Relacion> {

  private enum Property {
    /* Proyecto ref */
    PROYECTO_REF("proyectoRef");

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

  private RelacionPredicateResolver() {
  }

  public static RelacionPredicateResolver getInstance() {
    return new RelacionPredicateResolver();
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  private Predicate buildByProyectoRef(ComparisonNode node, Root<Relacion> root, CriteriaQuery<?> query,
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

    String proyectoRef = node.getArguments().get(0);

    return cb.or(
        cb.and(cb.equal(root.get(Relacion_.TIPO_ENTIDAD_DESTINO), Relacion.TipoEntidad.PROYECTO),
            cb.equal(root.get(Relacion_.ENTIDAD_DESTINO_REF), proyectoRef)),
        cb.and(cb.equal(root.get(Relacion_.TIPO_ENTIDAD_ORIGEN), Relacion.TipoEntidad.PROYECTO),
            cb.equal(root.get(Relacion_.ENTIDAD_ORIGEN_REF), proyectoRef)));
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<Relacion> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    switch (Property.fromCode(node.getSelector())) {
      case PROYECTO_REF:
        return buildByProyectoRef(node, root, query, criteriaBuilder);
      default:
        return null;
    }
  }

}
