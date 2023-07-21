package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.TipoFacturacionInput;
import org.crue.hercules.sgi.csp.dto.TipoFacturacionOutput;
import org.crue.hercules.sgi.csp.model.TipoFacturacion;
import org.crue.hercules.sgi.csp.service.TipoFacturacionService;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(TipoFacturacionController.REQUEST_MAPPING)
public class TipoFacturacionController {
  protected static final String REQUEST_MAPPING = "/tiposfacturacion";

  private final TipoFacturacionService service;
  private final ModelMapper modelMapper;

  /**
   * Devuelve una lista paginada y filtrada {@link TipoFacturacion}
   * activos.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link TipoFacturacion}
   *         paginadas y filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-C', 'CSP-PRO-E', 'CSP-PRO-B', 'CSP-PRO-MOD-V', 'CSP-PRO-INV-VR')")
  public ResponseEntity<Page<TipoFacturacionOutput>> findActivos(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findActivos(String query, Pageable paging) - start");
    Page<TipoFacturacion> page = service.findActivos(query, paging);

    if (page.isEmpty()) {
      log.debug("findActivos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findActivos(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoFacturacion}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link TipoFacturacion}
   *         paginadas y filtradas.
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-TFAC-V', 'CSP-TFAC-E', 'CSP-TFAC-B', 'CSP-TFAC-R')")
  public ResponseEntity<Page<TipoFacturacionOutput>> findAll(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<TipoFacturacion> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve el {@link TipoFacturacion} con el id indicado.
   * 
   * @param id Identificador de {@link TipoFacturacion}.
   * @return {@link TipoFacturacion} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-TFAC-E')")
  public TipoFacturacionOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    TipoFacturacion returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Crea un nuevo {@link TipoFacturacion}.
   * 
   * @param tipoFacturacionInput {@link TipoFacturacion} que
   *                             se quiere crear.
   * @return Nuevo {@link TipoFacturacion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('CSP-TFAC-C')")
  public ResponseEntity<TipoFacturacionOutput> create(
      @Valid @RequestBody TipoFacturacionInput tipoFacturacionInput) {
    log.debug("create(TipoFacturacionInput tipoFacturacionInput) - start");
    TipoFacturacion returnValue = service.create(convert(tipoFacturacionInput));
    log.debug("create(TipoFacturacionInput tipoFacturacionInput) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link TipoFacturacion} con el id indicado.
   * 
   * @param tipoFacturacionInput {@link TipoFacturacion} a
   *                             actualizar.
   * @param id                   id {@link TipoFacturacion} a
   *                             actualizar.
   * @return {@link TipoFacturacion} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('CSP-TFAC-E')")
  public TipoFacturacionOutput update(
      @Valid @RequestBody TipoFacturacionInput tipoFacturacionInput,
      @PathVariable Long id) {
    log.debug("update(TipoFacturacionInput tipoFacturacionInput, Long id) - start");
    TipoFacturacion returnValue = service.update(convert(id, tipoFacturacionInput));
    log.debug("update(TipoFacturacionInput tipoFacturacionInput, Long id) - end");
    return convert(returnValue);
  }

  /**
   * Activa la {@link TipoFacturacion} con id indicado.
   * 
   * @param id Identificador de {@link TipoFacturacion}.
   * @return {@link TipoFacturacion} actualizado.
   */
  @PatchMapping("/{id}/activar")
  @PreAuthorize("hasAuthority('CSP-TFAC-R')")
  public TipoFacturacionOutput activar(@PathVariable Long id) {
    log.debug("activar(Long id) - start");
    TipoFacturacion returnValue = service.activar(id);
    log.debug("activar(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Desactiva el {@link TipoFacturacion} con id indicado.
   * 
   * @param id Identificador de {@link TipoFacturacion}.
   * @return {@link TipoFacturacion} desactivada.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAuthority('CSP-TFAC-B')")
  public TipoFacturacionOutput desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    TipoFacturacion returnValue = service.desactivar(id);
    log.debug("desactivar(Long id) - end");
    return convert(returnValue);
  }

  private TipoFacturacionOutput convert(TipoFacturacion tipoFacturacion) {
    return modelMapper.map(tipoFacturacion, TipoFacturacionOutput.class);
  }

  private TipoFacturacion convert(TipoFacturacionInput tipoFacturacionInput) {
    return convert(null, tipoFacturacionInput);
  }

  private TipoFacturacion convert(Long id,
      TipoFacturacionInput tipoFacturacionInput) {
    TipoFacturacion tipoFacturacion = modelMapper.map(tipoFacturacionInput,
        TipoFacturacion.class);
    tipoFacturacion.setId(id);
    return tipoFacturacion;
  }

  private Page<TipoFacturacionOutput> convert(Page<TipoFacturacion> page) {
    List<TipoFacturacionOutput> content = page.getContent().stream()
        .map(this::convert).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

}
