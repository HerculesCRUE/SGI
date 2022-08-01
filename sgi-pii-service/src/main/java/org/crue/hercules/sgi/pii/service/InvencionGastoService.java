package org.crue.hercules.sgi.pii.service;

import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_NOTNULL;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_PARAMETER_ENTITY;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_PARAMETER_FIELD;

import java.math.BigDecimal;
import java.util.List;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.InvencionGastoNotFoundException;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.InvencionGasto;
import org.crue.hercules.sgi.pii.model.InvencionGasto.Estado;
import org.crue.hercules.sgi.pii.model.RepartoGasto;
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
   * Obtiene {@link InvencionGasto} por su id.
   *
   * @param id el id de la entidad {@link InvencionGasto}.
   * @return la entidad {@link InvencionGasto}.
   */
  public InvencionGasto findById(Long id) {
    log.debug("findById(Long id)  - start");

    final InvencionGasto returnValue = repository.findById(id)
        .orElseThrow(() -> new InvencionGastoNotFoundException(id));

    log.debug("findById(Long id)  - end");
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
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(InvencionGasto.class))
            .build());
    Assert.notNull(invencionGasto.getInvencionId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Invencion.class))
            .build());

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
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(InvencionGasto.class))
            .build());
    Assert.notNull(invencionGasto.getInvencionId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Invencion.class))
            .build());

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

  /**
   * Consolida la entidad {@link InvencionGasto} restando el importe deducido en
   * el reparto, del importe pendiente de deducir. Puede actualizar la entidad
   * {@link InvencionGasto} o crearla sino existe
   * 
   * @param invencionGasto  entidad {@link InvencionGasto}.
   * @param importeDeducido importe deducido en el reparto.
   * @return entidad {@link InvencionGasto} con el importe pendiente de deducir
   *         consolidado.
   */
  @Transactional
  public InvencionGasto consolidate(InvencionGasto invencionGasto, BigDecimal importeDeducido) {
    log.debug("consolidate(InvencionGasto invencionGasto, BigDecimal importeDeducir) - start");

    final InvencionGasto returnValue;
    if (invencionGasto.getId() != null) {
      InvencionGasto invencionGastoExistente = findById(invencionGasto.getId());
      validateImportePendienteDeducir(invencionGastoExistente, importeDeducido);
      invencionGastoExistente
          .setImportePendienteDeducir(invencionGastoExistente.getImportePendienteDeducir().subtract(importeDeducido));
      invencionGastoExistente.setEstado(calculateGastoEstado(invencionGastoExistente));

      returnValue = repository.save(invencionGastoExistente);
    } else {
      validateImportePendienteDeducir(invencionGasto, importeDeducido);
      invencionGasto.setImportePendienteDeducir(invencionGasto.getImportePendienteDeducir().subtract(importeDeducido));
      invencionGasto.setEstado(calculateGastoEstado(invencionGasto));

      returnValue = repository.save(invencionGasto);
    }

    log.debug("consolidate(InvencionGasto invencionGasto, BigDecimal importeDeducir) - end");
    return returnValue;
  }

  private void validateImportePendienteDeducir(InvencionGasto invencionGasto, BigDecimal importeDeducir) {
    Assert.isTrue(invencionGasto.getImportePendienteDeducir().compareTo(importeDeducir) >= 0,
        () -> ProblemMessage.builder().key(Assert.class, "isTrueImportePendienteGreaterOrEqualThanImporte")
            .parameter("amount", ApplicationContextSupport.getMessage(RepartoGasto.class, "importeADeducir"))
            .parameter("pendingAmount",
                ApplicationContextSupport.getMessage(InvencionGasto.class, "importePendienteDeducir"))
            .build());
  }

  private Estado calculateGastoEstado(InvencionGasto invencionGasto) {
    switch (invencionGasto.getImportePendienteDeducir().compareTo(new BigDecimal("0.00"))) {
      case 0:
        return Estado.DEDUCIDO;
      case 1:
        return Estado.DEDUCIDO_PARCIALMENTE;
      default:
        return Estado.NO_DEDUCIDO;
    }
  }
}
