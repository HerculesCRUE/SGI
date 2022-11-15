package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.GastoRequerimientoJustificacionConverter;
import org.crue.hercules.sgi.csp.dto.GastoRequerimientoJustificacionInput;
import org.crue.hercules.sgi.csp.dto.GastoRequerimientoJustificacionOutput;
import org.crue.hercules.sgi.csp.model.GastoRequerimientoJustificacion;
import org.crue.hercules.sgi.csp.service.GastoRequerimientoJustificacionService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * GastoRequerimientoJustificacionController
 */
@RestController
@RequestMapping(GastoRequerimientoJustificacionController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class GastoRequerimientoJustificacionController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "gastos-requerimientos-justificacion";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final GastoRequerimientoJustificacionService service;
  private final GastoRequerimientoJustificacionConverter converter;

  /**
   * Crea una entidad {@link GastoRequerimientoJustificacion}.
   * 
   * @param gastoRequerimientoJustificacion datos de la entidad
   *                                        {@link GastoRequerimientoJustificacion}
   *                                        a crear.
   * @return la entidad {@link GastoRequerimientoJustificacion} creada.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SJUS-E')")
  public ResponseEntity<GastoRequerimientoJustificacionOutput> create(
      @Valid @RequestBody GastoRequerimientoJustificacionInput gastoRequerimientoJustificacion) {
    log.debug("create(GastoRequerimientoJustificacionInput gastoRequerimientoJustificacion) - start");
    GastoRequerimientoJustificacionOutput returnValue = converter
        .convert(service.create(converter.convert(gastoRequerimientoJustificacion)));

    log.debug("create(GastoRequerimientoJustificacionInput gastoRequerimientoJustificacion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza la entidad {@link GastoRequerimientoJustificacion} con id indicado.
   * 
   * @param id                              identificador de la entidad
   *                                        {@link GastoRequerimientoJustificacion}.
   * @param gastoRequerimientoJustificacion datos de la entidad
   *                                        {@link GastoRequerimientoJustificacion}
   *                                        a actualizar.
   * @return la entidad {@link GastoRequerimientoJustificacion} actualizada.
   */
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SJUS-E')")
  public GastoRequerimientoJustificacionOutput update(@PathVariable Long id,
      @Valid @RequestBody GastoRequerimientoJustificacionInput gastoRequerimientoJustificacion) {
    log.debug("update(Long id, GastoRequerimientoJustificacionInput gastoRequerimientoJustificacion) - start");
    GastoRequerimientoJustificacionOutput returnValue = converter
        .convert(service.update(converter.convert(gastoRequerimientoJustificacion, id)));

    log.debug("update(Long id, GastoRequerimientoJustificacionInput gastoRequerimientoJustificacion) - end");
    return returnValue;
  }

  /**
   * Elimina el {@link GastoRequerimientoJustificacion} con el id indicado.
   * 
   * @param id Identificador de {@link GastoRequerimientoJustificacion}.
   */
  @DeleteMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SJUS-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.deleteById(id);
    log.debug("deleteById(Long id) - end");
  }

  @GetMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SJUS-E', 'CSP-SJUS-V')")
  public ResponseEntity<Page<GastoRequerimientoJustificacionOutput>> findAll(
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {

    Page<GastoRequerimientoJustificacion> page = service.findAll(query, paging);

    return !page.isEmpty() ? ResponseEntity.ok().body(converter.convert(page))
        : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
