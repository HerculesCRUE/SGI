package org.crue.hercules.sgi.pii.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.pii.dto.TipoProteccionInput;
import org.crue.hercules.sgi.pii.dto.TipoProteccionOutput;
import org.crue.hercules.sgi.pii.model.TipoProteccion;
import org.crue.hercules.sgi.pii.service.TipoProteccionService;
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
 * TipoProteccionController
 */
@RestController
@RequestMapping(TipoProteccionController.MAPPING)
@Slf4j
public class TipoProteccionController {
  public static final String MAPPING = "/tiposproteccion";

  private ModelMapper modelMapper;

  /** TipoProteccion service */
  private final TipoProteccionService service;

  /**
   * Instancia un nuevo TipoProteccionController.
   * 
   * @param service     Instancia de {@link TipoProteccionService}
   * @param modelMapper Instancia de {@link ModelMapper}
   */
  public TipoProteccionController(ModelMapper modelMapper, TipoProteccionService service) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link TipoProteccion} activos.
   * 
   * @param query  Filtro de búsqueda.
   * @param paging Información de Paginado.
   * @return Lista de entidades {@link TipoProteccion} paginadas y/o filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthority('PII-TPR-V', 'PII-TPR-C', 'PII-TPR-E', 'PII-TPR-B', 'PII-TPR-R', 'PII-INV-V', 'PII-INV-C', 'PII-INV-E', 'PII-INV-B', 'PII-INV-R')")
  ResponseEntity<Page<TipoProteccionOutput>> findActivos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<TipoProteccion> page = service.findActivos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoProteccion}.
   * 
   * @param query  Filtro de búsqueda.
   * @param paging Información de Paginado.
   * @return Lista de entidades {@link TipoProteccion} paginadas y/o filtradas.
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthority('PII-TPR-V', 'PII-TPR-C', 'PII-TPR-E', 'PII-TPR-B', 'PII-TPR-R')")
  ResponseEntity<Page<TipoProteccionOutput>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(String query, Pageable paging) - start");
    Page<TipoProteccion> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodos(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link TipoProteccion} activos que
   * sean Subtipos del {@link TipoProteccion} pasado por párametros.
   * 
   * @param query  Filtro de búsqueda.
   * @param paging Información de Paginado.
   * @return Lista de entidades {@link TipoProteccion} paginadas y/o filtradas.
   */
  @GetMapping("/{id}/subtipos")
  @PreAuthorize("hasAnyAuthority('PII-TPR-V', 'PII-TPR-C', 'PII-TPR-E', 'PII-TPR-B', 'PII-TPR-R', 'PII-INV-V', 'PII-INV-C', 'PII-INV-E')")
  ResponseEntity<Page<TipoProteccionOutput>> findSubtiposProteccion(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findSubtipos(@PathVariable Long id,String query, Pageable paging) - start");
    Page<TipoProteccion> page = service.findSubtiposProteccion(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findSubtipos(@PathVariable Long id,String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findSubtipos(@PathVariable Long id,String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link TipoProteccion} que son
   * Subtipos del {@link TipoProteccion} pasado por párametros sin importar su
   * estado.
   * 
   * @param query  Filtro de búsqueda.
   * @param paging Información de Paginado.
   * @return Lista de entidades {@link TipoProteccion} paginadas y/o filtradas.
   */
  @GetMapping("/{id}/subtipos/todos")
  @PreAuthorize("hasAnyAuthority('PII-TPR-V', 'PII-TPR-C', 'PII-TPR-E', 'PII-TPR-B', 'PII-TPR-R', 'PII-INV-V', 'PII-INV-C', 'PII-INV-E')")
  ResponseEntity<Page<TipoProteccionOutput>> findAllSubtiposProteccion(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSubtiposProteccion(@PathVariable Long id,String query, Pageable paging) - start");
    Page<TipoProteccion> page = service.findAllSubtiposProteccion(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllSubtiposProteccion(@PathVariable Long id,String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllSubtiposProteccion(@PathVariable Long id,String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve el {@link TipoProteccion} con el id indicado.
   * 
   * @param id Identificador de {@link TipoProteccion}.
   * @return {@link TipoProteccion} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('PII-TPR-V', 'PII-TPR-C', 'PII-TPR-E', 'PII-TPR-B', 'PII-TPR-R')")
  TipoProteccionOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    TipoProteccion returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Crea un nuevo {@link TipoProteccion}.
   * 
   * @param tipoProteccion {@link TipoProteccion} que se quiere crear.
   * @return Nuevo {@link TipoProteccion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('PII-TPR-C')")
  ResponseEntity<TipoProteccionOutput> create(@Valid @RequestBody TipoProteccionInput tipoProteccion) {
    log.debug("create(@Valid @RequestBody TipoProteccionInput tipoProteccion) - start");
    TipoProteccion returnValue = service.create(convert(tipoProteccion));
    log.debug("create(@Valid @RequestBody TipoProteccionInput tipoProteccion) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link TipoProteccion} con el id indicado.
   * 
   * @param tipoProteccion {@link TipoProteccion} a actualizar.
   * @param id             id {@link TipoProteccion} a actualizar.
   * @return {@link TipoProteccion} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-TPR-E')")
  TipoProteccionOutput update(@Valid @RequestBody TipoProteccionInput tipoProteccion, @PathVariable Long id) {
    log.debug("update(@Valid @RequestBody TipoProteccionInput tipoProteccion, @PathVariable Long id) - start");
    TipoProteccion returnValue = service.update(convert(id, tipoProteccion));
    log.debug("update(@Valid @RequestBody TipoProteccionInput tipoProteccion, @PathVariable Long id) - end");
    return convert(returnValue);
  }

  /**
   * Activa el {@link TipoProteccion} con id indicado.
   * 
   * @param id Identificador de {@link TipoProteccion}.
   * @return {@link TipoProteccion} actualizado.
   */
  @PatchMapping("/{id}/activar")
  @PreAuthorize("hasAuthority('PII-TPR-R')")
  TipoProteccionOutput activar(@PathVariable Long id) {
    log.debug("reactivar(Long id) - start");
    TipoProteccion returnValue = service.activar(id);
    log.debug("reactivar(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Desactiva el {@link TipoProteccion} con id indicado.
   * 
   * @param id Identificador de {@link TipoProteccion}.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAuthority('PII-TPR-B')")
  TipoProteccionOutput desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    TipoProteccion returnValue = service.desactivar(id);
    log.debug("desactivar(Long id) - end");
    return convert(returnValue);
  }

  private TipoProteccionOutput convert(TipoProteccion tipoProteccion) {
    return modelMapper.map(tipoProteccion, TipoProteccionOutput.class);
  }

  private TipoProteccion convert(TipoProteccionInput tipoProteccionInput) {
    return convert(null, tipoProteccionInput);
  }

  private TipoProteccion convert(Long id, TipoProteccionInput tipoProteccionInput) {
    TipoProteccion tipoProteccion = modelMapper.map(tipoProteccionInput, TipoProteccion.class);
    tipoProteccion.setId(id);
    if (tipoProteccionInput.getPadreId() != null) {
      TipoProteccion padre = new TipoProteccion();
      padre.setId(tipoProteccionInput.getPadreId());
      tipoProteccion.setPadre(padre);
    }
    return tipoProteccion;
  }

  private Page<TipoProteccionOutput> convert(Page<TipoProteccion> page) {
    List<TipoProteccionOutput> content = page.getContent().stream().map((tipoProteccion) -> convert(tipoProteccion))
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }
}
