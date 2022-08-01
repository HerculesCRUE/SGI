package org.crue.hercules.sgi.eer.repository.specification;

import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.model.EmpresaComposicionSociedad;
import org.crue.hercules.sgi.eer.model.EmpresaComposicionSociedad_;
import org.springframework.data.jpa.domain.Specification;

public class EmpresaComposicionSociedadSpecifications {

  private EmpresaComposicionSociedadSpecifications() {
  }

  /**
   * {@link EmpresaComposicionSociedad} del {@link Empresa} con el id indicado.
   * 
   * @param empresaId identificador del {@link Empresa}.
   * @return specification para obtener los {@link EmpresaComposicionSociedad} del
   *         {@link Empresa} con el id indicado.
   */
  public static Specification<EmpresaComposicionSociedad> byEmpresaId(Long empresaId) {
    return (root, query, cb) -> cb.equal(root.get(EmpresaComposicionSociedad_.empresaId), empresaId);
  }

  /**
   * {@link EmpresaComposicionSociedad} cuyo miembro sociedad empresa ref sea la
   * recibida.
   * 
   * @param miembroSociedadEmpresaRef miembro sociedad empresa ref de
   *                                  {@link EmpresaComposicionSociedad}
   * @return specification para obtener los {@link EmpresaComposicionSociedad}
   *         cuyo
   *         miembro sociedad empresa ref sea la recibida.
   */
  public static Specification<EmpresaComposicionSociedad> byMiembroSociedadEmpresaRef(
      String miembroSociedadEmpresaRef) {
    return (root, query, cb) -> cb.equal(root.get(EmpresaComposicionSociedad_.miembroSociedadEmpresaRef),
        miembroSociedadEmpresaRef);
  }

  /**
   * {@link EmpresaComposicionSociedad} cuyo miembro sociedad persona ref sea la
   * recibida.
   * 
   * @param miembroSociedadPersonaRef miembro sociedad persona ref de
   *                                  {@link EmpresaComposicionSociedad}
   * @return specification para obtener los {@link EmpresaComposicionSociedad}
   *         cuyo
   *         miembro sociedad persona ref sea la recibida.
   */
  public static Specification<EmpresaComposicionSociedad> byMiembroSociedadPersonaRef(
      String miembroSociedadPersonaRef) {
    return (root, query, cb) -> cb.equal(root.get(EmpresaComposicionSociedad_.miembroSociedadPersonaRef),
        miembroSociedadPersonaRef);
  }

  /**
   * {@link EmpresaComposicionSociedad} id diferente de
   * {@link EmpresaComposicionSociedad} con el
   * indicado.
   * 
   * @param id identificador de la {@link EmpresaComposicionSociedad}.
   * @return specification para comprobar si el id es diferentes del
   *         {@link EmpresaComposicionSociedad} indicado.
   */
  public static Specification<EmpresaComposicionSociedad> byIdNotEqual(Long id) {
    return (root, query, cb) -> {
      if (id == null) {
        return cb.isTrue(cb.literal(true)); // always true = no filtering
      }
      return cb.equal(root.get(EmpresaComposicionSociedad_.id), id).not();
    };
  }

}
