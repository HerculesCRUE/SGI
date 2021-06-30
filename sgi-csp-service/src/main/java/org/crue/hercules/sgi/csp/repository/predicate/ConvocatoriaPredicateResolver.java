package org.crue.hercules.sgi.csp.repository.predicate;

import java.time.Instant;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud_;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import io.github.perplexhub.rsql.RSQLOperators;

public class ConvocatoriaPredicateResolver implements SgiRSQLPredicateResolver<Convocatoria> {
  private enum Property {
    PLAZO_PRESENTACION_SOLICITUD("abiertoPlazoPresentacionSolicitud");

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

  private static ConvocatoriaPredicateResolver instance;

  private ConvocatoriaPredicateResolver() {
    // Do nothing. Hide external instanciation
  }

  public static ConvocatoriaPredicateResolver getInstance() {
    if (instance == null) {
      instance = new ConvocatoriaPredicateResolver();
    }
    return instance;
  }

  private static Predicate buildInPlazoPresentacionSolicitudes(ComparisonNode node, Root<Convocatoria> root,
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
    boolean applyFilter = Boolean.parseBoolean(node.getArguments().get(0));
    if (!applyFilter) {
      return cb.equal(cb.literal("1"), cb.literal("1"));
    }

    Instant now = Instant.now();
    Predicate plazoInicio = cb.lessThanOrEqualTo(root.get(Convocatoria_.configuracionSolicitud)
        .get(ConfiguracionSolicitud_.fasePresentacionSolicitudes).get(ConvocatoriaFase_.fechaInicio), now);
    Predicate plazoFin = cb.greaterThanOrEqualTo(root.get(Convocatoria_.configuracionSolicitud)
        .get(ConfiguracionSolicitud_.fasePresentacionSolicitudes).get(ConvocatoriaFase_.fechaFin), now);
    return cb.and(plazoInicio, plazoFin);
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<Convocatoria> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    switch (Property.fromCode(node.getSelector())) {
    case PLAZO_PRESENTACION_SOLICITUD:
      return buildInPlazoPresentacionSolicitudes(node, root, query, criteriaBuilder);
    default:
      return null;
    }
  }
}
