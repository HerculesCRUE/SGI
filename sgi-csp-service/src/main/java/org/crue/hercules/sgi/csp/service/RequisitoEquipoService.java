package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoEquipo;

public interface RequisitoEquipoService {

  /**
   * Guarda la entidad {@link RequisitoEquipo}.
   * 
   * @param requisitoEquipo la entidad {@link RequisitoEquipo} a guardar.
   * @return RequisitoEquipo la entidad {@link RequisitoEquipo} persistida.
   */
  RequisitoEquipo create(RequisitoEquipo requisitoEquipo);

  /**
   * Actualiza la entidad {@link RequisitoEquipo}.
   * 
   * @param requisitoEquipoActualizar la entidad {@link RequisitoEquipo} a
   *                                  guardar.
   * @param convocatoriaId            Identificador de la {@link Convocatoria}
   * @return RequisitoEquipo la entidad {@link RequisitoEquipo} persistida.
   */
  RequisitoEquipo update(RequisitoEquipo requisitoEquipoActualizar, Long convocatoriaId);

  /**
   * Obtiene el {@link RequisitoEquipo} para una {@link Convocatoria}.
   *
   * @param id el id de la {@link Convocatoria}.
   * @return la entidad {@link RequisitoEquipo} de la {@link Convocatoria}
   */
  RequisitoEquipo findByConvocatoriaId(Long id);

}
