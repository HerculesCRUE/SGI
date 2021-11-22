package org.crue.hercules.sgi.pii.service;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.ProcedimientoDocumentoNotFoundException;
import org.crue.hercules.sgi.pii.model.Procedimiento;
import org.crue.hercules.sgi.pii.model.ProcedimientoDocumento;
import org.crue.hercules.sgi.pii.repository.ProcedimientoDocumentoRepository;
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
public class ProcedimientoDocumentoService {

  private final ProcedimientoDocumentoRepository procedimientoDocumentoRepository;

  /**
   * Obtener todas las entidades {@link ProcedimientoDocumento} paginadas y/o
   * filtradas.
   *
   * @param pageable Información de la paginación.
   * @param query    Información del/los filtros a aplicar.
   * @return Lista de entidades {@link ProcedimientoDocumento} paginadas y/o
   *         filtradas.
   */
  public Page<ProcedimientoDocumento> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<ProcedimientoDocumento> specs = SgiRSQLJPASupport.toSpecification(query);

    log.debug("findAll(String query, Pageable pageable) - start");
    return this.procedimientoDocumentoRepository.findAll(specs, pageable);
  }

  /**
   * Obtiene un {@link ProcedimientoDocumento} por su id.
   *
   * @param id el id de la entidad {@link ProcedimientoDocumento}.
   * @return la entidad {@link ProcedimientoDocumento}.
   */
  public ProcedimientoDocumento findById(Long id) throws ProcedimientoDocumentoNotFoundException {

    return this.procedimientoDocumentoRepository.findById(id)
        .orElseThrow(() -> new ProcedimientoDocumentoNotFoundException(id));
  }

  public Page<ProcedimientoDocumento> findAllByProcedimientoId(Long procedimientoId, Pageable pageable) {

    return this.procedimientoDocumentoRepository.findAllByProcedimientoId(procedimientoId, pageable);
  }

  @Transactional
  public ProcedimientoDocumento create(@Valid ProcedimientoDocumento procedimientoDocumento) {
    log.debug("create(@Valid ProcedimientoDocumento procedimientoDocumento) - start");

    Assert.isNull(procedimientoDocumento.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(ProcedimientoDocumento.class)).build());
    Assert.notNull(procedimientoDocumento.getDocumentoRef(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field",
                ApplicationContextSupport
                    .getMessage("org.crue.hercules.sgi.pii.model.ProcedimientoDocumento.documentoRef"))
            .parameter("entity", ApplicationContextSupport.getMessage(ProcedimientoDocumento.class)).build());
    Assert.notNull(procedimientoDocumento.getNombre(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field",
                ApplicationContextSupport.getMessage("org.crue.hercules.sgi.pii.model.ProcedimientoDocumento.nombre"))
            .parameter("entity", ApplicationContextSupport.getMessage(ProcedimientoDocumento.class)).build());
    Assert.notNull(procedimientoDocumento.getProcedimientoId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field",
                ApplicationContextSupport
                    .getMessage("org.crue.hercules.sgi.pii.model.ProcedimientoDocumento.procedimientoId"))
            .parameter("entity", ApplicationContextSupport.getMessage(ProcedimientoDocumento.class)).build());

    ProcedimientoDocumento returnValue = procedimientoDocumentoRepository.save(procedimientoDocumento);

    log.debug("create(@Valid ProcedimientoDocumento procedimientoDocumento) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ProcedimientoDocumento}.
   *
   * @param procedimientoDocumento la entidad {@link ProcedimientoDocumento} a
   *                               actualizar.
   * @return la entidad {@link ProcedimientoDocumento} persistida.
   */
  @Transactional
  public ProcedimientoDocumento update(@Valid ProcedimientoDocumento procedimientoDocumento) {
    log.debug("update(@Valid ProcedimientoDocumento procedimientoDocumento) - start");

    Assert.notNull(procedimientoDocumento.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(ProcedimientoDocumento.class)).build());
    Assert.notNull(procedimientoDocumento.getDocumentoRef(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("documentoRef"))
            .parameter("entity", ApplicationContextSupport.getMessage(ProcedimientoDocumento.class)).build());
    Assert.notNull(procedimientoDocumento.getNombre(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("nombre"))
            .parameter("entity", ApplicationContextSupport.getMessage(ProcedimientoDocumento.class)).build());
    Assert.notNull(procedimientoDocumento.getProcedimientoId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("procedimientoId"))
            .parameter("entity", ApplicationContextSupport.getMessage(ProcedimientoDocumento.class)).build());

    return this.procedimientoDocumentoRepository.findById(procedimientoDocumento.getId())
        .map(procedimientoDocumentoExistente -> {

          procedimientoDocumentoExistente.setNombre(procedimientoDocumento.getNombre());

          ProcedimientoDocumento returnValue = procedimientoDocumentoRepository.save(procedimientoDocumentoExistente);
          log.debug("update(@Valid ProcedimientoDocumento procedimientoDocumento) - end");
          return returnValue;
        }).orElseThrow(() -> new ProcedimientoDocumentoNotFoundException(procedimientoDocumento.getId()));
  }

  /**
   * Elimina un {@link ProcedimientoDocumento} pasando su id
   * 
   * @param procedimiendoDocumentoId Id del {@link ProcedimientoDocumento} a
   *                                 eliminar
   */
  @Transactional
  public void delete(Long procedimiendoDocumentoId) {
    this.procedimientoDocumentoRepository.deleteById(procedimiendoDocumentoId);
  }

  /**
   * Elimina los {@link ProcedimientoDocumento} asociados al {@link Procedimiento}
   * con el id pasado por parámetros
   * 
   * @param procedimiendoId Id del {@link Procedimiento}
   */
  @Transactional
  public void deleteByProcedimiento(Long procedimiendoId) {
    this.procedimientoDocumentoRepository.deleteAllByProcedimientoId(procedimiendoId);
  }

}
