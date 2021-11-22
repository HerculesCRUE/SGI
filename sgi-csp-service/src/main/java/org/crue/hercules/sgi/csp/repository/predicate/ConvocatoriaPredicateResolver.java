package org.crue.hercules.sgi.csp.repository.predicate;

import java.time.Instant;
import java.util.regex.Pattern;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud_;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.model.RequisitoIPCategoriaProfesional;
import org.crue.hercules.sgi.csp.model.RequisitoIPCategoriaProfesional_;
import org.crue.hercules.sgi.csp.model.RequisitoIPNivelAcademico;
import org.crue.hercules.sgi.csp.model.RequisitoIPNivelAcademico_;
import org.crue.hercules.sgi.csp.model.RequisitoIP_;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import io.github.perplexhub.rsql.RSQLOperators;

public class ConvocatoriaPredicateResolver implements SgiRSQLPredicateResolver<Convocatoria> {
  private static final Pattern integerPattern = Pattern.compile("^\\d+$");

  private enum Property {
    PLAZO_PRESENTACION_SOLICITUD("abiertoPlazoPresentacionSolicitud"),
    /* REQUISITOS IP */
    REQUISITO_SEXO_IP("requisitoSexoIp"), REQUISITO_EDAD_IP("requisitoEdadMaximaIp"),
    REQUISITO_NIVEL_ACADEMICO_IP("requisitoNivelAcademicoIp"),
    REQUISITO_FECHA_NIVEL_ACADEMICO_IP("requisitoFechasNivelAcademicoIp"),
    REQUISITO_VINCULACION_IP("requisitoVinculacionIp"),
    REQUISITO_CATEGORIA_PROFESIONAL_IP("requisitoCategoriaProfesionalIp"),
    REQUISITO_FECHA_CATEGORIA_PROFESIONAL_IP("requisitoFechasCategoriaProfesionalIp");

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

  private Predicate buildInPlazoPresentacionSolicitudes(ComparisonNode node, Root<Convocatoria> root,
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

  /* Validaciones Requisito Ip */
  private Predicate buildInRequisitoSexoIp(ComparisonNode node, Root<Convocatoria> root, CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!operator.equals(RSQLOperators.EQUAL)) {
      // Unsupported Operator
      throw new IllegalArgumentException("Unsupported operator: " + operator + " for " + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException("Bad number of arguments for " + node.getSelector());
    }
    String sexoRef = node.getArguments().get(0);

    Predicate sexo = cb.equal(root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.sexoRef), sexoRef);
    Predicate isNull = cb.isNull(root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.sexoRef));

    return cb.or(isNull, sexo);
  }

  private Predicate buildInRequisitoEdadIp(ComparisonNode node, Root<Convocatoria> root, CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!operator.equals(RSQLOperators.GREATER_THAN_OR_EQUAL)) {
      // Unsupported Operator
      throw new IllegalArgumentException("Unsupported operator: " + operator + " for " + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException("Bad number of arguments for " + node.getSelector());
    }
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
    ComparisonOperator operator = node.getOperator();
    if (!operator.equals(RSQLOperators.EQUAL)) {
      // Unsupported Operator
      throw new IllegalArgumentException("Unsupported operator: " + operator + " for " + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException("Bad number of arguments for " + node.getSelector());
    }
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
    ComparisonOperator operator = node.getOperator();
    if (!operator.equals(RSQLOperators.EQUAL)) {
      // Unsupported Operator
      throw new IllegalArgumentException("Unsupported operator: " + operator + " for " + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException("Bad number of arguments for " + node.getSelector());
    }
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
    ComparisonOperator operator = node.getOperator();
    if (!operator.equals(RSQLOperators.EQUAL)) {
      // Unsupported Operator
      throw new IllegalArgumentException("Unsupported operator: " + operator + "for " + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException("Bad number of arguments for " + node.getSelector());
    }
    boolean hasVinculacionArgument = Boolean.parseBoolean(node.getArguments().get(0));

    Predicate hasVinvulacion = cb.equal(root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.vinculacionUniversidad),
        hasVinculacionArgument);

    Predicate isNullVinculacion = cb
        .isNull(root.get(Convocatoria_.requisitoIP).get(RequisitoIP_.vinculacionUniversidad));

    return cb.or(isNullVinculacion, hasVinvulacion);
  }

  private Predicate buildInRequisitoCategoriaProfesionalIp(ComparisonNode node, Root<Convocatoria> root,
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
    ComparisonOperator operator = node.getOperator();
    if (!operator.equals(RSQLOperators.EQUAL)) {
      // Unsupported Operator
      throw new IllegalArgumentException("Unsupported operator: " + operator + "for " + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException("Bad number of arguments for " + node.getSelector());
    }
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