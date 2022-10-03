package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.AlegacionRequerimientoConverter;
import org.crue.hercules.sgi.csp.dto.AlegacionRequerimientoInput;
import org.crue.hercules.sgi.csp.dto.AlegacionRequerimientoOutput;
import org.crue.hercules.sgi.csp.model.AlegacionRequerimiento;
import org.crue.hercules.sgi.csp.service.AlegacionRequerimientoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AlegacionRequerimientoController
 */
@RestController
@RequestMapping(AlegacionRequerimientoController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class AlegacionRequerimientoController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "alegaciones-requerimiento";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final AlegacionRequerimientoService service;
  private final AlegacionRequerimientoConverter converter;

  /**
   * Crea una entidad {@link AlegacionRequerimiento}.
   * 
   * @param alegacionRequerimiento datos de la entidad
   *                               {@link AlegacionRequerimiento}
   *                               a crear.
   * @return la entidad {@link AlegacionRequerimiento} creada.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SJUS-E')")
  public ResponseEntity<AlegacionRequerimientoOutput> create(
      @Valid @RequestBody AlegacionRequerimientoInput alegacionRequerimiento) {
    log.debug("create(AlegacionRequerimientoInput alegacionRequerimiento) - start");
    AlegacionRequerimientoOutput returnValue = converter
        .convert(service.create(converter.convert(alegacionRequerimiento)));

    log.debug("create(AlegacionRequerimientoInput alegacionRequerimiento) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza una entidad {@link AlegacionRequerimiento} con id
   * indicado.
   * 
   * @param id                     identificador de la entidad
   *                               {@link AlegacionRequerimiento}.
   * @param alegacionRequerimiento datos del DTO
   *                               {@link AlegacionRequerimientoInput}
   *                               a actualizar.
   * @return la entidad {@link AlegacionRequerimiento} actualizada.
   */
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SJUS-E')")
  public AlegacionRequerimientoOutput update(@PathVariable Long id,
      @Valid @RequestBody AlegacionRequerimientoInput alegacionRequerimiento) {
    log.debug(
        "update(Long id, AlegacionRequerimientoInput alegacionRequerimiento) - start");
    AlegacionRequerimientoOutput returnValue = converter
        .convert(service.update(converter.convert(alegacionRequerimiento, id)));

    log.debug(
        "update(Long id, AlegacionRequerimientoInput alegacionRequerimiento) - end");
    return returnValue;
  }
}
