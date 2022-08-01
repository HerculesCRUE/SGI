package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudPalabraClave;
import org.crue.hercules.sgi.csp.repository.SolicitudPalabraClaveRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudPalabraClaveSpecifications;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link SolicitudPalabraClave}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudPalabraClaveService {

  private final SolicitudPalabraClaveRepository repository;

  public SolicitudPalabraClaveService(SolicitudPalabraClaveRepository solicitudPalabraClaveRepository) {
    this.repository = solicitudPalabraClaveRepository;
  }

  /**
   * Obtiene los {@link SolicitudPalabraClave} para una entidad {@link Solicitud}
   * paginadas y/o filtradas.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @param query       la información del filtro.
   * @param pageable    la información de la paginación.
   * @return la lista de {@link SolicitudPalabraClave} de la
   *         {@link Solicitud} paginadas y/o filtradas.
   */
  public Page<SolicitudPalabraClave> findBySolicitudId(Long solicitudId, String query, Pageable pageable) {
    log.debug("findBySolicitudId(Long solicitudId, String query, Pageable pageable) - start");

    Specification<SolicitudPalabraClave> specs = SolicitudPalabraClaveSpecifications.bySolicitudId(solicitudId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudPalabraClave> returnValue = repository.findAll(specs, pageable);
    log.debug("findBySolicitudId(Long solicitudId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Actualiza la lista de {@link SolicitudPalabraClave} de la entidad
   * {@link Solicitud}, elimina los pre-existentes y añade la nueva lista.
   * 
   * @param solicitudId   el id del {@link Solicitud}.
   * @param palabrasClave la lista con las nuevas palabras claves.
   * @return La lista actualizada de {@link SolicitudPalabraClave}.
   */
  @Transactional
  public List<SolicitudPalabraClave> updatePalabrasClave(Long solicitudId,
      List<SolicitudPalabraClave> palabrasClave) {
    log.debug("updatePalabrasClave(Long solicitudId, List<SolicitudPalabraClave> palabrasClave) - start");

    // Las Palabras Clave tienen el solicitudId especificado
    Assert.isTrue(
        palabrasClave.stream()
            .allMatch(palabraClave -> palabraClave.getSolicitudId() == null
                || palabraClave.getSolicitudId().equals(solicitudId)),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key("org.crue.hercules.sgi.csp.exceptions.NoRelatedEntitiesException.message")
            .parameter("entity", ApplicationContextSupport.getMessage(SolicitudPalabraClave.class))
            .parameter("related", ApplicationContextSupport.getMessage(Proyecto.class)).build());

    // Eliminamos las SolicitudPalabraClave existentes para el
    // solicitudId dado
    repository.deleteInBulkBySolicitudId(solicitudId);

    List<SolicitudPalabraClave> returnValue = new ArrayList<>();
    if (!palabrasClave.isEmpty()) {
      // Eliminamos duplicados de la nueva lista
      List<SolicitudPalabraClave> uniquePalabrasClave = palabrasClave.stream().distinct()
          .collect(Collectors.toList());
      // Guardamos la nueva lista
      returnValue = repository.saveAll(uniquePalabrasClave);
    }

    log.debug("updatePalabrasClave(Long solicitudId, List<SolicitudPalabraClave> palabrasClave) - end");
    return returnValue;
  }
}
