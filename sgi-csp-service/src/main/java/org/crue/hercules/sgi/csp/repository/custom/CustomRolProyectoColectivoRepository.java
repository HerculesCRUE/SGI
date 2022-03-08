package org.crue.hercules.sgi.csp.repository.custom;

import java.util.List;

import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolProyectoColectivo;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link RolProyectoColectivo}.
 */
@Component
public interface CustomRolProyectoColectivoRepository {

  /**
   * Recupera los colectivos de {@link RolProyectoColectivo} asociados a un
   * {@link RolProyecto} activo.
   *
   * @return los colectivos de {@link RolProyectoColectivo} asociados a un
   *         {@link RolProyecto} activo
   */
  List<String> findColectivosActivos();

}
