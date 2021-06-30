package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion_;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento_;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumento_;
import org.springframework.data.jpa.domain.Specification;

public class ModeloTipoDocumentoSpecifications {

  /**
   * {@link ModeloTipoDocumento} activos con {@link TipoDocumento} activo.
   * 
   * @return specification para obtener los {@link ModeloTipoDocumento} activos.
   */
  public static Specification<ModeloTipoDocumento> activos() {
    return (root, query, cb) -> {
      return cb.and(cb.equal(root.get(ModeloTipoDocumento_.activo), Boolean.TRUE),
          cb.equal(root.get(ModeloTipoDocumento_.tipoDocumento).get(TipoDocumento_.activo), Boolean.TRUE));
    };
  }

  /**
   * {@link ModeloTipoDocumento} del {@link ModeloEjecucion} con el id indicado.
   * 
   * @param id identificador del {@link ModeloEjecucion}.
   * @return specification para obtener los {@link ModeloTipoDocumento} del
   *         {@link ModeloEjecucion} con el id indicado.
   */
  public static Specification<ModeloTipoDocumento> byModeloEjecucionId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ModeloTipoDocumento_.modeloEjecucion).get(ModeloEjecucion_.id), id);
    };
  }

}