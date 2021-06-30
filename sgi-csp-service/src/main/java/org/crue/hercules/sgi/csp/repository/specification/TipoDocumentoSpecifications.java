package org.crue.hercules.sgi.csp.repository.specification;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud_;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion_;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento_;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase_;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumento_;
import org.crue.hercules.sgi.csp.model.TipoFase_;
import org.springframework.data.jpa.domain.Specification;

public class TipoDocumentoSpecifications {

  /**
   * {@link TipoDocumento} activos.
   * 
   * @return specification para obtener los {@link TipoDocumento} activos.
   */
  public static Specification<TipoDocumento> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoDocumento_.activo), Boolean.TRUE);
    };
  }

  /**
   * {@link TipoDocumento} asociados a la fase de presentación de solicitudes de
   * la {@link Convocatoria}.
   * 
   * @param convocatoriaId Identidicador de la {@link Convocatoria}.
   * @return specification para obtener los {@link TipoDocumento} asociados a la
   *         fase de presentación de solicitudes de la {@link Convocatoria}.
   */
  public static Specification<TipoDocumento> tipoDocumentosFasePresentacionConvocatoria(Long convocatoriaId) {
    return (root, query, cb) -> {

      Subquery<Long> sqModeloEjecucion = query.subquery(Long.class);
      Root<Convocatoria> rootModeloEjecucion = sqModeloEjecucion.from(Convocatoria.class);
      sqModeloEjecucion.select(rootModeloEjecucion.get(Convocatoria_.modeloEjecucion).get(ModeloEjecucion_.id))
          .where(cb.equal(rootModeloEjecucion.get(Convocatoria_.id), convocatoriaId));

      Subquery<Long> sqTipoFase = query.subquery(Long.class);
      Root<ConfiguracionSolicitud> rootTipoFase = sqTipoFase.from(ConfiguracionSolicitud.class);
      sqTipoFase
          .select(rootTipoFase.get(ConfiguracionSolicitud_.fasePresentacionSolicitudes).get(ConvocatoriaFase_.tipoFase)
              .get(TipoFase_.id))
          .where(
              cb.equal(rootTipoFase.get(ConfiguracionSolicitud_.convocatoria).get(Convocatoria_.id), convocatoriaId));

      Subquery<Long> sqTipoDocumento = query.subquery(Long.class);
      Root<ModeloTipoDocumento> rootTipoDocumento = sqTipoDocumento.from(ModeloTipoDocumento.class);
      sqTipoDocumento.select(rootTipoDocumento.get(ModeloTipoDocumento_.tipoDocumento).get(TipoDocumento_.id))
          .where(cb.and(
              rootTipoDocumento.get(ModeloTipoDocumento_.modeloTipoFase).get(ModeloTipoFase_.tipoFase).get(TipoFase_.id)
                  .in(sqTipoFase),
              cb.equal(rootTipoDocumento.get(ModeloTipoDocumento_.modeloEjecucion), sqModeloEjecucion)));

      return root.get(TipoDocumento_.id).in(sqTipoDocumento);
    };
  }

}