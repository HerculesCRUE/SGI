package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.model.RequisitoIPNivelAcademico;
import org.crue.hercules.sgi.csp.repository.RequisitoIPNivelAcademicoRepository;
import org.crue.hercules.sgi.csp.repository.specification.RequisitoIPNivelAcademicoSpecifications;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Negocio de la entidad RequisitoIPNivelAcademico.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class RequisitoIPNivelAcademicoService {

  private final RequisitoIPNivelAcademicoRepository repository;

  public RequisitoIPNivelAcademicoService(RequisitoIPNivelAcademicoRepository repository) {
    this.repository = repository;
  }

  /**
   * Obtiene los {@link RequisitoIPNivelAcademico} para un {@link RequisitoIP}.
   *
   * @param requisitoIPId el id del {@link RequisitoIP}.
   * @return la lista de {@link RequisitoIPNivelAcademico} del {@link RequisitoIP}
   *         paginadas.
   */
  public List<RequisitoIPNivelAcademico> findByRequisitoIP(Long requisitoIPId) {
    log.debug("findByRequisitoIP(Long requisitoIPId, String query, Pageable paging) - start");

    Specification<RequisitoIPNivelAcademico> specs = RequisitoIPNivelAcademicoSpecifications
        .byRequisitoIPId(requisitoIPId);

    List<RequisitoIPNivelAcademico> returnValue = repository.findAll(specs);
    log.debug("findByRequisitoIP(Long requisitoIPId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Actualiza la lista de {@link RequisitoIPNivelAcademico} del
   * {@link RequisitoIP}, elimina los pre-existentes y añade la nueva lista.
   * 
   * @param requisitoIPId     el id del {@link RequisitoIP}.
   * @param nivelesAcademicos la lista con los nuevos niveles.
   * @return La lista actualizada de {@link RequisitoIPNivelAcademico}.
   */
  @Transactional
  public List<RequisitoIPNivelAcademico> updateNivelesAcademicos(Long requisitoIPId,
      List<RequisitoIPNivelAcademico> nivelesAcademicos) {
    log.debug("updateNivelesAcademicos(Long requisitoIPId, List<RequisitoIPNivelAcademico> nivelesAcademicos) - start");

    // Los nievelesAcademicos tienen el requisitoIPId especificado
    Assert.isTrue(
        nivelesAcademicos.stream()
            .allMatch(nivelAcademico -> nivelAcademico.getRequisitoIPId() == null
                || nivelAcademico.getRequisitoIPId().equals(requisitoIPId)),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key("org.crue.hercules.sgi.csp.exceptions.NoRelatedEntitiesException.message")
            .parameter("entity", ApplicationContextSupport.getMessage(RequisitoIPNivelAcademico.class))
            .parameter("related", ApplicationContextSupport.getMessage(RequisitoIP.class)).build());

    // Eliminamos los RequisitoIPNivelAcademico existentes para el requisitoIPId
    // dado
    repository.deleteInBulkByRequisitoIPId(requisitoIPId);

    List<RequisitoIPNivelAcademico> returnValue = new ArrayList<>();
    if (nivelesAcademicos != null && !nivelesAcademicos.isEmpty()) {
      // Eliminamos duplicados de la nueva lista
      List<RequisitoIPNivelAcademico> uniqueNivelesAcademicos = nivelesAcademicos.stream().distinct()
          .collect(Collectors.toList());
      // Guardamos la nueva lista
      returnValue = repository.saveAll(uniqueNivelesAcademicos);
    }

    log.debug("updateNivelesAcademicos(Long requisitoIPId, List<RequisitoIPNivelAcademico> nivelesAcademicos) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link RequisitoIPNivelAcademico} para un {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @return la lista de {@link RequisitoIPNivelAcademico} de la
   *         {@link Convocatoria}.
   */
  public List<RequisitoIPNivelAcademico> findByConvocatoria(Long convocatoriaId) {
    log.debug("findByConvocatoria(Long requisitoIPId) - start");

    Specification<RequisitoIPNivelAcademico> specs = RequisitoIPNivelAcademicoSpecifications
        .byConvocatoriaId(convocatoriaId);

    List<RequisitoIPNivelAcademico> returnValue = repository.findAll(specs);
    log.debug("findByConvocatoria(Long requisitoIPId) - end");
    return returnValue;
  }
}