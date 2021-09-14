package org.crue.hercules.sgi.pii.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.InvencionAreaConocimiento;
import org.crue.hercules.sgi.pii.repository.InvencionAreaConocimientoRepository;
import org.crue.hercules.sgi.pii.repository.specification.InvencionAreaConocimientoSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Negocio de la entidad InvencionAreaConocimiento.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class InvencionAreaConocimientoService {

  private final InvencionAreaConocimientoRepository repository;

  public InvencionAreaConocimientoService(InvencionAreaConocimientoRepository invencionAreaConocimientoRepository) {
    this.repository = invencionAreaConocimientoRepository;
  }

  /**
   * Obtiene los {@link InvencionAreaConocimiento} para una {@link Invencion}.
   *
   * @param invencionId el id de la {@link Invencion}.
   * @return la lista de {@link InvencionAreaConocimiento} de la
   *         {@link Invencion}.
   */
  public List<InvencionAreaConocimiento> findByInvencion(Long invencionId) {
    log.debug("findByInvencion(Long requisitoIPId) - start");

    Specification<InvencionAreaConocimiento> specs = InvencionAreaConocimientoSpecifications.byInvencionId(invencionId);

    List<InvencionAreaConocimiento> returnValue = repository.findAll(specs);
    log.debug("findByInvencion(Long requisitoIPId) - end");
    return returnValue;
  }

  /**
   * Actualiza la lista de {@link InvencionAreaConocimiento} de la
   * {@link Invencion}, elimina los pre-existentes y añade la nueva lista.
   * 
   * @param invencionId       el id del {@link Invencion}.
   * @param areasConocimiento la lista con los nuevos niveles.
   * @return La lista actualizada de {@link InvencionAreaConocimiento}.
   */
  @Transactional
  public List<InvencionAreaConocimiento> updateAreasConocimiento(Long invencionId,
      List<InvencionAreaConocimiento> areasConocimiento) {
    log.debug("updateAreasConocimiento(Long invencionId, List<InvencionAreaConocimiento> areasConocimiento) - start");

    // Las areasConocimiento tienen el invencionId especificado
    Assert.isTrue(
        areasConocimiento.stream()
            .allMatch(areaConocimiento -> areaConocimiento.getInvencionId() == null
                || areaConocimiento.getInvencionId().equals(invencionId)),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key("org.crue.hercules.sgi.csp.exceptions.NoRelatedEntitiesException.message")
            .parameter("entity", ApplicationContextSupport.getMessage(InvencionAreaConocimiento.class))
            .parameter("related", ApplicationContextSupport.getMessage(Invencion.class)).build());

    // Eliminamos los InvencionAreaConocimiento existentes para el
    // invencionId dado
    repository.deleteInBulkByInvencionId(invencionId);

    List<InvencionAreaConocimiento> returnValue = new ArrayList<>();
    if (areasConocimiento != null && !areasConocimiento.isEmpty()) {
      // Eliminamos duplicados de la nueva lista
      List<InvencionAreaConocimiento> uniqueSectoresAplicacion = areasConocimiento.stream().distinct()
          .collect(Collectors.toList());
      // Guardamos la nueva lista
      returnValue = repository.saveAll(uniqueSectoresAplicacion);
    }

    log.debug("updateAreasConocimiento(Long invencionId, List<InvencionAreaConocimiento> areasConocimiento) - end");
    return returnValue;
  }
}
