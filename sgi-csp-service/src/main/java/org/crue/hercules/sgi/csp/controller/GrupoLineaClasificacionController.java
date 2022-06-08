package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.GrupoLineaClasificacionConverter;
import org.crue.hercules.sgi.csp.dto.GrupoLineaClasificacionInput;
import org.crue.hercules.sgi.csp.dto.GrupoLineaClasificacionOutput;
import org.crue.hercules.sgi.csp.model.GrupoLineaClasificacion;
import org.crue.hercules.sgi.csp.service.GrupoLineaClasificacionService;
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
 * GrupoLineaClasificacionController
 */
@RestController
@RequestMapping(GrupoLineaClasificacionController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class GrupoLineaClasificacionController {

  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "gruposlineasclasificaciones";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final GrupoLineaClasificacionService service;
  private final GrupoLineaClasificacionConverter converter;

  /**
   * Crea nuevo {@link GrupoLineaClasificacion}.
   * 
   * @param grupoLineaClasificacion {@link GrupoLineaClasificacion} que se quiere
   *                                crear.
   * @return Nuevo {@link GrupoLineaClasificacion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-GIN-E')")
  public ResponseEntity<GrupoLineaClasificacionOutput> create(
      @Valid @RequestBody GrupoLineaClasificacionInput grupoLineaClasificacion) {
    log.debug("create(GrupoLineaClasificacionInput grupoLineaClasificacion) - start");
    GrupoLineaClasificacionOutput returnValue = converter
        .convert(service.create(converter.convert(grupoLineaClasificacion)));
    log.debug("create(GrupoLineaClasificacionInput grupoLineaClasificacion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Elimina el {@link GrupoLineaClasificacion} con el id indicado.
   * 
   * @param id Identificador de {@link GrupoLineaClasificacion}.
   */
  @DeleteMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

}
