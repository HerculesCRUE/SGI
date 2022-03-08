package org.crue.hercules.sgi.csp.repository.custom;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolProyectoColectivo;
import org.crue.hercules.sgi.csp.model.RolProyectoColectivo_;
import org.crue.hercules.sgi.csp.model.RolProyecto_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA custom repository para {@link RolProyectoColectivo}.
 */
@Slf4j
@Component
public class CustomRolProyectoColectivoRepositoryImpl implements CustomRolProyectoColectivoRepository {

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Recupera los colectivos de {@link RolProyectoColectivo} asociados a un
   * {@link RolProyecto} activo.
   *
   * @return los colectivos de {@link RolProyectoColectivo} asociados a un
   *         {@link RolProyecto} activo
   */
  @Override
  public List<String> findColectivosActivos() {
    log.debug("findColectivosActivos() - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<String> cq = cb.createQuery(String.class);
    Root<RolProyectoColectivo> root = cq.from(RolProyectoColectivo.class);
    Join<RolProyectoColectivo, RolProyecto> joinRolProyecto = root.join(RolProyectoColectivo_.rolProyecto,
        JoinType.INNER);

    cq.select(root.get(RolProyectoColectivo_.colectivoRef)).where(cb.isTrue(joinRolProyecto.get(RolProyecto_.activo)))
        .distinct(true);

    List<String> returnValue = entityManager.createQuery(cq).getResultList();

    log.debug("findColectivosActivos() - end");
    return returnValue;
  }

}
