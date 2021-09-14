package org.crue.hercules.sgi.pii.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.TipoProcedimientoNotFoundException;
import org.crue.hercules.sgi.pii.model.TipoProcedimiento;
import org.crue.hercules.sgi.pii.model.TipoProcedimiento.OnActivar;
import org.crue.hercules.sgi.pii.repository.TipoProcedimientoRepository;
import org.crue.hercules.sgi.pii.repository.specification.TipoProcedimientoSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Validated
public class TipoProcedimientoService {

  private final TipoProcedimientoRepository tipoProcedimientoRepository;
  private final Validator validator;

  public Page<TipoProcedimiento> findActivos(String query, Pageable paging) {

    Specification<TipoProcedimiento> specs = TipoProcedimientoSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    return this.tipoProcedimientoRepository.findAll(specs, paging);
  }

  /**
   * Obtener todas las entidades {@link TipoProcedimiento} paginadas y/o
   * filtradas.
   *
   * @param pageable Información de la paginación.
   * @param query    Información del/los filtros a aplicar.
   * @return Lista de entidades {@link TipoProcedimiento} paginadas y/o filtradas.
   */
  public Page<TipoProcedimiento> findAll(String query, Pageable pageable) {

    Specification<TipoProcedimiento> specs = SgiRSQLJPASupport.toSpecification(query);

    return this.tipoProcedimientoRepository.findAll(specs, pageable);
  }

  /**
   * Crea un nuevo {@link TipoProcedimiento}.
   *
   * @param tipoProcedimiento la entidad {@link TipoProcedimiento} a guardar.
   * @return la entidad {@link TipoProcedimiento} persistida.
   */
  @Validated({ TipoProcedimiento.OnCrear.class })
  public TipoProcedimiento create(@Valid TipoProcedimiento tipoProcedimiento) {

    Assert.isNull(tipoProcedimiento.getId(),
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(TipoProcedimiento.class)).build());

    tipoProcedimiento.setActivo(true);

    return this.tipoProcedimientoRepository.save(tipoProcedimiento);
  }

  /**
   * Obtiene un {@link TipoProcedimiento} por su id.
   *
   * @param id el id de la entidad {@link TipoProcedimiento}.
   * @return la entidad {@link TipoProcedimiento}.
   */
  public TipoProcedimiento findById(Long id) {

    return this.tipoProcedimientoRepository.findById(id).orElseThrow(() -> new TipoProcedimientoNotFoundException(id));
  }

  /**
   * Activar {@link TipoProcedimiento}.
   *
   * @param id Id del {@link TipoProcedimiento}.
   * @return Entidad {@link TipoProcedimiento} persistida activada.
   */
  @Validated({ TipoProcedimiento.OnActivar.class })
  public TipoProcedimiento activar(Long id) {

    Assert.notNull(id,
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(TipoProcedimiento.class)).build());

    return this.tipoProcedimientoRepository.findById(id).map(tipoProcedimiento -> {
      if (tipoProcedimiento.getActivo()) {
        return tipoProcedimiento;
      }
      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<TipoProcedimiento>> result = validator.validate(tipoProcedimiento, OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      tipoProcedimiento.setActivo(true);

      return this.tipoProcedimientoRepository.save(tipoProcedimiento);

    }).orElseThrow(() -> new TipoProcedimientoNotFoundException(id));
  }

  /**
   * Desactiva el {@link TipoProcedimiento}.
   *
   * @param id Id del {@link TipoProcedimiento}.
   * @return Entidad {@link TipoProcedimiento} persistida desactivada.
   */
  public TipoProcedimiento desactivar(Long id) {

    Assert.notNull(id,
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(TipoProcedimiento.class)).build());

    return this.tipoProcedimientoRepository.findById(id).map(tipoProcedimiento -> {

      if (!tipoProcedimiento.getActivo()) {
        return tipoProcedimiento;
      }
      tipoProcedimiento.setActivo(false);

      return this.tipoProcedimientoRepository.save(tipoProcedimiento);
    }).orElseThrow(() -> new TipoProcedimientoNotFoundException(id));
  }

  @Validated({ TipoProcedimiento.OnActualizar.class })
  public TipoProcedimiento update(@Valid TipoProcedimiento tipoProcedimiento) {

    Assert.notNull(tipoProcedimiento.getId(),
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(TipoProcedimiento.class)).build());

    return this.tipoProcedimientoRepository.findById(tipoProcedimiento.getId()).map(foundedTipoProteccion -> {

      foundedTipoProteccion.setNombre(tipoProcedimiento.getNombre());
      foundedTipoProteccion.setDescripcion(tipoProcedimiento.getDescripcion());

      return this.tipoProcedimientoRepository.save(foundedTipoProteccion);

    }).orElseThrow(() -> new TipoProcedimientoNotFoundException(tipoProcedimiento.getId()));
  }

}
