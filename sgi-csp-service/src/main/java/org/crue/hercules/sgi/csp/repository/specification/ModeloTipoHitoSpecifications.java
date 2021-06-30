package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion_;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito_;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.model.TipoHito_;
import org.springframework.data.jpa.domain.Specification;

public class ModeloTipoHitoSpecifications {

  /**
   * {@link ModeloTipoHito} activos con {@link TipoHito} activo.
   * 
   * @return specification para obtener los {@link ModeloTipoHito} activos.
   */
  public static Specification<ModeloTipoHito> activos() {
    return (root, query, cb) -> {
      return cb.and(cb.equal(root.get(ModeloTipoHito_.activo), Boolean.TRUE),
          cb.equal(root.get(ModeloTipoHito_.tipoHito).get(TipoHito_.activo), Boolean.TRUE));
    };
  }

  /**
   * {@link ModeloTipoHito} con activoConvocatoria a true.
   * 
   * @return specification para obtener los {@link ModeloTipoHito} con
   *         activoConvocatoria a true.
   */
  public static Specification<ModeloTipoHito> activosConvocatoria() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ModeloTipoHito_.convocatoria), Boolean.TRUE);
    };
  }

  /**
   * {@link ModeloTipoHito} con activoProyecto a true.
   * 
   * @return specification para obtener los {@link ModeloTipoHito} con
   *         activoProyecto a true.
   */
  public static Specification<ModeloTipoHito> activosProyecto() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ModeloTipoHito_.proyecto), Boolean.TRUE);
    };
  }

  /**
   * {@link ModeloTipoHito} con activoSolicitud a true.
   * 
   * @return specification para obtener los {@link ModeloTipoHito} con
   *         activoSolicitud a true.
   */
  public static Specification<ModeloTipoHito> activosSolcitud() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ModeloTipoHito_.solicitud), Boolean.TRUE);
    };
  }

  /**
   * {@link ModeloTipoHito} del {@link ModeloEjecucion} con el id indicado.
   * 
   * @param id identificador del {@link ModeloEjecucion}.
   * @return specification para obtener los {@link ModeloTipoHito} del
   *         {@link ModeloEjecucion} con el id indicado.
   */
  public static Specification<ModeloTipoHito> byModeloEjecucionId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ModeloTipoHito_.modeloEjecucion).get(ModeloEjecucion_.id), id);
    };
  }

}