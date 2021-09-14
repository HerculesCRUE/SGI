package org.crue.hercules.sgi.pii.repository.custom;

import java.util.List;

import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.InvencionInventor;

public interface CustomInvencionInventorRepository {

  /**
   * Devuelve verdadero si todos los {@link InvencionInventor} pasados por
   * parámetros pertenecen a la {@link Invencion} también pasada por parámetros.
   * 
   * @param invencionId         Id de la {@link Invencion}
   * @param invencionInventores Listado de {@link InvencionInventor}
   * @return Boolean
   */
  Boolean inventoresBelongsToInvencion(Long invencionId, List<Long> invencionInventores);

}
