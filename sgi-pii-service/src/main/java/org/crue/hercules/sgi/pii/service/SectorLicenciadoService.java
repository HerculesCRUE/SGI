package org.crue.hercules.sgi.pii.service;

import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_NOTNULL;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_PARAMETER_ENTITY;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_PARAMETER_FIELD;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.SectorLicenciadoNotFoundException;
import org.crue.hercules.sgi.pii.model.SectorAplicacion;
import org.crue.hercules.sgi.pii.model.SectorLicenciado;
import org.crue.hercules.sgi.pii.repository.SectorLicenciadoRepository;
import org.crue.hercules.sgi.pii.repository.specification.SectorLicenciadoSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link SectorLicenciado}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class SectorLicenciadoService {

  private final Validator validator;

  private final SectorLicenciadoRepository repository;

  public SectorLicenciadoService(Validator validator, SectorLicenciadoRepository sectorLicenciadoRepository) {
    this.validator = validator;
    this.repository = sectorLicenciadoRepository;
  }

  /**
   * Obtener todas las entidades {@link SectorLicenciado} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link SectorLicenciado} paginadas y/o
   *         filtradas.
   */
  public Page<SectorLicenciado> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<SectorLicenciado> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<SectorLicenciado> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link SectorLicenciado} para un Contrato.
   *
   * @param contratoRef el id del Contrato.
   * @return la lista de {@link SectorLicenciado} del Contrato.
   */
  public List<SectorLicenciado> findByContratoRef(String contratoRef) {
    log.debug("findByContratoRef(String contratoRef) - start");

    Specification<SectorLicenciado> specs = SectorLicenciadoSpecifications.byContratoRef(contratoRef);

    List<SectorLicenciado> returnValue = repository.findAll(specs);
    log.debug("findByContratoRef(String contratoRef) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link SectorLicenciado} por su id.
   *
   * @param id el id de la entidad {@link SectorLicenciado}.
   * @return la entidad {@link SectorLicenciado}.
   */
  public SectorLicenciado findById(Long id) {
    log.debug("findById(Long id)  - start");
    final SectorLicenciado returnValue = repository.findById(id)
        .orElseThrow(() -> new SectorLicenciadoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Guardar un nuevo {@link SectorLicenciado}.
   *
   * @param sectorLicenciado la entidad {@link SectorLicenciado} a guardar.
   * @return la entidad {@link SectorLicenciado} persistida.
   */
  @Transactional
  @Validated({ SectorLicenciado.OnCrear.class })
  public SectorLicenciado create(@Valid SectorLicenciado sectorLicenciado) {
    log.debug("create(SectorLicenciado sectorLicenciado) - start");

    Assert.isNull(sectorLicenciado.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(SectorLicenciado.class))
            .build());
    Assert.notNull(sectorLicenciado.getSectorAplicacion(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(SectorAplicacion.class))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(SectorLicenciado.class))
            .build());

    SectorLicenciado returnValue = repository.save(sectorLicenciado);

    log.debug("create(SectorLicenciado sectorLicenciado) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link SectorLicenciado}.
   *
   * @param sectorLicenciado la entidad {@link SectorLicenciado} a actualizar.
   * @return la entidad {@link SectorLicenciado} persistida.
   */
  @Transactional
  public SectorLicenciado update(SectorLicenciado sectorLicenciado) {
    log.debug("update(SectorLicenciado sectorLicenciado) - start");

    Assert.notNull(sectorLicenciado.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(SectorLicenciado.class))
            .build());
    Assert.notNull(sectorLicenciado.getSectorAplicacion(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(SectorAplicacion.class))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(SectorLicenciado.class))
            .build());

    return repository.findById(sectorLicenciado.getId()).map(sectorLicenciadoExistente -> {

      if (!sectorLicenciadoExistente.getSectorAplicacion().getId()
          .equals(sectorLicenciado.getSectorAplicacion().getId())) {
        // Si estamos mofificando el SectorAplicacion invocar validaciones asociadas
        // a OnActualizarSectorAplicacion
        Set<ConstraintViolation<SectorLicenciado>> result = validator.validate(sectorLicenciado,
            SectorLicenciado.OnActualizarSectorAplicacion.class);
        if (!result.isEmpty()) {
          throw new ConstraintViolationException(result);
        }
      }

      // Establecemos los campos actualizables con los recibidos
      sectorLicenciadoExistente.setFechaInicioLicencia(sectorLicenciado.getFechaInicioLicencia());
      sectorLicenciadoExistente.setFechaFinLicencia(sectorLicenciado.getFechaFinLicencia());
      sectorLicenciadoExistente.setInvencionId(sectorLicenciado.getInvencionId());
      sectorLicenciadoExistente.setSectorAplicacion(sectorLicenciado.getSectorAplicacion());
      sectorLicenciadoExistente.setContratoRef(sectorLicenciado.getContratoRef());
      sectorLicenciadoExistente.setPaisRef(sectorLicenciado.getPaisRef());
      sectorLicenciadoExistente.setExclusividad(sectorLicenciado.getExclusividad());

      // Actualizamos la entidad
      SectorLicenciado returnValue = repository.save(sectorLicenciadoExistente);
      log.debug("update(SectorLicenciado sectorLicenciado) - end");
      return returnValue;
    }).orElseThrow(() -> new SectorLicenciadoNotFoundException(sectorLicenciado.getId()));
  }

  /**
   * Elimina {@link SectorLicenciado} por id.
   * 
   * @param id El id de la entidad {@link SectorLicenciado}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");
    Assert.notNull(id, // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(SectorLicenciado.class))
            .build());
    if (!repository.existsById(id)) {
      throw new SectorLicenciadoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }
}
