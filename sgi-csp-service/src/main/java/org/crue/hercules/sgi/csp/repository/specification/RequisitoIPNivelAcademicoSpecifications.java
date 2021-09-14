package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.model.RequisitoIPNivelAcademico;
import org.crue.hercules.sgi.csp.model.RequisitoIPNivelAcademico_;
import org.crue.hercules.sgi.csp.model.RequisitoIP_;
import org.springframework.data.jpa.domain.Specification;

public class RequisitoIPNivelAcademicoSpecifications {

  /**
   * {@link RequisitoIPNivelAcademico} del {@link RequisitoIP} con el id indicado.
   * 
   * @param id identificador del {@link RequisitoIP}.
   * @return specification para obtener los {@link RequisitoIPNivelAcademico} del
   *         {@link RequisitoIP} con el id indicado.
   */
  public static Specification<RequisitoIPNivelAcademico> byRequisitoIPId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(RequisitoIPNivelAcademico_.requisitoIPId), id);
    };
  }

  /**
   * {@link RequisitoIPNivelAcademico} de la {@link Convocatoria} con el id
   * indicado.
   * 
   * @param id identificador de la {@link Convocatoria}.
   * @return specification para obtener los {@link RequisitoIPNivelAcademico} de
   *         la {@link Convocatoria} con el id indicado.
   */
  public static Specification<RequisitoIPNivelAcademico> byConvocatoriaId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(RequisitoIPNivelAcademico_.requisitoIP).get(RequisitoIP_.convocatoria), id);
    };
  }

}
