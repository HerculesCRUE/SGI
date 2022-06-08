package org.crue.hercules.sgi.prc.repository.specification;

import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica_;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.springframework.data.jpa.domain.Specification;

public class CampoProduccionCientificaSpecifications {

  private CampoProduccionCientificaSpecifications() {

  }

  /**
   * {@link CampoProduccionCientifica} de la {@link ProduccionCientifica} con el
   * id indicado.
   * 
   * @param id identificador de la {@link ProduccionCientifica}.
   * @return specification para obtener los {@link CampoProduccionCientifica} de
   *         la {@link ProduccionCientifica} con el id indicado.
   */
  public static Specification<CampoProduccionCientifica> byProduccionCientificaId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(CampoProduccionCientifica_.produccionCientificaId), id);
  }
}
