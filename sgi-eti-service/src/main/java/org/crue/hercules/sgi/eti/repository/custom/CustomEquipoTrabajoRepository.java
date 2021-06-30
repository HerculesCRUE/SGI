package org.crue.hercules.sgi.eti.repository.custom;

import java.util.List;

import org.crue.hercules.sgi.eti.dto.EquipoTrabajoWithIsEliminable;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link EquipoTrabajo}.
 */
@Component
public interface CustomEquipoTrabajoRepository {

  /**
   * Obtener todos los {@link EquipoTrabajo} para una determinada
   * {@link PeticionEvaluacion} con la informacion de si es eliminable o no.
   * 
   * No son eliminables los {@link EquipoTrabajo} que tienen tareas {@link Tarea}
   * que estan asociadas a una {@link Memoria} que no esta en alguno de los
   * siguiente estados: En elaboración, Completada, Favorable, Pendiente de
   * Modificaciones Mínimas, Pendiente de correcciones y No procede evaluar.
   * 
   * @param idPeticionEvaluacion Id de {@link PeticionEvaluacion}.
   * @return lista de {@link EquipoTrabajo} con la informacion de si son
   *         eliminables.
   */
  List<EquipoTrabajoWithIsEliminable> findAllByPeticionEvaluacionId(Long idPeticionEvaluacion);

}
