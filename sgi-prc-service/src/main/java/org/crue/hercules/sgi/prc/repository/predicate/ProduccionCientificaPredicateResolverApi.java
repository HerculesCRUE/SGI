package org.crue.hercules.sgi.prc.repository.predicate;

import java.time.Instant;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.framework.data.jpa.domain.Auditable_;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoFuente;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo_;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica_;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica_;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

public class ProduccionCientificaPredicateResolverApi extends ProduccionCientificaPredicateResolver {

  public enum Property {
    /* Fecha estado */
    FECHA_ESTADO("fechaEstado");

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

  private ProduccionCientificaPredicateResolverApi() {
  }

  public static ProduccionCientificaPredicateResolverApi getInstance() {
    return new ProduccionCientificaPredicateResolverApi();
  }

  private Predicate buildByFechaEstado(ComparisonNode node, Root<ProduccionCientifica> root, CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!operator.equals(RSQLOperators.GREATER_THAN_OR_EQUAL)) {
      // Unsupported Operator
      throw new IllegalArgumentException(UNSUPPORTED_OPERATOR + operator + FOR + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException(BAD_NUMBER_OF_ARGUMENTS_FOR + node.getSelector());
    }

    String fechaModificacionArgument = node.getArguments().get(0);
    Instant fechaModificacion = Instant.parse(fechaModificacionArgument);
    Join<ProduccionCientifica, EstadoProduccionCientifica> joinEstado = root.join(
        ProduccionCientifica_.estado, JoinType.LEFT);

    Root<ConfiguracionBaremo> rootConfiguracionBaremo = query.from(ConfiguracionBaremo.class);

    return cb.and(
        cb.greaterThanOrEqualTo(joinEstado.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.equal(rootConfiguracionBaremo.get(ConfiguracionBaremo_.epigrafeCVN),
            root.get(ProduccionCientifica_.epigrafeCVN)),
        cb.or(cb.equal(rootConfiguracionBaremo.get(ConfiguracionBaremo_.tipoFuente), TipoFuente.CVN),
            cb.equal(rootConfiguracionBaremo.get(ConfiguracionBaremo_.tipoFuente), TipoFuente.CVN_OTRO_SISTEMA)),
        cb.or(
            cb.equal(joinEstado.get(EstadoProduccionCientifica_.estado), TipoEstadoProduccion.VALIDADO),
            cb.equal(joinEstado.get(EstadoProduccionCientifica_.estado), TipoEstadoProduccion.RECHAZADO)));
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<ProduccionCientifica> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    Property property = Property.fromCode(node.getSelector());
    if (null != property && property.equals(Property.FECHA_ESTADO)) {
      return buildByFechaEstado(node, root, query, criteriaBuilder);
    } else {
      return null;
    }
  }

}
