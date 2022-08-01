package org.crue.hercules.sgi.pii.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.InvencionPalabraClaveNotFoundException;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.InvencionPalabraClave;
import org.crue.hercules.sgi.pii.repository.InvencionPalabraClaveRepository;
import org.crue.hercules.sgi.pii.repository.specification.InvencionPalabraClaveSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link InvencionPalabraClave}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class InvencionPalabraClaveService {

  private final InvencionPalabraClaveRepository repository;

  public InvencionPalabraClaveService(InvencionPalabraClaveRepository invencionPalabraClaveRepository) {
    this.repository = invencionPalabraClaveRepository;
  }

  /**
   * Obtiene los {@link InvencionPalabraClave} para una {@link Invencion}
   * paginadas y/o filtradas.
   *
   * @param invencionId el id de la {@link Invencion}.
   * @param query       la información del filtro.
   * @param pageable    la información de la paginación.
   * @return la lista de {@link InvencionPalabraClave} de la
   *         {@link Invencion} paginadas y/o filtradas.
   */
  public Page<InvencionPalabraClave> findByInvencionId(Long invencionId, String query, Pageable pageable) {
    log.debug("findByInvencion(Long invencionId, String query, Pageable pageable) - start");

    Specification<InvencionPalabraClave> specs = InvencionPalabraClaveSpecifications.byInvencionId(invencionId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<InvencionPalabraClave> returnValue = repository.findAll(specs, pageable);
    log.debug("findByInvencion(Long invencionId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link InvencionPalabraClave} por su id.
   *
   * @param id el id de la entidad {@link InvencionPalabraClave}.
   * @return la entidad {@link InvencionPalabraClave}.
   */
  public InvencionPalabraClave findById(Long id) {
    log.debug("findById(Long id)  - start");
    final InvencionPalabraClave returnValue = repository.findById(id)
        .orElseThrow(() -> new InvencionPalabraClaveNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Actualiza la lista de {@link InvencionPalabraClave} de la
   * {@link Invencion}, elimina los pre-existentes y añade la nueva lista.
   * 
   * @param invencionId   el id del {@link Invencion}.
   * @param palabrasClave la lista con las nuevas palabras claves.
   * @return La lista actualizada de {@link InvencionPalabraClave}.
   */
  @Transactional
  public List<InvencionPalabraClave> updatePalabrasClave(Long invencionId,
      List<InvencionPalabraClave> palabrasClave) {
    log.debug("updatePalabrasClave(Long invencionId, List<InvencionPalabraClave> palabrasClave) - start");

    // Las Palabras Clave tienen el invencionId especificado
    Assert.isTrue(
        palabrasClave.stream()
            .allMatch(palabraClave -> palabraClave.getInvencionId() == null
                || palabraClave.getInvencionId().equals(invencionId)),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key("org.crue.hercules.sgi.csp.exceptions.NoRelatedEntitiesException.message")
            .parameter("entity", ApplicationContextSupport.getMessage(InvencionPalabraClave.class))
            .parameter("related", ApplicationContextSupport.getMessage(Invencion.class)).build());

    // Eliminamos las InvencionPalabraClave existentes para el
    // invencionId dado
    repository.deleteInBulkByInvencionId(invencionId);

    List<InvencionPalabraClave> returnValue = new ArrayList<>();
    if (!palabrasClave.isEmpty()) {
      // Eliminamos duplicados de la nueva lista
      List<InvencionPalabraClave> uniquePalabrasClave = palabrasClave.stream().distinct()
          .collect(Collectors.toList());
      // Guardamos la nueva lista
      returnValue = repository.saveAll(uniquePalabrasClave);
    }

    log.debug("updatePalabrasClave(Long invencionId, List<InvencionPalabraClave> palabrasClave) - end");
    return returnValue;
  }
}
