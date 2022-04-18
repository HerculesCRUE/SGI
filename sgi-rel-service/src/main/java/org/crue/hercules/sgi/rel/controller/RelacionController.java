package org.crue.hercules.sgi.rel.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.rel.dto.RelacionInput;
import org.crue.hercules.sgi.rel.dto.RelacionOutput;
import org.crue.hercules.sgi.rel.model.Relacion;
import org.crue.hercules.sgi.rel.service.RelacionService;
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
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * RelacionController
 */
@RestController
@RequestMapping(RelacionController.MAPPING)
@Slf4j
public class RelacionController {
  public static final String MAPPING = "/relaciones";

  private ModelMapper modelMapper;

  /** Relacion service */
  private final RelacionService service;

  /**
   * Instancia un nuevo RelacionController.
   * 
   * @param service     {@link RelacionService}
   * @param modelMapper mapper
   */
  public RelacionController(ModelMapper modelMapper, RelacionService service) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Relacion}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return la lista de entidades {@link Relacion} paginadas y/o filtradas.
   */
  @GetMapping()
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-rel')) or hasAnyAuthority('REL-V', 'REL-E', 'REL-C', 'REL-B')")
  ResponseEntity<Page<RelacionOutput>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<Relacion> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve la {@link Relacion} con el id indicado.
   * 
   * @param id Identificador de {@link Relacion}.
   * @return {@link Relacion} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('REL-V', 'REL-E', 'REL-C', 'REL-B')")
  RelacionOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    Relacion returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Crea un nuevo {@link Relacion}.
   * 
   * @param relacion {@link Relacion} que se quiere crear.
   * @return Nuevo {@link Relacion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('REL-C')")
  ResponseEntity<RelacionOutput> create(@Valid @RequestBody RelacionInput relacion) {
    log.debug("create(SectorAplicacion sectorAplicacion) - start");
    Relacion returnValue = service.create(convert(relacion));
    log.debug("create(SectorAplicacion sectorAplicacion) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link Relacion} con el id indicado.
   * 
   * @param relacion {@link Relacion} a actualizar.
   * @param id       id {@link Relacion} a actualizar.
   * @return {@link Relacion} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('REL-E')")
  RelacionOutput update(@Valid @RequestBody RelacionInput relacion, @PathVariable Long id) {
    log.debug("update(Relacion relacion, Long id) - start");
    Relacion returnValue = service.update(convert(id, relacion));
    log.debug("update(Relacion relacion, Long id) - end");
    return convert(returnValue);
  }

  /**
   * Elimina {@link Relacion} con id indicado.
   * 
   * @param id Identificador de {@link Relacion}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('REL-B')")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    service.delete(id);
    log.debug("delete(Long id) - end");
  }

  private RelacionOutput convert(Relacion sectorAplicacion) {
    return modelMapper.map(sectorAplicacion, RelacionOutput.class);
  }

  private Relacion convert(RelacionInput sectorAplicacionInput) {
    return convert(null, sectorAplicacionInput);
  }

  private Relacion convert(Long id, RelacionInput sectorAplicacionInput) {
    Relacion sectorAplicacion = modelMapper.map(sectorAplicacionInput, Relacion.class);
    sectorAplicacion.setId(id);
    return sectorAplicacion;
  }

  private Page<RelacionOutput> convert(Page<Relacion> page) {
    List<RelacionOutput> content = page.getContent().stream().map((relacion) -> convert(relacion))
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }
}
