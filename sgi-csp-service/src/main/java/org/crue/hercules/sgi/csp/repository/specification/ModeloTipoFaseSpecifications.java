package org.crue.hercules.sgi.csp.repository.specification;

import javax.persistence.criteria.JoinType;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion_;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase_;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFase_;
import org.springframework.data.jpa.domain.Specification;

public class ModeloTipoFaseSpecifications {

  /**
   * {@link ModeloTipoFase} con activoConvocatoria a true.
   * 
   * @return specification para obtener los {@link ModeloTipoFase} con
   *         activoConvocatoria a true.
   */
  public static Specification<ModeloTipoFase> activosConvocatoria() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ModeloTipoFase_.convocatoria), Boolean.TRUE);
    };
  }

  /**
   * {@link ModeloTipoFase} con activoProyecto a true.
   * 
   * @return specification para obtener los {@link ModeloTipoFase} con
   *         activoProyecto a true.
   */
  public static Specification<ModeloTipoFase> activosProyecto() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ModeloTipoFase_.proyecto), Boolean.TRUE);
    };
  }

  /**
   * {@link ModeloTipoFase} con activoSolicitud a true.
   * 
   * @return specification para obtener los {@link ModeloTipoFase} con
   *         activoSolicitud a true.
   */
  public static Specification<ModeloTipoFase> activosSolcitud() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ModeloTipoFase_.solicitud), Boolean.TRUE);
    };
  }

  /**
   * {@link ModeloTipoFase} del {@link ModeloEjecucion} con el id indicado.
   * 
   * @param id identificador del {@link ModeloEjecucion}.
   * @return specification para obtener los {@link ModeloTipoFase} del
   *         {@link ModeloEjecucion} con el id indicado.
   */
  public static Specification<ModeloTipoFase> byModeloEjecucionId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ModeloTipoFase_.modeloEjecucion).get(ModeloEjecucion_.id), id);
    };
  }

  /**
   * {@link ModeloTipoFase} activos con {@link TipoFase} activo.
   * 
   * @return specification para obtener los {@link ModeloTipoFase} activos
   */
  public static Specification<ModeloTipoFase> activos() {
    return (root, query, cb) -> {
      root.join(ModeloTipoFase_.tipoFase, JoinType.INNER);
      return cb.and(cb.equal(root.get(ModeloTipoFase_.activo), Boolean.TRUE),
          cb.equal(root.get(ModeloTipoFase_.tipoFase).get(TipoFase_.activo), Boolean.TRUE));
    };
  }

}