package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.SolicitudGrupoConverter;
import org.crue.hercules.sgi.csp.dto.SolicitudGrupoInput;
import org.crue.hercules.sgi.csp.dto.SolicitudGrupoOutput;
import org.crue.hercules.sgi.csp.model.SolicitudGrupo;
import org.crue.hercules.sgi.csp.service.SolicitudGrupoService;
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
 * GrupoController
 */
@RestController
@RequestMapping(SolicitudGrupoController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class SolicitudGrupoController {

  public static final String REQUEST_MAPPING = "/solicitudgrupos";
  public static final String PATH_ID = "/{id}";

  private final SolicitudGrupoService service;
  private final SolicitudGrupoConverter converter;

  /**
   * Crea nuevo {@link SolicitudGrupo}
   * 
   * @param solicitudGrupo {@link SolicitudGrupo} que se quiere crear.
   * @return Nuevo {@link SolicitudGrupo} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-C', 'CSP-SOL-INV-C')")
  public ResponseEntity<SolicitudGrupoOutput> create(@Valid @RequestBody SolicitudGrupoInput solicitudGrupo) {
    log.debug("create(SolicitudGrupoInput solicitudGrupo) - start");
    SolicitudGrupoOutput returnValue = converter.convert(service.create(converter.convert(solicitudGrupo)));
    log.debug("create(SolicitudGrupoInput solicitudGrupo) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link SolicitudGrupo}.
   * 
   * @param solicitudGrupo {@link SolicitudGrupo} a actualizar.
   * @param id             Identificador {@link SolicitudGrupo} a actualizar.
   * @return {@link SolicitudGrupo} actualizado
   */
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-INV-ER')")
  public SolicitudGrupoOutput update(@Valid @RequestBody SolicitudGrupoInput solicitudGrupo, @PathVariable Long id) {
    log.debug("update(SolicitudGrupoInput solicitudGrupo, Long id) - start");
    SolicitudGrupoOutput returnValue = converter.convert(service.update(converter.convert(id, solicitudGrupo)));
    log.debug("update(SolicitudGrupoInput solicitudGrupo, Long id) - end");
    return returnValue;
  }

}
