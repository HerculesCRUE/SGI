package org.crue.hercules.sgi.csp.repository.custom;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link ProyectoProrroga}.
 */
@Component
public interface CustomProyectoProrrogaRepository {

  /**
   * Obtiene el {@link Proyecto} asociado al {@link ProyectoProrroga}.
   * 
   * @param id Id de la {@link ProyectoProrroga}.
   * @return {@link Proyecto} asignado
   */
  Optional<Proyecto> getProyecto(Long id);

  /**
   * Obtiene el {@link ModeloEjecucion} del {@link Proyecto} asociado al
   * {@link ProyectoProrroga}.
   * 
   * @param id Id de la {@link ProyectoProrroga}.
   * @return {@link ModeloEjecucion} del {@link Proyecto} asociado al
   *         {@link ProyectoProrroga}.
   */
  Optional<ModeloEjecucion> getModeloEjecucion(Long id);

}
