package org.crue.hercules.sgi.pii.repository.predicate;

import java.time.Instant;
import java.util.regex.Pattern;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.framework.data.jpa.domain.Auditable_;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.InvencionAreaConocimiento;
import org.crue.hercules.sgi.pii.model.InvencionInventor;
import org.crue.hercules.sgi.pii.model.InvencionPalabraClave;
import org.crue.hercules.sgi.pii.model.InvencionSectorAplicacion;
import org.crue.hercules.sgi.pii.model.Invencion_;
import org.crue.hercules.sgi.pii.model.PeriodoTitularidad;
import org.crue.hercules.sgi.pii.model.PeriodoTitularidadTitular;
import org.crue.hercules.sgi.pii.model.PeriodoTitularidad_;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion;
import org.crue.hercules.sgi.pii.model.TipoProteccion;
import org.crue.hercules.sgi.pii.model.TipoProteccion_;
import org.crue.hercules.sgi.pii.util.PredicateResolverUtil;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

public class InvencionPredicateResolver implements SgiRSQLPredicateResolver<Invencion> {
  private static final Pattern POSITIVE_NUMBER_PATTERN = Pattern.compile("^\\d+$");

  private enum Property {
    /* Tipo proteccion */
    TIPO_PROTECCION("tipoProteccion"),
    /* Fecha modificación */
    FECHA_MODIFICACION("fechaModificacion");

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

  private InvencionPredicateResolver() {
  }

  public static InvencionPredicateResolver getInstance() {
    return new InvencionPredicateResolver();
  }

  private Predicate buildByTipoProteccion(ComparisonNode node, Root<Invencion> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String argument = node.getArguments().get(0);
    if (!isPositiveNumber(argument)) {
      throw new IllegalArgumentException("Argument should be a positive integer for " + node.getSelector());
    }
    Long tipoProteccionId = Long.parseLong(argument);

    Join<Invencion, TipoProteccion> joinTipoProteccion = root.join(Invencion_.tipoProteccion);
    Join<TipoProteccion, TipoProteccion> joinSubtipoProteccion = joinTipoProteccion.join(TipoProteccion_.padre,
        JoinType.LEFT);

    return cb.and(
        cb.or(
            cb.equal(joinTipoProteccion.get(TipoProteccion_.id), tipoProteccionId),
            cb.equal(joinSubtipoProteccion.get(TipoProteccion_.id), tipoProteccionId)));
  }

  private Predicate buildByFechaModificacion(ComparisonNode node, Root<Invencion> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.GREATER_THAN_OR_EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String fechaModificacionArgument = node.getArguments().get(0);
    Instant fechaModificacion = Instant.parse(fechaModificacionArgument);
    ListJoin<Invencion, InvencionSectorAplicacion> joinInvencionSectoresAplicacion = root
        .join(Invencion_.sectoresAplicacion, JoinType.LEFT);
    ListJoin<Invencion, InvencionAreaConocimiento> joinInvencionAreaConocimiento = root
        .join(Invencion_.areasConocimiento, JoinType.LEFT);
    ListJoin<Invencion, InvencionPalabraClave> joinInvencionPalabraClave = root.join(Invencion_.palabrasClave,
        JoinType.LEFT);
    ListJoin<Invencion, InvencionInventor> joinInvencionInventor = root.join(Invencion_.inventores,
        JoinType.LEFT);
    ListJoin<Invencion, PeriodoTitularidad> joinPeriodoTitularidad = root.join(Invencion_.periodosTitularidad,
        JoinType.LEFT);
    ListJoin<PeriodoTitularidad, PeriodoTitularidadTitular> joinPeriodoTitularidadTitular = joinPeriodoTitularidad.join(
        PeriodoTitularidad_.titulares, JoinType.LEFT);
    ListJoin<Invencion, SolicitudProteccion> joinSolicitudProteccion = root.join(Invencion_.solicitudesProteccion,
        JoinType.LEFT);

    return cb.or(cb.greaterThanOrEqualTo(root.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinInvencionSectoresAplicacion.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinInvencionAreaConocimiento.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinInvencionPalabraClave.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinInvencionInventor.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinPeriodoTitularidad.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinPeriodoTitularidadTitular.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinSolicitudProteccion.get(Auditable_.lastModifiedDate), fechaModificacion));
  }

  public boolean isPositiveNumber(String value) {
    if (value == null) {
      return false;
    }
    return POSITIVE_NUMBER_PATTERN.matcher(value).matches();
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<Invencion> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    Property property = Property.fromCode(node.getSelector());
    if (property == null) {
      return null;
    }

    switch (property) {
      case TIPO_PROTECCION:
        return buildByTipoProteccion(node, root, criteriaBuilder);
      case FECHA_MODIFICACION:
        return buildByFechaModificacion(node, root, criteriaBuilder);
      default:
        return null;
    }
  }
}
