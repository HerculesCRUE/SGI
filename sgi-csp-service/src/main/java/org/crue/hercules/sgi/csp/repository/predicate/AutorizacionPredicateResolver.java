package org.crue.hercules.sgi.csp.repository.predicate;

import java.time.Instant;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.Autorizacion_;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion.Estado;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion_;
import org.crue.hercules.sgi.csp.util.PredicateResolverUtil;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import io.github.perplexhub.rsql.RSQLOperators;

public class AutorizacionPredicateResolver implements SgiRSQLPredicateResolver<Autorizacion> {
  private enum Property {
    /* Fecha modificación */
    FECHA_MODIFICACION("fechaModificacion");

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

  private AutorizacionPredicateResolver() {
  }

  public static AutorizacionPredicateResolver getInstance() {
    return new AutorizacionPredicateResolver();
  }

  private Predicate buildByFechaModificacion(ComparisonNode node, Root<Autorizacion> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.GREATER_THAN_OR_EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String fechaModificacionArgument = node.getArguments().get(0);
    Instant fechaModificacion = Instant.parse(fechaModificacionArgument);

    Join<Autorizacion, EstadoAutorizacion> joinEstadoAutorizacion = root.join(Autorizacion_.estado, JoinType.LEFT);

    return cb.and(
        cb.equal(joinEstadoAutorizacion.get(EstadoAutorizacion_.estado), Estado.AUTORIZADA),
        cb.greaterThanOrEqualTo(joinEstadoAutorizacion.get(EstadoAutorizacion_.fecha), fechaModificacion));
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<Autorizacion> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    Property property = Property.fromCode(node.getSelector());
    if (property == null) {
      return null;
    }

    switch (property) {
      case FECHA_MODIFICACION:
        return buildByFechaModificacion(node, root, criteriaBuilder);
      default:
        return null;
    }
  }

}
