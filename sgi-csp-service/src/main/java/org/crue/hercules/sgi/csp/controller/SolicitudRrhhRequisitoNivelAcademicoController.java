package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.SolicitudRrhhRequisitoNivelAcademicoConverter;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhRequisitoNivelAcademicoInput;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhRequisitoNivelAcademicoOutput;
import org.crue.hercules.sgi.csp.model.SolicitudRrhhRequisitoNivelAcademico;
import org.crue.hercules.sgi.csp.service.SolicitudRrhhRequisitoNivelAcademicoService;
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
 * SolicitudRrhhRequisitoNivelAcademicoController
 */
@RestController
@RequestMapping(SolicitudRrhhRequisitoNivelAcademicoController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class SolicitudRrhhRequisitoNivelAcademicoController {

  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "solicitud-rrhh-requisitos-nivel-academico";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final SolicitudRrhhRequisitoNivelAcademicoService service;
  private final SolicitudRrhhRequisitoNivelAcademicoConverter converter;

  /**
   * Crea nuevo {@link SolicitudRrhhRequisitoNivelAcademico}.
   * 
   * @param requisitoNivelAcademico {@link SolicitudRrhhRequisitoNivelAcademico}
   *                                que se quiere crear.
   * @return Nuevo {@link SolicitudRrhhRequisitoNivelAcademico} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-INV-ER')")
  public ResponseEntity<SolicitudRrhhRequisitoNivelAcademicoOutput> create(
      @Valid @RequestBody SolicitudRrhhRequisitoNivelAcademicoInput requisitoNivelAcademico) {
    log.debug("create(SolicitudRrhhRequisitoNivelAcademicoInput requisitoNivelAcademico) - start");
    SolicitudRrhhRequisitoNivelAcademicoOutput returnValue = converter
        .convert(service.create(converter.convert(requisitoNivelAcademico)));
    log.debug("create(SolicitudRrhhRequisitoNivelAcademicoInput requisitoNivelAcademico) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Elimina el {@link SolicitudRrhhRequisitoNivelAcademico} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitudRrhhRequisitoNivelAcademico}.
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
