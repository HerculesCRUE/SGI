package org.crue.hercules.sgi.prc.repository.custom;

import java.time.Instant;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.prc.model.EditorialPrestigio;
import org.crue.hercules.sgi.prc.model.EditorialPrestigio_;
import org.crue.hercules.sgi.prc.model.TablaEditorial;
import org.crue.hercules.sgi.prc.model.TablaEditorial_;
import org.crue.hercules.sgi.prc.repository.EditorialPrestigioRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link EditorialPrestigioRepository}.
 */
@Slf4j
@Component
public class CustomEditorialPrestigioRepositoryImpl implements CustomEditorialPrestigioRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Recupera las {@link EditorialPrestigio} de una año de baremación y de un
   * areaRef
   * 
   * @param areaRef               String
   * @param fechaInicioBaremacion fecha inicio de baremación
   * @param fechaFinBaremacion    fecha fin de baremación
   * 
   * @return Lista de {@link EditorialPrestigio}
   */
  public List<EditorialPrestigio> findByAreaRefAndAnioBaremacion(String areaRef, Instant fechaInicioBaremacion,
      Instant fechaFinBaremacion) {

    log.debug("findByAreaRefAndAnioBaremacion(areaRef, fechaInicioBaremacion, fechaFinBaremacion) : {} - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<EditorialPrestigio> cq = cb.createQuery(EditorialPrestigio.class);

    Root<EditorialPrestigio> root = cq.from(EditorialPrestigio.class);

    Join<EditorialPrestigio, TablaEditorial> joinTablaEditorial = root.join(EditorialPrestigio_.tablaEditorial);

    cq.select(root).where(
        cb.and(cb.equal(root.get(EditorialPrestigio_.areaRef), areaRef)),
        cb.and(cb.lessThanOrEqualTo(joinTablaEditorial.get(TablaEditorial_.fechaInicio), fechaFinBaremacion),
            cb.and(cb.or(cb.isNull(joinTablaEditorial.get(TablaEditorial_.fechaFin)),
                cb.greaterThanOrEqualTo(joinTablaEditorial.get(TablaEditorial_.fechaFin), fechaInicioBaremacion)))));

    log.debug("findByAreaRefAndAnioBaremacion(areaRef, fechaInicioBaremacion, fechaFinBaremacion) : {} - end");

    return entityManager.createQuery(cq).getResultList();
  }
}
