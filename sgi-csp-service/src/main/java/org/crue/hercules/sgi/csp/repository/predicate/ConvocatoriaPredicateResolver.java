package org.crue.hercules.sgi.csp.repository.predicate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud_;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante_;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.model.RequisitoIPCategoriaProfesional;
import org.crue.hercules.sgi.csp.model.RequisitoIPCategoriaProfesional_;
import org.crue.hercules.sgi.csp.model.RequisitoIPNivelAcademico;
import org.crue.hercules.sgi.csp.model.RequisitoIPNivelAcademico_;
import org.crue.hercules.sgi.csp.model.RequisitoIP_;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolProyecto_;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.util.PredicateResolverUtil;
import org.crue.hercules.sgi.framework.data.jpa.domain.Auditable_;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;
import org.springframework.util.CollectionUtils;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import io.github.perplexhub.rsql.RSQLOperators;

public class ConvocatoriaPredicateResolver implements SgiRSQLPredicateResolver<Convocatoria> {
  private static final Pattern integerPattern = Pattern.compile("^\\d+$");

  private enum Property {
    FECHA_ELIMINACION("fechaEliminacion"),
    PLAN_INVESTIGACION("planInvestigacion"),
    PLAZO_PRESENTACION_SOLICITUD("abiertoPlazoPresentacionSolicitud"),
    /* REQUISITOS IP */
    REQUISITO_SEXO_IP("requisitoSexoIp"), REQUISITO_EDAD_IP("requisitoEdadMaximaIp"),
    REQUISITO_NIVEL_ACADEMICO_IP("requisitoNivelAcademicoIp"),
    REQUISITO_FECHA_NIVEL_ACADEMICO_IP("requisitoFechasNivelAcademicoIp"),
    REQUISITO_VINCULACION_IP("requisitoVinculacionIp"),
    REQUISITO_CATEGORIA_PROFESIONAL_IP("requisitoCategoriaProfesionalIp"),
    REQUISITO_FECHA_CATEGORIA_PROFESIONAL_IP("requisitoFechasCategoriaProfesionalIp"),
    REQUISITO_PROYECTOS_COMPETITIVOS("requisitoProyectosCompetitivos");

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
  private final ProgramaRepository programaRepository;
  private final SgiConfigProperties sgiConfigProperties;

  private ConvocatoriaPredicateResolver(ProgramaRepository programaRepository,
      SgiConfigProperties sgiConfigProperties) {
    this.programaRepository = programaRepository;
    this.sgiConfigProperties = sgiConfigProperties;
  }

  public static ConvocatoriaPredicateResolver getInstance(ProgramaRepository programaRepository,
      SgiConfigProperties sgiConfigProperties) {
    if (instance == null) {
      instance = new ConvocatoriaPredicateResolver(programaRepository, sgiConfigProperties);
    }
    return instance;
  }

  private Predicate buildByPlanInvestigacion(ComparisonNode node, Root<Convocatoria> root, CriteriaBuilder cb) {
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

    ListJoin<Convocatoria, ConvocatoriaEntidadConvocante> joinEntidadesConvocantes = root.join(
        Convocatoria_.entidadesConvocantes,
        JoinType.LEFT);

    return cb.or(joinEntidadesConvocantes.get(ConvocatoriaEntidadConvocante_.programa).in(programasQuery),
        joinEntidadesConvocantes.get(ConvocatoriaEntidadConvocante_.programa).in(programasQuery));
  }

  private Predicate buildInPlazoPresentacionSolicitudes(ComparisonNode node, Root<Convocatoria> root,
      CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    boolean applyFilter = Boolean.parseBoolean(node.getArguments().get(0));
    if (!applyFilter) {
      return cb.isTrue(cb.literal(true));
    }

    Instant fechaActual = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).toInstant();
    Predicate plazoInicio = cb.lessThanOrEqualTo(root.get(Convocatoria_.configuracionSolicitud)
        .get(ConfiguracionSolicitud_.fasePresentacionSolicitudes).get(ConvocatoriaFase_.fechaInicio), fechaActual);
    Predicate plazoFin = cb.greaterThanOrEqualTo(root.get(Convocatoria_.configuracionSolicitud)
        .get(ConfiguracionSolicitud_.fasePresentacionSolicitudes).get(ConvocatoriaFase_.fechaFin), fechaActual);
    return cb.and(plazoInicio, plazoFin);
  }

  /* Validaciones Requisito Ip */
  private Predicate buildInRequisitoSexoIp(ComparisonNode node, Root<Convocatoria> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String sexoRef = node.getArguments().get(0);

    Predicate sexo = cb.equal(root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.sexoRef), sexoRef);
    Predicate isNull = cb.isNull(root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.sexoRef));

    return cb.or(isNull, sexo);
  }

  private Predicate buildInRequisitoEdadIp(ComparisonNode node, Root<Convocatoria> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.GREATER_THAN_OR_EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String edadArgument = node.getArguments().get(0);
    Predicate isNull = cb.isNull(root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.edadMaxima));
    if (isPositiveInteger(edadArgument)) {
      Integer edad = Integer.parseInt(edadArgument);

      Predicate edadMaxima = cb.greaterThanOrEqualTo(root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.edadMaxima),
          edad);

      return cb.or(isNull, edadMaxima);
    }
    return isNull;
  }

  private Predicate buildInRequisitoNivelAcademicoIp(ComparisonNode node, Root<Convocatoria> root,
      CriteriaQuery<?> query, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String nivelAcademicoArgument = node.getArguments().get(0);

    Join<Convocatoria, RequisitoIP> joinRequisitoIp = root.join(Convocatoria_.requisitoIP, JoinType.LEFT);
    ListJoin<RequisitoIP, RequisitoIPNivelAcademico> joinNivelesAcademicos = joinRequisitoIp
        .join(RequisitoIP_.nivelesAcademicos, JoinType.LEFT);
    Predicate nivelAcademico = cb.equal(joinNivelesAcademicos.get(RequisitoIPNivelAcademico_.nivelAcademicoRef),
        nivelAcademicoArgument);

    Subquery<Long> subquery = query.subquery(Long.class);
    Root<RequisitoIPNivelAcademico> rootSubquery = subquery.from(RequisitoIPNivelAcademico.class);
    subquery.select(rootSubquery.get(RequisitoIPNivelAcademico_.id));
    subquery.where(
        cb.equal(rootSubquery.get(RequisitoIPNivelAcademico_.requisitoIPId), joinRequisitoIp.get(RequisitoIP_.id)));
    Predicate isNull = cb.exists(subquery).not();

    return cb.or(isNull, nivelAcademico);
  }

  private Predicate buildInRequisitoFechasNivelAcademicoIp(ComparisonNode node, Root<Convocatoria> root,
      CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String fechaObtencionNivelAcademicoArgument = node.getArguments().get(0);
    Instant fechaObtencionNivelAcademicoInstant = Instant.parse(fechaObtencionNivelAcademicoArgument);

    Predicate fechaObtencionNivelAcademicoMaximaIsBefore = cb.lessThanOrEqualTo(
        root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.fechaMaximaNivelAcademico),
        fechaObtencionNivelAcademicoInstant);

    Predicate fechaObtencionNivelAcademicoMinimaIsAfter = cb.greaterThanOrEqualTo(
        root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.fechaMinimaNivelAcademico),
        fechaObtencionNivelAcademicoInstant);

    Predicate isNullFechaMaxima = cb
        .isNull(root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.fechaMaximaNivelAcademico));

    Predicate isNullFechaMinima = cb
        .isNull(root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.fechaMinimaNivelAcademico));

    return cb.or(cb.and(isNullFechaMaxima, fechaObtencionNivelAcademicoMinimaIsAfter),
        cb.and(isNullFechaMinima, fechaObtencionNivelAcademicoMaximaIsBefore),
        cb.and(fechaObtencionNivelAcademicoMaximaIsBefore, fechaObtencionNivelAcademicoMinimaIsAfter),
        cb.and(isNullFechaMaxima, isNullFechaMinima));
  }

  private Predicate buildInRequisitoVinculacionIp(ComparisonNode node, Root<Convocatoria> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    boolean hasVinculacionArgument = Boolean.parseBoolean(node.getArguments().get(0));

    Predicate hasVinvulacion = cb.equal(root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.vinculacionUniversidad),
        hasVinculacionArgument);

    Predicate isNullVinculacion = cb
        .isNull(root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.vinculacionUniversidad));

    return cb.or(isNullVinculacion, hasVinvulacion);
  }

  private Predicate buildInRequisitoCategoriaProfesionalIp(ComparisonNode node, Root<Convocatoria> root,
      CriteriaQuery<?> query, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String nivelAcademicoArgument = node.getArguments().get(0);

    Join<Convocatoria, RequisitoIP> joinRequisitoIPCategoriaProfesional = root.join(Convocatoria_.requisitoIP,
        JoinType.LEFT);
    ListJoin<RequisitoIP, RequisitoIPCategoriaProfesional> joinCategoriasProfesionales = joinRequisitoIPCategoriaProfesional
        .join(RequisitoIP_.categoriasProfesionales, JoinType.LEFT);
    Predicate categoriaProfesional = cb.equal(
        joinCategoriasProfesionales.get(RequisitoIPCategoriaProfesional_.categoriaProfesionalRef),
        nivelAcademicoArgument);

    Subquery<Long> subquery = query.subquery(Long.class);
    Root<RequisitoIPCategoriaProfesional> rootSubquery = subquery.from(RequisitoIPCategoriaProfesional.class);
    subquery.select(rootSubquery.get(RequisitoIPCategoriaProfesional_.id));
    subquery.where(cb.equal(rootSubquery.get(RequisitoIPCategoriaProfesional_.requisitoIPId),
        joinRequisitoIPCategoriaProfesional.get(RequisitoIP_.id)));
    Predicate isNull = cb.exists(subquery).not();

    return cb.or(isNull, categoriaProfesional);
  }

  private Predicate buildInRequisitoFechasCategoriaProfesionalIp(ComparisonNode node, Root<Convocatoria> root,
      CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String fechaObtencionCategoriaProfesionalArgument = node.getArguments().get(0);
    Instant fechaObtencionCategoriaProfesionalInstant = Instant.parse(fechaObtencionCategoriaProfesionalArgument);

    Predicate fechaObtencionCategoriaProfesionalMaximaIsBefore = cb.lessThanOrEqualTo(
        root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.fechaMaximaCategoriaProfesional),
        fechaObtencionCategoriaProfesionalInstant);

    Predicate fechaObtencionCategoriaProfesionalMinimaIsAfter = cb.greaterThanOrEqualTo(
        root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.fechaMinimaCategoriaProfesional),
        fechaObtencionCategoriaProfesionalInstant);

    Predicate isNullFechaMaxima = cb
        .isNull(root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.fechaMaximaCategoriaProfesional));

    Predicate isNullFechaMinima = cb
        .isNull(root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.fechaMinimaCategoriaProfesional));

    return cb.or(cb.and(isNullFechaMaxima, fechaObtencionCategoriaProfesionalMinimaIsAfter),
        cb.and(isNullFechaMinima, fechaObtencionCategoriaProfesionalMaximaIsBefore),
        cb.and(fechaObtencionCategoriaProfesionalMaximaIsBefore, fechaObtencionCategoriaProfesionalMinimaIsAfter),
        cb.and(isNullFechaMaxima, isNullFechaMinima));
  }

  private Predicate buildInRequisitoProyectosCompetitivos(ComparisonNode node, Root<Convocatoria> root,
      CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String personaRef = node.getArguments().get(0);

    Instant fechaActual = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).toInstant();

    Predicate numMaximoCompetitivosActivos = cb.or(cb
        .isNull(root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.numMaximoCompetitivosActivos)),
        cb.greaterThanOrEqualTo(root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.numMaximoCompetitivosActivos),
            countProyectosClasificacionCvnPersona(cb, query, personaRef, ClasificacionCVN.COMPETITIVOS, fechaActual)));

    Predicate numMaximoNoCompetitivosActivos = cb.or(cb
        .isNull(root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.numMaximoNoCompetitivosActivos)),
        cb.greaterThanOrEqualTo(root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.numMaximoNoCompetitivosActivos),
            countProyectosClasificacionCvnPersona(cb, query, personaRef, ClasificacionCVN.COMPETITIVOS,
                fechaActual)));

    Predicate numMinimoCompetitivos = cb.or(cb
        .isNull(root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.numMinimoCompetitivos)),
        cb.lessThanOrEqualTo(root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.numMinimoCompetitivos),
            countProyectosClasificacionCvnPersona(cb, query, personaRef, ClasificacionCVN.NO_COMPETITIVOS, null)));

    Predicate numMinimoNoCompetitivos = cb.or(cb
        .isNull(root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.numMinimoNoCompetitivos)),
        cb.lessThanOrEqualTo(root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.numMinimoNoCompetitivos),
            countProyectosClasificacionCvnPersona(cb, query, personaRef, ClasificacionCVN.NO_COMPETITIVOS, null)));

    return cb.and(numMaximoCompetitivosActivos,
        numMaximoNoCompetitivosActivos, numMinimoCompetitivos, numMinimoNoCompetitivos);
  }

  private Subquery<Integer> countProyectosClasificacionCvnPersona(CriteriaBuilder cb, CriteriaQuery<?> cq,
      String personaRef, ClasificacionCVN clasificacionCvn, Instant fecha) {

    Subquery<Integer> subquery = cq.subquery(Integer.class);
    Root<Proyecto> subqueryRoot = subquery.from(Proyecto.class);
    Join<Proyecto, ProyectoEquipo> joinProyectoEquipo = subqueryRoot.join(Proyecto_.equipo);
    Join<ProyectoEquipo, RolProyecto> joinRolProyecto = joinProyectoEquipo.join(ProyectoEquipo_.rolProyecto);

    List<Predicate> listPredicates = new ArrayList<>();
    listPredicates.add(cb.equal(subqueryRoot.get(Proyecto_.clasificacionCVN), clasificacionCvn));
    listPredicates.add(cb.equal(joinProyectoEquipo.get(ProyectoEquipo_.personaRef), personaRef));
    listPredicates.add(cb.isTrue(joinRolProyecto.get(RolProyecto_.rolPrincipal)));

    if (fecha != null) {
      listPredicates.add(
          cb.and(
              cb.or(cb.isNull(joinProyectoEquipo.get(ProyectoEquipo_.fechaInicio)),
                  cb.lessThanOrEqualTo(joinProyectoEquipo.get(ProyectoEquipo_.fechaInicio), fecha)),
              cb.or(cb.isNull(joinProyectoEquipo.get(ProyectoEquipo_.fechaFin)),
                  cb.greaterThanOrEqualTo(joinProyectoEquipo.get(ProyectoEquipo_.fechaFin), fecha))));
    }

    subquery.select(cb.count(subqueryRoot.get(Proyecto_.id)).as(Integer.class));
    subquery.where(listPredicates.toArray(new Predicate[] {}));

    return subquery;
  }

  private Predicate buildByFechaEliminacion(ComparisonNode node, Root<Convocatoria> root, CriteriaBuilder cb) {
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
  public Predicate toPredicate(ComparisonNode node, Root<Convocatoria> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    Property property = Property.fromCode(node.getSelector());
    if (property == null) {
      return null;
    }
    switch (property) {
      case FECHA_ELIMINACION:
        return buildByFechaEliminacion(node, root, criteriaBuilder);
      case PLAN_INVESTIGACION:
        return buildByPlanInvestigacion(node, root, criteriaBuilder);
      case PLAZO_PRESENTACION_SOLICITUD:
        return buildInPlazoPresentacionSolicitudes(node, root, criteriaBuilder);
      /* REQUISITO IP */
      case REQUISITO_SEXO_IP:
        return buildInRequisitoSexoIp(node, root, criteriaBuilder);
      case REQUISITO_EDAD_IP:
        return buildInRequisitoEdadIp(node, root, criteriaBuilder);
      case REQUISITO_NIVEL_ACADEMICO_IP:
        return buildInRequisitoNivelAcademicoIp(node, root, query, criteriaBuilder);
      case REQUISITO_FECHA_NIVEL_ACADEMICO_IP:
        return buildInRequisitoFechasNivelAcademicoIp(node, root, criteriaBuilder);
      case REQUISITO_VINCULACION_IP:
        return buildInRequisitoVinculacionIp(node, root, criteriaBuilder);
      case REQUISITO_CATEGORIA_PROFESIONAL_IP:
        return buildInRequisitoCategoriaProfesionalIp(node, root, query, criteriaBuilder);
      case REQUISITO_FECHA_CATEGORIA_PROFESIONAL_IP:
        return buildInRequisitoFechasCategoriaProfesionalIp(node, root, criteriaBuilder);
      case REQUISITO_PROYECTOS_COMPETITIVOS:
        return buildInRequisitoProyectosCompetitivos(node, root, query, criteriaBuilder);
      default:
        return null;
    }
  }

  private boolean isPositiveInteger(String strInt) {
    if (strInt == null) {
      return false;
    }
    return integerPattern.matcher(strInt).matches();
  }
}