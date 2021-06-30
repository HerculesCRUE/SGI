package org.crue.hercules.sgi.csp.repository.specification;

import java.util.List;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud_;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.springframework.data.jpa.domain.Specification;

public class ConvocatoriaSpecifications {

  /**
   * {@link Convocatoria} con Activo a True
   * 
   * @return specification para obtener las {@link Convocatoria} activas
   */
  public static Specification<Convocatoria> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Convocatoria_.activo), Boolean.TRUE);
    };
  }

  /**
   * {@link Convocatoria} registradas
   * 
   * @return specification para obtener las {@link Convocatoria} registradas
   */
  public static Specification<Convocatoria> registradas() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Convocatoria_.estado), Convocatoria.Estado.REGISTRADA);
    };
  }

  /**
   * {@link Convocatoria} cuya {@link ConfiguracionSolicitud} tiene tramitación
   * SGI
   * 
   * @return specification para obtener las {@link Convocatoria} cuya
   *         {@link ConfiguracionSolicitud} tiene tramitación SGI
   */
  public static Specification<Convocatoria> configuracionSolicitudTramitacionSGI() {
    return (root, query, cb) -> {
      Subquery<Long> queryConfiguracionSolicitud = query.subquery(Long.class);
      Root<ConfiguracionSolicitud> subqRoot = queryConfiguracionSolicitud.from(ConfiguracionSolicitud.class);
      queryConfiguracionSolicitud.select(subqRoot.get(ConfiguracionSolicitud_.convocatoria).get(Convocatoria_.id))
          .where(cb.equal(subqRoot.get(ConfiguracionSolicitud_.TRAMITACION_SG_I), Boolean.TRUE));
      return root.get(Convocatoria_.id).in(queryConfiguracionSolicitud);
    };
  }

  /**
   * {@link Convocatoria} unidadGestionRef.
   * 
   * @param acronimos lista de acronimos
   * @return specification para obtener los {@link Convocatoria} cuyo
   *         unidadGestionRef se encuentre entre los recibidos.
   */
  public static Specification<Convocatoria> acronimosIn(List<String> acronimos) {
    return (root, query, cb) -> {
      return root.get(Convocatoria_.unidadGestionRef).in(acronimos);

    };
  }
}
