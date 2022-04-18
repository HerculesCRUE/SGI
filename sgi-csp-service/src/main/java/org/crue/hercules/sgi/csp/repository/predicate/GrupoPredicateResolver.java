package org.crue.hercules.sgi.csp.repository.predicate;

import java.time.Instant;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.repository.specification.GrupoSpecifications;
import org.crue.hercules.sgi.csp.util.PredicateResolverUtil;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import io.github.perplexhub.rsql.RSQLOperators;

public class GrupoPredicateResolver implements SgiRSQLPredicateResolver<Grupo> {

  public enum Property {
    /* Persona autorizada */
    PERSONA_AUTORIZADA("personaAutorizada"),
    /* Responsable */
    RESPONSABLE("responsable");

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

  private final SgiConfigProperties sgiConfigProperties;

  private GrupoPredicateResolver(SgiConfigProperties sgiConfigProperties) {
    this.sgiConfigProperties = sgiConfigProperties;
  }

  public static GrupoPredicateResolver getInstance(SgiConfigProperties sgiConfigProperties) {
    return new GrupoPredicateResolver(sgiConfigProperties);
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<Grupo> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    Property property = Property.fromCode(node.getSelector());
    if (property == null) {
      return null;
    }

    switch (property) {
      case PERSONA_AUTORIZADA:
        return buildByPersonaAutorizada(node, root, query, criteriaBuilder);
      case RESPONSABLE:
        return buildByResponsable(node, root, query, criteriaBuilder);
      default:
        return null;
    }
  }

  private Predicate buildByPersonaAutorizada(ComparisonNode node, Root<Grupo> root, CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String personaRef = node.getArguments().get(0);
    Instant fechaActual = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).toInstant();

    return GrupoSpecifications.byPersonaAutorizada(personaRef, fechaActual).toPredicate(root, query, cb);
  }

  private Predicate buildByResponsable(ComparisonNode node, Root<Grupo> root, CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String personaRef = node.getArguments().get(0);
    Instant fechaActual = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).toInstant();

    return GrupoSpecifications.byResponsable(personaRef, fechaActual).toPredicate(root, query, cb);
  }

}
