package org.crue.hercules.sgi.csp.repository.custom;

import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoJustificacionOutput.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link ProyectoEquipo}.
 */
@Component
public interface CustomProyectoEquipoRepository {

  /**
   * {@link ProyectoEquipo} que son investigador o investigadores principales del
   * {@link Proyecto} con el id indicado.
   * 
   * Son investiador principales los {@link ProyectoEquipo} que a fecha actual
   * tiene el {@link RolProyecto} con el flag {@link RolProyecto#rolPrincipal} a
   * <code>true</code>.
   * 
   * @param proyectoId identificador del {@link Proyecto}.
   * @param fecha      fecha en la que se busca el investigador principal.
   * @return la lista de personaRef de los investigadores principales del
   *         {@link Proyecto} en el momento actual.
   */
  List<String> findPersonaRefInvestigadoresPrincipales(Long proyectoId, Instant fecha);

}
