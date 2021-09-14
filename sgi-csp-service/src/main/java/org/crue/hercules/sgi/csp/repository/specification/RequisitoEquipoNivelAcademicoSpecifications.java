package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.crue.hercules.sgi.csp.model.RequisitoEquipoNivelAcademico;
import org.crue.hercules.sgi.csp.model.RequisitoEquipoNivelAcademico_;
import org.springframework.data.jpa.domain.Specification;

public class RequisitoEquipoNivelAcademicoSpecifications {

  /**
   * {@link RequisitoEquipoNivelAcademico} del {@link RequisitoEquipo} con el id
   * indicado.
   * 
   * @param id identificador del {@link RequisitoEquipo}.
   * @return specification para obtener los {@link RequisitoEquipoNivelAcademico}
   *         del {@link RequisitoEquipo} con el id indicado.
   */
  public static Specification<RequisitoEquipoNivelAcademico> byRequisitoEquipoId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(RequisitoEquipoNivelAcademico_.requisitoEquipoId), id);
    };
  }

}
