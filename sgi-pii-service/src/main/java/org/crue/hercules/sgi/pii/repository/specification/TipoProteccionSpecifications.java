package org.crue.hercules.sgi.pii.repository.specification;

import org.crue.hercules.sgi.pii.model.TipoProteccion;
import org.crue.hercules.sgi.pii.model.TipoProteccion_;
import org.springframework.data.jpa.domain.Specification;

public class TipoProteccionSpecifications {
  /**
   * {@link TipoProteccion} activos.
   * 
   * @return Specification para obtener los {@link TipoProteccion} activos.
   */
  public static Specification<TipoProteccion> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoProteccion_.activo), Boolean.TRUE);
    };
  }

  /**
   * {@link TipoProteccion} no Subtipos.
   * 
   * @return Specification para obtener los {@link TipoProteccion} no Subtipos.
   */
  public static Specification<TipoProteccion> noSubtipos() {
    return (root, query, cb) -> {
      return cb.isNull(root.get(TipoProteccion_.padre));
    };
  }

  /**
   * Devuelve los {@link TipoProteccion} que son Subtipos del
   * {@link TipoProteccion} pasado por parámetros
   * 
   * @param id {@link Long} Id del {@link TipoProteccion} padre.
   * @return Specification para obtener los {@link TipoProteccion} que son
   *         Subtipos del {@link TipoProteccion} pasado por parámetro.
   */
  public static Specification<TipoProteccion> subtipos(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoProteccion_.padre), id);
    };
  }

}
