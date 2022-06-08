package org.crue.hercules.sgi.csp.repository.predicate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.model.AnualidadGasto;
import org.crue.hercules.sgi.csp.model.ContextoProyecto;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad_;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante_;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadGestora;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo_;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga_;
import org.crue.hercules.sgi.csp.model.ProyectoResponsableEconomico;
import org.crue.hercules.sgi.csp.model.ProyectoResponsableEconomico_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.crue.hercules.sgi.csp.model.RolProyecto_;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoProrrogaRepository;
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
    /* Fecha modificación */
    FECHA_MODIFICACION("fechaModificacion"),
    /* Con participación actual */
    PARTICIPACION_ACTUAL("participacionActual");

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
  private final ProyectoProrrogaRepository proyectoProrrogaRepository;
  private final SgiConfigProperties sgiConfigProperties;

  private ProyectoPredicateResolver(ProgramaRepository programaRepository,
      ProyectoProrrogaRepository proyectoProrrogaRepository,
      SgiConfigProperties sgiConfigProperties) {
    this.programaRepository = programaRepository;
    this.proyectoProrrogaRepository = proyectoProrrogaRepository;
    this.sgiConfigProperties = sgiConfigProperties;
  }

  public static ProyectoPredicateResolver getInstance(ProgramaRepository programaRepository,
      ProyectoProrrogaRepository proyectoProrrogaRepository, SgiConfigProperties sgiConfigProperties) {
    return new ProyectoPredicateResolver(programaRepository, proyectoProrrogaRepository, sgiConfigProperties);
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
    Instant now = Instant.now();
    if (finalizado) {
      return cb.and(cb.lessThan(root.get(Proyecto_.fechaFin), now));
    } else {
      return cb.and(cb.greaterThanOrEqualTo(root.get(Proyecto_.fechaFin), now));
    }
  }

  private Predicate buildByProrrogado(ComparisonNode node, Root<Proyecto> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    boolean prorrogado = Boolean.parseBoolean(node.getArguments().get(0));

    if (prorrogado) {
      ListJoin<Proyecto, ProyectoProrroga> joinProrrogas = root.join(Proyecto_.prorrogas, JoinType.INNER);
      return cb.and(cb.equal(joinProrrogas.get(ProyectoProrroga_.proyectoId), root.get(Proyecto_.id)));
    } else {
      List<Long> idsProyectoWithProrrogas = this.proyectoProrrogaRepository.findAll().stream()
          .map(ProyectoProrroga::getProyectoId).collect(Collectors.toList());
      return cb.and(cb.not(root.get(Proyecto_.id).in(idsProyectoWithProrrogas)));
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

    return cb.or(cb.greaterThanOrEqualTo(root.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinContexto.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinEquipos.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinEntidadGestora.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinAnualidad.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinAnualidadGasto.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinEntidadConvocante.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinEntidadFinanciadora.get(Auditable_.lastModifiedDate), fechaModificacion));
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
        return buildByProrrogado(node, root, criteriaBuilder);
      case FECHA_MODIFICACION:
        return buildByFechaModificacion(node, root, criteriaBuilder);
      case PARTICIPACION_ACTUAL:
        return buildByParticipacionActual(node, root, criteriaBuilder);
      default:
        return null;
    }
  }
}
