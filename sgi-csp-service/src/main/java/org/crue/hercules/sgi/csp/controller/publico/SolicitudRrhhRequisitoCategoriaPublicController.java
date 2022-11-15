package org.crue.hercules.sgi.csp.controller.publico;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.SolicitudRrhhRequisitoCategoriaConverter;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhRequisitoCategoriaInput;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhRequisitoCategoriaOutput;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudRrhhRequisitoCategoria;
import org.crue.hercules.sgi.csp.service.SolicitudRrhhRequisitoCategoriaService;
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
 * SolicitudRrhhRequisitoCategoriaPublicController
 */
@RestController
@RequestMapping(SolicitudRrhhRequisitoCategoriaPublicController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class SolicitudRrhhRequisitoCategoriaPublicController {

  public static final String PATH_DELIMITER = "/";
  public static final String PATH_PUBLIC = PATH_DELIMITER + "public";
  public static final String REQUEST_MAPPING = PATH_PUBLIC + PATH_DELIMITER
      + "solicitudes/{solicitudId}/solicitud-rrhh-requisitos-categoria";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final SolicitudRrhhRequisitoCategoriaService service;
  private final SolicitudRrhhRequisitoCategoriaConverter converter;

  /**
   * Crea nuevo {@link SolicitudRrhhRequisitoCategoria}.
   * 
   * @param solicitudId        Identificador {@link Solicitud} a actualizar.
   * @param requisitoCategoria {@link SolicitudRrhhRequisitoCategoria}
   *                           que se quiere crear.
   * @return Nuevo {@link SolicitudRrhhRequisitoCategoria} creado.
   */
  @PostMapping
  public ResponseEntity<SolicitudRrhhRequisitoCategoriaOutput> create(@PathVariable String solicitudId,
      @Valid @RequestBody SolicitudRrhhRequisitoCategoriaInput requisitoCategoria) {
    log.debug("create(String solicitudId, SolicitudRrhhRequisitoCategoriaInput requisitoCategoria) - start");
    SolicitudRrhhRequisitoCategoriaOutput returnValue = converter
        .convert(service.createByExternalUser(solicitudId, converter.convert(requisitoCategoria)));
    log.debug("create(String solicitudId, SolicitudRrhhRequisitoCategoriaInput requisitoCategoria) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Elimina el {@link SolicitudRrhhRequisitoCategoria} con el id indicado.
   * 
   * @param solicitudId Identificador {@link Solicitud} a actualizar.
   * @param id          Identificador de {@link SolicitudRrhhRequisitoCategoria}.
   */
  @DeleteMapping(PATH_ID)
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String solicitudId, @PathVariable Long id) {
    log.debug("delete(String solicitudId, Long id) - start");
    service.deleteByExternalUser(solicitudId, id);
    log.debug("delete(String solicitudId, Long id) - end");
  }

}
