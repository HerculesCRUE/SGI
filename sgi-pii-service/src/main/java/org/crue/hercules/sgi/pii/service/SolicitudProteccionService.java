package org.crue.hercules.sgi.pii.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.SolicitudProteccionNotFoundException;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion.OnActivar;
import org.crue.hercules.sgi.pii.repository.SolicitudProteccionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Validated
@Service
@RequiredArgsConstructor
@Slf4j
public class SolicitudProteccionService {

  private final SolicitudProteccionRepository solicitudProteccionRepository;
  private final Validator validator;

  /**
   * Obtiene una {@link SolicitudProteccion} por su id.
   *
   * @param id el id de la entidad {@link SolicitudProteccion}.
   * @return la entidad {@link SolicitudProteccion}.
   */
  public SolicitudProteccion findById(Long id) throws SolicitudProteccionNotFoundException {
    log.debug("findById(Long id)  - start");
    final SolicitudProteccion returnValue = this.solicitudProteccionRepository.findById(id)
        .orElseThrow(() -> new SolicitudProteccionNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Crea un nuevo {@link SolicitudProteccion}.
   *
   * @param solicitudProteccion la entidad {@link SolicitudProteccion} a guardar.
   * @return la entidad {@link SolicitudProteccion} persistida.
   */
  public SolicitudProteccion create(@Valid SolicitudProteccion solicitudProteccion) {

    Assert.isNull(solicitudProteccion.getId(),
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(SolicitudProteccion.class)).build());

    solicitudProteccion.setActivo(true);

    return this.solicitudProteccionRepository.save(solicitudProteccion);
  }

  /**
   * Activar {@link SolicitudProteccion}.
   *
   * @param id Id del {@link SolicitudProteccion}.
   * @return Entidad {@link SolicitudProteccion} persistida activada.
   */
  @Validated({ SolicitudProteccion.OnActivar.class })
  public SolicitudProteccion activar(Long id) {

    Assert.notNull(id,
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(SolicitudProteccion.class)).build());

    return this.solicitudProteccionRepository.findById(id).map(solicitudProteccion -> {
      if (solicitudProteccion.getActivo()) {
        return solicitudProteccion;
      }
      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<SolicitudProteccion>> result = validator.validate(solicitudProteccion, OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      solicitudProteccion.setActivo(true);

      return this.solicitudProteccionRepository.save(solicitudProteccion);

    }).orElseThrow(() -> new SolicitudProteccionNotFoundException(id));
  }

  /**
   * Desactiva el {@link SolicitudProteccion}.
   *
   * @param id Id del {@link SolicitudProteccion}.
   * @return Entidad {@link SolicitudProteccion} persistida desactivada.
   */
  public SolicitudProteccion desactivar(Long id) {

    Assert.notNull(id,
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(SolicitudProteccion.class)).build());

    return this.solicitudProteccionRepository.findById(id).map(solicitudProteccion -> {

      if (!solicitudProteccion.getActivo()) {
        return solicitudProteccion;
      }
      solicitudProteccion.setActivo(false);

      return this.solicitudProteccionRepository.save(solicitudProteccion);
    }).orElseThrow(() -> new SolicitudProteccionNotFoundException(id));
  }

  /**
   * Obtener todas las entidades {@link SolicitudProteccion} paginadas enlazadas a
   * una invención
   * 
   * @param invencionId id de la invención asociado a las solicitudes de
   *                    proteccion
   * @param paging      Información de la paginación.
   * @return Lista de entidades {@link SolicitudProteccion} paginadas
   */
  public Page<SolicitudProteccion> findByInvencionId(Long invencionId, Pageable paging) {
    return this.solicitudProteccionRepository.findByInvencionId(invencionId, paging);
  }
}
