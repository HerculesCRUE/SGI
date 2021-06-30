package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;

/**
 * Service Interface para gestionar {@link ConfiguracionSolicitud}.
 */

public interface ConfiguracionSolicitudService {

  /**
   * Guarda la entidad {@link ConfiguracionSolicitud}.
   * 
   * @param configuracionSolicitud la entidad {@link ConfiguracionSolicitud} a
   *                               guardar.
   * @return ConfiguracionSolicitud la entidad {@link ConfiguracionSolicitud}
   *         persistida.
   */
  ConfiguracionSolicitud create(ConfiguracionSolicitud configuracionSolicitud);

  /**
   * Actualiza los datos del {@link ConfiguracionSolicitud} por
   * {@link Convocatoria}
   * 
   * @param configuracionSolicitud {@link ConfiguracionSolicitud} con los datos
   *                               actualizados.
   * @param convocatoriaId         Identificador de la {@link Convocatoria}
   * @return ConfiguracionSolicitud {@link ConfiguracionSolicitud} actualizado.
   */
  ConfiguracionSolicitud update(final ConfiguracionSolicitud configuracionSolicitud, final Long convocatoriaId);

  /**
   * Obtiene una entidad {@link ConfiguracionSolicitud} por el id de la
   * {@link Convocatoria}.
   * 
   * @param id Identificador de la {@link Convocatoria}.
   * @return ConfiguracionSolicitud la entidad {@link ConfiguracionSolicitud}.
   */
  ConfiguracionSolicitud findByConvocatoriaId(final Long id);

}
