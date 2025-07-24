package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Programa_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProgramaSpecifications {

  /**
   * {@link Programa} activos.
   * 
   * @return specification para obtener los {@link Programa} activos.
   */
  public static Specification<Programa> activos() {
    return (root, query, cb) -> cb.isTrue(root.get(Programa_.activo));
  }

  /**
   * {@link Programa} con padre null (planes).
   * 
   * @return specification para obtener los planes.
   */
  public static Specification<Programa> planes() {
    return (root, query, cb) -> cb.isNull(root.get(Programa_.padre));
  }

  /**
   * {@link Programa} activos con padre con el id indicado.
   * 
   * @param programaId Identifiacdor del {@link Programa}.
   * @return specification para obtener los planes.
   */
  public static Specification<Programa> hijos(Long programaId) {
    Specification<Programa> hijos = (root, query, cb) -> cb.equal(root.get(Programa_.padre).get(Programa_.id),
        programaId);

    return Specification.where(hijos).and(activos());
  }

}
