package org.crue.hercules.sgi.csp.repository.predicate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud_;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante_;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud.Estado;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud_;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudModalidad;
import org.crue.hercules.sgi.csp.model.SolicitudModalidad_;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.util.PredicateResolverUtil;
import org.crue.hercules.sgi.framework.data.jpa.domain.Auditable_;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;
import org.springframework.util.CollectionUtils;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import io.github.perplexhub.rsql.RSQLOperators;

public class SolicitudPredicateResolver implements SgiRSQLPredicateResolver<Solicitud> {
  private enum Property {
    FECHA_ELIMINACION("fechaEliminacion"),
    REFERENCIA_CONVOCATORIA("referenciaConvocatoria"),
    PLAN_INVESTIGACION("planInvestigacion"),
    ABIERTO_PLAZO_PRESENTACION_SOLICITUD("abiertoPlazoPresentacionSolicitud"),
    PENDIENTE("pendiente");

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

  private static SolicitudPredicateResolver instance;
  private final ProgramaRepository programaRepository;
  private final SgiConfigProperties sgiConfigProperties;

  private SolicitudPredicateResolver(ProgramaRepository programaRepository, SgiConfigProperties sgiConfigProperties) {
    this.programaRepository = programaRepository;
    this.sgiConfigProperties = sgiConfigProperties;
  }

  public static SolicitudPredicateResolver getInstance(ProgramaRepository programaRepository,
      SgiConfigProperties sgiConfigProperties) {
    if (instance == null) {
      instance = new SolicitudPredicateResolver(programaRepository, sgiConfigProperties);
    }
    return instance;
  }

  private static Predicate buildByReferenciaConvocatoria(ComparisonNode node, Root<Solicitud> root,
      CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.IGNORE_CASE_LIKE);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String referenciaConvocatoria = "%" + node.getArguments().get(0) + "%";

    Join<Solicitud, Convocatoria> joinConvocatoria = root.join(Solicitud_.convocatoria, JoinType.LEFT);

    return cb.or(
        cb.and(cb.isNotNull(joinConvocatoria),
            cb.like(joinConvocatoria.get(Convocatoria_.codigo), referenciaConvocatoria)),
        cb.and(cb.and(cb.isNull(joinConvocatoria),
            cb.like(root.get(Solicitud_.convocatoriaExterna), referenciaConvocatoria))));
  }

  private Predicate buildByPlanInvestigacion(ComparisonNode node, Root<Solicitud> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    List<Programa> programasQuery = new ArrayList<>();
    List<Programa> programasHijos = new ArrayList<>();
    Long idProgramaRaiz = Long.parseLong(node.getArguments().get(0));
    Optional<Programa> programaRaizOpt = this.programaRepository.findById(idProgramaRaiz);
    if (programaRaizOpt.isPresent()) {
      programasQuery.add(programaRaizOpt.get());
      programasHijos.add(programaRaizOpt.get());
    }
    programasHijos = programaRepository.findByPadreIn(programasHijos);
    while (!CollectionUtils.isEmpty(programasHijos)) {
      programasQuery.addAll(programasHijos);
      programasHijos = programaRepository.findByPadreIn(programasHijos);
    }

    Join<Solicitud, Convocatoria> joinConvocatoria = root.join(Solicitud_.convocatoria, JoinType.LEFT);
    ListJoin<Convocatoria, ConvocatoriaEntidadConvocante> joinEntidadesConvocantes = joinConvocatoria
        .join(Convocatoria_.entidadesConvocantes, JoinType.LEFT);

    ListJoin<Solicitud, SolicitudModalidad> joinSolicitudModalidades = root.join(Solicitud_.modalidades, JoinType.LEFT);

    return cb.or(joinEntidadesConvocantes.get(ConvocatoriaEntidadConvocante_.programa).in(programasQuery),
        joinSolicitudModalidades.get(SolicitudModalidad_.programa).in(programasQuery));
  }

  private Predicate buildByAbiertoPlazoPresentacionSolicitudes(ComparisonNode node, Root<Solicitud> root,
      CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    boolean applyFilter = Boolean.parseBoolean(node.getArguments().get(0));
    if (!applyFilter) {
      return cb.isTrue(cb.literal(true));
    }

    Join<Solicitud, Convocatoria> joinConvocatoria = root.join(Solicitud_.convocatoria);

    Instant fechaActual = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).toInstant();
    Predicate plazoInicio = cb.lessThanOrEqualTo(joinConvocatoria.get(Convocatoria_.configuracionSolicitud)
        .get(ConfiguracionSolicitud_.fasePresentacionSolicitudes).get(ConvocatoriaFase_.fechaInicio), fechaActual);
    Predicate plazoFin = cb.greaterThanOrEqualTo(joinConvocatoria.get(Convocatoria_.configuracionSolicitud)
        .get(ConfiguracionSolicitud_.fasePresentacionSolicitudes).get(ConvocatoriaFase_.fechaFin), fechaActual);
    return cb.and(plazoInicio, plazoFin);
  }

  private Predicate buildByPendiente(ComparisonNode node, Root<Solicitud> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    boolean applyFilter = Boolean.parseBoolean(node.getArguments().get(0));
    if (!applyFilter) {
      return cb.isTrue(cb.literal(true));
    }

    root.join(Solicitud_.solicitudRrhh);
    Join<Solicitud, EstadoSolicitud> joinEstado = root.join(Solicitud_.estado);

    return cb.equal(joinEstado.get(EstadoSolicitud_.estado), Estado.SOLICITADA);
  }

  private Predicate buildByFechaEliminacion(ComparisonNode node, Root<Solicitud> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.GREATER_THAN_OR_EQUAL,
        RSQLOperators.LESS_THAN_OR_EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String fechaEliminacionArgument = node.getArguments().get(0);
    Instant fechaEliminacion = Instant.parse(fechaEliminacionArgument);

    if (node.getOperator().equals(RSQLOperators.GREATER_THAN_OR_EQUAL)) {
      return cb.greaterThanOrEqualTo(root.get(Auditable_.lastModifiedDate), fechaEliminacion);
    } else {
      return cb.lessThanOrEqualTo(root.get(Auditable_.lastModifiedDate), fechaEliminacion);
    }
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<Solicitud> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    Property property = Property.fromCode(node.getSelector());
    if (property == null) {
      return null;
    }

    switch (property) {
      case FECHA_ELIMINACION:
        return buildByFechaEliminacion(node, root, criteriaBuilder);
      case REFERENCIA_CONVOCATORIA:
        return buildByReferenciaConvocatoria(node, root, criteriaBuilder);
      case PLAN_INVESTIGACION:
        return buildByPlanInvestigacion(node, root, criteriaBuilder);
      case ABIERTO_PLAZO_PRESENTACION_SOLICITUD:
        return buildByAbiertoPlazoPresentacionSolicitudes(node, root, criteriaBuilder);
      case PENDIENTE:
        return buildByPendiente(node, root, criteriaBuilder);
      default:
        return null;
    }
  }
}
