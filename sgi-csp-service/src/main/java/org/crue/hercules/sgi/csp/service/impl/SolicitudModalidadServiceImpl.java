package org.crue.hercules.sgi.csp.service.impl;

import java.util.Objects;
import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.ProgramaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudModalidadNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudModalidad;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadConvocanteRepository;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudModalidadRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudModalidadSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudModalidadService;
import org.crue.hercules.sgi.csp.service.SolicitudService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.SolicitudAuthorityHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link Solicitud}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudModalidadServiceImpl implements SolicitudModalidadService {

  private static final String MODALIDAD_NOT_VALID = "La modalidad seleccionada no pertenece al arbol del programa de la convocatoria";

  private final SolicitudModalidadRepository repository;
  private final SolicitudRepository solicitudRepository;
  private final ProgramaRepository programaRepository;
  private final ConvocatoriaEntidadConvocanteRepository convocatoriaEntidadConvocanteRepository;
  private final SolicitudService solicitudService;
  private final SolicitudAuthorityHelper authorityHelper;

  public SolicitudModalidadServiceImpl(SolicitudModalidadRepository repository, SolicitudRepository solicitudRepository,
      ProgramaRepository programaRepository,
      ConvocatoriaEntidadConvocanteRepository convocatoriaEntidadConvocanteRepository,
      SolicitudService solicitudService,
      SolicitudAuthorityHelper authorityHelper) {
    this.repository = repository;
    this.solicitudRepository = solicitudRepository;
    this.programaRepository = programaRepository;
    this.convocatoriaEntidadConvocanteRepository = convocatoriaEntidadConvocanteRepository;
    this.solicitudService = solicitudService;
    this.authorityHelper = authorityHelper;
  }

  /**
   * Guarda la entidad {@link SolicitudModalidad}.
   * 
   * @param solicitudModalidad la entidad {@link SolicitudModalidad} a guardar.
   * @return solicitudModalidad la entidad {@link SolicitudModalidad} persistida.
   */
  @Override
  @Transactional
  public SolicitudModalidad create(SolicitudModalidad solicitudModalidad) {
    log.debug("create(SolicitudModalidad solicitudModalidad) - start");

    Assert.isNull(solicitudModalidad.getId(),
        "SolicitudModalidad id tiene que ser null para crear una SolicitudModalidad");
    Assert.notNull(solicitudModalidad.getSolicitudId(),
        "Solicitud id no puede ser null para crear una SolicitudModalidad");
    Assert.notNull(solicitudModalidad.getPrograma().getId(),
        "Programa id no puede ser null para crear una SolicitudModalidad");

    Solicitud solicitud = solicitudRepository.findById(solicitudModalidad.getSolicitudId())
        .orElseThrow(() -> new SolicitudNotFoundException(solicitudModalidad.getSolicitudId()));

    solicitudModalidad.setPrograma(programaRepository.findById(solicitudModalidad.getPrograma().getId())
        .orElseThrow(() -> new ProgramaNotFoundException(solicitudModalidad.getPrograma().getId())));

    // Comprobar que la modalidad seleccionada esta en el arbol que tiene como nodo
    // raiz el programa seleccionado en la ConvocatoriaEntidadConvocante
    Optional<ConvocatoriaEntidadConvocante> convocatoriaEntidadConvocante = convocatoriaEntidadConvocanteRepository
        .findByConvocatoriaIdAndEntidadRefAndProgramaId(solicitud.getConvocatoriaId(),
            solicitudModalidad.getEntidadRef(), solicitudModalidad.getProgramaConvocatoriaId());

    Assert.isTrue(convocatoriaEntidadConvocante.isPresent(),
        "No existe ninguna ConvocatoriaEntidadConvocante con el entidadRef para la convocatoria seleccionada");

    Assert.isTrue(
        isModalidadDescencientePrograma(solicitudModalidad.getPrograma(),
            convocatoriaEntidadConvocante.get().getPrograma()),
        MODALIDAD_NOT_VALID);

    SolicitudModalidad returnValue = repository.save(solicitudModalidad);

    log.debug("create(SolicitudModalidad solicitudModalidad) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link SolicitudModalidad}.
   * 
   * @param solicitudModalidad {@link SolicitudModalidad} con los datos
   *                           actualizados.
   * @return solicitudModalidad {@link SolicitudModalidad} actualizado.
   */
  @Override
  @Transactional
  public SolicitudModalidad update(SolicitudModalidad solicitudModalidad) {
    log.debug("update(SolicitudModalidad solicitudModalidad) - start");

    Assert.notNull(solicitudModalidad.getId(), "Id no puede ser null para actualizar SolicitudModalidad");
    Assert.notNull(solicitudModalidad.getSolicitudId(),
        "La solicitud no puede ser null para actualizar la SolicitudModalidad");
    Assert.notNull(solicitudModalidad.getPrograma().getId(),
        "Id Programa no puede ser null para crear la SolicitudModalidad");

    solicitudModalidad.setPrograma(programaRepository.findById(solicitudModalidad.getPrograma().getId())
        .orElseThrow(() -> new ProgramaNotFoundException(solicitudModalidad.getPrograma().getId())));

    // comprobar si la solicitud es modificable
    Assert.isTrue(solicitudService.modificable(solicitudModalidad.getSolicitudId()),
        "No se puede modificar SolicitudModalidad");

    return repository.findById(solicitudModalidad.getId()).map(data -> {

      Solicitud solicitud = solicitudRepository.findById(data.getSolicitudId())
          .orElseThrow(() -> new SolicitudNotFoundException(data.getSolicitudId()));

      // Comprobar que la modalidad seleccionada esta en el arbol que tiene como nodo
      // raiz el programa seleccionado en la ConvocatoriaEntidadConvocante
      Optional<ConvocatoriaEntidadConvocante> convocatoriaEntidadConvocante = convocatoriaEntidadConvocanteRepository
          .findByConvocatoriaIdAndEntidadRefAndProgramaId(solicitud.getConvocatoriaId(),
              data.getEntidadRef(), solicitudModalidad.getProgramaConvocatoriaId());

      // Comprobar que la modalidad seleccionada no es el nodo raiz del arbol
      Assert.isTrue(
          !solicitudModalidad.getPrograma().getId().equals(convocatoriaEntidadConvocante.get().getPrograma().getId()),
          "La modalidad seleccionada es el nodo raiz del arbol");

      Assert.isTrue(
          isModalidadDescencientePrograma(solicitudModalidad.getPrograma(),
              convocatoriaEntidadConvocante.get().getPrograma()),
          MODALIDAD_NOT_VALID);

      data.setPrograma(solicitudModalidad.getPrograma());
      SolicitudModalidad returnValue = repository.save(data);

      log.debug("update(SolicitudModalidad solicitudModalidad) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitudModalidadNotFoundException(solicitudModalidad.getId()));
  }

  /**
   * Elimina el {@link SolicitudModalidad}.
   *
   * @param id Id del {@link SolicitudModalidad}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "SolicitudModalidad id no puede ser null para eliminar un SolicitudModalidad");
    if (!repository.existsById(id)) {
      throw new SolicitudModalidadNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene una entidad {@link SolicitudModalidad} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudModalidad}.
   * @return solicitudModalidad la entidad {@link SolicitudModalidad}.
   */
  @Override
  public SolicitudModalidad findById(Long id) {
    log.debug("findById(Long id) - start");
    final SolicitudModalidad returnValue = repository.findById(id)
        .orElseThrow(() -> new SolicitudModalidadNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link SolicitudModalidad} para una {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @param query       la información del filtro.
   * @param paging      la información de la paginación.
   * @return la lista de entidades {@link SolicitudModalidad} de la
   *         {@link Solicitud} paginadas.
   */
  @Override
  public Page<SolicitudModalidad> findAllBySolicitud(Long solicitudId, String query, Pageable paging) {
    log.debug("findAllBySolicitud(Long solicitudId, String query, Pageable paging) - start");

    Specification<SolicitudModalidad> specs = SolicitudModalidadSpecifications.bySolicitudId(solicitudId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudModalidad> returnValue = repository.findAll(specs, paging);
    log.debug("findAllBySolicitud(Long solicitudId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Guarda la entidad {@link SolicitudModalidad}.
   * 
   * @param solicitudPublicId  el id de la {@link Solicitud}.
   * @param solicitudModalidad la entidad {@link SolicitudModalidad} a guardar.
   * @return solicitudModalidad la entidad {@link SolicitudModalidad} persistida.
   */
  @Override
  @Transactional
  public SolicitudModalidad createByExternalUser(String solicitudPublicId, SolicitudModalidad solicitudModalidad) {
    log.debug("createByExternalUser(String solicitudPublicId, SolicitudModalidad solicitudModalidad) - start");

    Solicitud solicitud = authorityHelper.getSolicitudByPublicId(solicitudPublicId);
    authorityHelper.checkExternalUserHasAuthorityModifySolicitud(solicitud);
    solicitudModalidad.setSolicitudId(solicitud.getId());

    Assert.isNull(solicitudModalidad.getId(),
        "SolicitudModalidad id tiene que ser null para crear una SolicitudModalidad");
    Assert.notNull(solicitudModalidad.getPrograma().getId(),
        "Programa id no puede ser null para crear una SolicitudModalidad");

    solicitudModalidad.setPrograma(programaRepository.findById(solicitudModalidad.getPrograma().getId())
        .orElseThrow(() -> new ProgramaNotFoundException(solicitudModalidad.getPrograma().getId())));

    // Comprobar que la modalidad seleccionada esta en el arbol que tiene como nodo
    // raiz el programa seleccionado en la ConvocatoriaEntidadConvocante
    Optional<ConvocatoriaEntidadConvocante> convocatoriaEntidadConvocante = convocatoriaEntidadConvocanteRepository
        .findByConvocatoriaIdAndEntidadRefAndProgramaId(solicitud.getConvocatoriaId(),
            solicitudModalidad.getEntidadRef(), solicitudModalidad.getProgramaConvocatoriaId());

    Assert.isTrue(convocatoriaEntidadConvocante.isPresent(),
        "No existe ninguna ConvocatoriaEntidadConvocante con el entidadRef para la convocatoria seleccionada");

    Assert.isTrue(
        isModalidadDescencientePrograma(solicitudModalidad.getPrograma(),
            convocatoriaEntidadConvocante.get().getPrograma()),
        MODALIDAD_NOT_VALID);

    SolicitudModalidad returnValue = repository.save(solicitudModalidad);

    log.debug("createByExternalUser(String solicitudPublicId, SolicitudModalidad solicitudModalidad) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link SolicitudModalidad}.
   * 
   * @param solicitudPublicId  el id de la {@link Solicitud}.
   * @param solicitudModalidad {@link SolicitudModalidad} con los datos
   *                           actualizados.
   * @return solicitudModalidad {@link SolicitudModalidad} actualizado.
   */
  @Override
  @Transactional
  public SolicitudModalidad updateByExternalUser(String solicitudPublicId, SolicitudModalidad solicitudModalidad) {
    log.debug("updateByExternalUser(String solicitudPublicId, SolicitudModalidad solicitudModalidad) - start");

    Assert.notNull(solicitudModalidad.getId(), "Id no puede ser null para actualizar SolicitudModalidad");
    Assert.notNull(solicitudModalidad.getSolicitudId(),
        "La solicitud no puede ser null para actualizar la SolicitudModalidad");
    Assert.notNull(solicitudModalidad.getPrograma().getId(),
        "Id Programa no puede ser null para crear la SolicitudModalidad");

    solicitudModalidad.setPrograma(programaRepository.findById(solicitudModalidad.getPrograma().getId())
        .orElseThrow(() -> new ProgramaNotFoundException(solicitudModalidad.getPrograma().getId())));

    // comprobar si la solicitud es modificable
    Assert.isTrue(solicitudService.modificable(solicitudModalidad.getSolicitudId()),
        "No se puede modificar SolicitudModalidad");

    return repository.findById(solicitudModalidad.getId()).map(data -> {

      Solicitud solicitud = authorityHelper.getSolicitudByPublicId(solicitudPublicId);
      authorityHelper.checkExternalUserHasAuthorityModifySolicitud(solicitud);
      Assert.isTrue(solicitud.getId().equals(data.getSolicitudId()), "No coincide el id de la solicitud");

      // Comprobar que la modalidad seleccionada esta en el arbol que tiene como nodo
      // raiz el programa seleccionado en la ConvocatoriaEntidadConvocante
      Optional<ConvocatoriaEntidadConvocante> convocatoriaEntidadConvocante = convocatoriaEntidadConvocanteRepository
          .findByConvocatoriaIdAndEntidadRefAndProgramaId(solicitud.getConvocatoriaId(),
              data.getEntidadRef(), solicitudModalidad.getProgramaConvocatoriaId());

      // Comprobar que la modalidad seleccionada no es el nodo raiz del arbol
      Assert.isTrue(
          !solicitudModalidad.getPrograma().getId().equals(convocatoriaEntidadConvocante.get().getPrograma().getId()),
          "La modalidad seleccionada es el nodo raiz del arbol");

      Assert.isTrue(
          isModalidadDescencientePrograma(solicitudModalidad.getPrograma(),
              convocatoriaEntidadConvocante.get().getPrograma()),
          MODALIDAD_NOT_VALID);

      data.setPrograma(solicitudModalidad.getPrograma());
      SolicitudModalidad returnValue = repository.save(data);

      log.debug("updateByExternalUser(String solicitudPublicId, SolicitudModalidad solicitudModalidad) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitudModalidadNotFoundException(solicitudModalidad.getId()));
  }

  /**
   * Elimina el {@link SolicitudModalidad}.
   *
   * @param solicitudPublicId el id de la {@link Solicitud}.
   * @param id                Id del {@link SolicitudModalidad}.
   */
  @Override
  @Transactional
  public void deleteByExternalUser(String solicitudPublicId, Long id) {
    log.debug("deleteByExternalUser(String solicitudPublicId, Long id) - start");

    Solicitud solicitud = authorityHelper.getSolicitudByPublicId(solicitudPublicId);
    authorityHelper.checkExternalUserHasAuthorityModifySolicitud(solicitud);

    AssertHelper.idNotNull(id, SolicitudModalidad.class);
    if (!repository.existsById(id)) {
      throw new SolicitudModalidadNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("deleteByExternalUser(String solicitudPublicId, Long id) - end");
  }

  /**
   * Obtiene las {@link SolicitudModalidad} para una {@link Solicitud}.
   *
   * @param solicitudPublicId el id de la {@link Solicitud}.
   * @param query             la información del filtro.
   * @param paging            la información de la paginación.
   * @return la lista de entidades {@link SolicitudModalidad} de la
   *         {@link Solicitud} paginadas.
   */
  @Override
  public Page<SolicitudModalidad> findAllBySolicitudPublicId(String solicitudPublicId, String query, Pageable paging) {
    log.debug("findAllBySolicitudPublicId(String solicitudPublicId, String query, Pageable paging) - start");
    Long solicitudId = authorityHelper.getSolicitudIdByPublicId(solicitudPublicId);
    Specification<SolicitudModalidad> specs = SolicitudModalidadSpecifications.bySolicitudId(solicitudId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudModalidad> returnValue = repository.findAll(specs, paging);
    log.debug("findAllBySolicitudPublicId(String solicitudPublicId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Comprueba si la modalidad pertenece al arbol del programa.
   * 
   * @param modalidad {@link Programa} que se quiere comprobar si es un
   *                  descendiente del programa.
   * @param programa  {@link Programa} buscado.
   * @return true si la modalidad es un descenciente del programa o false en caso
   *         contrario.
   */
  private boolean isModalidadDescencientePrograma(Programa modalidad, Programa programa) {
    log.debug("isModalidadDescencientePrograma(Programa modalidad, Programa programa) - start");

    boolean programaEncontrado = (Objects.equals(programa.getId(), modalidad.getId()));
    while (modalidad != null && modalidad.getPadre() != null && !programaEncontrado) {
      modalidad = programaRepository.findById(modalidad.getPadre().getId()).orElse(null);
      programaEncontrado = (modalidad != null && Objects.equals(programa.getId(), modalidad.getId()));
    }

    log.debug("isModalidadDescencientePrograma(Programa modalidad, Programa programa) - end");

    return programaEncontrado;
  }

}
