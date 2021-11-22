package org.crue.hercules.sgi.pii.repository.specification;

import org.crue.hercules.sgi.pii.model.SectorLicenciado;
import org.crue.hercules.sgi.pii.model.SectorLicenciado_;
import org.springframework.data.jpa.domain.Specification;

public class SectorLicenciadoSpecifications {

  /**
   * {@link SectorLicenciado} del Contrato con el id indicado.
   * 
   * @param contratoRef identificador del Contrato.
   * @return specification para obtener los {@link SectorLicenciado} del Contrato
   *         con el identificador indicado.
   */
  public static Specification<SectorLicenciado> byContratoRef(String contratoRef) {
    return (root, query, cb) -> {
      return cb.equal(root.get(SectorLicenciado_.contratoRef), contratoRef);
    };
  }
}
