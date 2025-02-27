package org.crue.hercules.sgi.csp.repository.predicate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.model.AnualidadGasto;
import org.crue.hercules.sgi.csp.model.ContextoProyecto;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad_;
import org.crue.hercules.sgi.csp.model.ProyectoAreaConocimiento;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto;
import org.crue.hercules.sgi.csp.model.ProyectoDocumento;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante_;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadGestora;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo_;
import org.crue.hercules.sgi.csp.model.ProyectoFacturacion;
import org.crue.hercules.sgi.csp.model.ProyectoFase;
import org.crue.hercules.sgi.csp.model.ProyectoHito;
import org.crue.hercules.sgi.csp.model.ProyectoPartida;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga_;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoResponsableEconomico;
import org.crue.hercules.sgi.csp.model.ProyectoResponsableEconomico_;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocio_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.crue.hercules.sgi.csp.model.RolProyecto_;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgempService;
import org.crue.hercules.sgi.csp.util.PredicateResolverUtil;
import org.crue.hercules.sgi.framework.data.jpa.domain.Auditable_;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import io.github.perplexhub.rsql.RSQLOperators;

public class ProyectoPredicateResolver implements SgiRSQLPredicateResolver<Proyecto> {
  private enum Property {
    /* Plan investigación */
    PLAN_INVESTIGACION("planInvestigacion"),
    /* Responsable proyecto */
    RESPONSABLE_PROYECTO("responsableProyecto"),
    /* Finalizado */
    FINALIZADO("finalizado"),
    /* Prorrogado */
    PRORROGADO("prorrogado"),
    /* Fecha eliminacion */
    FECHA_ELIMINACION("fechaEliminacion"),
    /* Fecha modificación */
    FECHA_MODIFICACION("fechaModificacion"),
    /* Con participación actual */
    PARTICIPACION_ACTUAL("participacionActual"),
    /* Pais socio proyecto */
    PROYECTO_SOCIO_PAIS_ID("socios.paisRef");

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

  private final ProgramaRepository programaRepository;
  private final SgiApiSgempService sgiApiSgempService;
  private final SgiConfigProperties sgiConfigProperties;

  private ProyectoPredicateResolver(ProgramaRepository programaRepository,
      SgiApiSgempService sgiApiSgempService,
      SgiConfigProperties sgiConfigProperties) {
    this.programaRepository = programaRepository;
    this.sgiApiSgempService = sgiApiSgempService;
    this.sgiConfigProperties = sgiConfigProperties;
  }

  public static ProyectoPredicateResolver getInstance(ProgramaRepository programaRepository,
      SgiApiSgempService sgiApiSgempService,
      SgiConfigProperties sgiConfigProperties) {
    return new ProyectoPredicateResolver(programaRepository, sgiApiSgempService, sgiConfigProperties);
  }

  private Predicate buildByPlanInvestigacion(ComparisonNode node, Root<Proyecto> root, CriteriaBuilder cb) {
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

    ListJoin<Proyecto, ProyectoEntidadConvocante> joinEntidadesConvocantes = root.join(Proyecto_.entidadesConvocantes,
        JoinType.LEFT);

    return cb.or(joinEntidadesConvocantes.get(ProyectoEntidadConvocante_.programa).in(programasQuery),
        joinEntidadesConvocantes.get(ProyectoEntidadConvocante_.programaConvocatoria).in(programasQuery));
  }

  private Predicate buildByResponsableEquipo(ComparisonNode node, Root<Proyecto> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String personaRef = node.getArguments().get(0);
    ListJoin<Proyecto, ProyectoEquipo> joinEquipos = root.join(Proyecto_.equipo, JoinType.LEFT);

    return cb.and(cb.equal(joinEquipos.get(ProyectoEquipo_.personaRef), personaRef),
        cb.equal(joinEquipos.get(ProyectoEquipo_.rolProyecto).get(RolProyecto_.rolPrincipal), true));
  }

  private Predicate buildByFinalizado(ComparisonNode node, Root<Proyecto> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    boolean finalizado = Boolean.parseBoolean(node.getArguments().get(0));
    Instant now = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).toInstant();
    Predicate fechaFinAlcanzada = cb.lessThan(root.get(Proyecto_.fechaFin), now);
    Predicate fechaFinDefinitivaAlcanzada = cb.lessThan(root.get(Proyecto_.fechaFinDefinitiva), now);
    Predicate fechaFinDefinitivaNula = cb.isNull(root.get(Proyecto_.fechaFinDefinitiva));
    Predicate fechaFinEnElFuturo = cb.greaterThanOrEqualTo(root.get(Proyecto_.fechaFin), now);
    Predicate fechaFinDefinitivaEnElFuturo = cb.greaterThanOrEqualTo(root.get(Proyecto_.fechaFinDefinitiva), now);

    if (finalizado) {
      return cb.or(cb.and(fechaFinAlcanzada, fechaFinDefinitivaNula), fechaFinDefinitivaAlcanzada);
    } else {
      return cb.or(cb.and(fechaFinEnElFuturo, fechaFinDefinitivaNula), fechaFinDefinitivaEnElFuturo);
    }
  }

  private Predicate buildByProrrogado(ComparisonNode node, Root<Proyecto> root, CriteriaQuery<?> cq,
      CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    boolean prorrogado = Boolean.parseBoolean(node.getArguments().get(0));

    Subquery<Long> subquery = cq.subquery(Long.class);
    Root<ProyectoProrroga> prorrogaRoot = subquery.from(ProyectoProrroga.class);
    subquery.select(prorrogaRoot.get(ProyectoProrroga_.proyectoId));
    subquery.where(cb.equal(prorrogaRoot.get(ProyectoProrroga_.proyectoId), root.get(Proyecto_.id)));

    if (prorrogado) {
      return cb.exists(subquery);
    } else {
      return cb.not(cb.exists(subquery));
    }
  }

  private Predicate buildByFechaEliminacion(ComparisonNode node, Root<Proyecto> root, CriteriaBuilder cb) {
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

  private Predicate buildByFechaModificacion(ComparisonNode node, Root<Proyecto> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.GREATER_THAN_OR_EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String fechaModificacionArgument = node.getArguments().get(0);
    Instant fechaModificacion = Instant.parse(fechaModificacionArgument);
    Join<Proyecto, ContextoProyecto> joinContexto = root.join(Proyecto_.contexto, JoinType.LEFT);
    ListJoin<Proyecto, ProyectoEquipo> joinEquipos = root.join(Proyecto_.equipo, JoinType.LEFT);
    ListJoin<Proyecto, ProyectoEntidadGestora> joinEntidadGestora = root.join(Proyecto_.entidadesGestoras,
        JoinType.LEFT);
    ListJoin<Proyecto, ProyectoEntidadConvocante> joinEntidadConvocante = root.join(Proyecto_.entidadesConvocantes,
        JoinType.LEFT);
    ListJoin<Proyecto, ProyectoEntidadFinanciadora> joinEntidadFinanciadora = root
        .join(Proyecto_.entidadesFinanciadoras, JoinType.LEFT);
    ListJoin<Proyecto, ProyectoAnualidad> joinAnualidad = root.join(Proyecto_.proyectosAnualidad, JoinType.LEFT);
    ListJoin<ProyectoAnualidad, AnualidadGasto> joinAnualidadGasto = joinAnualidad
        .join(ProyectoAnualidad_.anualidadesGasto, JoinType.LEFT);

    Join<Proyecto, EstadoProyecto> joinEstadoProyecto = root.join(Proyecto_.estado, JoinType.LEFT);
    ListJoin<Proyecto, ProyectoAreaConocimiento> joinAreasConocimiento = root.join(Proyecto_.areasConocimiento,
        JoinType.LEFT);
    ListJoin<Proyecto, ProyectoConceptoGasto> joinConceptosGasto = root.join(Proyecto_.conceptosGasto,
        JoinType.LEFT);
    ListJoin<Proyecto, ProyectoDocumento> joinDocumentos = root.join(Proyecto_.documentos, JoinType.LEFT);
    ListJoin<Proyecto, ProyectoFacturacion> joinFacturacion = root.join(Proyecto_.facturacion, JoinType.LEFT);
    ListJoin<Proyecto, ProyectoFase> joinFases = root.join(Proyecto_.fases, JoinType.LEFT);
    ListJoin<Proyecto, ProyectoHito> joinHitos = root.join(Proyecto_.hitos, JoinType.LEFT);
    ListJoin<Proyecto, ProyectoPartida> joinPartidas = root.join(Proyecto_.partidas, JoinType.LEFT);
    ListJoin<Proyecto, ProyectoPeriodoJustificacion> joinPeriodosJustificacion = root
        .join(Proyecto_.periodosJustificacion, JoinType.LEFT);
    ListJoin<Proyecto, ProyectoPeriodoSeguimiento> joinPeriodosSeguimiento = root.join(Proyecto_.periodosSeguimiento,
        JoinType.LEFT);
    ListJoin<Proyecto, ProyectoProrroga> joinProrrogas = root.join(Proyecto_.prorrogas, JoinType.LEFT);
    ListJoin<Proyecto, ProyectoProyectoSge> joinProyectosSge = root.join(Proyecto_.identificadoresSge, JoinType.LEFT);
    ListJoin<Proyecto, ProyectoResponsableEconomico> joinResponsablesEconomicos = root
        .join(Proyecto_.responsablesEconomicos, JoinType.LEFT);
    ListJoin<Proyecto, ProyectoSocio> joinSocios = root.join(Proyecto_.socios, JoinType.LEFT);

    return cb.or(
        cb.greaterThanOrEqualTo(joinAnualidad.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinAnualidadGasto.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinAreasConocimiento.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinConceptosGasto.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinContexto.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinDocumentos.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinEntidadConvocante.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinEntidadFinanciadora.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinEntidadGestora.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinEquipos.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinEstadoProyecto.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinFacturacion.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinFases.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinHitos.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinPartidas.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinPeriodosJustificacion.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinPeriodosSeguimiento.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinProrrogas.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinProyectosSge.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinResponsablesEconomicos.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinSocios.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(root.get(Auditable_.lastModifiedDate), fechaModificacion));
  }

  private Predicate buildByParticipacionActual(ComparisonNode node, Root<Proyecto> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    boolean participacionActual = Boolean.parseBoolean(node.getArguments().get(0));

    if (!participacionActual) {
      return cb.isTrue(cb.literal(true)); // always true = no filtering
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    Instant fechaActual = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).toInstant();

    ListJoin<Proyecto, ProyectoEquipo> joinEquipos = root.join(Proyecto_.equipo, JoinType.LEFT);

    Predicate fechaInicioMiembroEquipoLessOrNull = cb.or(
        cb.lessThanOrEqualTo(joinEquipos.get(ProyectoEquipo_.fechaInicio), fechaActual),
        cb.isNull(joinEquipos.get(ProyectoEquipo_.fechaInicio)));

    Predicate fechaFinMiembroEquipoGreaterOrNull = cb.or(
        cb.greaterThanOrEqualTo(joinEquipos.get(ProyectoEquipo_.fechaFin), fechaActual),
        cb.isNull(joinEquipos.get(ProyectoEquipo_.fechaFin)));

    Predicate participacionActualEquipo = cb.and(
        fechaInicioMiembroEquipoLessOrNull,
        fechaFinMiembroEquipoGreaterOrNull,
        cb.equal(joinEquipos.get(ProyectoEquipo_.personaRef), authentication.getName()));

    ListJoin<Proyecto, ProyectoResponsableEconomico> joinResponsableEconomico = root
        .join(Proyecto_.responsablesEconomicos, JoinType.LEFT);

    Predicate fechaInicioResponsableEconomicoLessOrNull = cb.or(
        cb.lessThanOrEqualTo(joinResponsableEconomico.get(ProyectoResponsableEconomico_.fechaInicio), fechaActual),
        cb.isNull(joinResponsableEconomico.get(ProyectoResponsableEconomico_.fechaInicio)));

    Predicate fechaFinResponsableEconomicoGreaterOrNull = cb.or(
        cb.greaterThanOrEqualTo(joinResponsableEconomico.get(ProyectoResponsableEconomico_.fechaFin), fechaActual),
        cb.isNull(joinResponsableEconomico.get(ProyectoResponsableEconomico_.fechaFin)));

    Predicate participacionActualResponsableEconomico = cb.and(
        fechaInicioResponsableEconomicoLessOrNull,
        fechaFinResponsableEconomicoGreaterOrNull,
        cb.equal(joinResponsableEconomico.get(ProyectoResponsableEconomico_.personaRef), authentication.getName()));

    return cb.or(participacionActualEquipo, participacionActualResponsableEconomico);
  }

  private Predicate buildByProyectoSocioPaisId(ComparisonNode node, Root<Proyecto> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String paisId = node.getArguments().get(0);

    List<String> empresaIds = sgiApiSgempService.findAllEmpresaIdsByPaisId(paisId);

    if (empresaIds.isEmpty()) {
      return cb.disjunction();
    }

    int sublistSize = 1000;
    Join<Proyecto, ProyectoSocio> sociosJoin = root.join(Proyecto_.socios);
    Path<String> empresaRefPath = sociosJoin.get(ProyectoSocio_.empresaRef);

    return IntStream.range(0, (empresaIds.size() + sublistSize - 1) /
        sublistSize)
        .mapToObj(i -> empresaRefPath.in(empresaIds.subList(
            i * sublistSize,
            Math.min((i + 1) * sublistSize, empresaIds.size()))))
        .reduce(cb::or)
        .orElse(cb.disjunction());
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<Proyecto> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    Property property = Property.fromCode(node.getSelector());

    if (property == null) {
      return null;
    }

    switch (property) {
      case PLAN_INVESTIGACION:
        return buildByPlanInvestigacion(node, root, criteriaBuilder);
      case RESPONSABLE_PROYECTO:
        return buildByResponsableEquipo(node, root, criteriaBuilder);
      case FINALIZADO:
        return buildByFinalizado(node, root, criteriaBuilder);
      case PRORROGADO:
        return buildByProrrogado(node, root, query, criteriaBuilder);
      case FECHA_ELIMINACION:
        return buildByFechaEliminacion(node, root, criteriaBuilder);
      case FECHA_MODIFICACION:
        return buildByFechaModificacion(node, root, criteriaBuilder);
      case PARTICIPACION_ACTUAL:
        return buildByParticipacionActual(node, root, criteriaBuilder);
      case PROYECTO_SOCIO_PAIS_ID:
        return buildByProyectoSocioPaisId(node, root, criteriaBuilder);
      default:
        return null;
    }
  }
}
