package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.model.Convocatoria;

/**
 * Service Interface para gestionar {@link RequisitoIP}.
 */
public interface RequisitoIPService {

  /**
   * Guardar un nuevo {@link RequisitoIP}.
   *
   * @param requisitoIP la entidad {@link RequisitoIP} a guardar.
   * @return la entidad {@link RequisitoIP} persistida.
   */
  RequisitoIP create(RequisitoIP requisitoIP);

  /**
   * Actualizar {@link RequisitoIP}.
   *
   * @param requisitoIPActualizar la entidad {@link RequisitoIP} a actualizar.
   * @param idConvocatoria        identificador de la {@link Convocatoria} a
   *                              actualizar.
   * @return la entidad {@link RequisitoIP} persistida.
   */
  RequisitoIP update(RequisitoIP requisitoIPActualizar, Long idConvocatoria);

  /**
   * Obtiene el {@link RequisitoIP} de la {@link Convocatoria}
   * 
   * @param id id de la {@link Convocatoria}
   * @return la entidad {@link RequisitoIP}
   */

  RequisitoIP findByConvocatoria(Long id);

}
