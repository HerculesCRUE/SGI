package org.crue.hercules.sgi.pii.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.pii.dto.InvencionDocumentoInput;
import org.crue.hercules.sgi.pii.dto.InvencionDocumentoOutput;
import org.crue.hercules.sgi.pii.model.InvencionDocumento;
import org.crue.hercules.sgi.pii.service.InvencionDocumentoService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/invenciondocumentos")
public class InvencionDocumentoController {

  private final ModelMapper modelMapper;
  private final InvencionDocumentoService invencionDocumentoService;

  /**
   * Crea un nuevo {@link InvencionDocumento}.
   * 
   * @param invencionDocumento {@link InvencionDocumento} que se quiere crear.
   * @return Nuevo {@link Invencion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthority('PII-INV-C', 'PII-INV-E')")
  ResponseEntity<InvencionDocumentoOutput> create(@Valid @RequestBody InvencionDocumentoInput invencionDocumento) {

    return new ResponseEntity<>(convert(this.invencionDocumentoService.create(convert(invencionDocumento))),
        HttpStatus.CREATED);
  }

  /**
   * Actualiza la {@link InvencionDocumento} con el id indicado.
   * 
   * @param invencionDocumento
   * @param id
   * @return {@link InvencionDocumento}
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-INV-E')")
  ResponseEntity<InvencionDocumentoOutput> update(@Valid @RequestBody InvencionDocumentoInput invencionDocumento,
      @PathVariable Long id) {

    return ResponseEntity.ok(convert(this.invencionDocumentoService.update(convert(id, invencionDocumento))));

  }

  /**
   * Elimina {@link InvencionDocumento} con id indicado.
   * 
   * @param id Identificador de {@link InvencionDocumento}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('PII-INV-E', 'PII-INV-C')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    this.invencionDocumentoService.delete(id);
  }

  private InvencionDocumentoOutput convert(InvencionDocumento invencionDocumento) {
    return modelMapper.map(invencionDocumento, InvencionDocumentoOutput.class);
  }

  private InvencionDocumento convert(InvencionDocumentoInput invencionDocumentoInput) {
    return convert(null, invencionDocumentoInput);
  }

  private InvencionDocumento convert(Long id, InvencionDocumentoInput invencionDocumentoInput) {
    InvencionDocumento invencionDocumento = modelMapper.map(invencionDocumentoInput, InvencionDocumento.class);
    invencionDocumento.setId(id);
    return invencionDocumento;
  }

}
