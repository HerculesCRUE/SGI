package org.crue.hercules.sgi.csp.repository.custom;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link Convocatoria}.
 */
@Component
public interface CustomConvocatoriaRepository {

  /**
   * Comprueba si existen datos vinculados a la {@link Convocatoria} de
   * {@link TipoFase}, {@link TipoHito}, {@link TipoEnlace} y
   * {@link TipoDocumento} con el fin de permitir la edición de los campos
   * unidadGestionRef y modeloEjecucion.
   *
   * @param id Id del {@link Convocatoria}.
   * @return true existen datos vinculados/false no existen datos vinculados.
   */
  Boolean tieneVinculaciones(Long id);

  /**
   * Comprueba si la {@link Convocatoria} está en estado 'Registrada' y existen
   * {@link Solicitud} o proyectos vinculados a la {@link Convocatoria} con el fin
   * de permitir la creación, modificación o eliminación de ciertas entidades
   * relacionadas con la propia {@link Convocatoria}.
   *
   * @param id Id del {@link Convocatoria}.
   * @return true registrada y con datos vinculados/false no registrada o sin
   *         datos vinculados.
   */
  Boolean esRegistradaConSolicitudesOProyectos(Long id);

  /**
   * Obtiene la Unidad de Gestión asignada a la {@link Convocatoria}.
   * 
   * @param id Id del {@link Convocatoria}.
   * @return unidadGestionRef asignada
   */
  Optional<String> getUnidadGestionRef(Long id);

  /**
   * Obtiene el {@link ModeloEjecucion} asignada a la {@link Convocatoria}.
   * 
   * @param id Id de la {@link Convocatoria}.
   * @return {@link ModeloEjecucion} asignado
   */
  Optional<ModeloEjecucion> getModeloEjecucion(Long id);

}
