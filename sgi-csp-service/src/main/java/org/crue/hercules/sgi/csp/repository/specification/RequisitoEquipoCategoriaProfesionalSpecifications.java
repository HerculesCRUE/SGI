package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.crue.hercules.sgi.csp.model.RequisitoEquipoCategoriaProfesional;
import org.crue.hercules.sgi.csp.model.RequisitoEquipoCategoriaProfesional_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequisitoEquipoCategoriaProfesionalSpecifications {

  /**
   * {@link RequisitoEquipoCategoriaProfesional} del {@link RequisitoEquipo} con
   * el id indicado.
   * 
   * @param id identificador del {@link RequisitoEquipo}.
   * @return specification para obtener las
   *         {@link RequisitoEquipoCategoriaProfesional} del
   *         {@link RequisitoEquipo} con el id indicado.
   */
  public static Specification<RequisitoEquipoCategoriaProfesional> byRequisitoEquipoId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(RequisitoEquipoCategoriaProfesional_.requisitoEquipoId), id);
  }

}
