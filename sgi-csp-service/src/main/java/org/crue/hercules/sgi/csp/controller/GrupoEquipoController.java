package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.GrupoEquipoConverter;
import org.crue.hercules.sgi.csp.dto.GrupoEquipoInput;
import org.crue.hercules.sgi.csp.dto.GrupoEquipoOutput;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.service.GrupoEquipoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
 * GrupoEquipoController
 */
@RestController
@RequestMapping(GrupoEquipoController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class GrupoEquipoController {

  public static final String REQUEST_MAPPING = "/gruposequipos";
  public static final String PATH_ID = "/{id}";

  private final GrupoEquipoService service;
  private final GrupoEquipoConverter converter;

  /**
   * Crea nuevo {@link GrupoEquipo}
   * 
   * @param grupoEquipo {@link GrupoEquipo} que se quiere crear.
   * @return Nuevo {@link GrupoEquipo} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-C', 'CSP-GIN-E')")
  public ResponseEntity<GrupoEquipoOutput> create(@Valid @RequestBody GrupoEquipoInput grupoEquipo) {
    log.debug("create(GrupoEquipoInput grupoEquipo) - start");
    GrupoEquipoOutput returnValue = converter.convert(service.create(converter.convert(grupoEquipo)));
    log.debug("create(GrupoEquipoInput grupoEquipo) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link GrupoEquipo}.
   * 
   * @param grupo {@link GrupoEquipo} a actualizar.
   * @param id    Identificador {@link GrupoEquipo} a actualizar.
   * @return {@link GrupoEquipo} actualizado
   */
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-GIN-E')")
  public GrupoEquipoOutput update(@Valid @RequestBody GrupoEquipoInput grupo, @PathVariable Long id) {
    log.debug("update(GrupoEquipoInput grupoEquipo, Long id) - start");
    GrupoEquipoOutput returnValue = converter.convert(service.update(converter.convert(id, grupo)));
    log.debug("update(GrupoEquipoInput grupoEquipo, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link GrupoEquipo} con el id indicado.
   * 
   * @param id Identificador de {@link GrupoEquipo}.
   * @return {@link GrupoEquipo} correspondiente al id
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V')")
  public GrupoEquipoOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    GrupoEquipoOutput returnValue = converter.convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

}
