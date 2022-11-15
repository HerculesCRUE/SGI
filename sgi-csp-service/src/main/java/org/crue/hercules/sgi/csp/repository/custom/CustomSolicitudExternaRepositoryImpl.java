package org.crue.hercules.sgi.csp.repository.custom;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.model.SolicitanteExterno;
import org.crue.hercules.sgi.csp.model.SolicitanteExterno_;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudExterna;
import org.crue.hercules.sgi.csp.model.SolicitudExterna_;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA custom repository para {@link SolicitudExterna}.
 */
@Slf4j
@Component
public class CustomSolicitudExternaRepositoryImpl implements CustomSolicitudExternaRepository {

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Devuelve el id de la {@link Solicitud} asociada a la
   * {@link SolicitudExterna}.
   *
   * @param uuid id {@link SolicitudExterna}.
   * @return id de la {@link Solicitud} asociada
   */
  @Override
  public Optional<Long> findSolicitudId(UUID uuid) {
    log.debug("findSolicitudId(UUID uuid) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<SolicitudExterna> root = cq.from(SolicitudExterna.class);

    cq.select(root.get(SolicitudExterna_.solicitudId)).where(cb.equal(root.get(SolicitudExterna_.id), uuid));

    List<Long> solicitudIds = entityManager.createQuery(cq).getResultList();

    log.debug("findSolicitudId(UUID uuid) - end");

    return solicitudIds.isEmpty() ? Optional.empty() : Optional.of(solicitudIds.get(0));
  }

  /**
   * Devuelve el id de la {@link SolicitudExterna} de la
   * {@link Solicitud} con uuid y numeroDocumentoSolicitante
   * indicados.
   *
   * @param uuid                       codigo interno de la {@link Solicitud}.
   * @param numeroDocumentoSolicitante Numero de documento del solicitante
   *                                   externo.
   * @return id de la {@link SolicitudExterna}
   */
  @Override
  public Optional<UUID> findPublicId(UUID uuid, String numeroDocumentoSolicitante) {
    log.debug("findPublicId(UUID uuid, String numeroDocumentoSolicitante) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<UUID> cq = cb.createQuery(UUID.class);
    Root<SolicitudExterna> root = cq.from(SolicitudExterna.class);
    Join<SolicitudExterna, Solicitud> joinSolicitud = root.join(SolicitudExterna_.solicitud);

    Subquery<Long> querySolicitanteExterno = cq.subquery(Long.class);
    Root<SolicitanteExterno> querySolicitanteExternoRoot = querySolicitanteExterno.from(SolicitanteExterno.class);
    querySolicitanteExterno.select(querySolicitanteExternoRoot.get(SolicitanteExterno_.solicitudId))
        .where(cb.like(cb.lower(querySolicitanteExternoRoot.get(SolicitanteExterno_.numeroDocumento)),
            numeroDocumentoSolicitante.toLowerCase()));

    cq.select(root.get(SolicitudExterna_.id))
        .where(cb.and(
            joinSolicitud.get(Solicitud_.id).in(querySolicitanteExterno),
            cb.equal(root.get(SolicitudExterna_.id), uuid)));

    List<UUID> ids = entityManager.createQuery(cq).getResultList();

    log.debug("findPublicId(UUID uuid, String numeroDocumentoSolicitante) - end");

    return ids.isEmpty() ? Optional.empty() : Optional.of(ids.get(0));
  }

}
