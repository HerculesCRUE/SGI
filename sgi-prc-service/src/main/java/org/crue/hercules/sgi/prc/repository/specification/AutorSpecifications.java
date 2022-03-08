package org.crue.hercules.sgi.prc.repository.specification;

import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.Autor_;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.springframework.data.jpa.domain.Specification;

public class AutorSpecifications {

  private AutorSpecifications() {
  }

  /**
   * {@link Autor} de la {@link ProduccionCientifica} con el id indicado.
   * 
   * @param id identificador de la {@link ProduccionCientifica}.
   * @return specification para obtener los {@link Autor} de
   *         la {@link ProduccionCientifica} con el id indicado.
   */
  public static Specification<Autor> byProduccionCientificaId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(Autor_.produccionCientificaId), id);
    };
  }
}
