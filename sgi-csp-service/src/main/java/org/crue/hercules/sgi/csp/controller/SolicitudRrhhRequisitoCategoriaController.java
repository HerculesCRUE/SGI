package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.SolicitudRrhhRequisitoCategoriaConverter;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhRequisitoCategoriaInput;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhRequisitoCategoriaOutput;
import org.crue.hercules.sgi.csp.model.SolicitudRrhhRequisitoCategoria;
import org.crue.hercules.sgi.csp.service.SolicitudRrhhRequisitoCategoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SolicitudRrhhRequisitoCategoriaController
 */
@RestController
@RequestMapping(SolicitudRrhhRequisitoCategoriaController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class SolicitudRrhhRequisitoCategoriaController {

  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "solicitud-rrhh-requisitos-categoria";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final SolicitudRrhhRequisitoCategoriaService service;
  private final SolicitudRrhhRequisitoCategoriaConverter converter;

  /**
   * Crea nuevo {@link SolicitudRrhhRequisitoCategoria}.
   * 
   * @param requisitoCategoria {@link SolicitudRrhhRequisitoCategoria}
   *                           que se quiere crear.
   * @return Nuevo {@link SolicitudRrhhRequisitoCategoria} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-INV-ER')")
  public ResponseEntity<SolicitudRrhhRequisitoCategoriaOutput> create(
      @Valid @RequestBody SolicitudRrhhRequisitoCategoriaInput requisitoCategoria) {
    log.debug("create(SolicitudRrhhRequisitoCategoriaInput requisitoCategoria) - start");
    SolicitudRrhhRequisitoCategoriaOutput returnValue = converter
        .convert(service.create(converter.convert(requisitoCategoria)));
    log.debug("create(SolicitudRrhhRequisitoCategoriaInput requisitoCategoria) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Elimina el {@link SolicitudRrhhRequisitoCategoria} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitudRrhhRequisitoCategoria}.
   */
  @DeleteMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-INV-ER')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

}
