package org.crue.hercules.sgi.csp.repository.specification;

import javax.persistence.criteria.JoinType;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.model.RequisitoIPCategoriaProfesional;
import org.crue.hercules.sgi.csp.model.RequisitoIPCategoriaProfesional_;
import org.crue.hercules.sgi.csp.model.RequisitoIP_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequisitoIPCategoriaProfesionalSpecifications {

  /**
   * {@link RequisitoIPCategoriaProfesional} con el id indicado.
   * 
   * @param id identificador del {@link RequisitoIPCategoriaProfesional}.
   * @return specification para obtener el {@link RequisitoIPCategoriaProfesional}
   *         con el id indicado.
   */
  public static Specification<RequisitoIPCategoriaProfesional> byId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(RequisitoIPCategoriaProfesional_.id), id);
  }

  /**
   * {@link RequisitoIPCategoriaProfesional} del {@link RequisitoIP} con el id
   * indicado.
   * 
   * @param id identificador del {@link RequisitoIP}.
   * @return specification para obtener las
   *         {@link RequisitoIPCategoriaProfesional} del {@link RequisitoIP} con
   *         el id indicado.
   */
  public static Specification<RequisitoIPCategoriaProfesional> byRequisitoIPId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(RequisitoIPCategoriaProfesional_.requisitoIPId), id);
  }

  /**
   * {@link RequisitoIPCategoriaProfesional} de la {@link Convocatoria} con el id
   * indicado.
   * 
   * @param id identificador de la {@link Convocatoria}.
   * @return specification para obtener los
   *         {@link RequisitoIPCategoriaProfesional} de la {@link Convocatoria}
   *         con el id indicado.
   */
  public static Specification<RequisitoIPCategoriaProfesional> byConvocatoriaId(Long id) {
    return (root, query, cb) -> cb
        .equal(root.get(RequisitoIPCategoriaProfesional_.requisitoIP).get(RequisitoIP_.convocatoria), id);
  }

  /**
   * {@link RequisitoIPCategoriaProfesional} con entidades relacionadas.
   * 
   * @return specification para obtener los
   *         {@link RequisitoIPCategoriaProfesional} con entidades relacionadas.
   */
  public static Specification<RequisitoIPCategoriaProfesional> withRelatedEntities() {
    return (root, query, cb) -> {
      root.join(RequisitoIPCategoriaProfesional_.categoriasProfesionalesSolicitudRrhh, JoinType.INNER);

      return cb.isTrue(cb.literal(true));
    };
  }

}
