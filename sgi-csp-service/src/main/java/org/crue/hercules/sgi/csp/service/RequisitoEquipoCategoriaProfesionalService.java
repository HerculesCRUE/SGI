package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.crue.hercules.sgi.csp.model.RequisitoEquipoCategoriaProfesional;
import org.crue.hercules.sgi.csp.repository.RequisitoEquipoCategoriaProfesionalRepository;
import org.crue.hercules.sgi.csp.repository.specification.RequisitoEquipoCategoriaProfesionalSpecifications;
import org.crue.hercules.sgi.csp.util.ConvocatoriaAuthorityHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Negocio de la entidad RequisitoEquipoCategoriaProfesional.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class RequisitoEquipoCategoriaProfesionalService {

  private final RequisitoEquipoCategoriaProfesionalRepository repository;
  private final ConvocatoriaAuthorityHelper authorityHelper;

  public RequisitoEquipoCategoriaProfesionalService(
      RequisitoEquipoCategoriaProfesionalRepository repository,
      ConvocatoriaAuthorityHelper authorityHelper) {
    this.repository = repository;
    this.authorityHelper = authorityHelper;
  }

  /**
   * Obtiene las {@link RequisitoEquipoCategoriaProfesional} para un
   * {@link RequisitoEquipo}.
   *
   * @param requisitoEquipoId el id del {@link RequisitoEquipo}.
   * @return la lista de {@link RequisitoEquipoCategoriaProfesional} del
   *         {@link RequisitoEquipo} paginadas.
   */
  public List<RequisitoEquipoCategoriaProfesional> findByRequisitoEquipo(Long requisitoEquipoId) {
    log.debug("findByRequisitoEquipo(Long requisitoEquipoId, String query, Pageable paging) - start");

    Specification<RequisitoEquipoCategoriaProfesional> specs = RequisitoEquipoCategoriaProfesionalSpecifications
        .byRequisitoEquipoId(requisitoEquipoId);

    List<RequisitoEquipoCategoriaProfesional> returnValue = repository.findAll(specs);
    log.debug("findByRequisitoEquipo(Long requisitoEquipoId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link RequisitoEquipoCategoriaProfesional} para una
   * {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @return la lista de {@link RequisitoEquipoCategoriaProfesional} de la
   *         {@link Convocatoria}.
   */
  public List<RequisitoEquipoCategoriaProfesional> findByConvocatoria(Long convocatoriaId) {
    log.debug("findByConvocatoria(Long convocatoriaId) - start");

    authorityHelper.checkUserHasAuthorityViewConvocatoria(convocatoriaId);

    // El id de la convocatoria y del requisito equipo son el mismo
    List<RequisitoEquipoCategoriaProfesional> returnValue = findByRequisitoEquipo(convocatoriaId);
    log.debug("findByConvocatoria(Long convocatoriaId) - end");
    return returnValue;
  }

  /**
   * Actualiza la lista de {@link RequisitoEquipoCategoriaProfesional} del
   * {@link RequisitoEquipo}, elimina los pre-existentes y añade la nueva lista.
   * 
   * @param requisitoEquipoId       el id del {@link RequisitoEquipo}.
   * @param categoriasProfesionales la lista con los nuevos niveles.
   * @return La lista actualizada de {@link RequisitoEquipoCategoriaProfesional}.
   */
  @Transactional
  public List<RequisitoEquipoCategoriaProfesional> updateCategoriasProfesionales(Long requisitoEquipoId,
      List<RequisitoEquipoCategoriaProfesional> categoriasProfesionales) {
    log.debug(
        "updateCategoriasProfesionales(Long requisitoEquipoId, List<RequisitoEquipoCategoriaProfesional> categoriasProfesionales) - start");

    // Las categoriasProfesionales tienen el requisitoEquipoId especificado
    Assert.isTrue(
        categoriasProfesionales.stream()
            .allMatch(categoriaProfesional -> categoriaProfesional.getRequisitoEquipoId() == null
                || categoriaProfesional.getRequisitoEquipoId().equals(requisitoEquipoId)),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key("org.crue.hercules.sgi.csp.exceptions.NoRelatedEntitiesException.message")
            .parameter("entity", ApplicationContextSupport.getMessage(RequisitoEquipoCategoriaProfesional.class))
            .parameter("related", ApplicationContextSupport.getMessage(RequisitoEquipo.class)).build());

    // Eliminamos las RequisitoEquipoCategoriaProfesional existentes para el
    // requisitoEquipoId dado
    repository.deleteInBulkByRequisitoEquipoId(requisitoEquipoId);

    List<RequisitoEquipoCategoriaProfesional> returnValue = new ArrayList<>();
    if (!categoriasProfesionales.isEmpty()) {
      // Eliminamos duplicados de la nueva lista
      List<RequisitoEquipoCategoriaProfesional> uniqueCategoriasProfesionales = categoriasProfesionales.stream()
          .distinct().collect(Collectors.toList());
      // Guardamos la nueva lista
      returnValue = repository.saveAll(uniqueCategoriasProfesionales);
    }

    log.debug(
        "updateCategoriasProfesionales(Long requisitoEquipoId, List<RequisitoEquipoCategoriaProfesional> categoriasProfesionales) - end");
    return returnValue;
  }
}