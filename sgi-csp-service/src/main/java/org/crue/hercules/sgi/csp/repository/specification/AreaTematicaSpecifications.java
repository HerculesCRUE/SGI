package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.crue.hercules.sgi.csp.model.AreaTematica_;
import org.springframework.data.jpa.domain.Specification;

public class AreaTematicaSpecifications {

  /**
   * {@link AreaTematica} activos.
   * 
   * @return specification para obtener los {@link AreaTematica} activos.
   */
  public static Specification<AreaTematica> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(AreaTematica_.activo), Boolean.TRUE);
    };
  }

  /**
   * {@link AreaTematica} con padre null (grupos).
   * 
   * @return specification para obtener los grupos.
   */
  public static Specification<AreaTematica> grupos() {
    return (root, query, cb) -> {
      return cb.isNull(root.get(AreaTematica_.padre));
    };
  }

  /**
   * {@link AreaTematica} con el nombre indicado.
   * 
   * @param nombre nombre del areaTematica
   * @return specification para obtener los grupos.
   */
  public static Specification<AreaTematica> byNombre(String nombre) {
    return (root, query, cb) -> {
      return cb.equal(root.get(AreaTematica_.nombre), nombre);
    };
  }

  /**
   * {@link AreaTematica} con padre null (grupos) con el nombre indicado y
   * activos.
   * 
   * @param nombre                nombre del areaTematica.
   * @param areaTematicaIdExcluir Identificador del {@link AreaTematica} que se
   *                              excluye de la busqueda.
   * @return specification para obtener los grupos.
   */
  public static Specification<AreaTematica> gruposByNombre(String nombre, Long areaTematicaIdExcluir) {
    return Specification.where(grupos()).and(byNombre(nombre)).and(byIdNotEqual(areaTematicaIdExcluir)).and(activos());
  }

  /**
   * {@link AreaTematica} activos con padre con el id indicado.
   * 
   * @param areaTematicaId Identifiacdor del {@link AreaTematica}.
   * @return specification para obtener los grupos.
   */
  public static Specification<AreaTematica> hijos(Long areaTematicaId) {
    Specification<AreaTematica> hijos = (root, query, cb) -> {
      return cb.equal(root.get(AreaTematica_.padre).get(AreaTematica_.id), areaTematicaId);
    };

    return Specification.where(hijos).and(activos());
  }

  /**
   * {@link AreaTematica} con id diferente del indicado.
   * 
   * @param id identificador de la {@link AreaTematica}.
   * @return specification para obtener los {@link AreaTematica} con id diferente
   *         del indicado.
   */
  private static Specification<AreaTematica> byIdNotEqual(Long id) {
    return (root, query, cb) -> {
      if (id == null) {
        return cb.isTrue(cb.literal(true)); // always true = no filtering
      }
      return cb.equal(root.get(AreaTematica_.id), id).not();
    };
  }

}