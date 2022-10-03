package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.IncidenciaDocumentacionRequerimientoConverter;
import org.crue.hercules.sgi.csp.dto.IncidenciaDocumentacionRequerimientoAlegacionInput;
import org.crue.hercules.sgi.csp.dto.IncidenciaDocumentacionRequerimientoInput;
import org.crue.hercules.sgi.csp.dto.IncidenciaDocumentacionRequerimientoOutput;
import org.crue.hercules.sgi.csp.model.IncidenciaDocumentacionRequerimiento;
import org.crue.hercules.sgi.csp.service.IncidenciaDocumentacionRequerimientoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
 * IncidenciaDocumentacionRequerimientoController
 */
@RestController
@RequestMapping(IncidenciaDocumentacionRequerimientoController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class IncidenciaDocumentacionRequerimientoController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "incidencias-documentacion";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";
  public static final String PATH_ALEGAR = PATH_ID + PATH_DELIMITER + "alegar";

  private final IncidenciaDocumentacionRequerimientoService service;
  private final IncidenciaDocumentacionRequerimientoConverter converter;

  /**
   * Elimina el {@link IncidenciaDocumentacionRequerimiento} con el id indicado.
   * 
   * @param id Identificador de {@link IncidenciaDocumentacionRequerimiento}.
   */
  @DeleteMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SJUS-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.deleteById(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Crea una entidad {@link IncidenciaDocumentacionRequerimiento}.
   * 
   * @param incidenciaDocumentacionRequerimiento datos de la entidad
   *                                             {@link IncidenciaDocumentacionRequerimiento}
   *                                             a crear.
   * @return la entidad {@link IncidenciaDocumentacionRequerimiento} creada.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SJUS-E')")
  public ResponseEntity<IncidenciaDocumentacionRequerimientoOutput> create(
      @Valid @RequestBody IncidenciaDocumentacionRequerimientoInput incidenciaDocumentacionRequerimiento) {
    log.debug("create(IncidenciaDocumentacionRequerimientoInput incidenciaDocumentacionRequerimiento) - start");
    IncidenciaDocumentacionRequerimientoOutput returnValue = converter
        .convert(service.create(converter.convert(incidenciaDocumentacionRequerimiento)));

    log.debug("create(IncidenciaDocumentacionRequerimientoInput incidenciaDocumentacionRequerimiento) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza una entidad {@link IncidenciaDocumentacionRequerimiento} con id
   * indicado.
   * 
   * @param id                               identificador de la entidad
   *                                         {@link IncidenciaDocumentacionRequerimiento}.
   * @param incidenciaDocumentoRequerimiento datos del DTO
   *                                         {@link IncidenciaDocumentacionRequerimientoInput}
   *                                         a actualizar.
   * @return la entidad {@link IncidenciaDocumentacionRequerimiento} actualizada.
   */
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SJUS-E')")
  public IncidenciaDocumentacionRequerimientoOutput update(@PathVariable Long id,
      @Valid @RequestBody IncidenciaDocumentacionRequerimientoInput incidenciaDocumentoRequerimiento) {
    log.debug(
        "update(Long id, IncidenciaDocumentacionRequerimientoInput incidenciaDocumentoRequerimiento) - start");
    IncidenciaDocumentacionRequerimientoOutput returnValue = converter
        .convert(service.update(converter.convert(incidenciaDocumentoRequerimiento, id)));

    log.debug(
        "update(Long id, IncidenciaDocumentacionRequerimientoInput incidenciaDocumentoRequerimiento) - end");
    return returnValue;
  }

  /**
   * Actualiza el campo alegacion de una entidad
   * {@link IncidenciaDocumentacionRequerimiento} con id
   * indicado.
   * 
   * @param id        identificador de la entidad
   *                  {@link IncidenciaDocumentacionRequerimiento}.
   * @param alegacion datos del DTO
   *                  {@link IncidenciaDocumentacionRequerimientoInput}
   *                  a actualizar.
   * @return la entidad {@link IncidenciaDocumentacionRequerimiento} actualizada.
   */
  @PatchMapping(PATH_ALEGAR)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SJUS-E')")
  public IncidenciaDocumentacionRequerimientoOutput updateAlegacion(@PathVariable Long id,
      @Valid @RequestBody IncidenciaDocumentacionRequerimientoAlegacionInput alegacion) {
    log.debug(
        "updateAlegacion(Long id, IncidenciaDocumentacionRequerimientoAlegacionInput alegacion) - start");
    IncidenciaDocumentacionRequerimientoOutput returnValue = converter
        .convert(service.updateAlegacion(converter.convert(alegacion, id)));

    log.debug(
        "updateAlegacion(Long id, IncidenciaDocumentacionRequerimientoAlegacionInput alegacion) - end");
    return returnValue;
  }
}
