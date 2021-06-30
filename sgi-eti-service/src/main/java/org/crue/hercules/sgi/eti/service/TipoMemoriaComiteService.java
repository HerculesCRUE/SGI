package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.model.TipoMemoriaComite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link TipoMemoriaComite}.
 */
public interface TipoMemoriaComiteService {

  /**
   * Devuelve la lista paginada de los tipos memoria de un comité
   * 
   * @param id     Identificador de {@link Comite}
   * @param paging Datos de la paginación
   * @return lista paginada de los tipos memoria
   */
  Page<TipoMemoria> findByComite(Long id, Pageable paging);

}