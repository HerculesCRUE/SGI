package org.crue.hercules.sgi.prc.repository.specification;

import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.Proyecto;
import org.crue.hercules.sgi.prc.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoSpecifications {

  private ProyectoSpecifications() {
  }

  /**
   * {@link Proyecto} de la {@link ProduccionCientifica} con el id indicado.
   * 
   * @param id identificador de la {@link ProduccionCientifica}.
   * @return specification para obtener las {@link Proyecto} de
   *         la {@link ProduccionCientifica} con el id indicado.
   */
  public static Specification<Proyecto> byProduccionCientificaId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(Proyecto_.produccionCientificaId), id);
  }
}
