package org.crue.hercules.sgi.csp.service.impl;

import java.util.Optional;

import org.crue.hercules.sgi.csp.dto.ProyectoFaseInput;
import org.crue.hercules.sgi.csp.exceptions.ProyectoFaseNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoFaseNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoFase;
import org.crue.hercules.sgi.csp.model.ProyectoFaseAviso;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoFaseRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.TipoFaseRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoFaseSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoFaseAvisoService;
import org.crue.hercules.sgi.csp.service.ProyectoFaseService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link ProyectoFase}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoFaseServiceImpl implements ProyectoFaseService {

  private final ProyectoFaseRepository repository;
  private final ProyectoRepository proyectoRepository;
  private final ModeloTipoFaseRepository modeloTipoFaseRepository;
  private final TipoFaseRepository tipoFaseRepository;
  private final ProyectoFaseAvisoService proyectoFaseAvisoService;

  public ProyectoFaseServiceImpl(
      ProyectoFaseRepository proyectoFaseRepository,
      ProyectoRepository proyectoRepository,
      ModeloTipoFaseRepository modeloTipoFaseRepository,
      TipoFaseRepository tipoFaseRepository,
      ProyectoFaseAvisoService proyectoFaseAvisoService) {

    this.repository = proyectoFaseRepository;
    this.proyectoRepository = proyectoRepository;
    this.modeloTipoFaseRepository = modeloTipoFaseRepository;
    this.tipoFaseRepository = tipoFaseRepository;
    this.proyectoFaseAvisoService = proyectoFaseAvisoService;
  }

  /**
   * Guarda la entidad {@link ProyectoFase}.
   * 
   * @param proyectoFaseInput la entidad {@link ProyectoFase} a guardar.
   * @return ProyectoFase la entidad {@link ProyectoFase} persistida.
   */
  @Override
  @Transactional
  public ProyectoFase create(ProyectoFaseInput proyectoFaseInput) {
    log.debug("create(ProyectoFaseInput proyectoFaseInput) - start");

    this.validarRequeridosProyectoFase(proyectoFaseInput);
    this.validarProyectoFase(proyectoFaseInput, null);

    ProyectoFase toCreate = ProyectoFase.builder()
        .fechaFin(proyectoFaseInput.getFechaFin())
        .fechaInicio(proyectoFaseInput.getFechaInicio())
        .observaciones(proyectoFaseInput.getObservaciones())
        .proyectoId(proyectoFaseInput.getProyectoId())
        .tipoFase(tipoFaseRepository.findById(proyectoFaseInput.getTipoFaseId())
            .orElseThrow(() -> new TipoFaseNotFoundException(proyectoFaseInput.getTipoFaseId())))
        .build();

    ProyectoFase proyectoFase = repository.save(toCreate);

    ProyectoFaseAviso aviso1 = proyectoFaseAvisoService.create(proyectoFase.getId(), proyectoFaseInput.getAviso1());
    ProyectoFaseAviso aviso2 = proyectoFaseAvisoService.create(proyectoFase.getId(), proyectoFaseInput.getAviso2());

    proyectoFase.setProyectoFaseAviso1(aviso1);
    proyectoFase.setProyectoFaseAviso2(aviso2);

    if (aviso1 != null || aviso2 != null) {
      proyectoFase = repository.save(proyectoFase);
    }

    log.debug("create(ProyectoFaseInput proyectoFaseInput) - end");
    return proyectoFase;
  }

  /**
   * Actualiza la entidad {@link ProyectoFase}.
   * 
   * @param proyectoFaseActualizar la entidad {@link ProyectoFase} a guardar.
   * @return ProyectoFase la entidad {@link ProyectoFase} persistida.
   */
  @Override
  @Transactional
  public ProyectoFase update(Long id, ProyectoFaseInput proyectoFaseActualizar) {
    log.debug("update(ProyectoFase ProyectoFaseActualizar) - start");

    Assert.notNull(id, "ProyectoFase id no puede ser null para actualizar un ProyectoFase");
    this.validarRequeridosProyectoFase(proyectoFaseActualizar);

    return repository.findById(id).map(proyectoFase -> {

      validarProyectoFase(proyectoFaseActualizar, id);

      proyectoFase.setFechaInicio(proyectoFaseActualizar.getFechaInicio());
      proyectoFase.setFechaFin(proyectoFaseActualizar.getFechaFin());
      proyectoFase.setObservaciones(proyectoFaseActualizar.getObservaciones());
      proyectoFase.setTipoFase(this.tipoFaseRepository.findById(proyectoFaseActualizar.getTipoFaseId())
          .orElseThrow(() -> new TipoFaseNotFoundException(proyectoFaseActualizar.getTipoFaseId())));

      if (proyectoFaseActualizar.getAviso1() != null && proyectoFase.getProyectoFaseAviso1() == null) {
        ProyectoFaseAviso aviso1 = this.proyectoFaseAvisoService.create(proyectoFase.getId(),
            proyectoFaseActualizar.getAviso1());
        proyectoFase.setProyectoFaseAviso1(aviso1);
      } else if (this.proyectoFaseAvisoService.deleteAvisoIfPossible(proyectoFaseActualizar.getAviso1(),
          proyectoFase.getProyectoFaseAviso1())) {
        proyectoFase.setProyectoFaseAviso1(null);
      } else {
        this.proyectoFaseAvisoService.updateAvisoIfNeeded(proyectoFaseActualizar.getAviso1(),
            proyectoFase.getProyectoFaseAviso1(),
            proyectoFase.getId());
      }

      if (proyectoFaseActualizar.getAviso2() != null && proyectoFase.getProyectoFaseAviso2() == null) {
        ProyectoFaseAviso aviso2 = this.proyectoFaseAvisoService.create(proyectoFase.getId(),
            proyectoFaseActualizar.getAviso2());
        proyectoFase.setProyectoFaseAviso2(aviso2);
      } else if (this.proyectoFaseAvisoService.deleteAvisoIfPossible(proyectoFaseActualizar.getAviso2(),
          proyectoFase.getProyectoFaseAviso2())) {
        proyectoFase.setProyectoFaseAviso2(null);
      } else {
        this.proyectoFaseAvisoService.updateAvisoIfNeeded(proyectoFaseActualizar.getAviso2(),
            proyectoFase.getProyectoFaseAviso2(),
            proyectoFase.getId());
      }

      return repository.save(proyectoFase);

    }).orElseThrow(() -> new ProyectoFaseNotFoundException(id));

  }

  /**
   * Elimina la {@link ProyectoFase}.
   *
   * @param id Id del {@link ProyectoFase}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "ProyectoFase id no puede ser null para eliminar un ProyectoFase");
    Optional<ProyectoFase> toDelete = repository.findById(id);
    if (!toDelete.isPresent()) {
      throw new ProyectoFaseNotFoundException(id);
    }
    this.proyectoFaseAvisoService.deleteAvisoIfPossible(null,
        toDelete.get().getProyectoFaseAviso1());
    this.proyectoFaseAvisoService.deleteAvisoIfPossible(null,
        toDelete.get().getProyectoFaseAviso2());

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Obtiene {@link ProyectoFase} por su id.
   *
   * @param id el id de la entidad {@link ProyectoFase}.
   * @return la entidad {@link ProyectoFase}.
   */
  @Override
  public ProyectoFase findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ProyectoFase returnValue = repository.findById(id).orElseThrow(() -> new ProyectoFaseNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;

  }

  /**
   * Obtiene los {@link ProyectoFase} para un {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoFase} del {@link Proyecto}
   *         paginadas.
   */
  @Override
  public Page<ProyectoFase> findAllByProyecto(Long proyectoId, String query, Pageable pageable) {
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - start");
    Specification<ProyectoFase> specs = ProyectoFaseSpecifications.byProyectoId(proyectoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoFase> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Realiza las validaciones necesarias para la creación y modificación de
   * {@link ProyectoFase}
   * 
   * @param datosProyectoFase
   * @param datosOriginales
   */
  private void validarProyectoFase(ProyectoFaseInput datosProyectoFase, Long id) {
    log.debug("validarProyectoFase(ProyectoFase datosProyectoFase) - start");

    // Se autocompletan los datos de las fechas en caso necesario.
    // Podrá darse el caso que una fase pueda recogerse en una sola fecha, en este
    // caso se indicaría el mismo valor tanto en fecha de inicio como en fecha de
    // fin.
    if (datosProyectoFase.getFechaInicio() == null) {
      datosProyectoFase.setFechaInicio(datosProyectoFase.getFechaFin());
    }
    if (datosProyectoFase.getFechaFin() == null) {
      datosProyectoFase.setFechaFin(datosProyectoFase.getFechaInicio());
    }

    Assert.isTrue(datosProyectoFase.getFechaFin().compareTo(datosProyectoFase.getFechaInicio()) >= 0,
        "La fecha de fin debe ser posterior a la fecha de inicio");

    // Se comprueba la existencia del proyecto
    Long proyectoId = datosProyectoFase.getProyectoId();
    if (!proyectoRepository.existsById(proyectoId)) {
      throw new ProyectoNotFoundException(proyectoId);
    }
    // Se recupera el Id de ModeloEjecucion para las siguientes validaciones
    Optional<ModeloEjecucion> modeloEjecucion = proyectoRepository.getModeloEjecucion(proyectoId);
    Long modeloEjecucionId = modeloEjecucion.isPresent() ? modeloEjecucion.get().getId() : null;

    // TipoFase
    Optional<ModeloTipoFase> modeloTipoFase = modeloTipoFaseRepository
        .findByModeloEjecucionIdAndTipoFaseId(modeloEjecucionId, datosProyectoFase.getTipoFaseId());

    // Está asignado al ModeloEjecucion
    Assert.isTrue(modeloTipoFase.isPresent(),
        "Tipo Fase no disponible para el ModeloEjecucion '"
            + ((modeloEjecucion.isPresent()) ? modeloEjecucion.get().getNombre() : "Proyecto sin modelo asignado")
            + "'");

    // La asignación al ModeloEjecucion está activa
    Assert.isTrue(modeloTipoFase.get().getActivo(), "ModeloTipoFase '" + modeloTipoFase.get().getTipoFase().getNombre()
        + "' no está activo para el ModeloEjecucion '" + modeloTipoFase.get().getModeloEjecucion().getNombre() + "'");

    // El TipoFase está activo
    Assert.isTrue(modeloTipoFase.get().getTipoFase().getActivo(),
        "TipoFase '" + modeloTipoFase.get().getTipoFase().getNombre() + "' no está activo");

    datosProyectoFase.setTipoFaseId(modeloTipoFase.get().getTipoFase().getId());

    Assert.isTrue(!existsProyectoFaseConFechasSolapadas(datosProyectoFase, id),
        "Ya existe un registro para la misma Fase en ese rango de fechas");

    log.debug("validarProyectoFase(ProyectoFase datosProyectoFase) - end");
  }

  /**
   * Comprueba la presencia de los datos requeridos al crear o modificar
   * {@link ProyectoFase}
   * 
   * @param datosProyectoFase
   */
  private void validarRequeridosProyectoFase(ProyectoFaseInput datosProyectoFase) {
    log.debug("validarRequeridosProyectoFase(ProyectoFase datosProyectoFase) - start");

    Assert.isTrue(datosProyectoFase.getProyectoId() != null,
        "Id Proyecto no puede ser null para realizar la acción sobre ProyectoFase");

    Assert.isTrue(datosProyectoFase.getTipoFaseId() != null,
        "Id Tipo Fase no puede ser null para realizar la acción sobre ProyectoFase");

    Assert.isTrue(datosProyectoFase.getFechaInicio() != null || datosProyectoFase.getFechaFin() != null,
        "Debe indicarse al menos una fecha para realizar la acción sobre ProyectoFase");

    log.debug("validarRequeridosProyectoFase(ProyectoFase datosProyectoFase) - end");

  }

  /**
   * Comprueba que existen {@link ProyectoFase} para una {@link Proyecto} con el
   * mismo {@link TipoFase} y con las fechas y sus horas solapadas
   *
   * @param proyectoFaseInput {@link Proyecto} a comprobar.
   * 
   * @return true si exite la coincidencia
   */

  private Boolean existsProyectoFaseConFechasSolapadas(ProyectoFaseInput proyectoFaseInput, Long id) {

    log.debug("existsProyectoFaseConFechasSolapadas(ProyectoFase proyectoFase) - start");
    Specification<ProyectoFase> specByRangoFechaSolapados = ProyectoFaseSpecifications
        .byRangoFechaSolapados(proyectoFaseInput.getFechaInicio(), proyectoFaseInput.getFechaFin());
    Specification<ProyectoFase> specByProyecto = ProyectoFaseSpecifications
        .byProyectoId(proyectoFaseInput.getProyectoId());
    Specification<ProyectoFase> specByTipoFase = ProyectoFaseSpecifications
        .byTipoFaseId(proyectoFaseInput.getTipoFaseId());
    Specification<ProyectoFase> specByIdNotEqual = ProyectoFaseSpecifications.byIdNotEqual(id);

    Specification<ProyectoFase> specs = Specification.where(specByProyecto).and(specByRangoFechaSolapados)
        .and(specByTipoFase).and(specByIdNotEqual);

    Page<ProyectoFase> proyectoFases = repository.findAll(specs, Pageable.unpaged());

    Boolean returnValue = !proyectoFases.isEmpty();
    log.debug("existsProyectoFaseConFechasSolapadas(ProyectoFase proyectoFase) - end");

    return returnValue;

  }

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link ProyectoFase}
   *
   * @param proyectoId Id del {@link Proyecto}.
   * @return si existe o no el proyecto
   */
  @Override
  public boolean existsByProyecto(Long proyectoId) {
    return repository.existsByProyectoId(proyectoId);
  }

}
