package org.crue.hercules.sgi.csp.repository.custom;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link ProyectoPaqueteTrabajo}.
 */
@Slf4j
@Component
public class CustomProyectoPaqueteTrabajoRepositoryImpl implements CustomProyectoPaqueteTrabajoRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Obtiene el valor del campo PaqueteTrabajo del {@link Proyecto} asignadao al
   * {@link ProyectoPaqueteTrabajo}.
   * 
   * @param id Id de la {@link ProyectoPaqueteTrabajo}.
   * @return true si se permiten {@link ProyectoPaqueteTrabajo}, false si no se
   *         permiten {@link ProyectoPaqueteTrabajo}
   */
  public Optional<Boolean> getPermitePaquetesTrabajo(Long id) {
    log.debug("getPermitePaquetesTrabajo(Long id) - start");
    Optional<Boolean> returnValue = Optional.empty();

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Boolean> cq = cb.createQuery(Boolean.class);
    Root<ProyectoPaqueteTrabajo> root = cq.from(ProyectoPaqueteTrabajo.class);

    Predicate finalPredicate = cb.equal(root.get(ProyectoPaqueteTrabajo_.id), id);
    cq.select(root.get(ProyectoPaqueteTrabajo_.proyecto).get(Proyecto_.permitePaquetesTrabajo)).where(finalPredicate);

    try {
      returnValue = entityManager.createQuery(cq).getResultList().stream().findFirst();
    } catch (NullPointerException e) {
    }

    log.debug("getPermitePaquetesTrabajo(Long id) - stop");
    return returnValue;
  }

}
