package org.crue.hercules.sgi.prc.repository.specification;

import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.model.ValorCampo_;
import org.springframework.data.jpa.domain.Specification;

public class ValorCampoSpecifications {

  private ValorCampoSpecifications() {
  }

  /**
   * {@link ValorCampo} de la {@link CampoProduccionCientifica} con el id
   * indicado.
   * 
   * @param id identificador de la {@link CampoProduccionCientifica}.
   * @return specification para obtener las {@link ValorCampo} de
   *         la {@link CampoProduccionCientifica} con el id indicado.
   */
  public static Specification<ValorCampo> byCampoProduccionCientificaId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(ValorCampo_.campoProduccionCientificaId), id);
  }
}
