package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.GrupoEquipoInstrumentalConverter;
import org.crue.hercules.sgi.csp.dto.GrupoEquipoInstrumentalInput;
import org.crue.hercules.sgi.csp.dto.GrupoEquipoInstrumentalOutput;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEquipoInstrumental;
import org.crue.hercules.sgi.csp.model.GrupoLineaEquipoInstrumental;
import org.crue.hercules.sgi.csp.service.GrupoEquipoInstrumentalService;
import org.crue.hercules.sgi.csp.service.GrupoLineaEquipoInstrumentalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * GrupoEquipoInstrumentalController
 */
@RestController
@RequestMapping(GrupoEquipoInstrumentalController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
@Validated
public class GrupoEquipoInstrumentalController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "gruposequiposinstrumentales";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";
  public static final String PATH_GRUPO_LINEA_EQUIPO_INSTRUMENTAL = PATH_ID + PATH_DELIMITER
      + "gruposlineasequiposinstrumentales";

  // Services
  private final GrupoEquipoInstrumentalService service;
  private final GrupoLineaEquipoInstrumentalService grupoLineaEquipoInstrumentalService;

  // Converters
  private final GrupoEquipoInstrumentalConverter converter;

  /**
   * Crea nuevo {@link GrupoEquipoInstrumental}
   * 
   * @param grupoEquipo {@link GrupoEquipoInstrumental} que se quiere crear.
   * @return Nuevo {@link GrupoEquipoInstrumental} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-C', 'CSP-GIN-E')")
  public ResponseEntity<GrupoEquipoInstrumentalOutput> create(
      @Valid @RequestBody GrupoEquipoInstrumentalInput grupoEquipo) {
    log.debug("create(GrupoEquipoInstrumentalInput grupoEquipo) - start");
    GrupoEquipoInstrumentalOutput returnValue = converter.convert(service.create(converter.convert(grupoEquipo)));
    log.debug("create(GrupoEquipoInstrumentalInput grupoEquipo) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link GrupoEquipoInstrumental}.
   * 
   * @param grupo {@link GrupoEquipoInstrumental} a actualizar.
   * @param id    Identificador {@link GrupoEquipoInstrumental} a actualizar.
   * @return {@link GrupoEquipoInstrumental} actualizado
   */
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-GIN-E')")
  public GrupoEquipoInstrumentalOutput update(@Valid @RequestBody GrupoEquipoInstrumentalInput grupo,
      @PathVariable Long id) {
    log.debug("update(GrupoEquipoInstrumentalInput grupoEquipo, Long id) - start");
    GrupoEquipoInstrumentalOutput returnValue = converter.convert(service.update(converter.convert(id, grupo)));
    log.debug("update(GrupoEquipoInstrumentalInput grupoEquipo, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link GrupoEquipoInstrumental} con el id indicado.
   * 
   * @param id Identificador de {@link GrupoEquipoInstrumental}.
   * @return {@link GrupoEquipoInstrumental} correspondiente al id
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V', 'CSP-GIN-INV-VR')")
  public GrupoEquipoInstrumentalOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    GrupoEquipoInstrumentalOutput returnValue = converter.convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link GrupoEquipoInstrumental} del {@link Grupo} con
   * el listado grupoEquiposInstrumentales añadiendo, editando o eliminando los
   * elementos segun proceda.
   * 
   * @param id                         Id del {@link Grupo}.
   * @param grupoEquiposInstrumentales lista con los nuevos
   *                                   {@link GrupoEquipoInstrumental} a
   *                                   guardar.
   * @return Lista actualizada con los {@link GrupoEquipoInstrumental}.
   */
  @PatchMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V')")
  public ResponseEntity<List<GrupoEquipoInstrumentalOutput>> update(@PathVariable Long id,
      @RequestBody List<@Valid GrupoEquipoInstrumentalInput> grupoEquiposInstrumentales) {
    log.debug("update(List<GrupoEquipoInstrumentalInput> grupoEquiposInstrumentales, grupoId) - start");
    List<GrupoEquipoInstrumentalOutput> returnValue = converter
        .convertGrupoEquipoInstrumentals(service.update(id, converter.convertGrupoEquipoInstrumentalInput(
            grupoEquiposInstrumentales)));
    log.debug("update(List<GrupoEquipoInstrumentalInput> grupoEquiposInstrumentales, grupoId) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Comprueba la existencia de un Grupo Equipo Instrumental presente en el
   * {@link GrupoLineaEquipoInstrumental} con el id
   *
   * @param id Identificador de Grupo Equipo Instrumental
   * @return {@link HttpStatus#OK} si existe y {@link HttpStatus#NO_CONTENT} si
   *         no.
   */
  @RequestMapping(path = PATH_GRUPO_LINEA_EQUIPO_INSTRUMENTAL, method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V')")
  public ResponseEntity<Void> existsGrupoEquipoInstrumentalInGrupoLineaEquipoInstrumental(@PathVariable Long id) {
    log.debug("GrupoEquipo existsGrupoEquipoInstrumentalInGrupoLineaEquipoInstrumental(Long id) - start");
    boolean exists = grupoLineaEquipoInstrumentalService
        .existsGrupoLineaEquipoInstrumentalInGrupoEquipoInstrumental(id);
    log.debug("GrupoEquipo existsGrupoEquipoInstrumentalInGrupoLineaEquipoInstrumental(Long id) - end");
    return exists ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
