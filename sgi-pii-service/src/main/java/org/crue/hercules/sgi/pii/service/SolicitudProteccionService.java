package org.crue.hercules.sgi.pii.service;

import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_NOTNULL;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_PARAMETER_ENTITY;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_PARAMETER_FIELD;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.config.SgiConfigProperties;
import org.crue.hercules.sgi.pii.dto.InvencionDto.SolicitudProteccionDto;
import org.crue.hercules.sgi.pii.enums.TipoPropiedad;
import org.crue.hercules.sgi.pii.exceptions.SolicitudProteccionNoDeletableException;
import org.crue.hercules.sgi.pii.exceptions.SolicitudProteccionNotFoundException;
import org.crue.hercules.sgi.pii.exceptions.ViaProteccionNotFoundException;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion.EstadoSolicitudProteccion;
import org.crue.hercules.sgi.pii.model.ViaProteccion;
import org.crue.hercules.sgi.pii.repository.InvencionGastoRepository;
import org.crue.hercules.sgi.pii.repository.PaisValidadoRepository;
import org.crue.hercules.sgi.pii.repository.ProcedimientoRepository;
import org.crue.hercules.sgi.pii.repository.SolicitudProteccionRepository;
import org.crue.hercules.sgi.pii.repository.ViaProteccionRepository;
import org.crue.hercules.sgi.pii.repository.specification.InvencionGastoSpecifications;
import org.crue.hercules.sgi.pii.repository.specification.PaisValidadoSpecifications;
import org.crue.hercules.sgi.pii.repository.specification.ProcedimientoSpecifications;
import org.crue.hercules.sgi.pii.repository.specification.SolicitudProteccionSpecifications;
import org.crue.hercules.sgi.pii.util.PeriodDateUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
  private final PaisValidadoRepository paisValidadoRepository;
  private final ProcedimientoRepository procedimientoRepository;
  private final InvencionGastoRepository invencionGastoRepository;
  private final Validator validator;
  private final SgiConfigProperties sgiConfigProperties;

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
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(SolicitudProteccion.class))
            .build());

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
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(SolicitudProteccion.class))
            .build());

    Assert.notNull(solicitudProteccion.getViaProteccion().getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(ViaProteccion.class))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(SolicitudProteccion.class))
            .build());

    Optional<ViaProteccion> viaProteccionOptional = this.viaProteccionRepository
        .findById(solicitudProteccion.getViaProteccion().getId());
    viaProteccionOptional.filter(elem -> elem.getTipoPropiedad().equals(TipoPropiedad.INDUSTRIAL))
        .ifPresent(viaProteccion -> {
          Assert.notNull(solicitudProteccion.getEstado(),
              // Defer message resolution untill is needed
              () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
                  .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD,
                      ApplicationContextSupport
                          .getMessage("org.crue.hercules.sgi.pii.model.SolicitudProteccion.estado"))
                  .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                      ApplicationContextSupport.getMessage(SolicitudProteccion.class))
                  .build());
          if (solicitudProteccion.getEstado().equals(EstadoSolicitudProteccion.CADUCADA)) {
            Assert.notNull(solicitudProteccion.getFechaCaducidad(),
                // Defer message resolution untill is needed
                () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
                    .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("fechaCaducidad"))
                    .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                        ApplicationContextSupport.getMessage(SolicitudProteccion.class))
                    .build());
            Assert.notNull(solicitudProteccion.getTipoCaducidad(),
                // Defer message resolution untill is needed
                () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
                    .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("tipoCaducidad"))
                    .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                        ApplicationContextSupport.getMessage(SolicitudProteccion.class))
                    .build());
            if (Boolean.TRUE.equals(viaProteccion.getPaisEspecifico())) {
              Assert.notNull(solicitudProteccion.getPaisProteccionRef(),
                  // Defer message resolution untill is needed
                  () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
                      .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("pais"))
                      .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                          ApplicationContextSupport.getMessage(SolicitudProteccion.class))
                      .build());
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
   * Elimina la {@link SolicitudProteccion}.
   *
   * @param id Id del {@link SolicitudProteccion}.
   */
  public void delete(Long id) {

    Assert.notNull(id,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(SolicitudProteccion.class))
            .build());

    if (!this.solicitudProteccionRepository.existsById(id)) {
      throw new SolicitudProteccionNotFoundException(id);
    }

    if (invencionGastoRepository.count(InvencionGastoSpecifications.bySolicitudProteccionId(id)) > 0
        || paisValidadoRepository.count(PaisValidadoSpecifications.bySolicitudProteccionId(id)) > 0
        || procedimientoRepository.count(ProcedimientoSpecifications.bySolicitudProteccionId(id)) > 0) {
      throw new SolicitudProteccionNoDeletableException(id);
    }

    this.solicitudProteccionRepository.deleteById(id);
  }

  /**
   * Obtener todas las entidades {@link SolicitudProteccion} paginadas enlazadas a
   * una invención
   * 
   * @param invencionId id de la invención asociado a las solicitudes de
   *                    proteccion
   * @param query       Parámetros de búsqueda
   * @param paging      Información de la paginación.
   * @return Lista de entidades {@link SolicitudProteccion} paginadas
   */
  public Page<SolicitudProteccion> findByInvencionId(Long invencionId, String query, Pageable paging) {
    Specification<SolicitudProteccion> specs = SolicitudProteccionSpecifications.byInvencionId(invencionId)
        .and(SgiRSQLJPASupport.toSpecification(query));
    return this.solicitudProteccionRepository.findAll(specs, paging);
  }

  /**
   * Devuelve una lista de {@link SolicitudProteccionDto} que están en el rango de
   * baremación
   * 
   * @param invencionId id de {@link Invencion}
   * @param anioInicio  año inicio de baremación
   * @param anioFin     año fin de baremación
   * 
   * @return Lista de {@link SolicitudProteccionDto}
   */
  public List<SolicitudProteccionDto> findSolicitudProteccionInRangoBaremacion(Long invencionId,
      Integer anioInicio, Integer anioFin) {

    Instant fechaInicioBaremacion = PeriodDateUtil.calculateFechaInicioBaremacionByAnio(
        anioInicio, sgiConfigProperties.getTimeZone());

    Instant fechaFinBaremacion = PeriodDateUtil.calculateFechaFinBaremacionByAnio(
        anioFin, sgiConfigProperties.getTimeZone());

    return solicitudProteccionRepository.findSolicitudProteccionInRangoBaremacion(invencionId, fechaInicioBaremacion,
        fechaFinBaremacion);
  }

}
