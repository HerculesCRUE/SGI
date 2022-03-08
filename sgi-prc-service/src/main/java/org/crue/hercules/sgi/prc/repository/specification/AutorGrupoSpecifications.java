package org.crue.hercules.sgi.prc.repository.specification;

import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.AutorGrupo;
import org.crue.hercules.sgi.prc.model.AutorGrupo_;
import org.springframework.data.jpa.domain.Specification;

public class AutorGrupoSpecifications {

  private AutorGrupoSpecifications() {
  }

  /**
   * {@link AutorGrupo} del {@link Autor} con el id indicado.
   * 
   * @param id identificador del {@link Autor}.
   * @return specification para obtener los {@link AutorGrupo} del
   *         {@link Autor} con el id indicado.
   */
  public static Specification<AutorGrupo> byAutorId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(AutorGrupo_.autorId), id);
    };
  }
}
