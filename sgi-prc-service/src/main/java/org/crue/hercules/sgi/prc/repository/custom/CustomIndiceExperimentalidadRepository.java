package org.crue.hercules.sgi.prc.repository.custom;

import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.prc.model.IndiceExperimentalidad;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link IndiceExperimentalidad}.
 */
@Component
public interface CustomIndiceExperimentalidadRepository {

  /**
   * Recupera las {@link IndiceExperimentalidad} de una año de baremación y de un
   * departamentoRef
   * 
   * @param departamentoRef       string
   * @param fechaInicioBaremacion fecha inicio de baremación
   * @param fechaFinBaremacion    fecha fin de baremación
   * 
   * @return Lista de {@link IndiceExperimentalidad}
   */
  List<IndiceExperimentalidad> findByDepartamentoRefAndAnioBaremacion(String departamentoRef,
      Instant fechaInicioBaremacion, Instant fechaFinBaremacion);
}