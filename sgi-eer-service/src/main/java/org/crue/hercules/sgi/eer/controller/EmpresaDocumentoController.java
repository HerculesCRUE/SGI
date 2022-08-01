package org.crue.hercules.sgi.eer.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.eer.converter.EmpresaDocumentoConverter;
import org.crue.hercules.sgi.eer.dto.EmpresaDocumentoInput;
import org.crue.hercules.sgi.eer.dto.EmpresaDocumentoOutput;
import org.crue.hercules.sgi.eer.model.EmpresaDocumento;
import org.crue.hercules.sgi.eer.service.EmpresaDocumentoService;
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

/**
 * EmpresaDocumentoController
 */
@RestController
@RequestMapping(EmpresaDocumentoController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class EmpresaDocumentoController {
  public static final String REQUEST_MAPPING = "/empresasdocumentos";
  public static final String PATH_ID = "/{id}";

  // Services
  private final EmpresaDocumentoService service;
  // Converters
  private final EmpresaDocumentoConverter converter;

  /**
   * Crea nuevo {@link EmpresaDocumento}
   * 
   * @param empresaDocumento {@link EmpresaDocumento} que se quiere crear.
   * @return Nuevo {@link EmpresaDocumento} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('EER-EER-E')")
  public ResponseEntity<EmpresaDocumentoOutput> create(@Valid @RequestBody EmpresaDocumentoInput empresaDocumento) {
    log.debug("create(EmpresaDocumentoInput empresaDocumento) - start");
    EmpresaDocumentoOutput returnValue = converter.convert(service.create(converter.convert(empresaDocumento)));
    log.debug("create(EmpresaDocumentoInput empresaDocumento) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link EmpresaDocumento}.
   * 
   * @param empresaDocumento {@link EmpresaDocumento} a actualizar.
   * @param id               Identificador {@link EmpresaDocumento} a actualizar.
   * @return {@link EmpresaDocumento} actualizado
   */
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAuthority('EER-EER-E')")
  public EmpresaDocumentoOutput update(@Valid @RequestBody EmpresaDocumentoInput empresaDocumento,
      @PathVariable Long id) {
    log.debug("update(EmpresaDocumentoInput empresaDocumento, Long id) - start");
    EmpresaDocumentoOutput returnValue = converter.convert(service.update(converter.convert(id, empresaDocumento)));
    log.debug("update(EmpresaDocumentoInput empresaDocumento, Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link EmpresaDocumento}.
   * 
   * @param id Identificador {@link EmpresaDocumento} a eliminar.
   */
  @DeleteMapping(PATH_ID)
  @PreAuthorize("hasAuthority('EER-EER-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    service.delete(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Consulta {@link EmpresaDocumento}.
   * 
   * @param id Identificador {@link EmpresaDocumento} a consultar.
   * @return {@link EmpresaDocumento} solicitado
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthority('EER-EER-E', 'EER-EER-V')")
  public EmpresaDocumentoOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    EmpresaDocumentoOutput returnValue = converter.convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }
}
