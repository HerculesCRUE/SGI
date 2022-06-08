package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.GrupoPersonaAutorizadaConverter;
import org.crue.hercules.sgi.csp.dto.GrupoPersonaAutorizadaInput;
import org.crue.hercules.sgi.csp.dto.GrupoPersonaAutorizadaOutput;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoPersonaAutorizada;
import org.crue.hercules.sgi.csp.service.GrupoPersonaAutorizadaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * GrupoPersonaAutorizadaController
 */
@RestController
@RequestMapping(GrupoPersonaAutorizadaController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
@Validated
public class GrupoPersonaAutorizadaController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "grupospersonasautorizadas";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final GrupoPersonaAutorizadaService service;
  private final GrupoPersonaAutorizadaConverter converter;

  /**
   * Devuelve el {@link GrupoPersonaAutorizada} con el id indicado.
   * 
   * @param id Identificador de {@link GrupoPersonaAutorizada}.
   * @return {@link GrupoPersonaAutorizada} correspondiente al id
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V')")
  public GrupoPersonaAutorizadaOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    GrupoPersonaAutorizadaOutput returnValue = converter.convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link GrupoPersonaAutorizada} del {@link Grupo}
   * con el
   * listado grupoPersonaAutorizadas añadiendo, editando o eliminando los
   * elementos segun
   * proceda.
   * 
   * @param id                       Id del {@link Grupo}.
   * @param grupoPersonasAutorizadas lista con los nuevos
   *                                 {@link GrupoPersonaAutorizada} a
   *                                 guardar.
   * @return Lista actualizada con los {@link GrupoPersonaAutorizada}.
   */
  @PatchMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V')")
  public ResponseEntity<List<GrupoPersonaAutorizadaOutput>> update(@PathVariable Long id,
      @RequestBody List<@Valid GrupoPersonaAutorizadaInput> grupoPersonasAutorizadas) {
    log.debug("update(Long id, List<GrupoPersonaAutorizadaInput> grupoPersonaAutorizadas) - start");
    List<GrupoPersonaAutorizadaOutput> returnValue = converter
        .convertGrupoPersonaAutorizadas(service.update(id, converter.convertGrupoPersonaAutorizadaInput(
            grupoPersonasAutorizadas)));
    log.debug("update(Long id, List<GrupoPersonaAutorizadaInput> grupoPersonaAutorizadas) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

}
