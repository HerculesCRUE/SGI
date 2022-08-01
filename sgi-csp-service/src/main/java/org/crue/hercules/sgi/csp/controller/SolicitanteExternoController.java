package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.SolicitanteExternoConverter;
import org.crue.hercules.sgi.csp.dto.SolicitanteExternoInput;
import org.crue.hercules.sgi.csp.dto.SolicitanteExternoOutput;
import org.crue.hercules.sgi.csp.model.SolicitanteExterno;
import org.crue.hercules.sgi.csp.service.SolicitanteExternoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SolicitanteExternoController
 */
@RestController
@RequestMapping(SolicitanteExternoController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
@Validated
public class SolicitanteExternoController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "solicitantes-externos";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final SolicitanteExternoService service;
  private final SolicitanteExternoConverter converter;

  /**
   * Crea nuevo {@link SolicitanteExterno}
   * 
   * @param solicitanteExterno {@link SolicitanteExterno} que se quiere crear.
   * @return Nuevo {@link SolicitanteExterno} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-INV-ER')")
  public ResponseEntity<SolicitanteExternoOutput> create(
      @Valid @RequestBody SolicitanteExternoInput solicitanteExterno) {
    log.debug("create(SolicitanteExternoInput solicitanteExterno) - start");
    SolicitanteExternoOutput returnValue = converter.convert(service.create(converter.convert(solicitanteExterno)));
    log.debug("create(SolicitanteExternoInput solicitanteExterno) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link SolicitanteExterno}.
   * 
   * @param solicitante {@link SolicitanteExterno} a actualizar.
   * @param id          Identificador {@link SolicitanteExterno} a actualizar.
   * @return {@link SolicitanteExterno} actualizado
   */
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-INV-ER')")
  public SolicitanteExternoOutput update(@Valid @RequestBody SolicitanteExternoInput solicitante,
      @PathVariable Long id) {
    log.debug("update(SolicitanteExternoInput solicitanteExterno, Long id) - start");
    SolicitanteExternoOutput returnValue = converter.convert(service.update(converter.convert(id, solicitante)));
    log.debug("update(SolicitanteExternoInput solicitanteExterno, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link SolicitanteExterno} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitanteExterno}.
   * @return {@link SolicitanteExterno} correspondiente al id
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-SOL-INV-ER')")
  public SolicitanteExternoOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    SolicitanteExternoOutput returnValue = converter.convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

}
