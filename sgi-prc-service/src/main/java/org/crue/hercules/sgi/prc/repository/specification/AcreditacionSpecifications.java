package org.crue.hercules.sgi.prc.repository.specification;

import org.crue.hercules.sgi.prc.model.Acreditacion;
import org.crue.hercules.sgi.prc.model.Acreditacion_;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.springframework.data.jpa.domain.Specification;

public class AcreditacionSpecifications {

  private AcreditacionSpecifications() {
  }

  /**
   * {@link Acreditacion} de la {@link ProduccionCientifica} con el id indicado.
   * 
   * @param id identificador de la {@link ProduccionCientifica}.
   * @return specification para obtener los {@link Acreditacion} de
   *         la {@link ProduccionCientifica} con el id indicado.
   */
  public static Specification<Acreditacion> byProduccionCientificaId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(Acreditacion_.produccionCientificaId), id);
  }
}
