package org.crue.hercules.sgi.pii.service;

import java.util.List;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.InvencionIngresoNotFoundException;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.InvencionIngreso;
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
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(InvencionIngreso.class)).build());
    Assert.notNull(invencionIngreso.getInvencionId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(Invencion.class)).build());

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
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(InvencionIngreso.class)).build());
    Assert.notNull(invencionIngreso.getInvencionId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(Invencion.class)).build());

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
}
