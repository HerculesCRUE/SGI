package org.crue.hercules.sgi.csp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaEntidadConvocanteNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.EntidadConvocanteDuplicatedException;
import org.crue.hercules.sgi.csp.exceptions.ProgramaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessSolicitudException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadConvocanteRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaEntidadConvocanteSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadConvocanteService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.ConvocatoriaAuthorityHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de
 * {@link ConvocatoriaEntidadConvocante}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaEntidadConvocanteServiceImpl implements ConvocatoriaEntidadConvocanteService {

  private final ConvocatoriaEntidadConvocanteRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ProgramaRepository programaRepository;
  private final ConvocatoriaService convocatoriaService;
  private final SolicitudRepository solicitudRepository;
  private final ConvocatoriaAuthorityHelper authorityHelper;

  public ConvocatoriaEntidadConvocanteServiceImpl(
      ConvocatoriaEntidadConvocanteRepository convocatoriaEntidadConvocanteRepository,
      ConvocatoriaRepository convocatoriaRepository, ProgramaRepository programaRepository,
      ConvocatoriaService convocatoriaService,
      SolicitudRepository solicitudRepository,
      ConvocatoriaAuthorityHelper authorityHelper) {
    this.repository = convocatoriaEntidadConvocanteRepository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.programaRepository = programaRepository;
    this.convocatoriaService = convocatoriaService;
    this.solicitudRepository = solicitudRepository;
    this.authorityHelper = authorityHelper;
  }

  /**
   * Actualiza el listado de {@link ConvocatoriaEntidadConvocante} de la
   * {@link Convocatoria} con el listado entidadesConvocantes
   * creando, editando o eliminando los elementos segun proceda.
   *
   * @param convocatoriaId       Id de la {@link Convocatoria}.
   * @param entidadesConvocantes lista con los nuevos
   *                             {@link ConvocatoriaEntidadConvocante} a guardar.
   * @return la lista de entidades {@link ConvocatoriaEntidadConvocante}
   *         persistida.
   */
  @Override
  @Transactional
  public List<ConvocatoriaEntidadConvocante> updateEntidadesConvocantesConvocatoria(Long convocatoriaId,
      List<ConvocatoriaEntidadConvocante> entidadesConvocantes) {
    log.debug(
        "updateEntidadesConvocantesConvocatoria(Long convocatoriaId, List<ConvocatoriaEntidadConvocante> entidadesConvocantes) - start");

    AssertHelper.idNotNull(convocatoriaId, Convocatoria.class);
    List<ConvocatoriaEntidadConvocante> entidadesConvocantesUpdated = entidadesConvocantes.stream()
        .map(entidadConvocante -> {
          checkDuplicated(entidadConvocante, entidadesConvocantes);

          if (entidadConvocante.getPrograma() == null || entidadConvocante.getPrograma().getId() == null) {
            entidadConvocante.setPrograma(null);
          } else {
            entidadConvocante
                .setPrograma(programaRepository.findById(entidadConvocante.getPrograma().getId())
                    .orElseThrow(
                        () -> new ProgramaNotFoundException(entidadConvocante.getPrograma().getId())));
            Assert.isTrue(entidadConvocante.getPrograma().getActivo(), "El Programa debe estar Activo");
          }

          entidadConvocante.setConvocatoriaId(convocatoriaId);

          return entidadConvocante;
        }).collect(Collectors.toList());

    Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaId)
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaId));

    // comprobar si convocatoria es modificable
    Assert.isTrue(
        convocatoriaService.isRegistradaConSolicitudesOProyectos(convocatoriaId,
            convocatoria.getUnidadGestionRef(), new String[] {
                ConvocatoriaAuthorityHelper.CSP_CON_C,
                ConvocatoriaAuthorityHelper.CSP_CON_E
            }),
        "No se puede crear ConvocatoriaEntidadConvocante. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");

    Specification<ConvocatoriaEntidadConvocante> specs = ConvocatoriaEntidadConvocanteSpecifications
        .byConvocatoriaId(convocatoriaId);
    List<ConvocatoriaEntidadConvocante> entidadesConvocantesBD = repository.findAll(specs);

    // Entidades Convocantes eliminados
    List<ConvocatoriaEntidadConvocante> entiadesConvocantesEliminar = entidadesConvocantesBD.stream()
        .filter(entidadConvocante -> entidadesConvocantesUpdated.stream().map(ConvocatoriaEntidadConvocante::getId)
            .noneMatch(id -> Objects.equals(id, entidadConvocante.getId())))
        .collect(Collectors.toList());

    if (!entiadesConvocantesEliminar.isEmpty()) {
      repository.deleteAll(entiadesConvocantesEliminar);
    }

    if (entidadesConvocantes.isEmpty()) {
      return new ArrayList<>();
    }

    // Entidades Convocantes crear/actualizar
    List<ConvocatoriaEntidadConvocante> entidadesConvocantesCreateOrUpdate = entidadesConvocantesUpdated.stream()
        .map(entidadConvocanteUpdated -> {
          if (entidadConvocanteUpdated.getId() == null) {
            return entidadConvocanteUpdated;
          }

          ConvocatoriaEntidadConvocante entidadConvocanteBD = entidadesConvocantesBD.stream()
              .filter(entidad -> entidad.getId().equals(entidadConvocanteUpdated.getId()))
              .findFirst().get();

          return copyUpdatedValues(entidadConvocanteBD, entidadConvocanteUpdated);
        })
        .collect(Collectors.toList());

    List<ConvocatoriaEntidadConvocante> returnValue = repository.saveAll(entidadesConvocantesCreateOrUpdate);

    log.debug(
        "updateEntidadesConvocantesConvocatoria(Long convocatoriaId, List<ConvocatoriaEntidadConvocante> entidadesConvocantes) - end");
    return returnValue;
  }

  /**
   * Guardar un nuevo {@link ConvocatoriaEntidadConvocante}.
   *
   * @param convocatoriaEntidadConvocante la entidad
   *                                      {@link ConvocatoriaEntidadConvocante} a
   *                                      guardar.
   * @return la entidad {@link ConvocatoriaEntidadConvocante} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaEntidadConvocante create(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante) {
    log.debug("create(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante) - start");

    Assert.isNull(convocatoriaEntidadConvocante.getId(),
        "ConvocatoriaEntidadConvocante id tiene que ser null para crear un nuevo ConvocatoriaEntidadConvocante");

    Assert.notNull(convocatoriaEntidadConvocante.getConvocatoriaId(),
        "Id Convocatoria no puede ser null para crear ConvocatoriaEntidadGestora");

    Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaEntidadConvocante.getConvocatoriaId())
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaEntidadConvocante.getConvocatoriaId()));

    // comprobar si convocatoria es modificable
    Assert.isTrue(
        convocatoriaService.isRegistradaConSolicitudesOProyectos(convocatoriaEntidadConvocante.getConvocatoriaId(),
            convocatoria.getUnidadGestionRef(), new String[] {
                ConvocatoriaAuthorityHelper.CSP_CON_C,
                ConvocatoriaAuthorityHelper.CSP_CON_E
            }),
        "No se puede crear ConvocatoriaEntidadConvocante. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");

    Assert.isTrue(
        !repository.findByConvocatoriaIdAndEntidadRefAndProgramaId(
            convocatoriaEntidadConvocante.getConvocatoriaId(),
            convocatoriaEntidadConvocante.getEntidadRef(),
            convocatoriaEntidadConvocante.getPrograma().getId()).isPresent(),
        "Ya existe una asociación activa para esa Convocatoria y Entidad");

    if (convocatoriaEntidadConvocante.getPrograma() != null) {
      if (convocatoriaEntidadConvocante.getPrograma().getId() == null) {
        convocatoriaEntidadConvocante.setPrograma(null);
      } else {
        convocatoriaEntidadConvocante
            .setPrograma(programaRepository.findById(convocatoriaEntidadConvocante.getPrograma().getId())
                .orElseThrow(() -> new ProgramaNotFoundException(convocatoriaEntidadConvocante.getPrograma().getId())));
        Assert.isTrue(convocatoriaEntidadConvocante.getPrograma().getActivo(), "El Programa debe estar Activo");
      }
    }

    ConvocatoriaEntidadConvocante returnValue = repository.save(convocatoriaEntidadConvocante);

    log.debug("create(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ConvocatoriaEntidadConvocante}.
   *
   * @param convocatoriaEntidadConvocanteActualizar la entidad
   *                                                {@link ConvocatoriaEntidadConvocante}
   *                                                a actualizar.
   * @return la entidad {@link ConvocatoriaEntidadConvocante} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaEntidadConvocante update(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocanteActualizar) {
    log.debug("update(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocanteActualizar) - start");

    Assert.notNull(convocatoriaEntidadConvocanteActualizar.getId(),
        "ConvocatoriaEntidadConvocante id no puede ser null para actualizar un ConvocatoriaEntidadConvocante");

    return repository.findById(convocatoriaEntidadConvocanteActualizar.getId()).map(convocatoriaEntidadConvocante -> {

      // comprobar si convocatoria es modificable
      Assert.isTrue(
          convocatoriaService.isRegistradaConSolicitudesOProyectos(convocatoriaEntidadConvocante.getConvocatoriaId(),
              null, new String[] { ConvocatoriaAuthorityHelper.CSP_CON_E }),
          "No se puede modificar ConvocatoriaEntidadConvocante. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");

      repository
          .findByConvocatoriaIdAndEntidadRefAndProgramaId(
              convocatoriaEntidadConvocanteActualizar.getConvocatoriaId(),
              convocatoriaEntidadConvocanteActualizar.getEntidadRef(),
              convocatoriaEntidadConvocanteActualizar.getPrograma().getId())
          .ifPresent(convocatoriaR -> Assert.isTrue(convocatoriaEntidadConvocante.getId().equals(convocatoriaR.getId()),
              "Ya existe una asociación activa para esa Convocatoria y Entidad"));

      if (convocatoriaEntidadConvocanteActualizar.getPrograma() != null) {
        if (convocatoriaEntidadConvocanteActualizar.getPrograma().getId() == null) {
          convocatoriaEntidadConvocanteActualizar.setPrograma(null);
        } else {
          convocatoriaEntidadConvocanteActualizar.setPrograma(
              programaRepository.findById(convocatoriaEntidadConvocanteActualizar.getPrograma().getId()).orElseThrow(
                  () -> new ProgramaNotFoundException(convocatoriaEntidadConvocanteActualizar.getPrograma().getId())));
          Assert.isTrue(convocatoriaEntidadConvocanteActualizar.getPrograma().getActivo(),
              "El Programa debe estar Activo");
        }
      }

      convocatoriaEntidadConvocante.setPrograma(convocatoriaEntidadConvocanteActualizar.getPrograma());

      ConvocatoriaEntidadConvocante returnValue = repository.save(convocatoriaEntidadConvocante);
      log.debug("update(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocanteActualizar) - end");
      return returnValue;
    }).orElseThrow(
        () -> new ConvocatoriaEntidadConvocanteNotFoundException(convocatoriaEntidadConvocanteActualizar.getId()));
  }

  /**
   * Elimina el {@link ConvocatoriaEntidadConvocante}.
   *
   * @param id Id del {@link ConvocatoriaEntidadConvocante}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id,
        "ConvocatoriaEntidadConvocante id no puede ser null para desactivar un ConvocatoriaEntidadConvocante");

    Optional<ConvocatoriaEntidadConvocante> entidadConvocante = repository.findById(id);
    if (entidadConvocante.isPresent()) {
      // comprobar si convocatoria es modificable
      Assert.isTrue(
          convocatoriaService.isRegistradaConSolicitudesOProyectos(entidadConvocante.get().getConvocatoriaId(),
              null, new String[] { ConvocatoriaAuthorityHelper.CSP_CON_E }),
          "No se puede eliminar ConvocatoriaEntidadConvocante. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");
    } else {
      throw new ConvocatoriaEntidadConvocanteNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link ConvocatoriaEntidadConvocante} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaEntidadConvocante}.
   * @return la entidad {@link ConvocatoriaEntidadConvocante}.
   */
  @Override
  public ConvocatoriaEntidadConvocante findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConvocatoriaEntidadConvocante returnValue = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaEntidadConvocanteNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ConvocatoriaEntidadConvocante} para una
   * {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaEntidadConvocante} de la
   *         {@link Convocatoria} paginadas.
   */
  @Override
  public Page<ConvocatoriaEntidadConvocante> findAllByConvocatoria(Long convocatoriaId, String query,
      Pageable pageable) {
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - start");

    authorityHelper.checkUserHasAuthorityViewConvocatoria(convocatoriaId);

    Specification<ConvocatoriaEntidadConvocante> specs = ConvocatoriaEntidadConvocanteSpecifications
        .byConvocatoriaId(convocatoriaId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ConvocatoriaEntidadConvocante> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ConvocatoriaEntidadConvocante} de la {@link Convocatoria}
   * para una {@link Solicitud} si el usuario que realiza la peticion es el
   * solicitante o el tutor de la {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Convocatoria}.
   * @param pageable    la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaEntidadConvocante} de la
   *         {@link Convocatoria} paginadas.
   */
  @Override
  public Page<ConvocatoriaEntidadConvocante> findAllBySolicitudAndUserIsSolicitanteOrTutor(Long solicitudId,
      Pageable pageable) {
    log.debug("findAllBySolicitudAndUserIsSolicitanteOrTutor(Long solicitudId, Pageable pageable) - start");

    String personaRef = SecurityContextHolder.getContext().getAuthentication().getName();

    Solicitud solicitud = solicitudRepository
        .findOne(
            SolicitudSpecifications.bySolicitanteOrTutor(personaRef).and(SolicitudSpecifications.byId(solicitudId)))
        .orElseThrow(UserNotAuthorizedToAccessSolicitudException::new);

    if (Objects.isNull(solicitud.getConvocatoriaId())) {
      return new PageImpl<>(new ArrayList<>());
    }

    Specification<ConvocatoriaEntidadConvocante> specs = ConvocatoriaEntidadConvocanteSpecifications
        .byConvocatoriaId(solicitud.getConvocatoriaId());

    Page<ConvocatoriaEntidadConvocante> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllBySolicitudAndUserIsSolicitanteOrTutor(Long solicitudId, Pageable pageable) - end");
    return returnValue;
  }

  private void checkDuplicated(ConvocatoriaEntidadConvocante entidadConvocante,
      List<ConvocatoriaEntidadConvocante> entidadesConvocantes) {

    if (entidadesConvocantes.isEmpty()) {
      return;
    }

    List<ConvocatoriaEntidadConvocante> otherEntidadesConvocantes = entidadesConvocantes.stream()
        .filter(entidad -> !(Objects.equals(entidad.getEntidadRef(), entidadConvocante.getEntidadRef())
            && Objects.equals(
                entidad.getPrograma() != null ? entidad.getPrograma().getId() : null,
                entidadConvocante.getPrograma() != null ? entidadConvocante.getPrograma().getId() : null)))
        .collect(Collectors.toList());

    if (otherEntidadesConvocantes.size() != (entidadesConvocantes.size() - 1)) {
      throw new EntidadConvocanteDuplicatedException();
    }

    boolean duplicated = otherEntidadesConvocantes.stream()
        .anyMatch(entidad -> isDuplicated(entidad, entidadConvocante));

    if (duplicated) {
      throw new EntidadConvocanteDuplicatedException();
    }
  }

  private boolean isDuplicated(
      ConvocatoriaEntidadConvocante entidadConvocante1,
      ConvocatoriaEntidadConvocante entidadConvocante2) {

    Programa programaEntidadConvocante1 = entidadConvocante1.getPrograma();
    Programa programaEntidadConvocante2 = entidadConvocante2.getPrograma();

    boolean entidadRefEquals = entidadConvocante1.getEntidadRef().equals(entidadConvocante2.getEntidadRef());
    boolean programaNull = programaEntidadConvocante1 == null || programaEntidadConvocante2 == null;

    boolean inProgramaParentsEntidad1 = programaEntidadConvocante2 != null
        && getProgramaParentsIds(programaEntidadConvocante1, new ArrayList<>())
            .contains(programaEntidadConvocante2.getId());
    boolean inProgramaParentsEntidad2 = programaEntidadConvocante1 != null
        && getProgramaParentsIds(programaEntidadConvocante2, new ArrayList<>())
            .contains(programaEntidadConvocante1.getId());

    return entidadRefEquals && (programaNull || inProgramaParentsEntidad1 || inProgramaParentsEntidad2);
  }

  private List<Long> getProgramaParentsIds(Programa programa, List<Long> programaIds) {
    if (programa == null) {
      return programaIds;
    }

    programaIds.add(programa.getId());

    if (programa.getPadre() != null) {
      this.getProgramaParentsIds(programa.getPadre(), programaIds);
    }

    return programaIds;
  }

  private ConvocatoriaEntidadConvocante copyUpdatedValues(
      ConvocatoriaEntidadConvocante entidadConvocante,
      ConvocatoriaEntidadConvocante entidadConvocanteUpdated) {
    entidadConvocante.setPrograma(entidadConvocanteUpdated.getPrograma());
    return entidadConvocante;
  }

}
