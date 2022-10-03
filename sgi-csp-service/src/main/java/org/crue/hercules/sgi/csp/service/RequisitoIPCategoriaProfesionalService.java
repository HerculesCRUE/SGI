package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.RequisitoIPCategoriaProfesionalNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessSolicitudException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.model.RequisitoIPCategoriaProfesional;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.repository.RequisitoIPCategoriaProfesionalRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.specification.RequisitoIPCategoriaProfesionalSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.ConvocatoriaAuthorityHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Negocio de la entidad RequisitoIPCategoriaProfesional.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class RequisitoIPCategoriaProfesionalService {

  private final RequisitoIPCategoriaProfesionalRepository repository;
  private final SolicitudRepository solicitudRepository;
  private final ConvocatoriaAuthorityHelper authorityHelper;

  public RequisitoIPCategoriaProfesionalService(
      RequisitoIPCategoriaProfesionalRepository repository,
      SolicitudRepository solicitudRepository,
      ConvocatoriaAuthorityHelper authorityHelper) {
    this.repository = repository;
    this.solicitudRepository = solicitudRepository;
    this.authorityHelper = authorityHelper;
  }

  /**
   * Obtiene las {@link RequisitoIPCategoriaProfesional} para un
   * {@link RequisitoIP}.
   *
   * @param requisitoIPId el id del {@link RequisitoIP}.
   * @return la lista de {@link RequisitoIPCategoriaProfesional} del
   *         {@link RequisitoIP} paginadas.
   */
  public List<RequisitoIPCategoriaProfesional> findByRequisitoIP(Long requisitoIPId) {
    log.debug("findByRequisitoIP(Long requisitoIPId) - start");

    Specification<RequisitoIPCategoriaProfesional> specs = RequisitoIPCategoriaProfesionalSpecifications
        .byRequisitoIPId(requisitoIPId);

    List<RequisitoIPCategoriaProfesional> returnValue = repository.findAll(specs);
    log.debug("findByRequisitoIP(Long requisitoIPId) - end");
    return returnValue;
  }

  /**
   * Actualiza la lista de {@link RequisitoIPCategoriaProfesional} del
   * {@link RequisitoIP}, elimina los pre-existentes y añade la nueva lista.
   * 
   * @param requisitoIPId           el id del {@link RequisitoIP}.
   * @param categoriasProfesionales la lista con los nuevos niveles.
   * @return La lista actualizada de {@link RequisitoIPCategoriaProfesional}.
   */
  @Transactional
  public List<RequisitoIPCategoriaProfesional> updateCategoriasProfesionales(Long requisitoIPId,
      List<RequisitoIPCategoriaProfesional> categoriasProfesionales) {
    log.debug(
        "updateCategoriasProfesionales(Long requisitoIPId, List<RequisitoIPCategoriaProfesional> categoriasProfesionales) - start");

    // Las categoriasProfesionales tienen el requisitoIPId especificado
    Assert.isTrue(
        categoriasProfesionales.stream()
            .allMatch(categoriaProfesional -> categoriaProfesional.getRequisitoIPId() == null
                || categoriaProfesional.getRequisitoIPId().equals(requisitoIPId)),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key("org.crue.hercules.sgi.csp.exceptions.NoRelatedEntitiesException.message")
            .parameter(AssertHelper.PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(RequisitoIPCategoriaProfesional.class))
            .parameter("related", ApplicationContextSupport.getMessage(RequisitoIP.class)).build());

    // Recuperamos las categorias profesionales existentes en base de datos
    List<RequisitoIPCategoriaProfesional> categoriasProfesionalesBD = repository.findByRequisitoIPId(requisitoIPId);

    List<Long> idsCategoriasProfesionalesActualizadas = categoriasProfesionales.stream()
        .map(RequisitoIPCategoriaProfesional::getId)
        .collect(Collectors.toList());

    // Categorias profesionales a eliminar (existen en base de datos pero no están
    // entre los que se van a modificar)
    List<RequisitoIPCategoriaProfesional> categoriasProfesionalesEliminar = categoriasProfesionalesBD.stream()
        .filter(categoriaProfesional -> !idsCategoriasProfesionalesActualizadas.contains(categoriaProfesional.getId()))
        .collect(Collectors.toList());

    // Eliminamos las categorias profesionales no existentes en la nueva lista
    if (!categoriasProfesionalesEliminar.isEmpty()) {
      repository.deleteAll(categoriasProfesionalesEliminar);
    }

    // Id's de categorias profesionales a crear (no tienen id)
    List<RequisitoIPCategoriaProfesional> categoriasProfesionalesCrear = categoriasProfesionales.stream()
        .filter(categoriaProfesional -> categoriaProfesional.getId() == null).collect(Collectors.toList());

    if (!categoriasProfesionalesCrear.isEmpty()) {
      // Eliminamos duplicados de la nueva lista
      List<RequisitoIPCategoriaProfesional> uniqueCategoriasProfesionales = categoriasProfesionalesCrear.stream()
          .distinct()
          .collect(Collectors.toList());
      // Guardamos la nueva lista
      repository.saveAll(uniqueCategoriasProfesionales);
    }

    List<RequisitoIPCategoriaProfesional> returnValue = repository.findByRequisitoIPId(requisitoIPId);

    log.debug(
        "updateCategoriasProfesionales(Long requisitoIPId, List<RequisitoIPCategoriaProfesional> categoriasProfesionales) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link RequisitoIPCategoriaProfesional} para un
   * {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @return la lista de {@link RequisitoIPCategoriaProfesional} de la
   *         {@link Convocatoria}.
   */
  public List<RequisitoIPCategoriaProfesional> findByConvocatoria(Long convocatoriaId) {
    log.debug("findByConvocatoria(Long requisitoIPId) - start");

    authorityHelper.checkUserHasAuthorityViewConvocatoria(convocatoriaId);

    Specification<RequisitoIPCategoriaProfesional> specs = RequisitoIPCategoriaProfesionalSpecifications
        .byConvocatoriaId(convocatoriaId);

    List<RequisitoIPCategoriaProfesional> returnValue = repository.findAll(specs);
    log.debug("findByConvocatoria(Long requisitoIPId) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link RequisitoIPCategoriaProfesional} de la
   * {@link Convocatoria} de la {@link Solicitud} si el usuario que realiza la
   * peticion es el solicitante de la {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @return la lista de {@link RequisitoIPCategoriaProfesional} de la
   *         {@link Convocatoria} de la {@link Solicitud}.
   */
  public List<RequisitoIPCategoriaProfesional> findBySolicitudAndUserIsSolicitante(Long solicitudId) {
    log.debug("findBySolicitudAndUserIsSolicitante(Long solicitudId) - start");

    String personaRef = SecurityContextHolder.getContext().getAuthentication().getName();

    Solicitud solicitud = solicitudRepository.findOne(SolicitudSpecifications.bySolicitante(personaRef).and(
        SolicitudSpecifications.byId(solicitudId)))
        .orElseThrow(UserNotAuthorizedToAccessSolicitudException::new);

    if (Objects.isNull(solicitud.getConvocatoriaId())) {
      return new ArrayList<>();
    }

    Specification<RequisitoIPCategoriaProfesional> specs = RequisitoIPCategoriaProfesionalSpecifications
        .byConvocatoriaId(solicitud.getConvocatoriaId());

    List<RequisitoIPCategoriaProfesional> returnValue = repository.findAll(specs);
    log.debug("findBySolicitudAndUserIsSolicitante(Long solicitudId) - end");
    return returnValue;
  }

  /**
   * Hace las comprobaciones necesarias para determinar si el
   * {@link RequisitoIPCategoriaProfesional} puede ser eliminado.
   *
   * @param requisitoIpId Id del {@link RequisitoIP}.
   * @param id            Id del {@link RequisitoIPCategoriaProfesional}.
   * @return <code>true</code> Si se permite / <code>false</code>
   *         Si no se permite
   */
  public boolean eliminable(Long requisitoIpId, Long id) {
    Specification<RequisitoIPCategoriaProfesional> specs = RequisitoIPCategoriaProfesionalSpecifications.byId(id)
        .and(RequisitoIPCategoriaProfesionalSpecifications.byRequisitoIPId(requisitoIpId));

    Optional<RequisitoIPCategoriaProfesional> requisitoIpNivelAcademico = repository.findOne(specs);
    if (!requisitoIpNivelAcademico.isPresent()) {
      throw new RequisitoIPCategoriaProfesionalNotFoundException(id);
    }

    specs = specs.and(RequisitoIPCategoriaProfesionalSpecifications.withRelatedEntities());
    return repository.count(specs) == 0;
  }

}