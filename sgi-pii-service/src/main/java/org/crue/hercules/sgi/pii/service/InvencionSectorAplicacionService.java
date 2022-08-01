package org.crue.hercules.sgi.pii.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.InvencionSectorAplicacion;
import org.crue.hercules.sgi.pii.repository.InvencionSectorAplicacionRepository;
import org.crue.hercules.sgi.pii.repository.specification.InvencionSectorAplicacionSpecifications;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Negocio de la entidad InvencionSectorAplicacion.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class InvencionSectorAplicacionService {

  private final InvencionSectorAplicacionRepository repository;

  public InvencionSectorAplicacionService(InvencionSectorAplicacionRepository invencionSectorAplicacionRepository) {
    this.repository = invencionSectorAplicacionRepository;
  }

  /**
   * Obtiene los {@link InvencionSectorAplicacion} para una {@link Invencion}.
   *
   * @param invencionId el id de la {@link Invencion}.
   * @return la lista de {@link InvencionSectorAplicacion} de la
   *         {@link Invencion}.
   */
  public List<InvencionSectorAplicacion> findByInvencion(Long invencionId) {
    log.debug("findByInvencion(Long invencionId) - start");

    Specification<InvencionSectorAplicacion> specs = InvencionSectorAplicacionSpecifications.byInvencionId(invencionId);

    List<InvencionSectorAplicacion> returnValue = repository.findAll(specs);
    log.debug("findByInvencion(Long invencionId) - end");
    return returnValue;
  }

  /**
   * Actualiza la lista de {@link InvencionSectorAplicacion} de la
   * {@link Invencion}, elimina los pre-existentes y añade la nueva lista.
   * 
   * @param invencionId        el id del {@link Invencion}.
   * @param sectoresAplicacion la lista con los nuevos niveles.
   * @return La lista actualizada de {@link InvencionSectorAplicacion}.
   */
  @Transactional
  public List<InvencionSectorAplicacion> updateSectoresAplicacion(Long invencionId,
      List<InvencionSectorAplicacion> sectoresAplicacion) {
    log.debug("updateSectoresAplicacion(Long invencionId, List<InvencionSectorAplicacion> sectoresAplicacion) - start");

    // Los sectoresAplicacion tienen el invencionId especificado
    Assert.isTrue(
        sectoresAplicacion.stream()
            .allMatch(sectorAplicacion -> sectorAplicacion.getInvencionId() == null
                || sectorAplicacion.getInvencionId().equals(invencionId)),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key("org.crue.hercules.sgi.csp.exceptions.NoRelatedEntitiesException.message")
            .parameter("entity", ApplicationContextSupport.getMessage(InvencionSectorAplicacion.class))
            .parameter("related", ApplicationContextSupport.getMessage(Invencion.class)).build());

    // Eliminamos los InvencionSectorAplicacion existentes para el
    // invencionId dado
    repository.deleteInBulkByInvencionId(invencionId);

    List<InvencionSectorAplicacion> returnValue = new ArrayList<>();
    if (!sectoresAplicacion.isEmpty()) {
      // Eliminamos duplicados de la nueva lista
      List<InvencionSectorAplicacion> uniqueSectoresAplicacion = sectoresAplicacion.stream().distinct()
          .collect(Collectors.toList());
      // Guardamos la nueva lista
      returnValue = repository.saveAll(uniqueSectoresAplicacion);
    }

    log.debug("updateSectoresAplicacion(Long invencionId, List<InvencionSectorAplicacion> sectoresAplicacion) - end");
    return returnValue;
  }
}
