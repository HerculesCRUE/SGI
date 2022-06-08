package org.crue.hercules.sgi.prc.repository.specification;

import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.AutorGrupo;
import org.crue.hercules.sgi.prc.model.AutorGrupo_;
import org.crue.hercules.sgi.prc.model.Autor_;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
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
    return (root, query, cb) -> cb.equal(root.get(AutorGrupo_.autorId), id);
  }

  /**
   * {@link AutorGrupo} de la {@link ProduccionCientifica} con el id indicado.
   * 
   * @param produccionCientificaId identificador de una
   *                               {@link ProduccionCientifica}
   * @return specification para obtener los {@link AutorGrupo} de la
   *         {@link ProduccionCientifica} con el id indicado
   */
  public static Specification<AutorGrupo> byProduccionCientificaId(
      Long produccionCientificaId) {
    return (root, query, cb) -> {
      Join<AutorGrupo, Autor> joinAutor = root.join(AutorGrupo_.autor, JoinType.LEFT);

      return cb.equal(joinAutor.get(Autor_.produccionCientificaId), produccionCientificaId);
    };
  }

  /**
   * {@link AutorGrupo} de la {@link ProduccionCientifica} con el id indicado y
   * que pertenezcan a uno de los grupos indicados.
   * 
   * @param produccionCientificaId identificador de una
   *                               {@link ProduccionCientifica}
   * @param gruposRef              lista de ids de grupos
   * @return specification para obtener los {@link AutorGrupo} de la
   *         {@link ProduccionCientifica} con el id indicado y que pertenezcan a
   *         uno de los grupos indicados
   */
  public static Specification<AutorGrupo> byProduccionCientificaIdAndInGruposRef(Long produccionCientificaId,
      List<Long> gruposRef) {
    return (root, query, cb) -> {
      Join<AutorGrupo, Autor> joinAutor = root.join(AutorGrupo_.autor, JoinType.LEFT);

      return cb.and(
          root.get(AutorGrupo_.grupoRef).in(gruposRef),
          cb.equal(joinAutor.get(Autor_.produccionCientificaId), produccionCientificaId));
    };
  }
}
