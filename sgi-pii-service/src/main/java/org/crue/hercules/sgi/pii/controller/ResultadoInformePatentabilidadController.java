package org.crue.hercules.sgi.pii.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.pii.dto.ResultadoInformePatentabilidadInput;
import org.crue.hercules.sgi.pii.dto.ResultadoInformePatentabilidadOutput;
import org.crue.hercules.sgi.pii.model.ResultadoInformePatentabilidad;
import org.crue.hercules.sgi.pii.model.TipoProteccion;
import org.crue.hercules.sgi.pii.service.ResultadoInformePatentabilidadService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ResultadoInformePatentabilidadController
 */
@RestController
@RequestMapping(ResultadoInformePatentabilidadController.MAPPING)
@Slf4j
public class ResultadoInformePatentabilidadController {
  public static final String MAPPING = "/resultadosinformepatentabilidad";

  private ModelMapper modelMapper;

  private final ResultadoInformePatentabilidadService service;

  /**
   * Instanciar nuevo {@link ResultadoInformePatentabilidadController}.
   * 
   * @param modelMapper {@link ModelMapper}
   * @param service     {@link ResultadoInformePatentabilidadService}
   */
  public ResultadoInformePatentabilidadController(ModelMapper modelMapper,
      ResultadoInformePatentabilidadService service) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ResultadoInformePatentabilidad} activos.
   * 
   * @param query  Filtro de búsqueda.
   * @param paging Información de paginación.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthority('PII-RIP-V','PII-RIP-C','PII-RIP-E','PII-RIP-B','PII-RIP-R', 'PII-INV-V', 'PII-INV-E')")
  ResponseEntity<Page<ResultadoInformePatentabilidadOutput>> findActivos(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug(
        "findActivos(@RequestParam(name = 'q', required = false) String query, @RequestPageable(sort = 's') Pageable paging) - start");
    Page<ResultadoInformePatentabilidad> page = service.findActivos(query, paging);

    if (page.isEmpty()) {
      log.debug(
          "findActivos(@RequestParam(name = 'q', required = false) String query, @RequestPageable(sort = 's') Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug(
        "findActivos(@RequestParam(name = 'q', required = false) String query, @RequestPageable(sort = 's') Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ResultadoInformePatentabilidad}.
   * 
   * @param query  Filtro de búsqueda.
   * @param paging Información de paginación.
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthority('PII-RIP-V','PII-RIP-C','PII-RIP-E','PII-RIP-B','PII-RIP-R')")
  ResponseEntity<Page<ResultadoInformePatentabilidadOutput>> findAll(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug(
        "findAll(@RequestParam(name = 'q', required = false) String query, @RequestPageable(sort = 's') Pageable paging) - start");
    Page<ResultadoInformePatentabilidad> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug(
          "findAll(@RequestParam(name = 'q', required = false) String query, @RequestPageable(sort = 's') Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug(
        "findAll(@RequestParam(name = 'q', required = false) String query, @RequestPageable(sort = 's') Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve el {@link ResultadoInformePatentabilidad} con el id indicado.
   * 
   * @param id Identificador de {@link ResultadoInformePatentabilidad}.
   * @return {@link ResultadoInformePatentabilidad} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('PII-RIP-V', 'PII-RIP-C', 'PII-RIP-E', 'PII-RIP-B', 'PII-RIP-R')")
  ResultadoInformePatentabilidadOutput findById(@PathVariable Long id) {
    log.debug("findById(@PathVariable Long id) - start");
    ResultadoInformePatentabilidad returnValue = this.service.findById(id);
    log.debug("findById(@PathVariable Long id) - end");
    return convert(returnValue);
  }

  /**
   * Crea un nuevo {@link ResultadoInformePatentabilidad}.
   * 
   * @param resultadoInformePatentabilidadInput {@link ResultadoInformePatentabilidad}
   *                                            que se quiere crear.
   * @return Nuevo {@link ResultadoInformePatentabilidad} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('PII-RIP-C')")
  ResponseEntity<ResultadoInformePatentabilidadOutput> create(
      @Valid @RequestBody ResultadoInformePatentabilidadInput resultadoInformePatentabilidadInput) {
    log.debug("create(@Valid @RequestBody ResultadoInformePatentabilidadInput resultadoInformePatentabilidad) - start");
    ResultadoInformePatentabilidad returnValue = service.create(convert(resultadoInformePatentabilidadInput));
    log.debug("create(@Valid @RequestBody ResultadoInformePatentabilidadInput resultadoInformePatentabilidad) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ResultadoInformePatentabilidad} con el id indicado.
   * 
   * @param resultadoInformePatentabilidad {@link TipoProteccion} a actualizar.
   * @param id                             {@link TipoProteccion} a actualizar.
   * @return {@link TipoProteccion} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-RIP-E')")
  ResultadoInformePatentabilidadOutput update(
      @Valid @RequestBody ResultadoInformePatentabilidadInput resultadoInformePatentabilidad, @PathVariable Long id) {
    log.debug(
        "update(@Valid @RequestBody ResultadoInformePatentabilidad resultadoInformePatentabilidad, @PathVariable Long id) - start");
    ResultadoInformePatentabilidad returnValue = service.update(convert(id, resultadoInformePatentabilidad));
    log.debug(
        "update(@Valid @RequestBody ResultadoInformePatentabilidad resultadoInformePatentabilidad, @PathVariable Long id) - end");

    return convert(returnValue);
  }

  /**
   * Activa el {@link ResultadoInformePatentabilidad} con id indicado.
   * 
   * @param id Identificador de {@link ResultadoInformePatentabilidad}.
   * @return {@link ResultadoInformePatentabilidad} actualizado.
   */
  @PatchMapping("/{id}/activar")
  @PreAuthorize("hasAuthority('PII-RIP-R')")
  ResultadoInformePatentabilidadOutput activar(@PathVariable Long id) {
    log.debug("activar(@PathVariable Long id) - start");
    ResultadoInformePatentabilidad returnValue = this.service.enable(id);
    log.debug("activar(@PathVariable Long id) - end");
    return convert(returnValue);
  }

  /**
   * Desactiva el {@link ResultadoInformePatentabilidad} con id indicado.
   * 
   * @param id Identificador de {@link ResultadoInformePatentabilidad}.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAuthority('PII-RIP-B')")
  ResultadoInformePatentabilidadOutput desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    ResultadoInformePatentabilidad returnValue = service.disable(id);
    log.debug("desactivar(Long id) - end");
    return convert(returnValue);
  }

  private ResultadoInformePatentabilidad convert(
      ResultadoInformePatentabilidadInput resultadoInformePatentabilidadInput) {
    return convert(null, resultadoInformePatentabilidadInput);
  }

  private ResultadoInformePatentabilidad convert(Long id,
      ResultadoInformePatentabilidadInput resultadoInformePatentabilidadInput) {
    ResultadoInformePatentabilidad resultadoInformePatentabilidad = modelMapper.map(resultadoInformePatentabilidadInput,
        ResultadoInformePatentabilidad.class);
    resultadoInformePatentabilidad.setId(id);
    return resultadoInformePatentabilidad;
  }

  private ResultadoInformePatentabilidadOutput convert(ResultadoInformePatentabilidad resultadoInformePatentabilidad) {
    return modelMapper.map(resultadoInformePatentabilidad, ResultadoInformePatentabilidadOutput.class);
  }

  private Page<ResultadoInformePatentabilidadOutput> convert(Page<ResultadoInformePatentabilidad> page) {
    List<ResultadoInformePatentabilidadOutput> content = page.getContent().stream()
        .map((resultadoInformePatentabilidad) -> convert(resultadoInformePatentabilidad)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }
}