package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.FuenteFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena;
import org.crue.hercules.sgi.csp.repository.FuenteFinanciacionRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoEntidadFinanciadoraAjenaRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoRepository;
import org.crue.hercules.sgi.csp.repository.TipoFinanciacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudProyectoEntidadFinanciadoraAjenaSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEntidadFinanciadoraAjenaService;
import org.crue.hercules.sgi.csp.service.SolicitudService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de
 * {@link SolicitudProyectoEntidadFinanciadoraAjena}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudProyectoEntidadFinanciadoraAjenaServiceImpl
    implements SolicitudProyectoEntidadFinanciadoraAjenaService {

  private final SolicitudProyectoEntidadFinanciadoraAjenaRepository repository;
  private final FuenteFinanciacionRepository fuenteFinanciacionRepository;
  private final TipoFinanciacionRepository tipoFinanciacionRepository;
  private final SolicitudService solicitudService;
  private final SolicitudProyectoRepository solicitudProyectoRepository;

  public SolicitudProyectoEntidadFinanciadoraAjenaServiceImpl(
      SolicitudProyectoEntidadFinanciadoraAjenaRepository solicitudProyectoEntidadFinanciadoraAjenaRepository,
      FuenteFinanciacionRepository fuenteFinanciacionRepository, TipoFinanciacionRepository tipoFinanciacionRepository,
      SolicitudService solicitudService, SolicitudProyectoRepository solicitudProyectoRepository) {
    this.repository = solicitudProyectoEntidadFinanciadoraAjenaRepository;
    this.fuenteFinanciacionRepository = fuenteFinanciacionRepository;
    this.tipoFinanciacionRepository = tipoFinanciacionRepository;
    this.solicitudService = solicitudService;
    this.solicitudProyectoRepository = solicitudProyectoRepository;
  }

  /**
   * Guardar un nuevo {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   *
   * @param solicitudProyectoEntidadFinanciadoraAjena la entidad
   *                                                  {@link SolicitudProyectoEntidadFinanciadoraAjena}
   *                                                  a guardar.
   * @return la entidad {@link SolicitudProyectoEntidadFinanciadoraAjena}
   *         persistida.
   */
  @Override
  @Transactional
  public SolicitudProyectoEntidadFinanciadoraAjena create(
      SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena) {
    log.debug("create(SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena) - start");

    Assert.isNull(solicitudProyectoEntidadFinanciadoraAjena.getId(),
        "SolicitudProyectoEntidadFinanciadoraAjena id tiene que ser null para crear un nuevo SolicitudProyectoEntidadFinanciadoraAjena");

    Assert.notNull(solicitudProyectoEntidadFinanciadoraAjena.getSolicitudProyectoId(),
        "Id SolicitudProyecto no puede ser null para crear SolicitudProyectoEntidadFinanciadoraAjena");

    validateData(solicitudProyectoEntidadFinanciadoraAjena, null);

    SolicitudProyectoEntidadFinanciadoraAjena returnValue = repository.save(solicitudProyectoEntidadFinanciadoraAjena);

    log.debug("create(SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   *
   * @param solicitudProyectoEntidadFinanciadoraAjenaActualizar la entidad
   *                                                            {@link SolicitudProyectoEntidadFinanciadoraAjena}
   *                                                            a actualizar.
   * @return la entidad {@link SolicitudProyectoEntidadFinanciadoraAjena}
   *         persistida.
   */
  @Override
  @Transactional
  public SolicitudProyectoEntidadFinanciadoraAjena update(
      SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjenaActualizar) {
    log.debug(
        "update(SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjenaActualizar) - start");
    SolicitudProyecto solicitudProyecto = solicitudProyectoRepository
        .findById(solicitudProyectoEntidadFinanciadoraAjenaActualizar.getSolicitudProyectoId())
        .orElseThrow(() -> new SolicitudProyectoNotFoundException(
            solicitudProyectoEntidadFinanciadoraAjenaActualizar.getSolicitudProyectoId()));
    Assert.notNull(solicitudProyectoEntidadFinanciadoraAjenaActualizar.getId(),
        "SolicitudProyectoEntidadFinanciadoraAjena id no puede ser null para actualizar un SolicitudProyectoEntidadFinanciadoraAjena");

    // comprobar si la solicitud es modificable
    Assert.isTrue(solicitudService.modificable(solicitudProyecto.getId()),
        "No se puede modificar SolicitudProyectoEntidadFinanciadoraAjena");

    return repository.findById(solicitudProyectoEntidadFinanciadoraAjenaActualizar.getId())
        .map(solicitudProyectoEntidadFinanciadoraAjena -> {

          validateData(solicitudProyectoEntidadFinanciadoraAjenaActualizar, solicitudProyectoEntidadFinanciadoraAjena);

          solicitudProyectoEntidadFinanciadoraAjena
              .setFuenteFinanciacion(solicitudProyectoEntidadFinanciadoraAjenaActualizar.getFuenteFinanciacion());
          solicitudProyectoEntidadFinanciadoraAjena
              .setTipoFinanciacion(solicitudProyectoEntidadFinanciadoraAjenaActualizar.getTipoFinanciacion());
          solicitudProyectoEntidadFinanciadoraAjena.setPorcentajeFinanciacion(
              solicitudProyectoEntidadFinanciadoraAjenaActualizar.getPorcentajeFinanciacion());
          solicitudProyectoEntidadFinanciadoraAjena
              .setImporteFinanciacion(solicitudProyectoEntidadFinanciadoraAjenaActualizar.getImporteFinanciacion());

          SolicitudProyectoEntidadFinanciadoraAjena returnValue = repository
              .save(solicitudProyectoEntidadFinanciadoraAjena);
          log.debug(
              "update(SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjenaActualizar) - end");
          return returnValue;
        }).orElseThrow(() -> new SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException(
            solicitudProyectoEntidadFinanciadoraAjenaActualizar.getId()));
  }

  /**
   * Elimina el {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   *
   * @param id Id del {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id,
        "SolicitudProyectoEntidadFinanciadoraAjena id no puede ser null para desactivar un SolicitudProyectoEntidadFinanciadoraAjena");

    if (!repository.existsById(id)) {
      throw new SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link SolicitudProyectoEntidadFinanciadoraAjena} por su id.
   *
   * @param id el id de la entidad
   *           {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   * @return la entidad {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   */
  @Override
  public SolicitudProyectoEntidadFinanciadoraAjena findById(Long id) {
    log.debug("findById(Long id)  - start");
    final SolicitudProyectoEntidadFinanciadoraAjena returnValue = repository.findById(id)
        .orElseThrow(() -> new SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link SolicitudProyectoEntidadFinanciadoraAjena} para una
   * {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @param query       la información del filtro.
   * @param pageable    la información de la paginación.
   * @return la lista de entidades
   *         {@link SolicitudProyectoEntidadFinanciadoraAjena} de la
   *         {@link Solicitud} paginadas.
   */
  public Page<SolicitudProyectoEntidadFinanciadoraAjena> findAllBySolicitud(Long solicitudId, String query,
      Pageable pageable) {
    log.debug("findAllBySolicitud(Long solicitudId, String query, Pageable pageable) - start");
    Specification<SolicitudProyectoEntidadFinanciadoraAjena> specs = SolicitudProyectoEntidadFinanciadoraAjenaSpecifications
        .bySolicitudId(solicitudId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudProyectoEntidadFinanciadoraAjena> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllBySolicitud(Long solicitudId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Valida los nuevos datos de {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   * 
   * @param updateData  nuevo valor del
   *                    {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   * @param currentData valor actual del
   *                    {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   */
  private void validateData(SolicitudProyectoEntidadFinanciadoraAjena updateData,
      SolicitudProyectoEntidadFinanciadoraAjena currentData) {
    log.debug(
        "validateData(SolicitudProyectoEntidadFinanciadoraAjena updateData, SolicitudProyectoEntidadFinanciadoraAjena currentData) - start");

    if (updateData.getFuenteFinanciacion() != null) {
      if (updateData.getFuenteFinanciacion().getId() == null) {
        updateData.setFuenteFinanciacion(null);
      } else {
        updateData.setFuenteFinanciacion(
            fuenteFinanciacionRepository.findById(updateData.getFuenteFinanciacion().getId()).orElseThrow(
                () -> new FuenteFinanciacionNotFoundException(updateData.getFuenteFinanciacion().getId())));

        if (updateData.getFuenteFinanciacion() != null) {
          Assert.isTrue((currentData != null && currentData.getFuenteFinanciacion() != null
              && currentData.getFuenteFinanciacion().getId() == updateData.getFuenteFinanciacion().getId())
              || updateData.getFuenteFinanciacion().getActivo(), "La FuenteFinanciacion debe estar Activo");
        }
      }
    }

    if (updateData.getTipoFinanciacion() != null) {
      if (updateData.getTipoFinanciacion().getId() == null) {
        updateData.setTipoFinanciacion(null);
      } else {
        updateData.setTipoFinanciacion(tipoFinanciacionRepository.findById(updateData.getTipoFinanciacion().getId())
            .orElseThrow(() -> new TipoFinanciacionNotFoundException(updateData.getTipoFinanciacion().getId())));

        if (updateData.getTipoFinanciacion() != null) {
          Assert.isTrue((currentData != null && currentData.getTipoFinanciacion() != null
              && currentData.getTipoFinanciacion().getId() == updateData.getTipoFinanciacion().getId())
              || updateData.getTipoFinanciacion().getActivo(), "El TipoFinanciacion debe estar Activo");
        }
      }
    }

    log.debug(
        "validateData(SolicitudProyectoEntidadFinanciadoraAjena updateData, SolicitudProyectoEntidadFinanciadoraAjena currentData) - end");
  }
}
