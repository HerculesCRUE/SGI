package org.crue.hercules.sgi.csp.repository.predicate;

import java.math.BigDecimal;
import java.time.Instant;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.SingularAttribute;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaTitulo_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante_;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadFinanciadora_;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo_;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge_;
import org.crue.hercules.sgi.csp.model.ProyectoTitulo;
import org.crue.hercules.sgi.csp.model.ProyectoTitulo_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.crue.hercules.sgi.csp.model.RolProyecto_;
import org.crue.hercules.sgi.csp.util.PredicateResolverUtil;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import io.github.perplexhub.rsql.RSQLOperators;

public class ProyectoProyectoSgePredicateResolver implements SgiRSQLPredicateResolver<ProyectoProyectoSge> {

  private static final String LIKE_WILDCARD_PERCENT = "%";

  public enum Property {
    /* fecha inicio proyecto */
    FECHA_INICIO_PROYECTO("fechaInicio"),
    /* Fecha fin proyecto */
    FECHA_FIN_PROYECTO("fechaFin"),
    /* Nombre proyecto */
    NOMBRE_PROYECTO("nombre"),
    /* Responsable proyecto */
    RESPONSABLE_PROYECTO("responsable"),
    /* Codigo externo del proyecto */
    CODIGO_EXTERNO("codigoExterno"),
    /* Referencia interna */
    CODIGO_INTERNO("codigoInterno"),
    /* Convocatoria Id */
    CONVOCATORIA("convocatoria"),
    /* Titulo convocatoria */
    TITULO_CONVOCATORIA("tituloConvocatoria"),
    /* Importe concedido costes directos */
    IMPORTE_CONCEDIDO("importeConcedido"),
    /* Importe concedido costes indirectos */
    IMPORTE_CONCEDIDO_COSTES_INDIRECTOS("importeConcedidoCostesIndirectos"),
    /* Entidad convocante */
    ENTIDAD_CONVOCANTE("entidadConvocante"),
    /* Entidad financiadora */
    ENTIDAD_FINANCIADORA("entidadFinanciadora"),
    /* Fecha fin definitiva proyecto */
    FECHA_FIN_DEFINITIVA_PROYECTO("fechaFinDefinitiva");

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

  private ProyectoProyectoSgePredicateResolver(SgiConfigProperties sgiConfigProperties) {
    this.sgiConfigProperties = sgiConfigProperties;
  }

  public static ProyectoProyectoSgePredicateResolver getInstance(SgiConfigProperties sgiConfigProperties) {
    return new ProyectoProyectoSgePredicateResolver(sgiConfigProperties);
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<ProyectoProyectoSge> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    Property property = Property.fromCode(node.getSelector());
    if (property == null) {
      return null;
    }

    switch (property) {
      case FECHA_INICIO_PROYECTO:
        return buildByFechaProyecto(node, root, query, criteriaBuilder, Proyecto_.fechaInicio);
      case FECHA_FIN_PROYECTO:
        return buildByFechaProyecto(node, root, query, criteriaBuilder, Proyecto_.fechaFin);
      case NOMBRE_PROYECTO:
        return buildByNombreProyecto(node, root, query, criteriaBuilder);
      case RESPONSABLE_PROYECTO:
        return buildByResponsableProyecto(node, root, query, criteriaBuilder);
      case CODIGO_EXTERNO:
        return buildByCodigoExterno(node, root, query, criteriaBuilder);
      case CODIGO_INTERNO:
        return buildByCodigoInterno(node, root, query, criteriaBuilder);
      case CONVOCATORIA:
        return buildByConvocatoria(node, root, query, criteriaBuilder);
      case TITULO_CONVOCATORIA:
        return buildByTituloConvocatoria(node, root, query, criteriaBuilder);
      case IMPORTE_CONCEDIDO:
        return buildByImporteConcedido(node, root, query, criteriaBuilder);
      case IMPORTE_CONCEDIDO_COSTES_INDIRECTOS:
        return buildByImporteConcedidoCostesIndirectos(node, root, query, criteriaBuilder);
      case ENTIDAD_CONVOCANTE:
        return buildByEntidadConvocante(node, root, query, criteriaBuilder);
      case ENTIDAD_FINANCIADORA:
        return buildByEntidadFinanciadora(node, root, query, criteriaBuilder);
      default:
        return null;
    }
  }

  private Predicate buildByFechaProyecto(ComparisonNode node, Root<ProyectoProyectoSge> root, CriteriaQuery<?> query,
      CriteriaBuilder cb, SingularAttribute<Proyecto, Instant> singularAttribute) {
    ComparisonOperator[] validOperators = new ComparisonOperator[] {
        RSQLOperators.GREATER_THAN,
        RSQLOperators.GREATER_THAN_OR_EQUAL,
        RSQLOperators.LESS_THAN, RSQLOperators.LESS_THAN_OR_EQUAL
    };

    PredicateResolverUtil.validateOperatorIsSupported(node, validOperators);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    ComparisonOperator operator = node.getOperator();
    Instant fecha = Instant.parse(node.getArguments().get(0));

    Subquery<Proyecto> subquery = query.subquery(Proyecto.class);
    Root<ProyectoProyectoSge> subRoot = subquery.correlate(root);
    Join<ProyectoProyectoSge, Proyecto> joinProyecto = subRoot.join(ProyectoProyectoSge_.proyecto);

    Predicate predicate = null;

    if (operator.equals(RSQLOperators.GREATER_THAN)) {
      predicate = cb.greaterThan(joinProyecto.get(singularAttribute), fecha);
    } else if (operator.equals(RSQLOperators.GREATER_THAN_OR_EQUAL)) {
      predicate = cb.greaterThanOrEqualTo(joinProyecto.get(singularAttribute), fecha);
    } else if (operator.equals(RSQLOperators.LESS_THAN)) {
      predicate = cb.lessThan(joinProyecto.get(singularAttribute), fecha);
    } else if (operator.equals(RSQLOperators.LESS_THAN_OR_EQUAL)) {
      predicate = cb.lessThanOrEqualTo(joinProyecto.get(singularAttribute), fecha);
    }

    subquery.select(joinProyecto)
        .where(predicate);

    return cb.exists(subquery);
  }

  private Predicate buildByNombreProyecto(ComparisonNode node, Root<ProyectoProyectoSge> root, CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.IGNORE_CASE_LIKE);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String nombreProyecto = node.getArguments().get(0);

    Subquery<Proyecto> subquery = query.subquery(Proyecto.class);
    Root<ProyectoProyectoSge> subRoot = subquery.correlate(root);
    Join<ProyectoProyectoSge, Proyecto> joinProyecto = subRoot.join(ProyectoProyectoSge_.proyecto);
    Join<Proyecto, ProyectoTitulo> joinProyectoTitulo = joinProyecto.join(Proyecto_.titulo);

    subquery.select(joinProyecto)
        .where(cb.like(cb.lower(joinProyectoTitulo.get(ProyectoTitulo_.value)),
            LIKE_WILDCARD_PERCENT + nombreProyecto.toLowerCase() + LIKE_WILDCARD_PERCENT));

    return cb.exists(subquery);
  }

  private Predicate buildByResponsableProyecto(ComparisonNode node, Root<ProyectoProyectoSge> root,
      CriteriaQuery<?> query, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String personaRef = node.getArguments().get(0);
    Instant fechaActual = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).toInstant();

    Subquery<Proyecto> subquery = query.subquery(Proyecto.class);
    Root<ProyectoProyectoSge> subRoot = subquery.correlate(root);
    Join<ProyectoProyectoSge, Proyecto> joinProyecto = subRoot.join(ProyectoProyectoSge_.proyecto);
    ListJoin<Proyecto, ProyectoEquipo> joinEquipos = joinProyecto.join(Proyecto_.equipo);

    Predicate personaRefEquals = cb.equal(joinEquipos.get(ProyectoEquipo_.personaRef), personaRef);
    Predicate rolPrincipal = cb.equal(joinEquipos.get(ProyectoEquipo_.rolProyecto).get(RolProyecto_.rolPrincipal),
        true);
    Predicate greaterThanFechaInicio = cb.lessThanOrEqualTo(joinEquipos.get(ProyectoEquipo_.fechaInicio), fechaActual);
    Predicate lowerThanFechaFin = cb.or(cb.isNull(joinEquipos.get(ProyectoEquipo_.fechaFin)),
        cb.greaterThanOrEqualTo(joinEquipos.get(ProyectoEquipo_.fechaFin), fechaActual));

    Predicate fechaLowerThanFechaInicioGrupo = cb.greaterThan(
        joinEquipos.get(ProyectoEquipo_.proyecto).get(Proyecto_.fechaInicio), fechaActual);
    Predicate fechaGreaterThanFechaFinGrupo = cb.lessThan(
        joinEquipos.get(ProyectoEquipo_.proyecto).get(Proyecto_.fechaFin), fechaActual);

    subquery.select(joinProyecto)
        .where(cb.and(
            personaRefEquals,
            rolPrincipal,
            cb.or(fechaLowerThanFechaInicioGrupo, greaterThanFechaInicio),
            cb.or(fechaGreaterThanFechaFinGrupo, lowerThanFechaFin)));

    return cb.exists(subquery);
  }

  private Predicate buildByCodigoExterno(ComparisonNode node, Root<ProyectoProyectoSge> root, CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String codigoExterno = node.getArguments().get(0);

    Subquery<Proyecto> subquery = query.subquery(Proyecto.class);
    Root<ProyectoProyectoSge> subRoot = subquery.correlate(root);
    Join<ProyectoProyectoSge, Proyecto> joinProyecto = subRoot.join(ProyectoProyectoSge_.proyecto);

    subquery.select(joinProyecto)
        .where(cb.equal(joinProyecto.get(Proyecto_.codigoExterno), codigoExterno));

    return cb.exists(subquery);
  }

  private Predicate buildByCodigoInterno(ComparisonNode node, Root<ProyectoProyectoSge> root, CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String codigoInterno = node.getArguments().get(0);

    Subquery<Proyecto> subquery = query.subquery(Proyecto.class);
    Root<ProyectoProyectoSge> subRoot = subquery.correlate(root);
    Join<ProyectoProyectoSge, Proyecto> joinProyecto = subRoot.join(ProyectoProyectoSge_.proyecto);

    subquery.select(joinProyecto)
        .where(cb.equal(joinProyecto.get(Proyecto_.codigoInterno), codigoInterno));

    return cb.exists(subquery);
  }

  private Predicate buildByConvocatoria(ComparisonNode node, Root<ProyectoProyectoSge> root,
      CriteriaQuery<?> query, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.IGNORE_CASE_LIKE);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String convocatoriaId = node.getArguments().get(0);

    Subquery<Proyecto> subquery = query.subquery(Proyecto.class);
    Root<ProyectoProyectoSge> subRoot = subquery.correlate(root);
    Join<ProyectoProyectoSge, Proyecto> joinProyecto = subRoot.join(ProyectoProyectoSge_.proyecto);

    subquery.select(joinProyecto)
        .where(cb.equal(joinProyecto.get(Proyecto_.convocatoriaId), convocatoriaId));

    return cb.exists(subquery);
  }

  private Predicate buildByTituloConvocatoria(ComparisonNode node, Root<ProyectoProyectoSge> root,
      CriteriaQuery<?> query, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.IGNORE_CASE_LIKE);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String tituloConvocatoria = node.getArguments().get(0);

    Subquery<Proyecto> subquery = query.subquery(Proyecto.class);
    Root<ProyectoProyectoSge> subRoot = subquery.correlate(root);
    Join<ProyectoProyectoSge, Proyecto> joinProyecto = subRoot.join(ProyectoProyectoSge_.proyecto);
    Join<Proyecto, Convocatoria> joinConvocatoria = joinProyecto.join(Proyecto_.convocatoria);

    subquery.select(joinProyecto)
        .where(cb.like(cb.lower(joinConvocatoria.join(Convocatoria_.titulo).get(ConvocatoriaTitulo_.value)),
            LIKE_WILDCARD_PERCENT + tituloConvocatoria.toLowerCase() + LIKE_WILDCARD_PERCENT));

    return cb.exists(subquery);
  }

  private Predicate buildByImporteConcedido(ComparisonNode node, Root<ProyectoProyectoSge> root, CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String importeConcedidoArgument = node.getArguments().get(0);
    BigDecimal importeConcedido = new BigDecimal(importeConcedidoArgument);

    Subquery<Proyecto> subquery = query.subquery(Proyecto.class);
    Root<ProyectoProyectoSge> subRoot = subquery.correlate(root);
    Join<ProyectoProyectoSge, Proyecto> joinProyecto = subRoot.join(ProyectoProyectoSge_.proyecto);

    subquery.select(joinProyecto)
        .where(cb.equal(joinProyecto.get(Proyecto_.importeConcedido), importeConcedido));

    return cb.exists(subquery);
  }

  private Predicate buildByImporteConcedidoCostesIndirectos(ComparisonNode node, Root<ProyectoProyectoSge> root,
      CriteriaQuery<?> query, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String importeConcedidoCostesIndirectosArgument = node.getArguments().get(0);
    BigDecimal importeConcedidoCostesIndirectos = new BigDecimal(importeConcedidoCostesIndirectosArgument);

    Subquery<Proyecto> subquery = query.subquery(Proyecto.class);
    Root<ProyectoProyectoSge> subRoot = subquery.correlate(root);
    Join<ProyectoProyectoSge, Proyecto> joinProyecto = subRoot.join(ProyectoProyectoSge_.proyecto);

    subquery.select(joinProyecto)
        .where(
            cb.equal(joinProyecto.get(Proyecto_.importeConcedidoCostesIndirectos), importeConcedidoCostesIndirectos));

    return cb.exists(subquery);
  }

  private Predicate buildByEntidadConvocante(ComparisonNode node, Root<ProyectoProyectoSge> root,
      CriteriaQuery<?> query, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String entidadConvocanteRef = node.getArguments().get(0);

    Subquery<Proyecto> subquery = query.subquery(Proyecto.class);
    Root<ProyectoProyectoSge> subRoot = subquery.correlate(root);
    Join<ProyectoProyectoSge, Proyecto> joinProyecto = subRoot.join(ProyectoProyectoSge_.proyecto);
    Join<Proyecto, ProyectoEntidadConvocante> joinEntidadConvocante = joinProyecto.join(Proyecto_.entidadesConvocantes);

    subquery.select(joinProyecto)
        .where(cb.equal(joinEntidadConvocante.get(ProyectoEntidadConvocante_.entidadRef), entidadConvocanteRef));

    return cb.exists(subquery);
  }

  private Predicate buildByEntidadFinanciadora(ComparisonNode node, Root<ProyectoProyectoSge> root,
      CriteriaQuery<?> query, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String entidadFinanciadoraRef = node.getArguments().get(0);

    Subquery<Proyecto> subquery = query.subquery(Proyecto.class);
    Root<ProyectoProyectoSge> subRoot = subquery.correlate(root);
    Join<ProyectoProyectoSge, Proyecto> joinProyecto = subRoot.join(ProyectoProyectoSge_.proyecto);
    Join<Proyecto, ProyectoEntidadFinanciadora> joinEntidadFinanciadora = joinProyecto
        .join(Proyecto_.entidadesFinanciadoras);

    subquery.select(joinProyecto)
        .where(cb.equal(joinEntidadFinanciadora.get(ProyectoEntidadFinanciadora_.entidadRef), entidadFinanciadoraRef));

    return cb.exists(subquery);
  }
}
