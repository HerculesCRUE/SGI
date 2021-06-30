package org.crue.hercules.sgi.eti.repository.custom;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.eti.dto.EquipoTrabajoWithIsEliminable;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo_;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion_;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.crue.hercules.sgi.eti.model.Tarea_;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom repository para {@link EquipoTrabajo}.
 */
@Slf4j
@Component
public class CustomEquipoTrabajoRepositoryImpl implements CustomEquipoTrabajoRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

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
  @Override
  public List<EquipoTrabajoWithIsEliminable> findAllByPeticionEvaluacionId(Long idPeticionEvaluacion) {
    log.debug("findAllByPeticionEvaluacionId : {} - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<EquipoTrabajoWithIsEliminable> cq = cb.createQuery(EquipoTrabajoWithIsEliminable.class);

    // Define FROM clause
    Root<EquipoTrabajo> root = cq.from(EquipoTrabajo.class);

    cq.multiselect(root.get(EquipoTrabajo_.id), root.get(EquipoTrabajo_.personaRef),
        root.get(EquipoTrabajo_.peticionEvaluacion), isNotEliminable(root, cb, cq).isNull().alias("eliminable"));

    // Where
    cq.where(cb.equal(root.get(EquipoTrabajo_.peticionEvaluacion).get(PeticionEvaluacion_.id), idPeticionEvaluacion));

    TypedQuery<EquipoTrabajoWithIsEliminable> typedQuery = entityManager.createQuery(cq);

    List<EquipoTrabajoWithIsEliminable> result = typedQuery.getResultList();

    log.debug("findAllByPeticionEvaluacionId : {} - end");
    return result;
  }

  /**
   * Recupera el id de la primera tarea del equipo de trabajo que no sea
   * eliminable.
   * 
   * @param root root
   * @param cb   criteria builder
   * @param cq   criteria query
   * @return subquery para recuperar la primera tara del equipo de trabajo que no
   *         sea eliminable.
   */
  private Subquery<Long> isNotEliminable(Root<EquipoTrabajo> root, CriteriaBuilder cb,
      CriteriaQuery<EquipoTrabajoWithIsEliminable> cq) {
    log.debug("isNotEliminable : {} - start");

    Subquery<Long> queryNotEliminable = cq.subquery(Long.class);
    Root<Tarea> rootQueryNotEliminable = queryNotEliminable.from(Tarea.class);

    queryNotEliminable.select(cb.min(rootQueryNotEliminable.get(Tarea_.id))).where(
        cb.not(rootQueryNotEliminable.get(Tarea_.memoria).get(Memoria_.estadoActual).get(TipoEstadoMemoria_.id)
            .in(Arrays.asList(1L, 2L, 6L, 7L, 8L))),
        cb.equal(rootQueryNotEliminable.get(Tarea_.equipoTrabajo).get(EquipoTrabajo_.id), root.get(EquipoTrabajo_.id)));

    log.debug("isNotEliminable : {} - end");

    return queryNotEliminable;
  }

}