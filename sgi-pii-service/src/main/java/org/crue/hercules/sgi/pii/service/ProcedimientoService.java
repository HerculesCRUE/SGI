package org.crue.hercules.sgi.pii.service;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.TipoProcedimiento;
import org.crue.hercules.sgi.pii.exceptions.ProcedimientoNotFoundException;
import org.crue.hercules.sgi.pii.exceptions.TipoProcedimientoNotFoundException;
import org.crue.hercules.sgi.pii.model.Procedimiento;
import org.crue.hercules.sgi.pii.repository.ProcedimientoRepository;
import org.crue.hercules.sgi.pii.repository.TipoProcedimientoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Validated
@Slf4j
public class ProcedimientoService {

  private final ProcedimientoRepository procedimientoRepository;
  private final TipoProcedimientoRepository tipoProcedimientoRepository;

  /**
   * Obtener todas las entidades {@link Procedimiento} paginadas y/o filtradas.
   *
   * @param pageable Información de la paginación.
   * @param query    Información del/los filtros a aplicar.
   * @return Lista de entidades {@link Procedimiento} paginadas y/o filtradas.
   */
  public Page<Procedimiento> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<Procedimiento> specs = SgiRSQLJPASupport.toSpecification(query);

    log.debug("findAll(String query, Pageable pageable) - start");
    return this.procedimientoRepository.findAll(specs, pageable);
  }

  /**
   * Obtiene un {@link Procedimiento} por su id.
   *
   * @param id el id de la entidad {@link Procedimiento}.
   * @return la entidad {@link Procedimiento}.
   */
  public Procedimiento findById(Long id) throws ProcedimientoNotFoundException {

    return this.procedimientoRepository.findById(id).orElseThrow(() -> new ProcedimientoNotFoundException(id));
  }

  public Page<Procedimiento> findAllBySolicitudProteccionId(Long solicitudProteccionId, Pageable pageable) {

    return this.procedimientoRepository.findAllBySolicitudProteccionId(solicitudProteccionId, pageable);
  }

  /**
   * Crea un {@link Procedimiento}
   * 
   * @param procedimiento entidad a crear
   * @return Entidad creada
   */
  @Transactional
  public Procedimiento create(@Valid Procedimiento procedimiento) {
    log.debug("create(@Valid Procedimiento procedimiento) - start");

    Assert.isNull(procedimiento.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(Procedimiento.class)).build());

    Assert.notNull(procedimiento.getSolicitudProteccionId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("solicitudProteccionId"))
            .parameter("entity", ApplicationContextSupport.getMessage(Procedimiento.class)).build());

    final TipoProcedimiento tipoProcedimiento = tipoProcedimientoRepository
        .findById(procedimiento.getTipoProcedimiento().getId())
        .orElseThrow(() -> new TipoProcedimientoNotFoundException(procedimiento.getTipoProcedimiento().getId()));
    procedimiento.setTipoProcedimiento(tipoProcedimiento);

    Procedimiento returnValue = this.procedimientoRepository.save(procedimiento);

    log.debug("create(@Valid Procedimiento procedimiento) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link Procedimiento}.
   *
   * @param procedimiento la entidad {@link Procedimiento} a actualizar.
   * @return la entidad {@link Procedimiento} persistida.
   */
  @Transactional
  public Procedimiento update(@Valid Procedimiento procedimiento) {
    log.debug("update(@Valid Procedimiento procedimiento) - start");

    Assert.notNull(procedimiento.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(Procedimiento.class)).build());
    Assert.notNull(procedimiento.getSolicitudProteccionId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("solicitudProteccion"))
            .parameter("entity", ApplicationContextSupport.getMessage(Procedimiento.class)).build());
    Assert.notNull(procedimiento.getTipoProcedimiento(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field",
                ApplicationContextSupport.getMessage("org.crue.hercules.sgi.pii.model.Procedimiento.tipoProcedimiento"))
            .parameter("entity", ApplicationContextSupport.getMessage(Procedimiento.class)).build());

    return this.procedimientoRepository.findById(procedimiento.getId()).map(procedimientoExistente -> {

      procedimientoExistente.setAccionATomar(procedimiento.getAccionATomar());
      procedimientoExistente.setComentarios(procedimiento.getComentarios());
      procedimientoExistente.setFecha(procedimiento.getFecha());
      procedimientoExistente.setFechaLimiteAccion(procedimiento.getFechaLimiteAccion());
      procedimientoExistente.setGenerarAviso(procedimiento.getGenerarAviso());
      procedimientoExistente.setTipoProcedimiento(procedimiento.getTipoProcedimiento());

      Procedimiento returnValue = procedimientoRepository.saveAndFlush(procedimientoExistente);
      procedimientoRepository.refresh(returnValue);
      log.debug("update(@Valid Procedimiento procedimiento) - end");
      return returnValue;
    }).orElseThrow(() -> new ProcedimientoNotFoundException(procedimiento.getId()));
  }

  /**
   * Elimina un {@link Procedimiento} pasando su id
   * 
   * @param procedimiendoId Id del {@link Procedimiento} a eliminar
   */
  @Transactional
  public void delete(Long procedimiendoId) {
    this.procedimientoRepository.deleteById(procedimiendoId);
  }

}
