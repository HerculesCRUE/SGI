package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.model.Configuracion;

/**
 * Service Interface para gestionar {@link Configuracion}.
 */
public interface ConfiguracionService {

  /**
   * Actualizar {@link Configuracion}.
   *
   * @param configuracion la entidad {@link Configuracion} a actualizar.
   * @return la entidad {@link Configuracion} persistida.
   */
  Configuracion update(Configuracion configuracion);

  /**
   * Devuelve la {@link Configuracion}
   *
   * @return la {@link Configuracion}
   */
  Configuracion findConfiguracion();

}