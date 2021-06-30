package org.crue.hercules.sgi.csp.repository.custom;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link ProyectoPaqueteTrabajo}.
 */
@Component
public interface CustomProyectoPaqueteTrabajoRepository {

  /**
   * Obtiene el valor del campo PaqueteTrabajo del {@link Proyecto} asignadaoal
   * {@link ProyectoPaqueteTrabajo}.
   * 
   * @param id Id de la {@link ProyectoPaqueteTrabajo}.
   * @return true si se permiten {@link ProyectoPaqueteTrabajo}, false si no se
   *         permiten {@link ProyectoPaqueteTrabajo}
   */
  Optional<Boolean> getPermitePaquetesTrabajo(Long id);

}
