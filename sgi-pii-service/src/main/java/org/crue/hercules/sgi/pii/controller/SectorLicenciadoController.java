package org.crue.hercules.sgi.pii.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.pii.dto.SectorLicenciadoInput;
import org.crue.hercules.sgi.pii.dto.SectorLicenciadoOutput;
import org.crue.hercules.sgi.pii.model.SectorLicenciado;
import org.crue.hercules.sgi.pii.service.SectorLicenciadoService;
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
 * SectorLicenciadoController
 */
@RestController
@RequestMapping(SectorLicenciadoController.MAPPING)
@Slf4j
public class SectorLicenciadoController {
  public static final String MAPPING = "/sectoreslicenciados";

  private ModelMapper modelMapper;
  /** SectorLicenciado service */
  private final SectorLicenciadoService service;

  /**
   * Instancia un nuevo SectorLicenciadoController.
   * 
   * @param service     {@link SectorLicenciadoService}
   * @param modelMapper mapper
   */
  public SectorLicenciadoController(ModelMapper modelMapper, SectorLicenciadoService service) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link SectorLicenciado}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return la lista de entidades {@link SectorLicenciado} paginadas y/o
   *         filtradas.
   */
  @GetMapping("")
  @PreAuthorize("hasAnyAuthority('PII-INV-V', 'PII-INV-E')")
  public ResponseEntity<Page<SectorLicenciadoOutput>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<SectorLicenciado> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve una lista {@link SectorLicenciado}.
   * 
   * @param contratoRef identificador de contrato.
   * @return la lista de entidades {@link SectorLicenciado} asociadas al contrato.
   */
  @GetMapping(path = "", params = { "contratoRef" })
  @PreAuthorize("hasAnyAuthority('PII-INV-V', 'PII-INV-E')")
  public ResponseEntity<List<SectorLicenciadoOutput>> findByContratoRef(
      @RequestParam(required = true) String contratoRef) {
    log.debug("findByContratoRef(String contratoRef) - start");
    List<SectorLicenciado> page = service.findByContratoRef(contratoRef);

    if (page.isEmpty()) {
      log.debug("findByContratoRef(String contratoRef) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findByContratoRef(String contratoRef) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve el {@link SectorLicenciado} con el id indicado.
   * 
   * @param id Identificador de {@link SectorLicenciado}.
   * @return {@link SectorLicenciado} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('PII-INV-V', 'PII-INV-E')")
  public SectorLicenciadoOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    SectorLicenciado returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Crea un nuevo {@link SectorLicenciado}.
   * 
   * @param sectorLicenciado {@link SectorLicenciado} que se quiere crear.
   * @return Nuevo {@link SectorLicenciado} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('PII-INV-E')")
  public ResponseEntity<SectorLicenciadoOutput> create(@Valid @RequestBody SectorLicenciadoInput sectorLicenciado) {
    log.debug("create(SectorLicenciado sectorLicenciado) - start");
    SectorLicenciado returnValue = service.create(convert(sectorLicenciado));
    log.debug("create(SectorLicenciado sectorLicenciado) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link SectorLicenciado} con el id indicado.
   * 
   * @param sectorLicenciado {@link SectorLicenciado} a actualizar.
   * @param id               id {@link SectorLicenciado} a actualizar.
   * @return {@link SectorLicenciado} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-INV-E')")
  public SectorLicenciadoOutput update(@Valid @RequestBody SectorLicenciadoInput sectorLicenciado,
      @PathVariable Long id) {
    log.debug("update(SectorLicenciado sectorLicenciado, Long id) - start");
    SectorLicenciado returnValue = service.update(convert(id, sectorLicenciado));
    log.debug("update(SectorLicenciado sectorLicenciado, Long id) - end");
    return convert(returnValue);
  }

  /**
   * Elimina {@link SectorLicenciado} con id indicado.
   * 
   * @param id Identificador de {@link SectorLicenciado}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-INV-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    service.delete(id);
    log.debug("delete(Long id) - end");
  }

  // Converters
  private SectorLicenciadoOutput convert(SectorLicenciado sectorLicenciado) {
    return modelMapper.map(sectorLicenciado, SectorLicenciadoOutput.class);
  }

  private Page<SectorLicenciadoOutput> convert(Page<SectorLicenciado> page) {
    List<SectorLicenciadoOutput> content = page.getContent().stream()
        .map(this::convert).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private List<SectorLicenciadoOutput> convert(List<SectorLicenciado> list) {
    return list.stream().map(this::convert).collect(Collectors.toList());
  }

  private SectorLicenciado convert(SectorLicenciadoInput sectorLicenciadoInput) {
    return convert(null, sectorLicenciadoInput);
  }

  private SectorLicenciado convert(Long id, SectorLicenciadoInput sectorLicenciadoInput) {
    SectorLicenciado sectorLicenciado = modelMapper.map(sectorLicenciadoInput, SectorLicenciado.class);
    sectorLicenciado.setId(id);
    return sectorLicenciado;
  }
}
