package org.crue.hercules.sgi.eti.repository.custom;

import java.util.List;

import org.crue.hercules.sgi.eti.dto.TareaWithIsEliminable;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link Tarea}.
 */
@Component
public interface CustomTareaRepository {

  /**
   * Devuelve una lista paginada de {@link Tarea} para una determinada
   * {@link PeticionEvaluacion} con la informacion de si es eliminable o no.
   * 
   * No son eliminables las {@link Tarea} que estan asociadas a una
   * {@link Memoria} que no esta en alguno de los siguiente estados: En
   * elaboración, Completada, Favorable, Pendiente de Modificaciones Mínimas,
   * Pendiente de correcciones y No procede evaluar.
   * 
   * @param idPeticionEvaluacion Id de {@link PeticionEvaluacion}.
   * @return lista de tareas con la informacion de si son eliminables.
   */
  List<TareaWithIsEliminable> findAllByPeticionEvaluacionId(Long idPeticionEvaluacion);

}
