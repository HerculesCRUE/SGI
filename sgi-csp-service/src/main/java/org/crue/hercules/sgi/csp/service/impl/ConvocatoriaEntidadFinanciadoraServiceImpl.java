package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaEntidadFinanciadoraNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.FuenteFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessConvocatoriaException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Convocatoria.Estado;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadFinanciadoraRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.FuenteFinanciacionRepository;
import org.crue.hercules.sgi.csp.repository.TipoFinanciacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaEntidadFinanciadoraSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadFinanciadoraService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de
 * {@link ConvocatoriaEntidadFinanciadora}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaEntidadFinanciadoraServiceImpl implements ConvocatoriaEntidadFinanciadoraService {

  private final ConvocatoriaEntidadFinanciadoraRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ConfiguracionSolicitudRepository configuracionSolicitudRepository;
  private final FuenteFinanciacionRepository fuenteFinanciacionRepository;
  private final TipoFinanciacionRepository tipoFinanciacionRepository;
  private final ConvocatoriaService convocatoriaService;

  public ConvocatoriaEntidadFinanciadoraServiceImpl(
      ConvocatoriaEntidadFinanciadoraRepository convocatoriaEntidadFinanciadoraRepository,
      ConvocatoriaRepository convocatoriaRepository, FuenteFinanciacionRepository fuenteFinanciacionRepository,
      TipoFinanciacionRepository tipoFinanciacionRepository, ConvocatoriaService convocatoriaService,
      ConfiguracionSolicitudRepository configuracionSolicitudRepository) {
    this.repository = convocatoriaEntidadFinanciadoraRepository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.fuenteFinanciacionRepository = fuenteFinanciacionRepository;
    this.tipoFinanciacionRepository = tipoFinanciacionRepository;
    this.convocatoriaService = convocatoriaService;
    this.configuracionSolicitudRepository = configuracionSolicitudRepository;
  }

  /**
   * Guardar un nuevo {@link ConvocatoriaEntidadFinanciadora}.
   *
   * @param convocatoriaEntidadFinanciadora la entidad
   *                                        {@link ConvocatoriaEntidadFinanciadora}
   *                                        a guardar.
   * @return la entidad {@link ConvocatoriaEntidadFinanciadora} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaEntidadFinanciadora create(ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora) {
    log.debug("create(ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora) - start");

    Assert.isNull(convocatoriaEntidadFinanciadora.getId(),
        "ConvocatoriaEntidadFinanciadora id tiene que ser null para crear un nuevo ConvocatoriaEntidadFinanciadora");

    Assert.notNull(convocatoriaEntidadFinanciadora.getConvocatoriaId(),
        "Id Convocatoria no puede ser null para crear ConvocatoriaEntidadFinanciadora");

    Assert.isTrue(
        convocatoriaEntidadFinanciadora.getPorcentajeFinanciacion() == null
            || convocatoriaEntidadFinanciadora.getPorcentajeFinanciacion().floatValue() >= 0,
        "PorcentajeFinanciacion no puede ser negativo");

    Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaEntidadFinanciadora.getConvocatoriaId())
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaEntidadFinanciadora.getConvocatoriaId()));

    // comprobar si convocatoria es modificable
    Assert.isTrue(
        convocatoriaService.isRegistradaConSolicitudesOProyectos(convocatoriaEntidadFinanciadora.getConvocatoriaId(),
            convocatoria.getUnidadGestionRef(), new String[] { "CSP-CON-C", "CSP-CON-E" }),
        "No se puede crear ConvocatoriaEntidadFinanciadora. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");

    if (convocatoriaEntidadFinanciadora.getFuenteFinanciacion() != null) {
      if (convocatoriaEntidadFinanciadora.getFuenteFinanciacion().getId() == null) {
        convocatoriaEntidadFinanciadora.setFuenteFinanciacion(null);
      } else {
        convocatoriaEntidadFinanciadora.setFuenteFinanciacion(
            fuenteFinanciacionRepository.findById(convocatoriaEntidadFinanciadora.getFuenteFinanciacion().getId())
                .orElseThrow(() -> new FuenteFinanciacionNotFoundException(
                    convocatoriaEntidadFinanciadora.getFuenteFinanciacion().getId())));
        Assert.isTrue(convocatoriaEntidadFinanciadora.getFuenteFinanciacion().getActivo(),
            "La FuenteFinanciacion debe estar Activo");
      }
    }

    if (convocatoriaEntidadFinanciadora.getTipoFinanciacion() != null) {
      if (convocatoriaEntidadFinanciadora.getTipoFinanciacion().getId() == null) {
        convocatoriaEntidadFinanciadora.setTipoFinanciacion(null);
      } else {
        convocatoriaEntidadFinanciadora.setTipoFinanciacion(
            tipoFinanciacionRepository.findById(convocatoriaEntidadFinanciadora.getTipoFinanciacion().getId())
                .orElseThrow(() -> new TipoFinanciacionNotFoundException(
                    convocatoriaEntidadFinanciadora.getTipoFinanciacion().getId())));
        Assert.isTrue(convocatoriaEntidadFinanciadora.getTipoFinanciacion().getActivo(),
            "El TipoFinanciacion debe estar Activo");
      }
    }

    ConvocatoriaEntidadFinanciadora returnValue = repository.save(convocatoriaEntidadFinanciadora);

    log.debug("create(ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ConvocatoriaEntidadFinanciadora}.
   *
   * @param convocatoriaEntidadFinanciadoraActualizar la entidad
   *                                                  {@link ConvocatoriaEntidadFinanciadora}
   *                                                  a actualizar.
   * @return la entidad {@link ConvocatoriaEntidadFinanciadora} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaEntidadFinanciadora update(
      ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadoraActualizar) {
    log.debug("update(ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadoraActualizar) - start");

    Assert.notNull(convocatoriaEntidadFinanciadoraActualizar.getId(),
        "ConvocatoriaEntidadFinanciadora id no puede ser null para actualizar un ConvocatoriaEntidadFinanciadora");

    Assert.isTrue(
        convocatoriaEntidadFinanciadoraActualizar.getPorcentajeFinanciacion() == null
            || convocatoriaEntidadFinanciadoraActualizar.getPorcentajeFinanciacion().floatValue() >= 0,
        "PorcentajeFinanciacion no puede ser negativo");

    if (convocatoriaEntidadFinanciadoraActualizar.getFuenteFinanciacion() != null) {
      if (convocatoriaEntidadFinanciadoraActualizar.getFuenteFinanciacion().getId() == null) {
        convocatoriaEntidadFinanciadoraActualizar.setFuenteFinanciacion(null);
      } else {
        convocatoriaEntidadFinanciadoraActualizar.setFuenteFinanciacion(fuenteFinanciacionRepository
            .findById(convocatoriaEntidadFinanciadoraActualizar.getFuenteFinanciacion().getId())
            .orElseThrow(() -> new FuenteFinanciacionNotFoundException(
                convocatoriaEntidadFinanciadoraActualizar.getFuenteFinanciacion().getId())));
      }
    }

    if (convocatoriaEntidadFinanciadoraActualizar.getTipoFinanciacion() != null) {
      if (convocatoriaEntidadFinanciadoraActualizar.getTipoFinanciacion().getId() == null) {
        convocatoriaEntidadFinanciadoraActualizar.setTipoFinanciacion(null);
      } else {
        convocatoriaEntidadFinanciadoraActualizar.setTipoFinanciacion(
            tipoFinanciacionRepository.findById(convocatoriaEntidadFinanciadoraActualizar.getTipoFinanciacion().getId())
                .orElseThrow(() -> new TipoFinanciacionNotFoundException(
                    convocatoriaEntidadFinanciadoraActualizar.getTipoFinanciacion().getId())));
      }
    }

    return repository.findById(convocatoriaEntidadFinanciadoraActualizar.getId())
        .map(convocatoriaEntidadFinanciadora -> {
          if (convocatoriaEntidadFinanciadoraActualizar.getFuenteFinanciacion() != null) {
            Assert.isTrue(
                (convocatoriaEntidadFinanciadora.getFuenteFinanciacion() != null
                    && convocatoriaEntidadFinanciadora.getFuenteFinanciacion()
                        .getId() == convocatoriaEntidadFinanciadoraActualizar.getFuenteFinanciacion().getId())
                    || convocatoriaEntidadFinanciadoraActualizar.getFuenteFinanciacion().getActivo(),
                "La FuenteFinanciacion debe estar Activo");
          }

          if (convocatoriaEntidadFinanciadoraActualizar.getTipoFinanciacion() != null) {
            Assert.isTrue(
                (convocatoriaEntidadFinanciadora.getTipoFinanciacion() != null
                    && convocatoriaEntidadFinanciadora.getTipoFinanciacion()
                        .getId() == convocatoriaEntidadFinanciadoraActualizar.getTipoFinanciacion().getId())
                    || convocatoriaEntidadFinanciadoraActualizar.getTipoFinanciacion().getActivo(),
                "El TipoFinanciacion debe estar Activo");
          }

          // comprobar si convocatoria es modificable
          Assert.isTrue(
              convocatoriaService.isRegistradaConSolicitudesOProyectos(
                  convocatoriaEntidadFinanciadora.getConvocatoriaId(), null, new String[] { "CSP-CON-E" }),
              "No se puede modificar ConvocatoriaEntidadFinanciadora. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");

          convocatoriaEntidadFinanciadora
              .setFuenteFinanciacion(convocatoriaEntidadFinanciadoraActualizar.getFuenteFinanciacion());
          convocatoriaEntidadFinanciadora
              .setTipoFinanciacion(convocatoriaEntidadFinanciadoraActualizar.getTipoFinanciacion());
          convocatoriaEntidadFinanciadora
              .setPorcentajeFinanciacion(convocatoriaEntidadFinanciadoraActualizar.getPorcentajeFinanciacion());
          convocatoriaEntidadFinanciadora
              .setImporteFinanciacion(convocatoriaEntidadFinanciadoraActualizar.getImporteFinanciacion());

          ConvocatoriaEntidadFinanciadora returnValue = repository.save(convocatoriaEntidadFinanciadora);
          log.debug("update(ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadoraActualizar) - end");
          return returnValue;
        }).orElseThrow(() -> new ConvocatoriaEntidadFinanciadoraNotFoundException(
            convocatoriaEntidadFinanciadoraActualizar.getId()));
  }

  /**
   * Elimina el {@link ConvocatoriaEntidadFinanciadora}.
   *
   * @param id Id del {@link ConvocatoriaEntidadFinanciadora}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id,
        "ConvocatoriaEntidadFinanciadora id no puede ser null para desactivar un ConvocatoriaEntidadFinanciadora");

    repository.findById(id).map(convocatoriaEntidadFinanciadora -> {

      // comprobar si convocatoria es modificable
      Assert.isTrue(
          convocatoriaService.isRegistradaConSolicitudesOProyectos(convocatoriaEntidadFinanciadora.getConvocatoriaId(),
              null, new String[] { "CSP-CON-E" }),
          "No se puede eliminar ConvocatoriaEntidadFinanciadora. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");

      return convocatoriaEntidadFinanciadora;
    }).orElseThrow(() -> new ConvocatoriaEntidadFinanciadoraNotFoundException(id));

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link ConvocatoriaEntidadFinanciadora} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaEntidadFinanciadora}.
   * @return la entidad {@link ConvocatoriaEntidadFinanciadora}.
   */
  @Override
  public ConvocatoriaEntidadFinanciadora findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConvocatoriaEntidadFinanciadora returnValue = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaEntidadFinanciadoraNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ConvocatoriaEntidadFinanciadora} para una
   * {@link Convocatoria}.
   *
   * @param idConvocatoria el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaEntidadFinanciadora} de la
   *         {@link Convocatoria} paginadas.
   */
  public Page<ConvocatoriaEntidadFinanciadora> findAllByConvocatoria(Long idConvocatoria, String query,
      Pageable pageable) {
    log.debug("findAllByConvocatoria(Long idConvocatoria, String query, Pageable pageable) - start");

    Convocatoria convocatoria = convocatoriaRepository.findById(idConvocatoria)
        .orElseThrow(() -> new ConvocatoriaNotFoundException(idConvocatoria));

    if (hasAuthorityViewInvestigador()) {
      ConfiguracionSolicitud configuracionSolicitud = configuracionSolicitudRepository
          .findByConvocatoriaId(idConvocatoria)
          .orElseThrow(() -> new ConfiguracionSolicitudNotFoundException(idConvocatoria));
      if (!convocatoria.getEstado().equals(Estado.REGISTRADA)
          || Boolean.FALSE.equals(configuracionSolicitud.getTramitacionSGI())) {
        throw new UserNotAuthorizedToAccessConvocatoriaException();
      }
    }
    Specification<ConvocatoriaEntidadFinanciadora> specs = ConvocatoriaEntidadFinanciadoraSpecifications
        .byConvocatoriaId(idConvocatoria).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ConvocatoriaEntidadFinanciadora> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByConvocatoria(Long idConvocatoria, String query, Pageable pageable) - end");
    return returnValue;
  }

  private boolean hasAuthorityViewInvestigador() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-CON-INV-V");
  }

}
