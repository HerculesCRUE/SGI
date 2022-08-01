package org.crue.hercules.sgi.eer.repository.predicate;

import java.time.Instant;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.model.EmpresaAdministracionSociedad;
import org.crue.hercules.sgi.eer.model.EmpresaComposicionSociedad;
import org.crue.hercules.sgi.eer.model.EmpresaDocumento;
import org.crue.hercules.sgi.eer.model.EmpresaEquipoEmprendedor;
import org.crue.hercules.sgi.eer.model.Empresa_;
import org.crue.hercules.sgi.eer.util.PredicateResolverUtil;
import org.crue.hercules.sgi.framework.data.jpa.domain.Auditable_;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import io.github.perplexhub.rsql.RSQLOperators;

public class EmpresaPredicateResolver implements SgiRSQLPredicateResolver<Empresa> {

  public enum Property {
    /* Fecha modificación */
    FECHA_MODIFICACION("fechaModificacion");

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

  private EmpresaPredicateResolver() {
  }

  public static EmpresaPredicateResolver getInstance() {
    return new EmpresaPredicateResolver();
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<Empresa> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    Property property = Property.fromCode(node.getSelector());
    if (property != null && property.equals(Property.FECHA_MODIFICACION)) {
      return buildByFechaModificacion(node, root, criteriaBuilder);
    }

    return null;
  }

  private Predicate buildByFechaModificacion(ComparisonNode node, Root<Empresa> root,
      CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.GREATER_THAN_OR_EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String fechaModificacionArgument = node.getArguments().get(0);
    Instant fechaModificacion = Instant.parse(fechaModificacionArgument);
    ListJoin<Empresa, EmpresaAdministracionSociedad> joinAdministradores = root.join(Empresa_.administradores,
        JoinType.LEFT);
    ListJoin<Empresa, EmpresaComposicionSociedad> joinComposicionSociedad = root.join(Empresa_.composicionSociedad,
        JoinType.LEFT);
    ListJoin<Empresa, EmpresaDocumento> joinDocumentos = root.join(Empresa_.documentos, JoinType.LEFT);
    ListJoin<Empresa, EmpresaEquipoEmprendedor> joinEquipo = root.join(Empresa_.miembrosEquipoEmprendedor,
        JoinType.LEFT);

    return cb.or(cb.greaterThanOrEqualTo(root.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinAdministradores.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinComposicionSociedad.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinDocumentos.get(Auditable_.lastModifiedDate), fechaModificacion),
        cb.greaterThanOrEqualTo(joinEquipo.get(Auditable_.lastModifiedDate), fechaModificacion));
  }

}
