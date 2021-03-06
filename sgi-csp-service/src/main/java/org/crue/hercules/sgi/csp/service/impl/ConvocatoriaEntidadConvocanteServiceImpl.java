package org.crue.hercules.sgi.csp.service.impl;

import java.util.ArrayList;
import java.util.Objects;

import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaEntidadConvocanteNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProgramaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessConvocatoriaException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessSolicitudException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Convocatoria.Estado;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadConvocanteRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaEntidadConvocanteSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadConvocanteService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
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
 * Service Implementation para la gesti??n de
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
  private final ConfiguracionSolicitudRepository configuracionSolicitudRepository;
  private final SolicitudRepository solicitudRepository;

  public ConvocatoriaEntidadConvocanteServiceImpl(
      ConvocatoriaEntidadConvocanteRepository convocatoriaEntidadConvocanteRepository,
      ConvocatoriaRepository convocatoriaRepository, ProgramaRepository programaRepository,
      ConvocatoriaService convocatoriaService, ConfiguracionSolicitudRepository configuracionSolicitudRepository,
      SolicitudRepository solicitudRepository) {
    this.repository = convocatoriaEntidadConvocanteRepository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.programaRepository = programaRepository;
    this.convocatoriaService = convocatoriaService;
    this.configuracionSolicitudRepository = configuracionSolicitudRepository;
    this.solicitudRepository = solicitudRepository;
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
                "CSP-CON-E", "CSP-CON-C" }),
        "No se puede crear ConvocatoriaEntidadConvocante. No tiene los permisos necesarios o la convocatoria est?? registrada y cuenta con solicitudes o proyectos asociados");

    Assert.isTrue(
        !repository.findByConvocatoriaIdAndEntidadRef(convocatoriaEntidadConvocante.getConvocatoriaId(),
            convocatoriaEntidadConvocante.getEntidadRef()).isPresent(),
        "Ya existe una asociaci??n activa para esa Convocatoria y Entidad");

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
              null, new String[] { "CSP-CON-E" }),
          "No se puede modificar ConvocatoriaEntidadConvocante. No tiene los permisos necesarios o la convocatoria est?? registrada y cuenta con solicitudes o proyectos asociados");

      repository
          .findByConvocatoriaIdAndEntidadRef(convocatoriaEntidadConvocanteActualizar.getConvocatoriaId(),
              convocatoriaEntidadConvocanteActualizar.getEntidadRef())
          .ifPresent(convocatoriaR -> {
            Assert.isTrue(convocatoriaEntidadConvocante.getId().equals(convocatoriaR.getId()),
                "Ya existe una asociaci??n activa para esa Convocatoria y Entidad");
          });

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

    repository.findById(id).map(convocatoriaEntidadConvocante -> {

      // comprobar si convocatoria es modificable
      Assert.isTrue(
          convocatoriaService.isRegistradaConSolicitudesOProyectos(convocatoriaEntidadConvocante.getConvocatoriaId(),
              null, new String[] { "CSP-CON-E" }),
          "No se puede eliminar ConvocatoriaEntidadConvocante. No tiene los permisos necesarios o la convocatoria est?? registrada y cuenta con solicitudes o proyectos asociados");

      return convocatoriaEntidadConvocante;
    }).orElseThrow(() -> new ConvocatoriaEntidadConvocanteNotFoundException(id));

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
   * @param query          la informaci??n del filtro.
   * @param pageable       la informaci??n de la paginaci??n.
   * @return la lista de entidades {@link ConvocatoriaEntidadConvocante} de la
   *         {@link Convocatoria} paginadas.
   */
  @Override
  public Page<ConvocatoriaEntidadConvocante> findAllByConvocatoria(Long convocatoriaId, String query,
      Pageable pageable) {
    log.debug("findAllByConvocatoria(Long idConvocatoria, String query, Pageable pageable) - start");

    Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaId)
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaId));
    if (hasAuthorityViewInvestigador()) {
      ConfiguracionSolicitud configuracionSolicitud = configuracionSolicitudRepository
          .findByConvocatoriaId(convocatoriaId)
          .orElseThrow(() -> new ConfiguracionSolicitudNotFoundException(convocatoriaId));
      if (!convocatoria.getEstado().equals(Estado.REGISTRADA)
          || Boolean.FALSE.equals(configuracionSolicitud.getTramitacionSGI())) {
        throw new UserNotAuthorizedToAccessConvocatoriaException();
      }
    }

    Specification<ConvocatoriaEntidadConvocante> specs = ConvocatoriaEntidadConvocanteSpecifications
        .byConvocatoriaId(convocatoriaId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ConvocatoriaEntidadConvocante> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByConvocatoria(Long idConvocatoria, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ConvocatoriaEntidadConvocante} de la {@link Convocatoria}
   * para una {@link Solicitud} si el usuario que realiza la peticion es el
   * solicitante de la {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Convocatoria}.
   * @param pageable    la informaci??n de la paginaci??n.
   * @return la lista de entidades {@link ConvocatoriaEntidadConvocante} de la
   *         {@link Convocatoria} paginadas.
   */
  @Override
  public Page<ConvocatoriaEntidadConvocante> findAllBySolicitudAndUserIsSolicitante(Long solicitudId,
      Pageable pageable) {
    log.debug("findAllBySolicitudAndUserIsSolicitante(Long solicitudId, Pageable pageable) - start");

    String personaRef = SecurityContextHolder.getContext().getAuthentication().getName();

    Solicitud solicitud = solicitudRepository.findOne(SolicitudSpecifications.bySolicitante(personaRef).and(
        SolicitudSpecifications.byId(solicitudId)))
        .orElseThrow(UserNotAuthorizedToAccessSolicitudException::new);

    if (Objects.isNull(solicitud.getConvocatoriaId())) {
      return new PageImpl<>(new ArrayList<>());
    }

    Specification<ConvocatoriaEntidadConvocante> specs = ConvocatoriaEntidadConvocanteSpecifications
        .byConvocatoriaId(solicitud.getConvocatoriaId());

    Page<ConvocatoriaEntidadConvocante> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllBySolicitudAndUserIsSolicitante(Long solicitudId, Pageable pageable) - end");
    return returnValue;
  }

  private boolean hasAuthorityViewInvestigador() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-CON-INV-V");
  }
}
