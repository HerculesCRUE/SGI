package org.crue.hercules.sgi.csp.controller.publico;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.SolicitudRrhhRequisitoNivelAcademicoConverter;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhRequisitoNivelAcademicoInput;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhRequisitoNivelAcademicoOutput;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudRrhhRequisitoNivelAcademico;
import org.crue.hercules.sgi.csp.service.SolicitudRrhhRequisitoNivelAcademicoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * SolicitudRrhhRequisitoNivelAcademicoPublicController
 */
@RestController
@RequestMapping(SolicitudRrhhRequisitoNivelAcademicoPublicController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class SolicitudRrhhRequisitoNivelAcademicoPublicController {

  public static final String PATH_DELIMITER = "/";
  public static final String PATH_PUBLIC = PATH_DELIMITER + "public";
  public static final String REQUEST_MAPPING = PATH_PUBLIC + PATH_DELIMITER
      + "solicitudes/{solicitudId}/solicitud-rrhh-requisitos-nivel-academico";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final SolicitudRrhhRequisitoNivelAcademicoService service;
  private final SolicitudRrhhRequisitoNivelAcademicoConverter converter;

  /**
   * Crea nuevo {@link SolicitudRrhhRequisitoNivelAcademico}.
   * 
   * @param solicitudId             Identificador {@link Solicitud} a actualizar.
   * @param requisitoNivelAcademico {@link SolicitudRrhhRequisitoNivelAcademico}
   *                                que se quiere crear.
   * @return Nuevo {@link SolicitudRrhhRequisitoNivelAcademico} creado.
   */
  @PostMapping
  public ResponseEntity<SolicitudRrhhRequisitoNivelAcademicoOutput> create(@PathVariable String solicitudId,
      @Valid @RequestBody SolicitudRrhhRequisitoNivelAcademicoInput requisitoNivelAcademico) {
    log.debug("create(String solicitudId, SolicitudRrhhRequisitoNivelAcademicoInput requisitoNivelAcademico) - start");
    SolicitudRrhhRequisitoNivelAcademicoOutput returnValue = converter
        .convert(service.createByExternalUser(solicitudId, converter.convert(requisitoNivelAcademico)));
    log.debug("create(String solicitudId, SolicitudRrhhRequisitoNivelAcademicoInput requisitoNivelAcademico) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Elimina el {@link SolicitudRrhhRequisitoNivelAcademico} con el id indicado.
   * 
   * @param solicitudId Identificador {@link Solicitud} a actualizar.
   * @param id          Identificador de
   *                    {@link SolicitudRrhhRequisitoNivelAcademico}.
   */
  @DeleteMapping(PATH_ID)
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String solicitudId, @PathVariable Long id) {
    log.debug("delete(String solicitudId, Long id) - start");
    service.deleteByExternalUser(solicitudId, id);
    log.debug("delete(String solicitudId, Long id) - end");
  }

}
