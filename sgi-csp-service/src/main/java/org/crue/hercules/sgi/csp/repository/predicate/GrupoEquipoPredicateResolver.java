package org.crue.hercules.sgi.csp.repository.predicate;

import java.time.Instant;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo_;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion_;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigador;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigador_;
import org.crue.hercules.sgi.csp.model.Grupo_;
import org.crue.hercules.sgi.csp.util.PredicateResolverUtil;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import io.github.perplexhub.rsql.RSQLOperators;

public class GrupoEquipoPredicateResolver implements SgiRSQLPredicateResolver<GrupoEquipo> {

  public enum Property {
    /* Activo en la fecha actual */
    ACTIVO("activo"),
    /* Adscrito a linea investigacion */
    ADSCRITO_LINEA_INVESTIGACION("adscritoLineaInvestigacion"),
    /* Fecha participacion anterior a */
    FECHA_PARTICIPACION_ANTERIOR("fechaParticipacionAnterior"),
    /* Fecha participacion posterior a */
    FECHA_PARTICIPACION_POSTERIOR("fechaParticipacionPosterior");

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

  private GrupoEquipoPredicateResolver(SgiConfigProperties sgiConfigProperties) {
    this.sgiConfigProperties = sgiConfigProperties;
  }

  public static GrupoEquipoPredicateResolver getInstance(SgiConfigProperties sgiConfigProperties) {
    return new GrupoEquipoPredicateResolver(sgiConfigProperties);
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<GrupoEquipo> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    Property property = Property.fromCode(node.getSelector());
    if (property == null) {
      return null;
    }

    switch (property) {
      case ACTIVO:
        return buildByActivo(node, root, criteriaBuilder);
      case ADSCRITO_LINEA_INVESTIGACION:
        return buildByAdscriptoLineaInvestigacion(node, root, query, criteriaBuilder);
      case FECHA_PARTICIPACION_ANTERIOR:
        return buildByFechaParticipacionAnterior(node, root, criteriaBuilder);
      case FECHA_PARTICIPACION_POSTERIOR:
        return buildByFechaParticipacionPosterior(node, root, criteriaBuilder);
      default:
        return null;
    }
  }

  private Predicate buildByActivo(ComparisonNode node, Root<GrupoEquipo> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    boolean activoArgument = Boolean.parseBoolean(node.getArguments().get(0));

    Instant fechaActual = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).toInstant();

    Join<GrupoEquipo, Grupo> joinGrupo = root.join(GrupoEquipo_.grupo, JoinType.LEFT);

    Predicate greaterThanFechaInicio = cb.or(
        cb.lessThanOrEqualTo(root.get(GrupoEquipo_.fechaInicio), fechaActual),
        cb.and(
            cb.isNull(root.get(GrupoEquipo_.fechaInicio)),
            cb.or(
                cb.lessThanOrEqualTo(joinGrupo.get(Grupo_.fechaInicio), fechaActual))));

    Predicate lowerThanFechaFin = cb.or(
        cb.greaterThanOrEqualTo(root.get(GrupoEquipo_.fechaFin), fechaActual),
        cb.and(
            cb.isNull(root.get(GrupoEquipo_.fechaFin)),
            cb.or(
                cb.isNull(joinGrupo.get(Grupo_.fechaFin)),
                cb.greaterThanOrEqualTo(joinGrupo.get(Grupo_.fechaFin), fechaActual))));

    Predicate activoFechaActual = cb.and(
        greaterThanFechaInicio,
        lowerThanFechaFin);

    Predicate lowerThanFechaInicio = cb.or(
        cb.greaterThanOrEqualTo(root.get(GrupoEquipo_.fechaInicio), fechaActual),
        cb.and(
            cb.isNull(root.get(GrupoEquipo_.fechaInicio)),
            cb.or(
                cb.isNull(joinGrupo.get(Grupo_.fechaInicio)),
                cb.greaterThanOrEqualTo(joinGrupo.get(Grupo_.fechaInicio), fechaActual))));

    Predicate greaterThanFechaFin = cb.or(
        cb.lessThanOrEqualTo(root.get(GrupoEquipo_.fechaFin), fechaActual),
        cb.and(
            cb.isNull(root.get(GrupoEquipo_.fechaFin)),
            cb.or(
                cb.lessThanOrEqualTo(joinGrupo.get(Grupo_.fechaFin), fechaActual))));

    Predicate noActivoFechaActual = cb.or(
        lowerThanFechaInicio,
        greaterThanFechaFin);

    return activoArgument ? activoFechaActual : noActivoFechaActual;
  }

  private Predicate buildByAdscriptoLineaInvestigacion(ComparisonNode node, Root<GrupoEquipo> root,
      CriteriaQuery<?> query, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    Long lineaInvestigacionId = Long.valueOf(node.getArguments().get(0));

    Subquery<String> subquery = query.subquery(String.class);
    Root<GrupoLineaInvestigador> subRoot = subquery.from(GrupoLineaInvestigador.class);
    Join<GrupoLineaInvestigador, GrupoLineaInvestigacion> joinGrupoLineaInvestigacion = subRoot
        .join(GrupoLineaInvestigador_.grupoLineaInvestigacion);

    subquery.select(subRoot.get(GrupoLineaInvestigador_.personaRef))
        .distinct(true)
        .where(cb.and(
            cb.equal(subRoot.get(GrupoLineaInvestigador_.personaRef), root.get(GrupoEquipo_.personaRef)),
            cb.equal(joinGrupoLineaInvestigacion.get(GrupoLineaInvestigacion_.grupoId), root.get(GrupoEquipo_.grupoId)),
            cb.equal(joinGrupoLineaInvestigacion.get(GrupoLineaInvestigacion_.lineaInvestigacionId),
                lineaInvestigacionId)));

    return cb.exists(subquery);
  }

  private Predicate buildByFechaParticipacionAnterior(ComparisonNode node, Root<GrupoEquipo> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.LESS_THAN_OR_EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String fechaModificacionArgument = node.getArguments().get(0);
    Instant fechaFin = Instant.parse(fechaModificacionArgument);
    Join<GrupoEquipo, Grupo> joinGrupo = root.join(GrupoEquipo_.grupo, JoinType.LEFT);

    return cb.or(
        cb.lessThanOrEqualTo(root.get(GrupoEquipo_.fechaInicio), fechaFin),
        cb.and(
            cb.isNull(root.get(GrupoEquipo_.fechaInicio)),
            cb.lessThanOrEqualTo(joinGrupo.get(Grupo_.fechaInicio), fechaFin)));
  }

  private Predicate buildByFechaParticipacionPosterior(ComparisonNode node, Root<GrupoEquipo> root,
      CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.GREATER_THAN_OR_EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String fechaModificacionArgument = node.getArguments().get(0);
    Instant fechaInicio = Instant.parse(fechaModificacionArgument);
    Join<GrupoEquipo, Grupo> joinGrupo = root.join(GrupoEquipo_.grupo, JoinType.LEFT);

    return cb.or(
        cb.greaterThanOrEqualTo(root.get(GrupoEquipo_.fechaFin), fechaInicio),
        cb.and(
            cb.isNull(root.get(GrupoEquipo_.fechaFin)),
            cb.or(
                cb.isNull(joinGrupo.get(Grupo_.fechaFin)),
                cb.greaterThanOrEqualTo(joinGrupo.get(Grupo_.fechaFin), fechaInicio))));
  }

}
