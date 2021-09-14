package org.crue.hercules.sgi.csp.repository.custom;

import org.crue.hercules.sgi.csp.model.AnualidadGasto;
import org.crue.hercules.sgi.csp.model.AnualidadIngreso;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPartida;
import org.crue.hercules.sgi.csp.model.ProyectoPartida;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link Convocatoria}.
 */
@Component
public interface CustomConvocatoriaPartidaRepository {

  /**
   * Comprueba si la {@link ConvocatoriaPartida} está en estado 'Registrada' y
   * existen {@link AnualidadGasto} o {@link AnualidadIngreso} vinculados a la
   * {@link ProyectoPartida}
   *
   * @param id Id del {@link ConvocatoriaPartida}.
   * @return true registrada y con datos vinculados/false no registrada o sin
   *         datos vinculados.
   */
  Boolean isPosibleEditar(Long id);

}
