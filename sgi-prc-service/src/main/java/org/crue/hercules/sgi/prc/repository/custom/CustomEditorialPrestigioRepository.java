package org.crue.hercules.sgi.prc.repository.custom;

import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.prc.model.EditorialPrestigio;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link EditorialPrestigio}.
 */
@Component
public interface CustomEditorialPrestigioRepository {

  /**
   * Recupera las {@link EditorialPrestigio} de una año de baremación y de un
   * areaRef
   * 
   * @param areaRef               String
   * @param fechaInicioBaremacion fecha inicio de baremación
   * @param fechaFinBaremacion    fecha fin de baremación
   * 
   * @return Lista de {@link EditorialPrestigio}
   */
  List<EditorialPrestigio> findByAreaRefAndAnioBaremacion(String areaRef, Instant fechaInicioBaremacion,
      Instant fechaFinBaremacion);
}