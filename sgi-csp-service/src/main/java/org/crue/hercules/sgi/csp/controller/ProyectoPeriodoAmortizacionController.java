package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoAmortizacionInput;
import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoAmortizacionOutput;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoAmortizacion;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoAmortizacionService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoPeriodoAmortizacionController
 */
@RestController
@RequestMapping(ProyectoPeriodoAmortizacionController.MAPPING)
@Slf4j
public class ProyectoPeriodoAmortizacionController {
  public static final String MAPPING = "/proyectosperiodosamortizacion";

  private ModelMapper modelMapper;

  /** ProyectoPeriodoAmortizacion service */
  private final ProyectoPeriodoAmortizacionService service;

  /**
   * Instancia un nuevo ProyectoPeriodoAmortizacionController.
   * 
   * @param modelMapper {@link ModelMapper}
   * @param service     {@link ProyectoPeriodoAmortizacionService}
   */
  public ProyectoPeriodoAmortizacionController(ModelMapper modelMapper, ProyectoPeriodoAmortizacionService service) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link ProyectoPeriodoAmortizacion}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * 
   * @return lista paginada y filtrada {@link ProyectoPeriodoAmortizacion}.
   */
  @GetMapping()
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<Page<ProyectoPeriodoAmortizacionOutput>> findAll(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");

    Page<ProyectoPeriodoAmortizacion> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve el {@link ProyectoPeriodoAmortizacion} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoPeriodoAmortizacion}.
   * @return {@link ProyectoPeriodoAmortizacion} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ProyectoPeriodoAmortizacionOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProyectoPeriodoAmortizacion returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Crea un nuevo {@link ProyectoPeriodoAmortizacion}.
   * 
   * @param proyectoPeriodoAmortizacion {@link ProyectoPeriodoAmortizacion} que se
   *                                    quiere crear.
   * @return Nuevo {@link ProyectoPeriodoAmortizacion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<ProyectoPeriodoAmortizacionOutput> create(
      @Valid @RequestBody ProyectoPeriodoAmortizacionInput proyectoPeriodoAmortizacion) {
    log.debug("create(ProyectoPeriodoAmortizacion proyectoPeriodoAmortizacion) - start");
    ProyectoPeriodoAmortizacion returnValue = service.create(convert(proyectoPeriodoAmortizacion));
    log.debug("create(ProyectoPeriodoAmortizacion proyectoPeriodoAmortizacion) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ProyectoPeriodoAmortizacion} con el id indicado.
   * 
   * @param proyectoPeriodoAmortizacion {@link ProyectoPeriodoAmortizacion} a
   *                                    actualizar.
   * @param id                          id {@link ProyectoPeriodoAmortizacion} a
   *                                    actualizar.
   * @return {@link ProyectoPeriodoAmortizacion} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ProyectoPeriodoAmortizacionOutput update(
      @Valid @RequestBody ProyectoPeriodoAmortizacionInput proyectoPeriodoAmortizacion, @PathVariable Long id) {
    log.debug("update(ProyectoPeriodoAmortizacion proyectoPeriodoAmortizacion, Long id) - start");
    ProyectoPeriodoAmortizacion returnValue = service.update(convert(id, proyectoPeriodoAmortizacion));
    log.debug("update(ProyectoPeriodoAmortizacion proyectoPeriodoAmortizacion, Long id) - end");
    return convert(returnValue);
  }

  /**
   * Elimina el {@link ProyectoPeriodoAmortizacion} con id indicado.
   * 
   * @param id Identificador de {@link ProyectoPeriodoAmortizacion}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  private Page<ProyectoPeriodoAmortizacionOutput> convert(Page<ProyectoPeriodoAmortizacion> page) {
    List<ProyectoPeriodoAmortizacionOutput> content = page.getContent().stream()
        .map((proyectoPeriodoAmortizacion) -> convert(proyectoPeriodoAmortizacion)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private ProyectoPeriodoAmortizacionOutput convert(ProyectoPeriodoAmortizacion proyectoPeriodoAmortizacion) {
    return modelMapper.map(proyectoPeriodoAmortizacion, ProyectoPeriodoAmortizacionOutput.class);
  }

  private ProyectoPeriodoAmortizacion convert(ProyectoPeriodoAmortizacionInput proyectoPeriodoAmortizacionInput) {
    return convert(null, proyectoPeriodoAmortizacionInput);
  }

  private ProyectoPeriodoAmortizacion convert(Long id,
      ProyectoPeriodoAmortizacionInput proyectoPeriodoAmortizacionInput) {
    ProyectoPeriodoAmortizacion proyectoPeriodoAmortizacion = modelMapper.map(proyectoPeriodoAmortizacionInput,
        ProyectoPeriodoAmortizacion.class);
    proyectoPeriodoAmortizacion.setId(id);
    return proyectoPeriodoAmortizacion;
  }
}