package org.crue.hercules.sgi.pii.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.PeriodoTitularidadNotFoundException;
import org.crue.hercules.sgi.pii.model.PeriodoTitularidad;
import org.crue.hercules.sgi.pii.model.PeriodoTitularidadTitular;
import org.crue.hercules.sgi.pii.repository.PeriodoTitularidadRepository;
import org.crue.hercules.sgi.pii.repository.PeriodoTitularidadTitularRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar los {@link PeriodoTitularidadTitular}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class PeriodoTitularidadTitularService {

  private final PeriodoTitularidadTitularRepository repository;
  private final PeriodoTitularidadRepository periodoTitularidadrepository;

  public PeriodoTitularidadTitularService(PeriodoTitularidadTitularRepository periodoTitularidadTitularRepository,
      PeriodoTitularidadRepository periodoTitularidadrepository) {
    this.repository = periodoTitularidadTitularRepository;
    this.periodoTitularidadrepository = periodoTitularidadrepository;
  }

  /**
   * Primero elimina y luego guarda los {@link PeriodoTitularidadTitular} pasados
   * por parámetro. Se realiza de forma transaccional de manera que si falla la
   * operación en algún elemento, se revierte la operación completa, respetando de
   * esta forma el criterio previamente validado relativo a la suma de la
   * participación de los {@link PeriodoTitularidadTitular}
   *
   * @param periodoTitularidadTitulares las entidades
   *                                    {@link PeriodoTitularidadTitular} a
   *                                    modificar/crear/eliminar.
   * @param periodoTitularidadId        Id de la {@link PeriodoTitularidad}
   * @return las entidades {@link PeriodoTitularidadTitular} persistidas.
   */
  @Transactional
  public List<PeriodoTitularidadTitular> saveUpdateOrDeleteBatchMode(Long periodoTitularidadId,
      List<PeriodoTitularidadTitular> periodoTitularidadTitulares) {
    log.debug(
        "saveUpdateOrDeleteBatchMode(Long periodoTitularidadId, List<PeriodoTitularidadTitular> periodoTitularidadTitulares) - start");

    periodoTitularidadrepository.findById(periodoTitularidadId)
        .orElseThrow(() -> new PeriodoTitularidadNotFoundException(periodoTitularidadId));

    final Double totalParticipacion = periodoTitularidadTitulares.stream()
        .mapToDouble(el -> el.getParticipacion().doubleValue()).sum();

    Assert.isTrue(totalParticipacion == 100,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder()
            .key("org.crue.hercules.sgi.pii.model.PeriodoTitularidadTitular.participacion.completa")
            .parameter("entity", ApplicationContextSupport.getMessage(PeriodoTitularidadTitular.class)).build());

    periodoTitularidadTitulares.forEach(elem -> {
      commonEntityValidations(elem);
      elem.setPeriodoTitularidadId(periodoTitularidadId);
    });

    repository.deleteInBatch(this.repository.findAllByPeriodoTitularidadIdAndTitularRefNotIn(periodoTitularidadId,
        periodoTitularidadTitulares.stream().map(elem -> elem.getTitularRef()).collect(Collectors.toList())));

    final Map<String, PeriodoTitularidadTitular> existingTitularesMap = this.repository
        .findAllByPeriodoTitularidadId(periodoTitularidadId).stream()
        .collect(Collectors.toMap(PeriodoTitularidadTitular::getTitularRef, Function.identity()));

    periodoTitularidadTitulares.forEach(received -> {
      if (existingTitularesMap.containsKey(received.getTitularRef())) {
        existingTitularesMap.get(received.getTitularRef()).setParticipacion(received.getParticipacion());
      } else {
        existingTitularesMap.put(received.getTitularRef(), received);
      }
    });

    List<PeriodoTitularidadTitular> returnValue = repository.saveAll(existingTitularesMap.values());

    log.debug(
        "saveUpdateOrDeleteBatchMode(Long periodoTitularidadId, List<PeriodoTitularidadTitular> periodoTitularidadTitulares) - end");
    return returnValue;
  }

  private void commonEntityValidations(PeriodoTitularidadTitular periodoTitularidadTitular) {
    Assert.notNull(periodoTitularidadTitular.getTitularRef(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field",
                ApplicationContextSupport
                    .getMessage("org.crue.hercules.sgi.pii.model.PeriodoTitularidadTitular.getTitularRef"))
            .parameter("entity", ApplicationContextSupport.getMessage(PeriodoTitularidadTitular.class)).build());

    Assert.notNull(periodoTitularidadTitular.getParticipacion(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field",
                ApplicationContextSupport
                    .getMessage("org.crue.hercules.sgi.pii.model.PeriodoTitularidadTitular.participacion"))
            .parameter("entity", ApplicationContextSupport.getMessage(PeriodoTitularidadTitular.class)).build());
  }

  /**
   * Devuelve todos los {@link PeriodoTitularidadTitular} asociados al
   * {@link PeriodoTitularidad}
   * 
   * @param periodoTitularidadId el identificador de referencia para la búsqueda
   * @return la lista de {@link PeriodoTitularidadTitular}
   */
  public List<PeriodoTitularidadTitular> findAllByPeriodoTitularidadId(Long periodoTitularidadId)
      throws PeriodoTitularidadNotFoundException {
    periodoTitularidadrepository.findById(periodoTitularidadId)
        .orElseThrow(() -> new PeriodoTitularidadNotFoundException(periodoTitularidadId));

    return this.repository.findAllByPeriodoTitularidadId(periodoTitularidadId);
  }

}
