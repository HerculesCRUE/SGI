package org.crue.hercules.sgi.pii.service;

import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_NOTNULL;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_PARAMETER_ENTITY;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_PARAMETER_FIELD;

import java.math.BigDecimal;
import java.util.List;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.InvencionIngresoNotFoundException;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.InvencionIngreso;
import org.crue.hercules.sgi.pii.model.InvencionIngreso.Estado;
import org.crue.hercules.sgi.pii.model.RepartoIngreso;
import org.crue.hercules.sgi.pii.repository.InvencionIngresoRepository;
import org.crue.hercules.sgi.pii.repository.specification.InvencionIngresoSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Negocio de la entidad InvencionIngreso.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class InvencionIngresoService {

  private final InvencionIngresoRepository repository;

  public InvencionIngresoService(InvencionIngresoRepository invencionIngresoRepository) {
    this.repository = invencionIngresoRepository;
  }

  /**
   * Obtiene los {@link InvencionIngreso} para una {@link Invencion}.
   *
   * @param invencionId el id de la {@link Invencion}.
   * @return la lista de {@link InvencionIngreso} de la {@link Invencion}.
   */
  public List<InvencionIngreso> findByInvencionId(Long invencionId) {
    log.debug("findByInvencionId(Long invencionId) - start");

    Specification<InvencionIngreso> specs = InvencionIngresoSpecifications.byInvencionId(invencionId);

    List<InvencionIngreso> returnValue = repository.findAll(specs);
    log.debug("findByInvencionId(Long invencionId) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link InvencionIngreso} por su id.
   *
   * @param id el id de la entidad {@link InvencionIngreso}.
   * @return la entidad {@link InvencionIngreso}.
   */
  public InvencionIngreso findById(Long id) {
    log.debug("findById(Long id)  - start");

    final InvencionIngreso returnValue = repository.findById(id)
        .orElseThrow(() -> new InvencionIngresoNotFoundException(id));

    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Guardar un nuevo {@link InvencionIngreso}.
   *
   * @param invencionIngreso la entidad {@link InvencionIngreso} a guardar.
   * @return la entidad {@link InvencionIngreso} persistida.
   */
  @Transactional
  public InvencionIngreso create(InvencionIngreso invencionIngreso) {
    log.debug("create(InvencionIngreso invencionIngreso) - start");

    Assert.isNull(invencionIngreso.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(InvencionIngreso.class))
            .build());
    Assert.notNull(invencionIngreso.getInvencionId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Invencion.class))
            .build());

    InvencionIngreso returnValue = repository.save(invencionIngreso);

    log.debug("create(InvencionIngreso invencionIngreso) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link InvencionIngreso}.
   *
   * @param invencionIngreso la entidad {@link InvencionIngreso} a actualizar.
   * @return la entidad {@link InvencionIngreso} persistida.
   */
  @Transactional
  public InvencionIngreso update(InvencionIngreso invencionIngreso) {
    log.debug("update(InvencionIngreso invencionIngreso) - start");

    Assert.notNull(invencionIngreso.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(InvencionIngreso.class))
            .build());
    Assert.notNull(invencionIngreso.getInvencionId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Invencion.class))
            .build());

    return repository.findById(invencionIngreso.getId()).map(invencionIngresoExistente -> {

      // Establecemos los campos actualizables con los recibidos
      invencionIngresoExistente.setInvencionId(invencionIngreso.getInvencionId());
      invencionIngresoExistente.setIngresoRef(invencionIngreso.getIngresoRef());
      invencionIngresoExistente.setEstado(invencionIngreso.getEstado());
      invencionIngresoExistente.setImportePendienteRepartir(invencionIngreso.getImportePendienteRepartir());

      // Actualizamos la entidad
      InvencionIngreso returnValue = repository.save(invencionIngresoExistente);
      log.debug("update(InvencionIngreso invencionIngreso) - end");
      return returnValue;
    }).orElseThrow(() -> new InvencionIngresoNotFoundException(invencionIngreso.getId()));
  }

  /**
   * Consolida la entidad {@link InvencionIngreso} restando el importe repartido
   * en el reparto, del importe pendiente de repartir. Puede actualizar la entidad
   * {@link InvencionIngreso} o crearla sino existe
   * 
   * @param invencionIngreso entidad {@link InvencionIngreso}.
   * @param importeRepartido importe repartido en el reparto.
   * @return entidad {@link InvencionIngreso} con el importe pendiente de repartir
   *         consolidado.
   */
  @Transactional
  public InvencionIngreso consolidate(InvencionIngreso invencionIngreso, BigDecimal importeRepartido) {
    log.debug("consolidate(InvencionIngreso invencionIngreso, BigDecimal importeRepartido) - start");

    final InvencionIngreso returnValue;
    if (invencionIngreso.getId() != null) {
      InvencionIngreso invencionIngresoExistente = findById(invencionIngreso.getId());
      validateImportePendienteRepartir(invencionIngresoExistente, importeRepartido);
      invencionIngresoExistente.setImportePendienteRepartir(
          invencionIngresoExistente.getImportePendienteRepartir().subtract(importeRepartido));
      invencionIngresoExistente.setEstado(calculateGastoEstado(invencionIngresoExistente));

      returnValue = repository.save(invencionIngresoExistente);
    } else {
      validateImportePendienteRepartir(invencionIngreso, importeRepartido);
      invencionIngreso
          .setImportePendienteRepartir(invencionIngreso.getImportePendienteRepartir().subtract(importeRepartido));
      invencionIngreso.setEstado(calculateGastoEstado(invencionIngreso));

      returnValue = repository.save(invencionIngreso);
    }

    log.debug("consolidate(InvencionIngreso invencionIngreso, BigDecimal importeRepartido) - end");
    return returnValue;
  }

  private void validateImportePendienteRepartir(InvencionIngreso invencionIngreso, BigDecimal importeRepartir) {
    Assert.isTrue(invencionIngreso.getImportePendienteRepartir().compareTo(importeRepartir) >= 0,
        () -> ProblemMessage.builder().key(Assert.class, "isTrueImportePendienteGreaterOrEqualThanImporte")
            .parameter("amount", ApplicationContextSupport.getMessage(RepartoIngreso.class, "importeARepartir"))
            .parameter("pendingAmount",
                ApplicationContextSupport.getMessage(InvencionIngreso.class, "importePendienteRepartir"))
            .build());
  }

  private Estado calculateGastoEstado(InvencionIngreso invencionGasto) {
    switch (invencionGasto.getImportePendienteRepartir().compareTo(new BigDecimal("0.00"))) {
      case 0:
        return Estado.REPARTIDO;
      case 1:
        return Estado.REPARTIDO_PARCIALMENTE;
      default:
        return Estado.NO_REPARTIDO;
    }
  }
}
