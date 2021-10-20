package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.crue.hercules.sgi.csp.model.RequisitoEquipoNivelAcademico;
import org.crue.hercules.sgi.csp.repository.RequisitoEquipoNivelAcademicoRepository;
import org.crue.hercules.sgi.csp.repository.specification.RequisitoEquipoNivelAcademicoSpecifications;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Negocio de la entidad RequisitoEquipoNivelAcademico.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class RequisitoEquipoNivelAcademicoService {

  private final RequisitoEquipoNivelAcademicoRepository repository;

  public RequisitoEquipoNivelAcademicoService(RequisitoEquipoNivelAcademicoRepository repository) {
    this.repository = repository;
  }

  /**
   * Obtiene los {@link RequisitoEquipoNivelAcademico} para un
   * {@link RequisitoEquipo}.
   *
   * @param requisitoEquipoId el id del {@link RequisitoEquipo}.
   * @return la lista de {@link RequisitoEquipoNivelAcademico} del
   *         {@link RequisitoEquipo} paginadas.
   */
  public List<RequisitoEquipoNivelAcademico> findByRequisitoEquipo(Long requisitoEquipoId) {
    log.debug("findByRequisitoEquipo(Long requisitoEquipoId, String query, Pageable paging) - start");

    Specification<RequisitoEquipoNivelAcademico> specs = RequisitoEquipoNivelAcademicoSpecifications
        .byRequisitoEquipoId(requisitoEquipoId);

    List<RequisitoEquipoNivelAcademico> returnValue = repository.findAll(specs);
    log.debug("findByRequisitoEquipo(Long requisitoEquipoId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Actualiza la lista de {@link RequisitoEquipoNivelAcademico} del
   * {@link RequisitoEquipo}, elimina los pre-existentes y añade la nueva lista.
   * 
   * @param requisitoEquipoId el id del {@link RequisitoEquipo}.
   * @param nivelesAcademicos la lista con los nuevos niveles.
   * @return La lista actualizada de {@link RequisitoEquipoNivelAcademico}.
   */
  @Transactional
  public List<RequisitoEquipoNivelAcademico> updateNivelesAcademicos(Long requisitoEquipoId,
      List<RequisitoEquipoNivelAcademico> nivelesAcademicos) {
    log.debug(
        "updateNivelesAcademicos(Long requisitoEquipoId, List<RequisitoEquipoNivelAcademico> nivelesAcademicos) - start");

    // Los nievelesAcademicos tienen el requisitoEquipoId especificado
    Assert.isTrue(
        nivelesAcademicos.stream()
            .allMatch(nivelAcademico -> nivelAcademico.getRequisitoEquipoId() == null
                || nivelAcademico.getRequisitoEquipoId().equals(requisitoEquipoId)),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key("org.crue.hercules.sgi.csp.exceptions.NoRelatedEntitiesException.message")
            .parameter("entity", ApplicationContextSupport.getMessage(RequisitoEquipoNivelAcademico.class))
            .parameter("related", ApplicationContextSupport.getMessage(RequisitoEquipo.class)).build());

    // Eliminamos los RequisitoEquipoNivelAcademico existentes para el
    // requisitoEquipoId dado
    repository.deleteInBulkByRequisitoEquipoId(requisitoEquipoId);

    List<RequisitoEquipoNivelAcademico> returnValue = new ArrayList<>();
    if (nivelesAcademicos != null && !nivelesAcademicos.isEmpty()) {
      // Eliminamos duplicados de la nueva lista
      List<RequisitoEquipoNivelAcademico> uniqueNivelesAcademicos = nivelesAcademicos.stream().distinct()
          .collect(Collectors.toList());
      // Guardamos la nueva lista
      returnValue = repository.saveAll(uniqueNivelesAcademicos);
    }

    log.debug(
        "updateNivelesAcademicos(Long requisitoEquipoId, List<RequisitoEquipoNivelAcademico> nivelesAcademicos) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link RequisitoEquipoNivelAcademico} para una
   * {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @return la lista de {@link RequisitoEquipoNivelAcademico} de la
   *         {@link Convocatoria}.
   */
  public List<RequisitoEquipoNivelAcademico> findByConvocatoria(Long convocatoriaId) {
    log.debug("findByConvocatoria(Long convocatoriaId) - start");

    Specification<RequisitoEquipoNivelAcademico> specs = RequisitoEquipoNivelAcademicoSpecifications
        .byConvocatoriaId(convocatoriaId);

    List<RequisitoEquipoNivelAcademico> returnValue = repository.findAll(specs);
    log.debug("findByConvocatoria(Long convocatoriaId) - end");
    return returnValue;
  }

}