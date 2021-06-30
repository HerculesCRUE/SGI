package org.crue.hercules.sgi.eti.repository.custom;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.eti.dto.TareaWithIsEliminable;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo_;
import org.crue.hercules.sgi.eti.model.FormacionEspecifica;
import org.crue.hercules.sgi.eti.model.FormacionEspecifica_;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion_;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.crue.hercules.sgi.eti.model.Tarea_;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria_;
import org.crue.hercules.sgi.eti.model.TipoTarea;
import org.crue.hercules.sgi.eti.model.TipoTarea_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom repository para {@link Tarea}.
 */
@Slf4j
@Component
public class CustomTareaRepositoryImpl implements CustomTareaRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Devuelve una lista paginada de {@link Tarea} para una determinada
   * {@link PeticionEvaluacion} con la informacion de si es eliminable o no.
   * 
   * No son eliminables las {@link Tarea} que estan asociadas a una
   * {@link Memoria} que no esta en alguno de los siguiente estados: En
   * elaboración, Completada, Favorable, Pendiente de Modificaciones Mínimas,
   * Pendiente de correcciones y No procede evaluar.
   * 
   * 
   * @param idPeticionEvaluacion Id de {@link PeticionEvaluacion}.
   * @return lista de tareas con la informacion de si son eliminables.
   */
  @Override
  public List<TareaWithIsEliminable> findAllByPeticionEvaluacionId(Long idPeticionEvaluacion) {
    log.debug("findAllByPeticionEvaluacionId : {} - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<TareaWithIsEliminable> cq = cb.createQuery(TareaWithIsEliminable.class);

    // Define FROM clause
    Root<Tarea> root = cq.from(Tarea.class);

    Join<Tarea, TipoTarea> joinTipoTarea = root.join(Tarea_.tipoTarea, JoinType.LEFT);
    Join<Tarea, FormacionEspecifica> joinFormacionEspecifica = root.join(Tarea_.formacionEspecifica, JoinType.LEFT);

    cq.multiselect(root.get(Tarea_.id), root.get(Tarea_.equipoTrabajo), root.get(Tarea_.memoria),
        root.get(Tarea_.tarea), root.get(Tarea_.formacion), joinFormacionEspecifica.get(FormacionEspecifica_.id),
        joinFormacionEspecifica.get(FormacionEspecifica_.nombre),
        joinFormacionEspecifica.get(FormacionEspecifica_.activo), joinTipoTarea.get(TipoTarea_.id),
        joinTipoTarea.get(TipoTarea_.nombre), joinTipoTarea.get(TipoTarea_.activo), root.get(Tarea_.organismo),
        root.get(Tarea_.anio), isNotEliminable(root, cb, cq).isNull().alias("eliminable"));

    // Where
    cq.where(cb.equal(root.get(Tarea_.equipoTrabajo).get(EquipoTrabajo_.peticionEvaluacion).get(PeticionEvaluacion_.id),
        idPeticionEvaluacion));

    TypedQuery<TareaWithIsEliminable> typedQuery = entityManager.createQuery(cq);

    List<TareaWithIsEliminable> result = typedQuery.getResultList();

    log.debug("findAllByPeticionEvaluacionId : {} - end");
    return result;
  }

  /**
   * Recupera la tarea si no es eliminable.
   * 
   * @param root root
   * @param cb   criteria builder
   * @param cq   criteria query
   * @return subquery que la tarea si no es eliminable.
   */
  private Subquery<Tarea> isNotEliminable(Root<Tarea> root, CriteriaBuilder cb,
      CriteriaQuery<TareaWithIsEliminable> cq) {
    log.debug("isNotEliminable : {} - start");

    Subquery<Tarea> queryNotEliminable = cq.subquery(Tarea.class);
    Root<Tarea> rootQueryNotEliminable = queryNotEliminable.from(Tarea.class);

    queryNotEliminable.select(rootQueryNotEliminable).where(
        cb.not(rootQueryNotEliminable.get(Tarea_.memoria).get(Memoria_.estadoActual).get(TipoEstadoMemoria_.id)
            .in(Arrays.asList(1L, 2L, 6L, 7L, 8L))),
        cb.equal(rootQueryNotEliminable.get(Tarea_.id), root.get(Tarea_.id)));

    log.debug("isNotEliminable : {} - end");

    return queryNotEliminable;
  }
}