package org.crue.hercules.sgi.csp.repository.custom;

import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoPersonaAutorizada;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link GrupoPersonaAutorizada}.
 */
@Component
public interface CustomGrupoPersonaAutorizadaRepository {

  /**
   * {@link GrupoPersonaAutorizada} del {@link Grupo} en el momento actual.
   * 
   * @param grupoId identificador del {@link Grupo}.
   * @param fecha   fecha en la que se busca el investigador principal.
   * @return la lista de personaRef de los {@link GrupoPersonaAutorizada} del
   *         {@link Grupo} en el momento actual.
   */
  List<String> findPersonaRefs(Long grupoId, Instant fecha);

}
