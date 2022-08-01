package org.crue.hercules.sgi.pii.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.pii.dto.ProcedimientoDocumentoInput;
import org.crue.hercules.sgi.pii.dto.ProcedimientoDocumentoOutput;
import org.crue.hercules.sgi.pii.model.ProcedimientoDocumento;
import org.crue.hercules.sgi.pii.service.ProcedimientoDocumentoService;
import org.modelmapper.ModelMapper;
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
@Slf4j
@RestController
@RequestMapping(ProcedimientoDocumentoController.MAPPING)
public class ProcedimientoDocumentoController {
  public static final String MAPPING = "/procedimientodocumentos";

  private final ModelMapper modelMapper;
  private final ProcedimientoDocumentoService procedimientoDocumentoService;

  /**
   * Devuelve el {@link ProcedimientoDocumento} con el id indicado.
   * 
   * @param id Identificador de {@link ProcedimientoDocumento}.
   * @return {@link ProcedimientoDocumento} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('PII-INV-E', 'PII-INV-V')")
  public ResponseEntity<ProcedimientoDocumentoOutput> findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProcedimientoDocumento returnValue = procedimientoDocumentoService.findById(id);
    log.debug("findById(Long id) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.OK);
  }

  /**
   * Crea un nuevo {@link ProcedimientoDocumento}.
   * 
   * @param procedimientoDocumentoInput {@link ProcedimientoDocumentoInput} a
   *                                    crear.
   * @return Nuevo {@link ProcedimientoDocumento} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthority('PII-INV-C', 'PII-INV-E')")
  public ResponseEntity<ProcedimientoDocumentoOutput> create(
      @Valid @RequestBody ProcedimientoDocumentoInput procedimientoDocumentoInput) {

    return new ResponseEntity<>(
        convert(this.procedimientoDocumentoService.create(convert(procedimientoDocumentoInput))), HttpStatus.CREATED);
  }

  /**
   * * Actualiza el {@link ProcedimientoDocumento} con el id indicado.
   * 
   * @param procedimientoDocumentoInput {@link ProcedimientoDocumento} a
   *                                    actualizar
   * @param id                          Identificador del
   *                                    {@link ProcedimientoDocumento}
   * @return {@link ProcedimientoDocumento} actualizado
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-INV-E')")
  public ResponseEntity<ProcedimientoDocumentoOutput> update(
      @Valid @RequestBody ProcedimientoDocumentoInput procedimientoDocumentoInput, @PathVariable Long id) {

    return ResponseEntity
        .ok(convert(this.procedimientoDocumentoService.update(convert(id, procedimientoDocumentoInput))));

  }

  /**
   * Elimina el {@link ProcedimientoDocumento} con el id indicado.
   * 
   * @param id Identificador de {@link ProcedimientoDocumento}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('PII-INV-E', 'PII-INV-C')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    this.procedimientoDocumentoService.delete(id);
  }

  private ProcedimientoDocumentoOutput convert(ProcedimientoDocumento procedimientoDocumento) {
    return modelMapper.map(procedimientoDocumento, ProcedimientoDocumentoOutput.class);
  }

  private ProcedimientoDocumento convert(ProcedimientoDocumentoInput procedimientoDocumentoInput) {
    return convert(null, procedimientoDocumentoInput);
  }

  private ProcedimientoDocumento convert(Long id, ProcedimientoDocumentoInput procedimientoDocumentoInput) {
    ProcedimientoDocumento procedimientoDocumento = modelMapper.map(procedimientoDocumentoInput,
        ProcedimientoDocumento.class);
    procedimientoDocumento.setId(id);
    return procedimientoDocumento;
  }

}
