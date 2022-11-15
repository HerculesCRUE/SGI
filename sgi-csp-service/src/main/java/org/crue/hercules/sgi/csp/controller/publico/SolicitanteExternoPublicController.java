package org.crue.hercules.sgi.csp.controller.publico;

import java.util.UUID;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.SolicitanteExternoConverter;
import org.crue.hercules.sgi.csp.dto.SolicitanteExternoInput;
import org.crue.hercules.sgi.csp.dto.SolicitanteExternoOutput;
import org.crue.hercules.sgi.csp.model.SolicitanteExterno;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.service.SolicitanteExternoService;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping(SolicitanteExternoPublicController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
@Validated
public class SolicitanteExternoPublicController {
  public static final String PATH_DELIMITER = "/";
  public static final String PATH_PUBLIC = PATH_DELIMITER + "public";
  public static final String REQUEST_MAPPING = PATH_PUBLIC + PATH_DELIMITER
      + "solicitudes/{solicitudId}/solicitantes-externos";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final SolicitanteExternoService service;
  private final SolicitanteExternoConverter converter;

  /**
   * Crea nuevo {@link SolicitanteExterno}
   * 
   * @param solicitanteExterno {@link SolicitanteExterno} que se quiere crear.
   * @param solicitudId        Identificador {@link Solicitud}
   * @return Nuevo {@link SolicitanteExterno} creado.
   */
  @PostMapping
  public ResponseEntity<SolicitanteExternoOutput> create(@Valid @RequestBody SolicitanteExternoInput solicitanteExterno,
      @PathVariable String solicitudId) {
    log.debug("create(SolicitanteExternoInput solicitanteExterno, String solicitudId) - start");
    Pair<SolicitanteExterno, UUID> created = service.createByExternalUser(converter.convert(solicitanteExterno));
    SolicitanteExternoOutput returnValue = converter.convert(created.getFirst());
    returnValue.setSolicitudUUID(created.getSecond().toString());
    log.debug("create(SolicitanteExternoInput solicitanteExterno, String solicitudId) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link SolicitanteExterno}.
   * 
   * @param solicitante {@link SolicitanteExterno} a actualizar.
   * @param solicitudId Identificador {@link Solicitud}
   * @param id          Identificador {@link SolicitanteExterno} a actualizar.
   * @return {@link SolicitanteExterno} actualizado
   */
  @PutMapping(PATH_ID)
  public SolicitanteExternoOutput update(
      @Valid @RequestBody SolicitanteExternoInput solicitante,
      @PathVariable String solicitudId,
      @PathVariable Long id) {
    log.debug("update(SolicitanteExternoInput solicitanteExterno, String solicitudId, Long id) - start");
    SolicitanteExternoOutput returnValue = converter
        .convert(service.updateByExternalUser(solicitudId, converter.convert(id, solicitante)));
    log.debug("update(SolicitanteExternoInput solicitanteExterno, String solicitudId, Long id) - end");
    return returnValue;
  }

}
