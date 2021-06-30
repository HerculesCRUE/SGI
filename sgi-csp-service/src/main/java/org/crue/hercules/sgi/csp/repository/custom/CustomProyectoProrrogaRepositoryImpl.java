package org.crue.hercules.sgi.csp.repository.custom;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link ProyectoProrroga}.
 */
@Slf4j
@Component
public class CustomProyectoProrrogaRepositoryImpl implements CustomProyectoProrrogaRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Obtiene el {@link Proyecto} asociado al {@link ProyectoProrroga}.
   * 
   * @param id Id de la {@link ProyectoProrroga}.
   * @return {@link Proyecto} asignado
   */
  public Optional<Proyecto> getProyecto(Long id) {
    log.debug("Proyecto(Long id) - start");

    Optional<Proyecto> returnValue = Optional.empty();

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Proyecto> cq = cb.createQuery(Proyecto.class);
    Root<ProyectoProrroga> root = cq.from(ProyectoProrroga.class);

    Predicate finalPredicate = cb.equal(root.get(ProyectoProrroga_.id), id);
    cq.select(root.get(ProyectoProrroga_.proyecto)).where(finalPredicate);

    returnValue = entityManager.createQuery(cq).getResultList().stream().findFirst();

    log.debug("Proyecto(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene el {@link ModeloEjecucion} del {@link Proyecto} asociado al
   * {@link ProyectoProrroga}.
   * 
   * @param id Id de la {@link ProyectoProrroga}.
   * @return {@link ModeloEjecucion} del {@link Proyecto} asociado al
   *         {@link ProyectoProrroga}.
   */
  public Optional<ModeloEjecucion> getModeloEjecucion(Long id) {
    log.debug("getModeloEjecucion(Long id) - start");

    Optional<ModeloEjecucion> returnValue = Optional.empty();

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<ModeloEjecucion> cq = cb.createQuery(ModeloEjecucion.class);
    Root<ProyectoProrroga> root = cq.from(ProyectoProrroga.class);

    Predicate finalPredicate = cb.equal(root.get(ProyectoProrroga_.id), id);
    cq.select(root.get(ProyectoProrroga_.proyecto).get(Proyecto_.modeloEjecucion)).where(finalPredicate);

    returnValue = entityManager.createQuery(cq).getResultList().stream().findFirst();

    log.debug("getModeloEjecucion(Long id) - end");
    return returnValue;
  }

}
