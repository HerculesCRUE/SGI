package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.GrupoResponsableEconomicoConverter;
import org.crue.hercules.sgi.csp.dto.GrupoResponsableEconomicoInput;
import org.crue.hercules.sgi.csp.dto.GrupoResponsableEconomicoOutput;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoResponsableEconomico;
import org.crue.hercules.sgi.csp.service.GrupoResponsableEconomicoService;
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
 * GrupoResponsableEconomicoController
 */
@RestController
@RequestMapping(GrupoResponsableEconomicoController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
@Validated
public class GrupoResponsableEconomicoController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "gruposresponsableseconomicos";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final GrupoResponsableEconomicoService service;
  private final GrupoResponsableEconomicoConverter converter;

  /**
   * Devuelve el {@link GrupoResponsableEconomico} con el id indicado.
   * 
   * @param id Identificador de {@link GrupoResponsableEconomico}.
   * @return {@link GrupoResponsableEconomico} correspondiente al id
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V')")
  public GrupoResponsableEconomicoOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    GrupoResponsableEconomicoOutput returnValue = converter.convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link GrupoResponsableEconomico} del {@link Grupo}
   * con el
   * listado grupoResponsableEconomicos añadiendo, editando o eliminando los
   * elementos segun
   * proceda.
   * 
   * @param id                         Id del {@link Grupo}.
   * @param grupoResponsableEconomicos lista con los nuevos
   *                                   {@link GrupoResponsableEconomico} a
   *                                   guardar.
   * @return Lista actualizada con los {@link GrupoResponsableEconomico}.
   */
  @PatchMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V')")
  public ResponseEntity<List<GrupoResponsableEconomicoOutput>> update(@PathVariable Long id,
      @RequestBody List<@Valid GrupoResponsableEconomicoInput> grupoResponsableEconomicos) {
    log.debug("update(Long id, List<GrupoResponsableEconomicoInput> grupoResponsableEconomicos) - start");
    List<GrupoResponsableEconomicoOutput> returnValue = converter
        .convertGrupoResponsableEconomicos(service.update(id, converter.convertGrupoResponsableEconomicoInput(
            grupoResponsableEconomicos)));
    log.debug("update(Long id, List<GrupoResponsableEconomicoInput> grupoResponsableEconomicos) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

}
