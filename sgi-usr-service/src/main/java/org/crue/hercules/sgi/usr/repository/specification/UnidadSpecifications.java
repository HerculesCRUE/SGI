package org.crue.hercules.sgi.usr.repository.specification;

import org.crue.hercules.sgi.usr.model.Unidad;
import org.crue.hercules.sgi.usr.model.Unidad_;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class UnidadSpecifications {

  private UnidadSpecifications() {
  }

  /**
   * {@link Unidad} activos.
   * 
   * @return specification para obtener los {@link Unidad} activos.
   */
  public static Specification<Unidad> activos() {
    return (root, query, cb) -> cb.equal(root.get(Unidad_.activo), Boolean.TRUE);
  }

  /**
   * {@link Unidad} acrónimo.
   * 
   * @param acronimos listado de acrónimos.
   * @return specification para obtener los {@link Unidad} cuyo acrónimo se
   *         encuentre entre los recibidos.
   */
  public static Specification<Unidad> acronimosIn(List<String> acronimos) {
    return (root, query, cb) -> root.get(Unidad_.id).in(acronimos);
  }

}