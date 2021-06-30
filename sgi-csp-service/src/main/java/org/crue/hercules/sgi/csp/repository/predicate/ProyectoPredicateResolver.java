package org.crue.hercules.sgi.csp.repository.predicate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante_;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo_;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.crue.hercules.sgi.csp.model.RolProyecto_;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoProrrogaRepository;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;
import org.springframework.util.CollectionUtils;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
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
    PRORROGADO("prorrogado");

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

  private ProyectoPredicateResolver(ProgramaRepository programaRepository,
      ProyectoProrrogaRepository proyectoProrrogaRepository) {
    this.programaRepository = programaRepository;
    this.proyectoProrrogaRepository = proyectoProrrogaRepository;
  }

  public static ProyectoPredicateResolver getInstance(ProgramaRepository programaRepository,
      ProyectoProrrogaRepository proyectoProrrogaRepository) {
    return new ProyectoPredicateResolver(programaRepository, proyectoProrrogaRepository);
  }

  private Predicate buildByPlanInvestigacion(ComparisonNode node, Root<Proyecto> root, CriteriaQuery<?> query,
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

    List<Programa> programasQuery = new ArrayList<Programa>();
    List<Programa> programasHijos = new ArrayList<Programa>();
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

  private Predicate buildByResponsableEquipo(ComparisonNode node, Root<Proyecto> root, CriteriaQuery<?> query,
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

    String personaRef = node.getArguments().get(0);
    ListJoin<Proyecto, ProyectoEquipo> joinEquipos = root.join(Proyecto_.equipo, JoinType.LEFT);

    return cb.and(cb.equal(joinEquipos.get(ProyectoEquipo_.personaRef), personaRef),
        cb.equal(joinEquipos.get(ProyectoEquipo_.rolProyecto).get(RolProyecto_.rolPrincipal), true));
  }

  private Predicate buildByFinalizado(ComparisonNode node, Root<Proyecto> root, CriteriaQuery<?> query,
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

    boolean finalizado = Boolean.parseBoolean(node.getArguments().get(0));
    Instant now = Instant.now();
    if (finalizado) {
      return cb.and(cb.lessThan(root.get(Proyecto_.fechaFin), now));
    } else {
      return cb.and(cb.greaterThanOrEqualTo(root.get(Proyecto_.fechaFin), now));
    }
  }

  private Predicate buildByProrrogado(ComparisonNode node, Root<Proyecto> root, CriteriaQuery<?> query,
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

    boolean prorrogado = Boolean.parseBoolean(node.getArguments().get(0));

    if (prorrogado) {
      ListJoin<Proyecto, ProyectoProrroga> joinProrrogas = root.join(Proyecto_.prorrogas, JoinType.INNER);
      return cb.and(cb.equal(joinProrrogas.get(ProyectoProrroga_.proyectoId), root.get(Proyecto_.id)));
    } else {
      List<Long> idsProyectoWithProrrogas = this.proyectoProrrogaRepository.findAll().stream()
          .map(prorroga -> prorroga.getProyectoId()).collect(Collectors.toList());
      return cb.and(cb.not(root.get(Proyecto_.id).in(idsProyectoWithProrrogas)));
    }
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<Proyecto> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    switch (Property.fromCode(node.getSelector())) {
      case PLAN_INVESTIGACION:
        return buildByPlanInvestigacion(node, root, query, criteriaBuilder);
      case RESPONSABLE_PROYECTO:
        return buildByResponsableEquipo(node, root, query, criteriaBuilder);
      case FINALIZADO:
        return buildByFinalizado(node, root, query, criteriaBuilder);
      case PRORROGADO:
        return buildByProrrogado(node, root, query, criteriaBuilder);
      default:
        return null;
    }
  }
}
