package org.crue.hercules.sgi.pii.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.pii.dto.ProcedimientoDocumentoOutput;
import org.crue.hercules.sgi.pii.dto.ProcedimientoInput;
import org.crue.hercules.sgi.pii.dto.ProcedimientoOutput;
import org.crue.hercules.sgi.pii.model.Procedimiento;
import org.crue.hercules.sgi.pii.model.ProcedimientoDocumento;
import org.crue.hercules.sgi.pii.service.ProcedimientoDocumentoService;
import org.crue.hercules.sgi.pii.service.ProcedimientoService;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@RequestMapping(ProcedimientoController.MAPPING)
@Slf4j
public class ProcedimientoController {
  public static final String MAPPING = "/procedimientos";

  private final ProcedimientoService procedimientoService;
  private final ProcedimientoDocumentoService procedimientoDocumentoService;
  private final ModelMapper modelMapper;

  @PostConstruct
  public void mapperConfig() {
    modelMapper.addMappings(new PropertyMap<ProcedimientoInput, Procedimiento>() {
      protected void configure() {
        skip().setId(0L);
      }
    });
  }

  /**
   * Crea un nuevo {@link Procedimiento}.
   * 
   * @param procedimiento {@link Procedimiento} a crear.
   * @return {@link Procedimiento} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('PII-INV-E')")
  public ResponseEntity<ProcedimientoOutput> create(@Valid @RequestBody ProcedimientoInput procedimiento) {
    log.debug("create(@Valid @RequestBody ProcedimientoInput procedimiento) - start");
    Procedimiento returnValue = procedimientoService.create(convert(procedimiento));
    log.debug("create(@Valid @RequestBody ProcedimientoInput procedimiento) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Devuelve el {@link Procedimiento} con el id indicado.
   * 
   * @param id Identificador de {@link Procedimiento}.
   * @return {@link Procedimiento} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-INV-V')")
  public ResponseEntity<ProcedimientoOutput> findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    Procedimiento returnValue = procedimientoService.findById(id);
    log.debug("findById(Long id) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.OK);
  }

  /**
   * Actualiza el {@link Procedimiento} con el id indicado.
   * 
   * @param procedimiento {@link Procedimiento} a actualizar.
   * @param id            id {@link Procedimiento} a actualizar.
   * @return {@link Procedimiento} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-INV-E')")
  public ProcedimientoOutput update(@Valid @RequestBody ProcedimientoInput procedimiento, @PathVariable Long id) {
    log.debug("update(@Valid @RequestBody ProcedimientoInput procedimiento, @PathVariable Long id) - start");
    Procedimiento returnValue = procedimientoService.update(convert(id, procedimiento));
    log.debug("update(@Valid @RequestBody ProcedimientoInput procedimiento, @PathVariable Long id) - end");
    return convert(returnValue);
  }

  /**
   * Elimina el {@link Procedimiento} con id indicado.
   * 
   * @param id Identificador de {@link Procedimiento}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-INV-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    this.procedimientoDocumentoService.deleteByProcedimiento(id);
    this.procedimientoService.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve los {@link ProcedimientoDocumento} asociados al
   * {@link Procedimiento} con el id indicado.
   * 
   * @param procedimientoId Identificador del {@link Procedimiento}
   * @return Elementos paginados de tipo {@link ProcedimientoDocumento} asociados
   *         al {@link Procedimiento}
   */
  @GetMapping("/{procedimientoId}/documentos")
  @PreAuthorize("hasAnyAuthority('PII-INV-E', 'PII-INV-V')")
  public ResponseEntity<Page<ProcedimientoDocumentoOutput>> findProcedimientoDocumentosByProcedimiento(
      @PathVariable Long procedimientoId, @RequestPageable(sort = "s") Pageable paging) {
    log.debug(
        "findProcedimientoDocumentosByProcedimiento(@PathVariable Long procedimientoId, @RequestPageable(sort = 's') Pageable paging) - start");

    Page<ProcedimientoDocumento> page = procedimientoDocumentoService.findAllByProcedimientoId(procedimientoId, paging);

    if (page.isEmpty()) {
      log.debug(
          "findProcedimientoDocumentosByProcedimiento(@PathVariable Long procedimientoId, @RequestPageable(sort = 's') Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug(
        "findProcedimientoDocumentosByProcedimiento(@PathVariable Long procedimientoId, @RequestPageable(sort = 's') Pageable paging) - end");
    return ResponseEntity.ok().body(convertToProcedimientoDocumentosPage(page));
  }

  private Procedimiento convert(ProcedimientoInput procedimientoInput) {
    return convert(null, procedimientoInput);
  }

  private Procedimiento convert(Long id, ProcedimientoInput procedimientoInput) {
    Procedimiento procedimiento = modelMapper.map(procedimientoInput, Procedimiento.class);
    procedimiento.setId(id);
    return procedimiento;
  }

  private ProcedimientoOutput convert(Procedimiento procedimiento) {
    return modelMapper.map(procedimiento, ProcedimientoOutput.class);
  }

  private Page<ProcedimientoDocumentoOutput> convertToProcedimientoDocumentosPage(Page<ProcedimientoDocumento> page) {
    List<ProcedimientoDocumentoOutput> content = page.getContent().stream().map(this::convert)
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private ProcedimientoDocumentoOutput convert(ProcedimientoDocumento procedimiento) {
    return modelMapper.map(procedimiento, ProcedimientoDocumentoOutput.class);
  }

}
