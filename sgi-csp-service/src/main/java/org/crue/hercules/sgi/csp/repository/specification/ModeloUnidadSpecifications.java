package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion_;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.model.ModeloUnidad_;
import org.springframework.data.jpa.domain.Specification;

public class ModeloUnidadSpecifications {

  /**
   * {@link ModeloUnidad} activos.
   * 
   * @return specification para obtener los {@link ModeloUnidad} activos.
   */
  public static Specification<ModeloUnidad> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ModeloUnidad_.activo), Boolean.TRUE);
    };
  }

  /**
   * {@link ModeloUnidad} del {@link ModeloEjecucion} con el id indicado.
   * 
   * @param id identificador del {@link ModeloEjecucion}.
   * @return specification para obtener los {@link ModeloUnidad} del
   *         {@link ModeloEjecucion} con el id indicado.
   */
  public static Specification<ModeloUnidad> byModeloEjecucionId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ModeloUnidad_.modeloEjecucion).get(ModeloEjecucion_.id), id);
    };
  }

}