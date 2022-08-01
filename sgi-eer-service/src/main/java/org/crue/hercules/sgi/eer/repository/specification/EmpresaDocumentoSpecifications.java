package org.crue.hercules.sgi.eer.repository.specification;

import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.model.EmpresaDocumento;
import org.crue.hercules.sgi.eer.model.EmpresaDocumento_;
import org.springframework.data.jpa.domain.Specification;

public class EmpresaDocumentoSpecifications {

  private EmpresaDocumentoSpecifications() {

  }

  /**
   * {@link EmpresaDocumento} con el id de la {@link Empresa} indicada.
   * 
   * @param empresaId Identificador de la {@link Empresa}
   * @return specification para obtener los {@link EmpresaDocumento} con id con el
   *         id de la {@link Empresa} indicada.
   */
  public static Specification<EmpresaDocumento> byEmpresaId(Long empresaId) {
    return (root, query, cb) -> cb.equal(root.get(EmpresaDocumento_.empresaId), empresaId);
  }
}
