package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.GrupoLineaEquipoInstrumentalConverter;
import org.crue.hercules.sgi.csp.dto.GrupoLineaEquipoInstrumentalInput;
import org.crue.hercules.sgi.csp.dto.GrupoLineaEquipoInstrumentalOutput;
import org.crue.hercules.sgi.csp.model.GrupoLineaEquipoInstrumental;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion;
import org.crue.hercules.sgi.csp.service.GrupoLineaEquipoInstrumentalService;
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
 * GrupoLineaEquipoInstrumentalController
 */
@RestController
@RequestMapping(GrupoLineaEquipoInstrumentalController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
@Validated
public class GrupoLineaEquipoInstrumentalController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "gruposlineasequiposinstrumentales";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final GrupoLineaEquipoInstrumentalService service;
  private final GrupoLineaEquipoInstrumentalConverter converter;

  /**
   * Devuelve el {@link GrupoLineaEquipoInstrumental} con el id indicado.
   * 
   * @param id Identificador de {@link GrupoLineaEquipoInstrumental}.
   * @return {@link GrupoLineaEquipoInstrumental} correspondiente al id
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V')")
  public GrupoLineaEquipoInstrumentalOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    GrupoLineaEquipoInstrumentalOutput returnValue = converter.convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link GrupoLineaEquipoInstrumental} del
   * {@link GrupoLineaInvestigacion}
   * con el
   * listado grupoLineaEquipoInstrumentals añadiendo, editando o eliminando los
   * elementos segun
   * proceda.
   * 
   * @param id                               Id del
   *                                         {@link GrupoLineaInvestigacion}.
   * @param grupoLineasEquiposInstrumentales lista con los nuevos
   *                                         {@link GrupoLineaEquipoInstrumental}
   *                                         a guardar.
   * @return Lista actualizada con los {@link GrupoLineaEquipoInstrumental}.
   */
  @PatchMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V')")
  public ResponseEntity<List<GrupoLineaEquipoInstrumentalOutput>> update(@PathVariable Long id,
      @RequestBody List<@Valid GrupoLineaEquipoInstrumentalInput> grupoLineasEquiposInstrumentales) {
    log.debug("update(Long id, List<GrupoLineaEquipoInstrumentalInput> grupoLineasEquiposInstrumentales) - start");
    List<GrupoLineaEquipoInstrumentalOutput> returnValue = converter
        .convertGrupoLineaEquipoInstrumentals(service.update(id, converter.convertGrupoLineaEquipoInstrumentalInput(
            grupoLineasEquiposInstrumentales)));
    log.debug("update(Long id, List<GrupoLineaEquipoInstrumentalInput> grupoLineasEquiposInstrumentales) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

}
