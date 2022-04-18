package org.crue.hercules.sgi.prc.repository.custom;

import java.time.Instant;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.prc.model.IndiceExperimentalidad;
import org.crue.hercules.sgi.prc.model.IndiceExperimentalidad_;
import org.crue.hercules.sgi.prc.model.TablaIndice;
import org.crue.hercules.sgi.prc.model.TablaIndice_;
import org.crue.hercules.sgi.prc.repository.IndiceExperimentalidadRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link IndiceExperimentalidadRepository}.
 */
@Slf4j
@Component
public class CustomIndiceExperimentalidadRepositoryImpl implements CustomIndiceExperimentalidadRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Recupera las {@link IndiceExperimentalidad} de una año de baremación y de un
   * departamentoRef
   * 
   * @param departamentoRef       String
   * @param fechaInicioBaremacion fecha inicio de baremación
   * @param fechaFinBaremacion    fecha fin de baremación
   * 
   * @return Lista de {@link IndiceExperimentalidad}
   */
  public List<IndiceExperimentalidad> findByDepartamentoRefAndAnioBaremacion(String departamentoRef,
      Instant fechaInicioBaremacion,
      Instant fechaFinBaremacion) {

    log.debug(
        "findByDepartamentoRefAndAnioBaremacion(departamentoRef, fechaInicioBaremacion, fechaFinBaremacion) : {} - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<IndiceExperimentalidad> cq = cb.createQuery(IndiceExperimentalidad.class);

    Root<IndiceExperimentalidad> root = cq.from(IndiceExperimentalidad.class);

    Join<IndiceExperimentalidad, TablaIndice> joinTablaIndice = root.join(IndiceExperimentalidad_.tablaIndice);

    cq.select(root).where(
        cb.and(cb.equal(root.get(IndiceExperimentalidad_.departamentoRef), departamentoRef)),
        cb.and(cb.lessThanOrEqualTo(joinTablaIndice.get(TablaIndice_.fechaInicio), fechaFinBaremacion),
            cb.and(cb.or(cb.isNull(joinTablaIndice.get(TablaIndice_.fechaFin)),
                cb.greaterThanOrEqualTo(joinTablaIndice.get(TablaIndice_.fechaFin), fechaInicioBaremacion)))));

    log.debug(
        "findByDepartamentoRefAndAnioBaremacion(departamentoRef, fechaInicioBaremacion, fechaFinBaremacion) : {} - end");

    return entityManager.createQuery(cq).getResultList();
  }
}
