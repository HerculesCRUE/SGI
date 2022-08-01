package org.crue.hercules.sgi.eer.repository.specification;

import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.model.EmpresaEquipoEmprendedor;
import org.crue.hercules.sgi.eer.model.EmpresaEquipoEmprendedor_;
import org.springframework.data.jpa.domain.Specification;

public class EmpresaEquipoEmprendedorSpecifications {

  private EmpresaEquipoEmprendedorSpecifications() {
  }

  /**
   * {@link EmpresaEquipoEmprendedor} del {@link Empresa} con el id indicado.
   * 
   * @param empresaId identificador del {@link Empresa}.
   * @return specification para obtener los {@link EmpresaEquipoEmprendedor} del
   *         {@link Empresa} con el id indicado.
   */
  public static Specification<EmpresaEquipoEmprendedor> byEmpresaId(Long empresaId) {
    return (root, query, cb) -> cb.equal(root.get(EmpresaEquipoEmprendedor_.empresaId), empresaId);
  }

  /**
   * {@link EmpresaEquipoEmprendedor} cuyo miembro equipo ref sea la recibida.
   * 
   * @param miembroEquipoRef miembro equipo ref de
   *                         {@link EmpresaEquipoEmprendedor}
   * @return specification para obtener los {@link EmpresaEquipoEmprendedor} cuyo
   *         miembro equipo ref sea la recibida.
   */
  public static Specification<EmpresaEquipoEmprendedor> byMiembroEquipoRef(String miembroEquipoRef) {
    return (root, query, cb) -> cb.equal(root.get(EmpresaEquipoEmprendedor_.miembroEquipoRef), miembroEquipoRef);
  }

  /**
   * {@link EmpresaEquipoEmprendedor} id diferente de
   * {@link EmpresaEquipoEmprendedor} con el
   * indicado.
   * 
   * @param id identificador de la {@link EmpresaEquipoEmprendedor}.
   * @return specification para comprobar si el id es diferentes del
   *         {@link EmpresaEquipoEmprendedor} indicado.
   */
  public static Specification<EmpresaEquipoEmprendedor> byIdNotEqual(Long id) {
    return (root, query, cb) -> {
      if (id == null) {
        return cb.isTrue(cb.literal(true)); // always true = no filtering
      }
      return cb.equal(root.get(EmpresaEquipoEmprendedor_.id), id).not();
    };
  }

}
