package org.crue.hercules.sgi.pii.service;

import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.enums.TipoPropiedad;
import org.crue.hercules.sgi.pii.exceptions.SolicitudProteccionNotFoundException;
import org.crue.hercules.sgi.pii.exceptions.ViaProteccionNotFoundException;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion.EstadoSolicitudProteccion;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion.OnActivar;
import org.crue.hercules.sgi.pii.model.ViaProteccion;
import org.crue.hercules.sgi.pii.repository.SolicitudProteccionRepository;
import org.crue.hercules.sgi.pii.repository.ViaProteccionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
  private final ViaProteccionRepository viaProteccionRepository;
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
  @Transactional
  @Validated({ SolicitudProteccion.OnCrear.class })
  public SolicitudProteccion create(@Valid SolicitudProteccion solicitudProteccion) {

    Assert.isNull(solicitudProteccion.getId(),
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(SolicitudProteccion.class)).build());

    solicitudProteccion.setActivo(true);

    return this.solicitudProteccionRepository.save(solicitudProteccion);
  }

  /**
   * Actualizar {@link SolicitudProteccion}.
   *
   * @param solicitudProteccion la entidad {@link SolicitudProteccion} a
   *                            actualizar.
   * @return la entidad {@link SolicitudProteccion} persistida.
   */
  @Transactional
  @Validated({ SolicitudProteccion.OnActualizar.class })
  public SolicitudProteccion update(@Valid SolicitudProteccion solicitudProteccion) {
    log.debug("update(@Valid SolicitudProteccion solicitudProteccion) - start");

    Assert.notNull(solicitudProteccion.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(SolicitudProteccion.class)).build());

    Assert.notNull(solicitudProteccion.getViaProteccion().getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage(ViaProteccion.class))
            .parameter("entity", ApplicationContextSupport.getMessage(SolicitudProteccion.class)).build());

    Optional<ViaProteccion> viaProteccionOptional = this.viaProteccionRepository
        .findById(solicitudProteccion.getViaProteccion().getId());
    viaProteccionOptional.filter(elem -> elem.getTipoPropiedad().equals(TipoPropiedad.INDUSTRIAL))
        .ifPresent(viaProteccion -> {
          Assert.notNull(solicitudProteccion.getEstado(),
              // Defer message resolution untill is needed
              () -> ProblemMessage.builder().key(Assert.class, "notNull")
                  .parameter("field",
                      ApplicationContextSupport
                          .getMessage("org.crue.hercules.sgi.pii.model.SolicitudProteccion.estado"))
                  .parameter("entity", ApplicationContextSupport.getMessage(SolicitudProteccion.class)).build());
          if (solicitudProteccion.getEstado().equals(EstadoSolicitudProteccion.CADUCADA)) {
            Assert.notNull(solicitudProteccion.getFechaCaducidad(),
                // Defer message resolution untill is needed
                () -> ProblemMessage.builder().key(Assert.class, "notNull")
                    .parameter("field", ApplicationContextSupport.getMessage("fechaCaducidad"))
                    .parameter("entity", ApplicationContextSupport.getMessage(SolicitudProteccion.class)).build());
            Assert.notNull(solicitudProteccion.getTipoCaducidad(),
                // Defer message resolution untill is needed
                () -> ProblemMessage.builder().key(Assert.class, "notNull")
                    .parameter("field", ApplicationContextSupport.getMessage("tipoCaducidad"))
                    .parameter("entity", ApplicationContextSupport.getMessage(SolicitudProteccion.class)).build());
            if (Boolean.TRUE.equals(viaProteccion.getPaisEspecifico())) {
              Assert.notNull(solicitudProteccion.getPaisProteccionRef(),
                  // Defer message resolution untill is needed
                  () -> ProblemMessage.builder().key(Assert.class, "notNull")
                      .parameter("field", ApplicationContextSupport.getMessage("pais"))
                      .parameter("entity", ApplicationContextSupport.getMessage(SolicitudProteccion.class)).build());
            }
          }
        });

    return this.solicitudProteccionRepository.findById(solicitudProteccion.getId())
        .map(solicitudProteccionExistente -> {
          solicitudProteccionExistente.setTitulo(solicitudProteccion.getTitulo());
          solicitudProteccionExistente.setFechaPrioridadSolicitud(solicitudProteccion.getFechaPrioridadSolicitud());
          if (!solicitudProteccionExistente.getViaProteccion().getId()
              .equals(solicitudProteccion.getViaProteccion().getId())) {
            Set<ConstraintViolation<SolicitudProteccion>> result = validator.validate(solicitudProteccion,
                SolicitudProteccion.OnActualizarViaProteccion.class);
            if (!result.isEmpty()) {
              throw new ConstraintViolationException(result);
            }
            ViaProteccion viaProteccionActualizar = this.viaProteccionRepository
                .findById(solicitudProteccion.getViaProteccion().getId())
                .orElseThrow(() -> new ViaProteccionNotFoundException(solicitudProteccion.getViaProteccion().getId()));
            solicitudProteccionExistente.setViaProteccion(viaProteccionActualizar);
          }
          solicitudProteccionExistente.setNumeroSolicitud(solicitudProteccion.getNumeroSolicitud());
          solicitudProteccionExistente.setComentarios(solicitudProteccion.getComentarios());
          solicitudProteccionExistente.setNumeroRegistro(solicitudProteccion.getNumeroRegistro());
          solicitudProteccionExistente
              .setFechaFinPriorPresFasNacRec(solicitudProteccion.getFechaFinPriorPresFasNacRec());

          if (solicitudProteccionExistente.getViaProteccion().getTipoPropiedad().equals(TipoPropiedad.INDUSTRIAL)) {
            solicitudProteccionExistente.setEstado(solicitudProteccion.getEstado());
            if (solicitudProteccionExistente.getEstado().equals(EstadoSolicitudProteccion.CADUCADA)) {
              solicitudProteccionExistente.setFechaCaducidad(solicitudProteccion.getFechaCaducidad());
              solicitudProteccionExistente.setTipoCaducidad(solicitudProteccion.getTipoCaducidad());
            }
          }
          solicitudProteccionExistente.setAgentePropiedadRef(solicitudProteccion.getAgentePropiedadRef());
          solicitudProteccionExistente.setPaisProteccionRef(solicitudProteccion.getPaisProteccionRef());
          solicitudProteccionExistente.setNumeroConcesion(solicitudProteccion.getNumeroConcesion());
          solicitudProteccionExistente.setFechaConcesion(solicitudProteccion.getFechaConcesion());
          solicitudProteccionExistente.setNumeroPublicacion(solicitudProteccion.getNumeroPublicacion());
          solicitudProteccionExistente.setFechaPublicacion(solicitudProteccion.getFechaPublicacion());

          // Actualizamos la entidad
          SolicitudProteccion returnValue = solicitudProteccionRepository.save(solicitudProteccionExistente);
          log.debug("update(@Valid SolicitudProteccion solicitudProteccion) - end");
          return returnValue;
        }).orElseThrow(() -> new SolicitudProteccionNotFoundException(solicitudProteccion.getId()));
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
      if (Boolean.TRUE.equals(solicitudProteccion.getActivo())) {
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

      if (Boolean.FALSE.equals(solicitudProteccion.getActivo())) {
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
