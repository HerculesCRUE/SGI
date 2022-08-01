package org.crue.hercules.sgi.eer.repository.specification;

import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.model.EmpresaAdministracionSociedad;
import org.crue.hercules.sgi.eer.model.EmpresaAdministracionSociedad_;
import org.springframework.data.jpa.domain.Specification;

public class EmpresaAdministracionSociedadSpecifications {

  private EmpresaAdministracionSociedadSpecifications() {
  }

  /**
   * {@link EmpresaAdministracionSociedad} del {@link Empresa} con el id indicado.
   * 
   * @param empresaId identificador del {@link Empresa}.
   * @return specification para obtener los {@link EmpresaAdministracionSociedad}
   *         del
   *         {@link Empresa} con el id indicado.
   */
  public static Specification<EmpresaAdministracionSociedad> byEmpresaId(Long empresaId) {
    return (root, query, cb) -> cb.equal(root.get(EmpresaAdministracionSociedad_.empresaId), empresaId);
  }

  /**
   * {@link EmpresaAdministracionSociedad} cuyo miembro sociedad empresa ref sea
   * la
   * recibida.
   * 
   * @param miembroEquipoAdministracionRef miembro sociedad empresa ref de
   *                                       {@link EmpresaAdministracionSociedad}
   * @return specification para obtener los {@link EmpresaAdministracionSociedad}
   *         cuyo
   *         miembro sociedad empresa ref sea la recibida.
   */
  public static Specification<EmpresaAdministracionSociedad> byMiembroSociedadEmpresaRef(
      String miembroEquipoAdministracionRef) {
    return (root, query, cb) -> cb.equal(root.get(EmpresaAdministracionSociedad_.miembroEquipoAdministracionRef),
        miembroEquipoAdministracionRef);
  }

  /**
   * {@link EmpresaAdministracionSociedad} id diferente de
   * {@link EmpresaAdministracionSociedad} con el
   * indicado.
   * 
   * @param id identificador de la {@link EmpresaAdministracionSociedad}.
   * @return specification para comprobar si el id es diferentes del
   *         {@link EmpresaAdministracionSociedad} indicado.
   */
  public static Specification<EmpresaAdministracionSociedad> byIdNotEqual(Long id) {
    return (root, query, cb) -> {
      if (id == null) {
        return cb.isTrue(cb.literal(true)); // always true = no filtering
      }
      return cb.equal(root.get(EmpresaAdministracionSociedad_.id), id).not();
    };
  }

}
