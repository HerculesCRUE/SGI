package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.TipoRegimenConcurrenciaInput;
import org.crue.hercules.sgi.csp.dto.TipoRegimenConcurrenciaOutput;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.service.TipoRegimenConcurrenciaService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
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
 * TipoRegimenConcurrenciaController
 */
@RestController
@RequestMapping(TipoRegimenConcurrenciaController.MAPPING)
@Slf4j
public class TipoRegimenConcurrenciaController {
  public static final String MAPPING = "/tiporegimenconcurrencias";

  private ModelMapper modelMapper;

  /** TipoRegimenConcurrenciaService service */
  private final TipoRegimenConcurrenciaService service;

  /**
   * Instancia un nuevo TipoRegimenConcurrenciaController.
   * 
   * @param modelMapper {@link ModelMapper}
   * @param service     {@link TipoRegimenConcurrenciaService}
   */
  public TipoRegimenConcurrenciaController(
      ModelMapper modelMapper, TipoRegimenConcurrenciaService service) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoRegimenConcurrencia}
   * activos.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link TipoRegimenConcurrencia}
   *         paginadas y filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C', 'CSP-CON-INV-V', 'CSP-CON-V', 'CSP-CON-E')")
  public ResponseEntity<Page<TipoRegimenConcurrenciaOutput>> findActivos(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findActivos(String query, Pageable paging) - start");
    Page<TipoRegimenConcurrencia> page = service.findActivos(query, paging);

    if (page.isEmpty()) {
      log.debug("findActivos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findActivos(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoRegimenConcurrencia}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link TipoRegimenConcurrencia}
   *         paginadas y filtradas.
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthority('CSP-TRCO-V', 'CSP-TRCO-E', 'CSP-TRCO-B', 'CSP-TRCO-R')")
  public ResponseEntity<Page<TipoRegimenConcurrenciaOutput>> findAll(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<TipoRegimenConcurrencia> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve el {@link TipoRegimenConcurrencia} con el id indicado.
   * 
   * @param id Identificador de {@link TipoRegimenConcurrencia}.
   * @return {@link TipoRegimenConcurrencia} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-TRCO-E')")
  public TipoRegimenConcurrenciaOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    TipoRegimenConcurrencia returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Crea un nuevo {@link TipoRegimenConcurrencia}.
   * 
   * @param tipoRegimenConcurrenciaInput {@link TipoRegimenConcurrencia} que
   *                                     se quiere crear.
   * @return Nuevo {@link TipoRegimenConcurrencia} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('CSP-TRCO-C')")
  public ResponseEntity<TipoRegimenConcurrenciaOutput> create(
      @Valid @RequestBody TipoRegimenConcurrenciaInput tipoRegimenConcurrenciaInput) {
    log.debug("create(TipoRegimenConcurrenciaInput tipoRegimenConcurrenciaInput) - start");
    TipoRegimenConcurrencia returnValue = service.create(convert(tipoRegimenConcurrenciaInput));
    log.debug("create(TipoRegimenConcurrenciaInput tipoRegimenConcurrenciaInput) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link TipoRegimenConcurrencia} con el id indicado.
   * 
   * @param tipoRegimenConcurrenciaInput {@link TipoRegimenConcurrencia} a
   *                                     actualizar.
   * @param id                           id {@link TipoRegimenConcurrencia} a
   *                                     actualizar.
   * @return {@link TipoRegimenConcurrencia} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('CSP-TRCO-E')")
  public TipoRegimenConcurrenciaOutput update(
      @Valid @RequestBody TipoRegimenConcurrenciaInput tipoRegimenConcurrenciaInput,
      @PathVariable Long id) {
    log.debug("update(TipoRegimenConcurrenciaInput tipoRegimenConcurrenciaInput, Long id) - start");
    TipoRegimenConcurrencia returnValue = service.update(convert(id, tipoRegimenConcurrenciaInput));
    log.debug("update(TipoRegimenConcurrenciaInput TipoRegimenConcurrenciaInput, Long id) - end");
    return convert(returnValue);
  }

  /**
   * Activa la {@link TipoRegimenConcurrencia} con id indicado.
   * 
   * @param id Identificador de {@link TipoRegimenConcurrencia}.
   * @return {@link TipoRegimenConcurrencia} actualizado.
   */
  @PatchMapping("/{id}/activar")
  @PreAuthorize("hasAuthority('CSP-TRCO-R')")
  public TipoRegimenConcurrenciaOutput activar(@PathVariable Long id) {
    log.debug("activar(Long id) - start");
    TipoRegimenConcurrencia returnValue = service.activar(id);
    log.debug("activar(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Desactiva el {@link TipoRegimenConcurrencia} con id indicado.
   * 
   * @param id Identificador de {@link TipoRegimenConcurrencia}.
   * @return {@link TipoRegimenConcurrencia} desactivada.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAuthority('CSP-TRCO-B')")
  public TipoRegimenConcurrenciaOutput desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    TipoRegimenConcurrencia returnValue = service.desactivar(id);
    log.debug("desactivar(Long id) - end");
    return convert(returnValue);
  }

  private TipoRegimenConcurrenciaOutput convert(TipoRegimenConcurrencia tipoRegimenConcurrencia) {
    return modelMapper.map(tipoRegimenConcurrencia, TipoRegimenConcurrenciaOutput.class);
  }

  private TipoRegimenConcurrencia convert(TipoRegimenConcurrenciaInput tipoRegimenConcurrenciaInput) {
    return convert(null, tipoRegimenConcurrenciaInput);
  }

  private TipoRegimenConcurrencia convert(Long id,
      TipoRegimenConcurrenciaInput tipoRegimenConcurrenciaInput) {
    TipoRegimenConcurrencia tipoRegimenConcurrencia = modelMapper.map(tipoRegimenConcurrenciaInput,
        TipoRegimenConcurrencia.class);
    tipoRegimenConcurrencia.setId(id);
    return tipoRegimenConcurrencia;
  }

  private Page<TipoRegimenConcurrenciaOutput> convert(Page<TipoRegimenConcurrencia> page) {
    List<TipoRegimenConcurrenciaOutput> content = page.getContent().stream()
        .map(tipoRegimenConcurrencia -> convert(tipoRegimenConcurrencia)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }
}
