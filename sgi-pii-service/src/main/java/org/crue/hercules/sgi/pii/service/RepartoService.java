package org.crue.hercules.sgi.pii.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.dto.RepartoCreateInput;
import org.crue.hercules.sgi.pii.dto.RepartoCreateInput.InvencionGastoCreateInput;
import org.crue.hercules.sgi.pii.dto.RepartoCreateInput.InvencionIngresoCreateInput;
import org.crue.hercules.sgi.pii.dto.RepartoCreateInput.RepartoGastoCreateInput;
import org.crue.hercules.sgi.pii.dto.RepartoCreateInput.RepartoIngresoCreateInput;
import org.crue.hercules.sgi.pii.exceptions.RepartoNotFoundException;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.InvencionGasto;
import org.crue.hercules.sgi.pii.model.InvencionIngreso;
import org.crue.hercules.sgi.pii.model.Reparto;
import org.crue.hercules.sgi.pii.model.RepartoEquipoInventor;
import org.crue.hercules.sgi.pii.model.Reparto.Estado;
import org.crue.hercules.sgi.pii.model.Reparto.OnActualizar;
import org.crue.hercules.sgi.pii.model.Reparto.OnEjecutar;
import org.crue.hercules.sgi.pii.model.RepartoGasto;
import org.crue.hercules.sgi.pii.model.RepartoIngreso;
import org.crue.hercules.sgi.pii.repository.RepartoRepository;
import org.crue.hercules.sgi.pii.repository.specification.RepartoSpecifications;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Negocio de la entidad Reparto.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class RepartoService {

  private final ModelMapper modelMapper;
  private final Validator validator;
  private final RepartoRepository repository;
  private final RepartoGastoService repartoGastoService;
  private final RepartoIngresoService repartoIngresoService;
  private final RepartoEquipoInventorService repartoEquipoInventorService;
  private final InvencionGastoService invencionGastoService;
  private final InvencionIngresoService invencionIngresoService;

  public RepartoService(ModelMapper modelMapper, RepartoRepository repartoRepository,
      RepartoGastoService repartoGastoService, RepartoIngresoService repartoIngresoService,
      RepartoEquipoInventorService repartoEquipoInventorService, InvencionGastoService invencionGastoService,
      InvencionIngresoService invencionIngresoService, Validator validator) {
    this.modelMapper = modelMapper;
    this.validator = validator;
    this.repository = repartoRepository;
    this.repartoGastoService = repartoGastoService;
    this.repartoIngresoService = repartoIngresoService;
    this.repartoEquipoInventorService = repartoEquipoInventorService;
    this.invencionGastoService = invencionGastoService;
    this.invencionIngresoService = invencionIngresoService;
  }

  /**
   * Obtiene los {@link Reparto} para una {@link Invencion}paginadas y/o
   * filtradas.
   * 
   * @param invencionId el id de la {@link Invencion}.
   * @param query       la información del filtro.
   * @param pageable    la información de la paginación.
   * @return la lista de {@link Reparto} de la {@link Invencion}paginadas y/o
   *         filtradas.
   */
  public Page<Reparto> findByInvencionId(Long invencionId, String query, Pageable pageable) {
    log.debug("findByInvencionId(Long invencionId, String query, Pageable pageable) - start");

    Specification<Reparto> specs = RepartoSpecifications.byInvencionId(invencionId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<Reparto> returnValue = repository.findAll(specs, pageable);
    log.debug("findByInvencionId(Long invencionId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link Reparto} por su id.
   *
   * @param id el id de la entidad {@link Reparto}.
   * @return la entidad {@link Reparto}.
   */
  public Reparto findById(Long id) {
    log.debug("findById(Long id)  - start");
    final Reparto returnValue = repository.findById(id).orElseThrow(() -> new RepartoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Guardar un nuevo {@link Reparto} con sus {@link RepartoGasto} y
   * {@link RepartoIngreso} asociados. Cada
   * {@link RepartoGasto}/{@link RepartoIngreso} también consolida su importe de
   * los importes pendientes de {@link InvencionGasto}/{@link InvencionIngreso},
   *
   * @param repartoInput la entidad {@link Reparto} a guardar.
   * @return la entidad {@link Reparto} persistida.
   */
  @Transactional
  public Reparto create(RepartoCreateInput repartoInput) {
    log.debug("create(Reparto repartoInput) - start");
    Reparto reparto = new Reparto();

    reparto.setFecha(Instant.now());
    reparto.setInvencionId(repartoInput.getInvencionId());
    reparto.setEstado(Estado.PENDIENTE_EJECUTAR);
    reparto
        .setImporteUniversidad(this.calculateImporteUniversidad(repartoInput.getGastos(), repartoInput.getIngresos()));
    Reparto repartoCreated = repository.save(reparto);

    repartoInput.getGastos().stream().forEach(gastoInput -> {
      InvencionGasto invencionGastoConsolidada = invencionGastoService
          .consolidate(convert(gastoInput.getInvencionGasto()), gastoInput.getImporteADeducir());
      RepartoGasto repartoGasto = new RepartoGasto();
      repartoGasto.setRepartoId(repartoCreated.getId());
      repartoGasto.setInvencionGastoId(invencionGastoConsolidada.getId());
      repartoGasto.setImporteADeducir(gastoInput.getImporteADeducir());

      repartoGastoService.create(repartoGasto);
    });

    repartoInput.getIngresos().stream().forEach(ingresoInput -> {
      InvencionIngreso invencionIngresoConsolidada = invencionIngresoService
          .consolidate(convert(ingresoInput.getInvencionIngreso()), ingresoInput.getImporteARepartir());
      RepartoIngreso repartoIngreso = new RepartoIngreso();
      repartoIngreso.setRepartoId(repartoCreated.getId());
      repartoIngreso.setInvencionIngresoId(invencionIngresoConsolidada.getId());
      repartoIngreso.setImporteARepartir(ingresoInput.getImporteARepartir());

      repartoIngresoService.create(repartoIngreso);
    });

    log.debug("create(Reparto repartoInput) - end");
    return repartoCreated;
  }

  private BigDecimal calculateImporteUniversidad(List<RepartoGastoCreateInput> gastos,
      List<RepartoIngresoCreateInput> ingresos) {
    return ingresos.stream().map(RepartoIngresoCreateInput::getImporteARepartir)
        .reduce(new BigDecimal("0.00"), BigDecimal::add).subtract(gastos.stream()
            .map(RepartoGastoCreateInput::getImporteADeducir).reduce(new BigDecimal("0.00"), BigDecimal::add));
  }

  private InvencionGasto convert(InvencionGastoCreateInput invencionGastoInput) {
    return modelMapper.map(invencionGastoInput, InvencionGasto.class);
  }

  private InvencionIngreso convert(InvencionIngresoCreateInput invencionIngresoInput) {
    return modelMapper.map(invencionIngresoInput, InvencionIngreso.class);
  }

  /**
   * Actualizar {@link Reparto}.
   *
   * @param reparto la entidad {@link Reparto} a actualizar.
   * @return la entidad {@link Reparto} persistida.
   */
  @Transactional
  public Reparto update(Reparto reparto) {
    log.debug("update(Reparto reparto) - start");

    Assert.notNull(reparto.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(Reparto.class)).build());

    return repository.findById(reparto.getId()).map(repartoExistente -> {

      // Invocar validaciones asociadas a OnActualizar
      Set<ConstraintViolation<Reparto>> result = validator.validate(repartoExistente, OnActualizar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      // Establecemos los campos actualizables con los recibidos
      repartoExistente.setImporteUniversidad(reparto.getImporteUniversidad());
      repartoExistente.setImporteEquipoInventor(reparto.getImporteEquipoInventor());

      // Actualizamos la entidad
      Reparto returnValue = repository.save(repartoExistente);
      log.debug("update(Reparto reparto) - end");
      return returnValue;
    }).orElseThrow(() -> new RepartoNotFoundException(reparto.getId()));
  }

  @Transactional
  public Reparto ejecutar(Long id) {
    log.debug("ejecutar(Long id) - start");

    return repository.findById(id).map(repartoExistente -> {
      if (repartoExistente.getEstado() == Estado.EJECUTADO) {
        log.debug("ejecutar(Long id) - end");
        return repartoExistente;
      }

      // Invocar validaciones asociadas a OnEjecutar
      Set<ConstraintViolation<Reparto>> result = validator.validate(repartoExistente, OnEjecutar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      // Actualizamos el estado del Reparto a EJECUTADO
      repartoExistente.setEstado(Estado.EJECUTADO);
      if (repartoExistente.getImporteEquipoInventor() == null) {
        final List<RepartoEquipoInventor> equipoInventor = repartoEquipoInventorService
            .findByRepartoId(repartoExistente.getId(), null);
        final BigDecimal importeEquipoInventor = equipoInventor.stream().map(
            inventor -> inventor.getImporteNomina().add(inventor.getImporteProyecto()).add(inventor.getImporteOtros()))
            .reduce(new BigDecimal("0.00"), BigDecimal::add);
        repartoExistente.setImporteEquipoInventor(importeEquipoInventor);
      }

      Reparto returnValue = repository.save(repartoExistente);
      log.debug("ejecutar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new RepartoNotFoundException(id));
  }
}
