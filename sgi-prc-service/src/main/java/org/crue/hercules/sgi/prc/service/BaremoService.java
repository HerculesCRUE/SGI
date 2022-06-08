package org.crue.hercules.sgi.prc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.prc.exceptions.NoRelatedEntitiesException;
import org.crue.hercules.sgi.prc.model.Baremo;
import org.crue.hercules.sgi.prc.model.BaseEntity;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.repository.BaremoRepository;
import org.crue.hercules.sgi.prc.repository.specification.BaremoSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link Baremo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class BaremoService {

  private final Validator validator;
  private final BaremoRepository repository;

  public BaremoService(BaremoRepository baremoRepository, Validator validator) {
    this.repository = baremoRepository;
    this.validator = validator;
  }

  /**
   * Obtiene los {@link Baremo} para una {@link ConvocatoriaBaremacion}.
   *
   * @param convocatoriaBaremacionId el id de la {@link ConvocatoriaBaremacion}.
   * @param query                    la información del filtro.
   * @param pageable                 la información de la paginación.
   * @return la lista de {@link Baremo} de la
   *         {@link ConvocatoriaBaremacion}.
   */
  public Page<Baremo> findByConvocatoriaBaremacionId(Long convocatoriaBaremacionId, String query, Pageable pageable) {
    log.debug("findByConvocatoriaBaremacionId(Long convocatoriaBaremacionId, Pageable pageable) - start");

    Specification<Baremo> specs = BaremoSpecifications.byConvocatoriaBaremacionId(convocatoriaBaremacionId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<Baremo> returnValue = repository.findAll(specs, pageable);
    log.debug("findByConvocatoriaBaremacionId(Long convocatoriaBaremacionId, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Actualiza la lista de {@link Baremo} de la
   * {@link ConvocatoriaBaremacion}, elimina los pre-existentes y añade la nueva
   * lista.
   * 
   * @param convocatoriaBaremacionId el id del {@link ConvocatoriaBaremacion}.
   * @param baremos                  la lista con los nuevos
   *                                 {@link Baremo}.
   * @return La lista actualizada de {@link Baremo}.
   */
  @Transactional
  public List<Baremo> updateBaremos(Long convocatoriaBaremacionId,
      List<Baremo> baremos) {
    log.debug("updateBaremos(Long convocatoriaBaremacionId, List<Baremo> baremos) - start");

    baremos.stream().forEach(baremo -> {
      // El baremo tiene el convocatoriaBaremacionId especificado
      if (!baremo.getConvocatoriaBaremacionId().equals(convocatoriaBaremacionId)) {
        throw new NoRelatedEntitiesException(Baremo.class, ConvocatoriaBaremacion.class);
      }
      // Invocar validaciones asociadas Update
      Set<ConstraintViolation<Baremo>> result = validator.validate(baremo, BaseEntity.Update.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }
    });

    // Eliminamos los Baremo existentes para el convocatoriaBaremacionId dado
    repository.deleteInBulkByConvocatoriaBaremacionId(convocatoriaBaremacionId);

    List<Baremo> returnValue = new ArrayList<>();
    if (!baremos.isEmpty()) {
      // Eliminamos duplicados de la nueva lista
      List<Baremo> uniqueBaremos = baremos.stream().distinct()
          .collect(Collectors.toList());
      // Guardamos la nueva lista
      returnValue = repository.saveAll(uniqueBaremos);
    }

    log.debug("updateBaremos(Long convocatoriaBaremacionId, List<Baremo> baremos) - end");
    return returnValue;
  }
}
