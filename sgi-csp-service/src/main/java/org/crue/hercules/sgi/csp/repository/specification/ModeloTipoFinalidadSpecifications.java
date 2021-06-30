package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion_;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad_;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoFinalidad_;
import org.springframework.data.jpa.domain.Specification;

public class ModeloTipoFinalidadSpecifications {

  /**
   * {@link ModeloTipoFinalidad} activos con {@link TipoFinalidad} activo.
   * 
   * @return specification para obtener los {@link ModeloTipoFinalidad} activos.
   */
  public static Specification<ModeloTipoFinalidad> activos() {
    return (root, query, cb) -> {
      return cb.and(cb.equal(root.get(ModeloTipoFinalidad_.activo), Boolean.TRUE),
          cb.equal(root.get(ModeloTipoFinalidad_.tipoFinalidad).get(TipoFinalidad_.activo), Boolean.TRUE));
    };
  }

  /**
   * {@link ModeloTipoFinalidad} del {@link ModeloEjecucion} con el id indicado.
   * 
   * @param id identificador del {@link ModeloEjecucion}.
   * @return specification para obtener los {@link ModeloTipoFinalidad} del
   *         {@link ModeloEjecucion} con el id indicado.
   */
  public static Specification<ModeloTipoFinalidad> byModeloEjecucionId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ModeloTipoFinalidad_.modeloEjecucion).get(ModeloEjecucion_.id), id);
    };
  }

}