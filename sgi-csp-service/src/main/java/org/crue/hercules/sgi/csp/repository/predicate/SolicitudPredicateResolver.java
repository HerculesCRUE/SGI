package org.crue.hercules.sgi.csp.repository.predicate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.ListAttribute;

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
import org.crue.hercules.sgi.csp.model.SolicitudDocumento;
import org.crue.hercules.sgi.csp.model.SolicitudModalidad;
import org.crue.hercules.sgi.csp.model.SolicitudModalidad_;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidad;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoResponsableEconomico;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio_;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto_;
import org.crue.hercules.sgi.csp.model.SolicitudRrhh;
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
    FECHA_MODIFICACION("fechaModificacion"),
    REFERENCIA_CONVOCATORIA("referenciaConvocatoria"),
    PLAN_INVESTIGACION("planInvestigacion"),
    ABIERTO_PLAZO_PRESENTACION_SOLICITUD("abiertoPlazoPresentacionSolicitud"),
    PENDIENTE("pendiente"),
    HISTORICO_ESTADO("historicoEstado"),
    HISTORICO_ESTADO_FECHA_DESDE("historicoEstadoFechaDesde"),
    HISTORICO_ESTADO_FECHA_HASTA("historicoEstadoFechaHasta");

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

  private Predicate buildByFechaModificacion(ComparisonNode node, Root<Solicitud> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.GREATER_THAN_OR_EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String fechaModificacionArgument = node.getArguments().get(0);
    Instant fechaModificacion = Instant.parse(fechaModificacionArgument);
    Join<Solicitud, SolicitudProyecto> joinSolicitudProyecto = root.join(Solicitud_.solicitudProyecto, JoinType.LEFT);
    Join<Solicitud, SolicitudRrhh> joinSolicitudRrhh = root.join(Solicitud_.solicitudRrhh, JoinType.LEFT);
    ListJoin<Solicitud, SolicitudDocumento> joinDocumentos = root.join(Solicitud_.documentos, JoinType.LEFT);
    ListJoin<Solicitud, SolicitudModalidad> joinModalidades = root.join(Solicitud_.modalidades, JoinType.LEFT);
    ListJoin<SolicitudProyecto, SolicitudProyectoEntidad> joinEntidadesSolicitudProyecto = joinSolicitudProyecto
        .join(SolicitudProyecto_.entidades, JoinType.LEFT);
    ListJoin<SolicitudProyecto, SolicitudProyectoEquipo> joinEquipoSolicitudProyecto = joinSolicitudProyecto
        .join(SolicitudProyecto_.equipo, JoinType.LEFT);
    ListJoin<SolicitudProyecto, SolicitudProyectoPresupuesto> joinPresupuestoSolicitudProyecto = joinSolicitudProyecto
        .join(SolicitudProyecto_.presupuesto, JoinType.LEFT);
    ListJoin<SolicitudProyecto, SolicitudProyectoResponsableEconomico> joinResponsableEconomicoSolicitudProyecto = joinSolicitudProyecto
        .join(SolicitudProyecto_.responsablesEconomicos, JoinType.LEFT);
    ListJoin<SolicitudProyecto, SolicitudProyectoSocio> joinSociosSolicitudProyecto = joinSolicitudProyecto
        .join(SolicitudProyecto_.socios, JoinType.LEFT);
    ListJoin<SolicitudProyectoSocio, SolicitudProyectoSocioEquipo> joinEquipoSociosSolicitudProyecto = joinSociosSolicitudProyecto
        .join(SolicitudProyectoSocio_.equipo, JoinType.LEFT);

    return cb.or(cb.greaterThanOrEqualTo(root.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinDocumentos.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinEntidadesSolicitudProyecto.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinEquipoSociosSolicitudProyecto.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinEquipoSolicitudProyecto.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinModalidades.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinPresupuestoSolicitudProyecto.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinResponsableEconomicoSolicitudProyecto.get(Auditable_.lastModifiedDate),
            fechaModificacion),
        cb.greaterThanOrEqualTo(joinSociosSolicitudProyecto.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinSolicitudProyecto.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinSolicitudRrhh.get(Auditable_.lastModifiedDate), fechaModificacion));
  }

  private Predicate buildByHistoricoEstado(ComparisonNode node, Root<Solicitud> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    EstadoSolicitud.Estado estado = EstadoSolicitud.Estado.valueOf(node.getArguments().get(0));

    ListJoin<Solicitud, EstadoSolicitud> joinEstados = joinList(root, Solicitud_.estados, JoinType.LEFT);

    return cb.equal(joinEstados.get(EstadoSolicitud_.estado), estado);
  }

  private Predicate buildByHistoricoEstadoFechaDesde(ComparisonNode node, Root<Solicitud> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.GREATER_THAN_OR_EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    Instant fechaDesde = Instant.parse(node.getArguments().get(0));

    ListJoin<Solicitud, EstadoSolicitud> joinEstados = joinList(root, Solicitud_.estados, JoinType.LEFT);

    return cb.greaterThanOrEqualTo(joinEstados.get(EstadoSolicitud_.fechaEstado), fechaDesde);
  }

  private Predicate buildByHistoricoEstadoFechaHasta(ComparisonNode node, Root<Solicitud> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.LESS_THAN_OR_EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    Instant fechaDesde = Instant.parse(node.getArguments().get(0));

    ListJoin<Solicitud, EstadoSolicitud> joinEstados = joinList(root, Solicitud_.estados, JoinType.LEFT);

    return cb.lessThanOrEqualTo(joinEstados.get(EstadoSolicitud_.fechaEstado), fechaDesde);
  }

  @SuppressWarnings("unchecked")
  private <K, Z> ListJoin<K, Z> joinList(From<?, K> from, ListAttribute<K, Z> attribute, JoinType joinType) {
    for (Join<K, ?> join : from.getJoins()) {
      boolean sameName = join.getAttribute().getName().equals(attribute.getName());

      if (sameName && join.getJoinType().equals(joinType)) {
        return (ListJoin<K, Z>) join;
      }
    }

    return from.join(attribute, joinType);
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
      case FECHA_MODIFICACION:
        return buildByFechaModificacion(node, root, criteriaBuilder);
      case REFERENCIA_CONVOCATORIA:
        return buildByReferenciaConvocatoria(node, root, criteriaBuilder);
      case HISTORICO_ESTADO:
        return buildByHistoricoEstado(node, root, criteriaBuilder);
      case HISTORICO_ESTADO_FECHA_DESDE:
        return buildByHistoricoEstadoFechaDesde(node, root, criteriaBuilder);
      case HISTORICO_ESTADO_FECHA_HASTA:
        return buildByHistoricoEstadoFechaHasta(node, root, criteriaBuilder);
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
