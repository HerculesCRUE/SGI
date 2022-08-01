package org.crue.hercules.sgi.eer.repository.specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.model.EmpresaEquipoEmprendedor;
import org.crue.hercules.sgi.eer.model.EmpresaEquipoEmprendedor_;
import org.crue.hercules.sgi.eer.model.Empresa_;
import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.springframework.data.jpa.domain.Specification;

public class EmpresaSpecifications {

  private EmpresaSpecifications() {
  }

  /**
   * {@link Empresa} activos.
   * 
   * @return specification para obtener los {@link Empresa} activos.
   */
  public static Specification<Empresa> activos() {
    return (root, query, cb) -> cb.isTrue(root.get(Activable_.activo));
  }

  /**
   * {@link Empresa} distintos.
   * 
   * @return specification para obtener las entidades {@link Empresa} sin
   *         repetidos.
   */
  public static Specification<Empresa> distinct() {
    return (root, query, cb) -> {
      query.distinct(true);
      return cb.isTrue(cb.literal(true));
    };
  }

  /**
   * {@link Empresa} con el id indicado.
   * 
   * @param empresaId Identificador del {@link Empresa}
   * @return specification para obtener los {@link Empresa} con id distinto del
   *         indicado.
   */
  public static Specification<Empresa> byId(Long empresaId) {
    return (root, query, cb) -> cb.equal(root.get(Empresa_.id), empresaId);
  }

  /**
   * {@link Empresa} con un id distinto del indicado.
   * 
   * @param empresaId Identificador del {@link Empresa}
   * @return specification para obtener los {@link Empresa} con id distinto del
   *         indicado.
   */
  public static Specification<Empresa> byIdNotEqual(Long empresaId) {
    return (root, query, cb) -> byId(empresaId).toPredicate(root, query, cb).not();
  }

  /**
   * {@link Empresa} para los que la persona esta en su
   * {@link EmpresaEquipoEmprendedor}
   * 
   * @param personaRef Identificador de la persona
   * @return specification para obtener los {@link Empresa} para los que la
   *         persona
   *         esta en su {@link EmpresaEquipoEmprendedor}
   */
  public static Specification<Empresa> byMiembroEquipoInEmpresaEquipoEmprendedor(String personaRef) {
    return (root, query, cb) -> {
      Join<Empresa, EmpresaEquipoEmprendedor> joinEmpresaEquipoEmprendedor = root
          .join(Empresa_.miembrosEquipoEmprendedor, JoinType.LEFT);

      return cb.equal(joinEmpresaEquipoEmprendedor.get(EmpresaEquipoEmprendedor_.miembroEquipoRef), personaRef);
    };
  }

}
