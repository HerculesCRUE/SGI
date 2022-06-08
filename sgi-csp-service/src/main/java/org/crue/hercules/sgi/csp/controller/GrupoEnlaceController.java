package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.GrupoEnlaceConverter;
import org.crue.hercules.sgi.csp.dto.GrupoEnlaceInput;
import org.crue.hercules.sgi.csp.dto.GrupoEnlaceOutput;
import org.crue.hercules.sgi.csp.model.GrupoEnlace;
import org.crue.hercules.sgi.csp.service.GrupoEnlaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * GrupoEnlaceController
 */
@RestController
@RequestMapping(GrupoEnlaceController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class GrupoEnlaceController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "grupoenlaces";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final GrupoEnlaceService service;
  private final GrupoEnlaceConverter converter;

  /**
   * Crea nuevo {@link GrupoEnlace}
   * 
   * @param grupoEnlace {@link GrupoEnlace} que se quiere crear.
   * @return Nuevo {@link GrupoEnlace} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-C', 'CSP-GIN-E')")
  public ResponseEntity<GrupoEnlaceOutput> create(
      @Valid @RequestBody GrupoEnlaceInput grupoEnlace) {
    log.debug("create(GrupoEnlaceInput grupoEnlace) - start");
    GrupoEnlaceOutput returnValue = converter.convert(service.create(converter.convert(grupoEnlace)));
    log.debug("create(GrupoEnlaceInput grupoEnlace) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link GrupoEnlace}.
   * 
   * @param grupoEnlace {@link GrupoEnlace} a actualizar.
   * @param id          Identificador {@link GrupoEnlace} a actualizar.
   * @return {@link GrupoEnlace} actualizado
   */
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-GIN-E')")
  public GrupoEnlaceOutput update(@Valid @RequestBody GrupoEnlaceInput grupoEnlace,
      @PathVariable Long id) {
    log.debug("update(GrupoEnlaceInput grupoEnlace, Long id) - start");
    GrupoEnlaceOutput returnValue = converter.convert(service.update(converter.convert(id, grupoEnlace)));
    log.debug("update(GrupoEnlaceInput grupoEnlace, Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link GrupoEnlace} con id indicado.
   * 
   * @param id Identificador de {@link GrupoEnlace}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-GIN-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve el {@link GrupoEnlace} con el id indicado.
   * 
   * @param id Identificador de {@link GrupoEnlace}.
   * @return {@link GrupoEnlace} correspondiente al id
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V')")
  public GrupoEnlaceOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    GrupoEnlaceOutput returnValue = converter.convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

}
