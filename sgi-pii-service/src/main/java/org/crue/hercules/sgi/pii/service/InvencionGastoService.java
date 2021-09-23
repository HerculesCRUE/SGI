package org.crue.hercules.sgi.pii.service;

import java.util.List;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.InvencionGastoNotFoundException;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.InvencionGasto;
import org.crue.hercules.sgi.pii.repository.InvencionGastoRepository;
import org.crue.hercules.sgi.pii.repository.specification.InvencionGastoSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Negocio de la entidad InvencionGasto.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class InvencionGastoService {

  private final InvencionGastoRepository repository;

  public InvencionGastoService(InvencionGastoRepository invencionGastoRepository) {
    this.repository = invencionGastoRepository;
  }

  /**
   * Obtiene los {@link InvencionGasto} para una {@link Invencion}.
   *
   * @param invencionId el id de la {@link Invencion}.
   * @return la lista de {@link InvencionGasto} de la {@link Invencion}.
   */
  public List<InvencionGasto> findByInvencionId(Long invencionId) {
    log.debug("findByInvencionId(Long invencionId) - start");

    Specification<InvencionGasto> specs = InvencionGastoSpecifications.byInvencionId(invencionId);

    List<InvencionGasto> returnValue = repository.findAll(specs);
    log.debug("findByInvencionId(Long invencionId) - end");
    return returnValue;
  }

  /**
   * Guardar un nuevo {@link InvencionGasto}.
   *
   * @param invencionGasto la entidad {@link InvencionGasto} a guardar.
   * @return la entidad {@link InvencionGasto} persistida.
   */
  @Transactional
  public InvencionGasto create(InvencionGasto invencionGasto) {
    log.debug("create(InvencionGasto invencionGasto) - start");

    Assert.isNull(invencionGasto.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(InvencionGasto.class)).build());
    Assert.notNull(invencionGasto.getInvencionId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(Invencion.class)).build());

    InvencionGasto returnValue = repository.save(invencionGasto);

    log.debug("create(InvencionGasto invencionGasto) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link InvencionGasto}.
   *
   * @param invencionGasto la entidad {@link InvencionGasto} a actualizar.
   * @return la entidad {@link InvencionGasto} persistida.
   */
  @Transactional
  public InvencionGasto update(InvencionGasto invencionGasto) {
    log.debug("update(InvencionGasto invencionGasto) - start");

    Assert.notNull(invencionGasto.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(InvencionGasto.class)).build());
    Assert.notNull(invencionGasto.getInvencionId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(Invencion.class)).build());

    return repository.findById(invencionGasto.getId()).map(invencionGastoExistente -> {

      // Establecemos los campos actualizables con los recibidos
      invencionGastoExistente.setInvencionId(invencionGasto.getInvencionId());
      invencionGastoExistente.setGastoRef(invencionGasto.getGastoRef());
      invencionGastoExistente.setSolicitudProteccionId(invencionGasto.getSolicitudProteccionId());
      invencionGastoExistente.setImportePendienteDeducir(invencionGasto.getImportePendienteDeducir());
      invencionGastoExistente.setEstado(invencionGasto.getEstado());

      // Actualizamos la entidad
      InvencionGasto returnValue = repository.save(invencionGastoExistente);
      log.debug("update(InvencionGasto invencionGasto) - end");
      return returnValue;
    }).orElseThrow(() -> new InvencionGastoNotFoundException(invencionGasto.getId()));
  }
}
