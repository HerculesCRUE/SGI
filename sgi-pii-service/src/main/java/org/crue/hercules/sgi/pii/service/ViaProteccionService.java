package org.crue.hercules.sgi.pii.service;

import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_NOTNULL;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_PARAMETER_ENTITY;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_PARAMETER_FIELD;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.ViaProteccionNotFoundException;
import org.crue.hercules.sgi.pii.model.ViaProteccion;
import org.crue.hercules.sgi.pii.model.ViaProteccion.OnActivar;
import org.crue.hercules.sgi.pii.repository.ViaProteccionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;

@Validated
@RequiredArgsConstructor
@Service
public class ViaProteccionService {

  private final ViaProteccionRepository viaProteccionRepository;
  private final Validator validator;

  /**
   * Obtener todas las entidades {@link ViaProteccion} paginadas y/o filtradas.
   *
   * @param pageable Información de la paginación.
   * @param query    Información del/los filtros a aplicar.
   * @return Lista de entidades {@link ViaProteccion} paginadas y/o filtradas.
   */
  public Page<ViaProteccion> findAll(String query, Pageable pageable) {

    return this.viaProteccionRepository.findAll(SgiRSQLJPASupport.toSpecification(query), pageable);
  }

  /**
   * Activar {@link ViaProteccion}.
   *
   * @param id Id del {@link ViaProteccion}.
   * @return Entidad {@link ViaProteccion} persistida activada.
   */
  @Validated({ ViaProteccion.OnActivar.class })
  public ViaProteccion activar(Long id) {

    Assert.notNull(id,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(ViaProteccion.class))
            .build());

    return this.viaProteccionRepository.findById(id).map(viaProteccion -> {
      if (viaProteccion.getActivo().booleanValue()) {
        return viaProteccion;
      }
      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<ViaProteccion>> result = validator.validate(viaProteccion, OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      viaProteccion.setActivo(true);

      return this.viaProteccionRepository.save(viaProteccion);

    }).orElseThrow(() -> new ViaProteccionNotFoundException(id));
  }

  /**
   * Desactiva el {@link ViaProteccion}.
   *
   * @param id Id del {@link ViaProteccion}.
   * @return Entidad {@link ViaProteccion} persistida desactivada.
   */
  public ViaProteccion desactivar(Long id) {

    Assert.notNull(id,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(ViaProteccion.class))
            .build());

    return this.viaProteccionRepository.findById(id).map(viaProteccion -> {

      if (!viaProteccion.getActivo().booleanValue()) {
        return viaProteccion;
      }
      viaProteccion.setActivo(false);

      return this.viaProteccionRepository.save(viaProteccion);
    }).orElseThrow(() -> new ViaProteccionNotFoundException(id));
  }

  /**
   * Crea un nuevo {@link ViaProteccion}.
   *
   * @param viaProteccion la entidad {@link ViaProteccion} a guardar.
   * @return la entidad {@link ViaProteccion} persistida.
   */
  @Validated({ ViaProteccion.OnCrear.class })
  public ViaProteccion create(@Valid ViaProteccion viaProteccion) {

    Assert.isNull(viaProteccion.getId(),
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(ViaProteccion.class))
            .build());

    viaProteccion.setActivo(true);

    return this.viaProteccionRepository.save(viaProteccion);
  }

  @Validated({ ViaProteccion.OnActualizar.class })
  public ViaProteccion update(@Valid ViaProteccion toUpdate) {

    Assert.notNull(toUpdate.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(ViaProteccion.class))
            .build());

    return this.viaProteccionRepository.findById(toUpdate.getId()).map(foundedVia -> {

      foundedVia.setDescripcion(toUpdate.getDescripcion());
      foundedVia.setNombre(toUpdate.getNombre());
      foundedVia.setExtensionInternacional(toUpdate.getExtensionInternacional());
      foundedVia.setMesesPrioridad(toUpdate.getMesesPrioridad());
      foundedVia.setPaisEspecifico(toUpdate.getPaisEspecifico());
      foundedVia.setTipoPropiedad(toUpdate.getTipoPropiedad());
      foundedVia.setVariosPaises(toUpdate.getVariosPaises());

      return this.viaProteccionRepository.save(foundedVia);
    }).orElseThrow(() -> new ViaProteccionNotFoundException(toUpdate.getId()));

  }
}
