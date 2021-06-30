package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Programa_;
import org.springframework.data.jpa.domain.Specification;

public class ProgramaSpecifications {

  /**
   * {@link Programa} activos.
   * 
   * @return specification para obtener los {@link Programa} activos.
   */
  public static Specification<Programa> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Programa_.activo), Boolean.TRUE);
    };
  }

  /**
   * {@link Programa} con padre null (planes).
   * 
   * @return specification para obtener los planes.
   */
  public static Specification<Programa> planes() {
    return (root, query, cb) -> {
      return cb.isNull(root.get(Programa_.padre));
    };
  }

  /**
   * {@link Programa} con el nombre indicado.
   * 
   * @param nombre nombre del programa
   * @return specification para obtener los planes.
   */
  public static Specification<Programa> byNombre(String nombre) {
    return (root, query, cb) -> {
      return cb.equal(root.get(Programa_.nombre), nombre);
    };
  }

  /**
   * {@link Programa} con padre null (planes) con el nombre indicado y activos.
   * 
   * @param nombre            nombre del programa.
   * @param programaIdExcluir Identificador del {@link Programa} que se excluye de
   *                          la busqueda.
   * @return specification para obtener los planes.
   */
  public static Specification<Programa> planesByNombre(String nombre, Long programaIdExcluir) {
    return Specification.where(planes()).and(byNombre(nombre)).and(byIdNotEqual(programaIdExcluir)).and(activos());
  }

  /**
   * {@link Programa} activos con padre con el id indicado.
   * 
   * @param programaId Identifiacdor del {@link Programa}.
   * @return specification para obtener los planes.
   */
  public static Specification<Programa> hijos(Long programaId) {
    Specification<Programa> hijos = (root, query, cb) -> {
      return cb.equal(root.get(Programa_.padre).get(Programa_.id), programaId);
    };

    return Specification.where(hijos).and(activos());
  }

  /**
   * {@link Programa} con id diferente del indicado.
   * 
   * @param id identificador de la {@link Programa}.
   * @return specification para obtener los {@link Programa} con id diferente del
   *         indicado.
   */
  private static Specification<Programa> byIdNotEqual(Long id) {
    return (root, query, cb) -> {
      if (id == null) {
        return cb.isTrue(cb.literal(true)); // always true = no filtering
      }
      return cb.equal(root.get(Programa_.id), id).not();
    };
  }

}