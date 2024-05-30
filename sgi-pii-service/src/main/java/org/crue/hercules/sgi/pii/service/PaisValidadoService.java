package org.crue.hercules.sgi.pii.service;

import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_NOTNULL;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_PARAMETER_ENTITY;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_PARAMETER_FIELD;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.PaisValidadoNotFoundException;
import org.crue.hercules.sgi.pii.exceptions.SolicitudProteccionNotFoundException;
import org.crue.hercules.sgi.pii.model.PaisValidado;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion;
import org.crue.hercules.sgi.pii.repository.PaisValidadoRepository;
import org.crue.hercules.sgi.pii.repository.SolicitudProteccionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * PaisValidadoService
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class PaisValidadoService {

  private final PaisValidadoRepository repository;
  private final SolicitudProteccionRepository solicitudRepository;

  public PaisValidadoService(PaisValidadoRepository paisValidadoRepository,
      SolicitudProteccionRepository solicitudRepository) {
    this.repository = paisValidadoRepository;
    this.solicitudRepository = solicitudRepository;
  }

  /**
   * Obtiene los {@link PaisValidado} asociados a la {@link SolicitudProteccion}
   *
   * @param solicitudProteccionId el id de la {@link SolicitudProteccion}.
   * @param pageable              la información de página.
   * @return la lista paginada y/o filtrada {@link PaisValidado}.
   */
  public Page<PaisValidado> findBySolicitudProteccionId(Long solicitudProteccionId, Pageable pageable) {
    log.debug("findBySolicitudProteccionId(Long solicitudProteccionId, Pageable pageable) - start");

    Page<PaisValidado> result = this.repository.findBySolicitudProteccionId(solicitudProteccionId, pageable);

    log.debug("findBySolicitudProteccionId(Long solicitudProteccionId, Pageable pageable) - end");
    return result;
  }

  /**
   * Comprueba si la {@link SolicitudProteccion} tiene {@link PaisValidado}
   * asociados
   *
   * @param solicitudProteccionId el id de la {@link SolicitudProteccion}.
   * @return Si la {@link SolicitudProteccion} tiene {@link PaisValidado}
   *         asociados
   */
  public boolean existsBySolicitudProteccionId(Long solicitudProteccionId) {
    log.debug("existsBySolicitudProteccionId(Long solicitudProteccionId) - start");
    boolean result = this.repository.existsBySolicitudProteccionId(solicitudProteccionId);
    log.debug("existsBySolicitudProteccionId(Long solicitudProteccionId) - end");
    return result;
  }

  /**
   * Crear {@link PaisValidado}.
   *
   * @param paisValidado entidad {@link PaisValidado} a guardar.
   * @return entidad {@link PaisValidado} persistida.
   */
  @Transactional
  public PaisValidado create(PaisValidado paisValidado) {
    log.debug("create(PaisValidado paisValidado) - start");

    Assert.isNull(paisValidado.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(PaisValidado.class))
            .build());
    this.commonValidations(paisValidado);

    PaisValidado returnValue = this.repository.save(paisValidado);

    log.debug("create(PaisValidado paisValidado) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link PaisValidado}.
   *
   * @param paisValidado entidad {@link PaisValidado} a actualizar.
   * @return entidad {@link PaisValidado} persistida.
   */
  @Transactional
  public PaisValidado update(PaisValidado paisValidado) {
    log.debug("update(PaisValidado paisValidado) - start");

    this.commonValidations(paisValidado);

    return this.repository.findById(paisValidado.getId()).map(paisValidadoExistente -> {
      paisValidadoExistente.setCodigoInvencion(paisValidado.getCodigoInvencion());
      paisValidadoExistente.setFechaValidacion(paisValidado.getFechaValidacion());
      paisValidadoExistente.setPaisRef(paisValidado.getPaisRef());

      PaisValidado returnValue = this.repository.save(paisValidadoExistente);
      log.debug("update(PaisValidado paisValidado) - end");
      return returnValue;
    }).orElseThrow(() -> new PaisValidadoNotFoundException(paisValidado.getId()));
  }

  @Transactional
  public void deleteById(Long id) {
    log.debug("deleteById(Long id) - start");
    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(PaisValidado.class))
            .build());
    if (!this.repository.existsById(id)) {
      throw new PaisValidadoNotFoundException(id);
    }
    repository.deleteById(id);
    log.debug("deleteById(Long id) - end");
  }

  private void commonValidations(PaisValidado paisValidado) {
    Assert.notNull(paisValidado.getSolicitudProteccionId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD,
                ApplicationContextSupport
                    .getMessage("org.crue.hercules.sgi.pii.model.SolicitudProteccion.solicitudProteccion"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(PaisValidado.class))
            .build());
    Assert.notNull(paisValidado.getFechaValidacion(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD,
                ApplicationContextSupport.getMessage("org.crue.hercules.sgi.pii.model.PaisValidado.fechaValidacion"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(PaisValidado.class))
            .build());
    Assert.notNull(paisValidado.getCodigoInvencion(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD,
                ApplicationContextSupport.getMessage("org.crue.hercules.sgi.pii.model.PaisValidado.codigoInvencion"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(PaisValidado.class))
            .build());
    Assert.notNull(paisValidado.getPaisRef(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD,
                ApplicationContextSupport.getMessage("org.crue.hercules.sgi.pii.model.PaisValidado.paisRef"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(PaisValidado.class))
            .build());
    if (!this.solicitudRepository.existsById(paisValidado.getSolicitudProteccionId())) {
      throw new SolicitudProteccionNotFoundException(paisValidado.getSolicitudProteccionId());
    }
  }

}
