package org.crue.hercules.sgi.csp.repository.custom;

import java.time.Instant;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoPersonaAutorizada;
import org.crue.hercules.sgi.csp.model.GrupoPersonaAutorizada_;
import org.crue.hercules.sgi.csp.model.Grupo_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA custom repository para {@link GrupoPersonaAutorizada}.
 */
@Slf4j
@Component
public class CustomGrupoPersonaAutorizadaRepositoryImpl implements CustomGrupoPersonaAutorizadaRepository {

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * {@link GrupoPersonaAutorizada} del {@link Grupo} en el momento actual.
   * 
   * @param grupoId identificador del {@link Grupo}.
   * @param fecha   fecha en la que se busca el investigador principal.
   * @return la lista de personaRef de los {@link GrupoPersonaAutorizada} del
   *         {@link Grupo} en el momento actual.
   */
  @Override
  public List<String> findPersonaRefs(Long grupoId, Instant fecha) {
    log.debug("findPersonaRefs(Long grupoId, Instant fecha) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<String> cq = cb.createQuery(String.class);
    Root<GrupoPersonaAutorizada> root = cq.from(GrupoPersonaAutorizada.class);

    Join<GrupoPersonaAutorizada, Grupo> joinGrupo = root.join(GrupoPersonaAutorizada_.grupo);

    Predicate grupoEquals = cb.equal(root.get(GrupoPersonaAutorizada_.grupoId), grupoId);
    Predicate greaterThanFechaInicio = cb.or(
        cb.lessThanOrEqualTo(root.get(GrupoPersonaAutorizada_.fechaInicio), fecha),
        cb.and(
            cb.isNull(root.get(GrupoPersonaAutorizada_.fechaInicio)),
            cb.or(
                cb.lessThanOrEqualTo(joinGrupo.get(Grupo_.fechaInicio), fecha))));

    Predicate lowerThanFechaFin = cb.or(
        cb.greaterThanOrEqualTo(root.get(GrupoPersonaAutorizada_.fechaFin),
            fecha),
        cb.and(
            cb.isNull(root.get(GrupoPersonaAutorizada_.fechaFin)),
            cb.or(
                cb.isNull(joinGrupo.get(Grupo_.fechaFin)),
                cb.greaterThanOrEqualTo(joinGrupo.get(Grupo_.fechaFin), fecha))));

    cq.select(root.get(GrupoPersonaAutorizada_.personaRef))
        .where(cb.and(
            grupoEquals,
            greaterThanFechaInicio,
            lowerThanFechaFin))
        .distinct(true);

    List<String> returnValue = entityManager.createQuery(cq).getResultList();

    log.debug("findPersonaRefs(Long grupoId, Instant fecha) - end");
    return returnValue;
  }

}
