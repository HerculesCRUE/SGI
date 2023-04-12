package org.crue.hercules.sgi.eti.repository.custom;

import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link Evaluador}.
 */
@Component
public interface CustomEvaluadorRepository {

  /**
   * Devuelve los evaluadores activos del comité indicado que no entre en
   * conflicto de intereses con ningún miembro del equipo investigador de la
   * memoria.
   * 
   * @param idComite        Identificador del {@link Comite}
   * @param idMemoria       Identificador de la {@link Memoria}
   * @param fechaEvaluacion la fecha de Evaluación de la
   *                        {@link ConvocatoriaReunion}
   * @return lista de evaluadores sin conflictos de intereses
   */
  List<Evaluador> findAllByComiteSinconflictoInteresesMemoria(Long idComite, Long idMemoria, Instant fechaEvaluacion);

}
