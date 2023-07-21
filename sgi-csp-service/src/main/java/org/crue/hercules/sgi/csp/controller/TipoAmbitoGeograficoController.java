package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.TipoAmbitoGeograficoInput;
import org.crue.hercules.sgi.csp.dto.TipoAmbitoGeograficoOutput;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.service.TipoAmbitoGeograficoService;
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
 * TipoAmbitoGeograficoController
 */
@RestController
@RequestMapping("/tipoambitogeograficos")
@Slf4j
public class TipoAmbitoGeograficoController {

  private ModelMapper modelMapper;
  private TipoAmbitoGeograficoService service;

  /**
   * Instancia un nuevo TipoAmbitoGeograficoController.
   * 
   * @param modelMapper {@link ModelMapper}
   * @param service     {@link TipoAmbitoGeograficoController}
   */
  public TipoAmbitoGeograficoController(
      ModelMapper modelMapper, TipoAmbitoGeograficoService service) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link TipoAmbitoGeografico}
   * activos.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link TipoAmbitoGeografico}
   *         paginadas y filtradas.
   */
  @GetMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-V','CSP-CON-INV-V','CSP-CON-C','CSP-CON-E','CSP-CON-B','CSP-CON-R','CSP-PRO-V', 'CSP-PRO-C', 'CSP-PRO-E', 'CSP-PRO-B', 'CSP-PRO-R', 'CSP-FNT-V', 'CSP-FNT-C', 'CSP-FNT-E', 'CSP-FNT-B', 'CSP-FNT-R')")

  public ResponseEntity<Page<TipoAmbitoGeograficoOutput>> findActivos(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findActivos(String query, Pageable paging) - start");
    Page<TipoAmbitoGeografico> page = service.findActivos(query, paging);

    if (page.isEmpty()) {
      log.debug("findActivos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findActivos(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Crea un nuevo {@link TipoAmbitoGeografico}.
   * 
   * @param tipoAmbitoGeografico {@link TipoAmbitoGeografico} que
   *                             se quiere crear.
   * @return Nuevo {@link TipoAmbitoGeografico} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('CSP-TAGE-C')")
  public ResponseEntity<TipoAmbitoGeograficoOutput> create(
      @Valid @RequestBody TipoAmbitoGeograficoInput tipoAmbitoGeografico) {
    log.debug("create(TipoAmbitoGeograficoInput tipoAmbitoGeografico) - start");
    TipoAmbitoGeografico returnValue = service.create(convert(tipoAmbitoGeografico));
    log.debug("create(TipoAmbitoGeograficoInput tipoAmbitoGeografico) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link TipoAmbitoGeografico} con el id indicado.
   * 
   * @param tipoAmbitoGeograficoInput {@link TipoAmbitoGeografico} a
   *                                  actualizar.
   * @param id                        id {@link TipoAmbitoGeografico} a
   *                                  actualizar.
   * @return {@link TipoAmbitoGeografico} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('CSP-TAGE-E')")
  public TipoAmbitoGeograficoOutput update(
      @Valid @RequestBody TipoAmbitoGeograficoInput tipoAmbitoGeograficoInput,
      @PathVariable Long id) {
    log.debug("update(TipoAmbitoGeograficoInput tipoAmbitoGeograficoInput, Long id) - start");
    TipoAmbitoGeografico returnValue = service.update(convert(id, tipoAmbitoGeograficoInput));
    log.debug("update(TipoAmbitoGeograficoInput tipoAmbitoGeograficoInput, Long id) - end");
    return convert(returnValue);
  }

  /**
   * Activa la {@link TipoAmbitoGeografico} con id indicado.
   * 
   * @param id Identificador de {@link TipoAmbitoGeografico}.
   * @return {@link TipoAmbitoGeografico} actualizado.
   */
  @PatchMapping("/{id}/activar")
  @PreAuthorize("hasAuthority('CSP-TAGE-R')")
  public TipoAmbitoGeograficoOutput activar(@PathVariable Long id) {
    log.debug("reactivar(Long id) - start");
    TipoAmbitoGeografico returnValue = service.activar(id);
    log.debug("reactivar(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Desactiva el {@link TipoAmbitoGeografico} con id indicado.
   * 
   * @param id Identificador de {@link TipoAmbitoGeografico}.
   * @return {@link TipoAmbitoGeografico} desactivada.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAuthority('CSP-TAGE-B')")
  public TipoAmbitoGeograficoOutput desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    TipoAmbitoGeografico returnValue = service.desactivar(id);
    log.debug("desactivar(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Devuelve una lista completa, paginada y filtrada de
   * {@link TipoAmbitoGeografico}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link TipoAmbitoGeografico}
   *         paginadas y filtradas.
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthority('CSP-TAGE-V', 'CSP-TAGE-C', 'CSP-TAGE-E')")
  public ResponseEntity<Page<TipoAmbitoGeograficoOutput>> findAll(
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<TipoAmbitoGeograficoOutput> page = convert(service.findAll(query, paging));

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link TipoAmbitoGeografico} con el id indicado.
   * 
   * @param id Identificador de {@link TipoAmbitoGeografico}.
   * @return {@link TipoAmbitoGeografico} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-C')")
  public TipoAmbitoGeograficoOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    TipoAmbitoGeografico returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  private TipoAmbitoGeograficoOutput convert(TipoAmbitoGeografico tipoAmbitoGeografico) {
    return modelMapper.map(tipoAmbitoGeografico, TipoAmbitoGeograficoOutput.class);
  }

  private TipoAmbitoGeografico convert(TipoAmbitoGeograficoInput tipoAmbitoGeograficoInput) {
    return convert(null, tipoAmbitoGeograficoInput);
  }

  private TipoAmbitoGeografico convert(Long id,
      TipoAmbitoGeograficoInput tipoAmbitoGeograficoInput) {
    TipoAmbitoGeografico tipoAmbitoGeografico = modelMapper.map(tipoAmbitoGeograficoInput,
        TipoAmbitoGeografico.class);
    tipoAmbitoGeografico.setId(id);
    return tipoAmbitoGeografico;
  }

  private Page<TipoAmbitoGeograficoOutput> convert(Page<TipoAmbitoGeografico> page) {
    List<TipoAmbitoGeograficoOutput> content = page.getContent().stream()
        .map(this::convert).collect(Collectors.toList());
    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

}