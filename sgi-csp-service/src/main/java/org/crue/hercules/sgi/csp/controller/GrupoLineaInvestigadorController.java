package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.GrupoLineaInvestigadorConverter;
import org.crue.hercules.sgi.csp.dto.GrupoLineaInvestigadorInput;
import org.crue.hercules.sgi.csp.dto.GrupoLineaInvestigadorOutput;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigador;
import org.crue.hercules.sgi.csp.service.GrupoLineaInvestigadorService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * GrupoLineaInvestigadorController
 */
@RestController
@RequestMapping(GrupoLineaInvestigadorController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
@Validated
public class GrupoLineaInvestigadorController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "gruposlineasinvestigadores";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final GrupoLineaInvestigadorService service;
  private final GrupoLineaInvestigadorConverter converter;

  /**
   * Devuelve el {@link GrupoLineaInvestigador} con el id indicado.
   * 
   * @param id Identificador de {@link GrupoLineaInvestigador}.
   * @return {@link GrupoLineaInvestigador} correspondiente al id
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V')")
  public GrupoLineaInvestigadorOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    GrupoLineaInvestigadorOutput returnValue = converter.convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link GrupoLineaInvestigador} del
   * {@link GrupoLineaInvestigacion}
   * con el
   * listado grupoLineaInvestigadors añadiendo, editando o eliminando los
   * elementos segun
   * proceda.
   * 
   * @param id                        Id del {@link GrupoLineaInvestigacion}.
   * @param grupoLineasInvestigadores lista con los nuevos
   *                                  {@link GrupoLineaInvestigador} a
   *                                  guardar.
   * @return Lista actualizada con los {@link GrupoLineaInvestigador}.
   */
  @PatchMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V')")
  public ResponseEntity<List<GrupoLineaInvestigadorOutput>> update(@PathVariable Long id,
      @RequestBody List<@Valid GrupoLineaInvestigadorInput> grupoLineasInvestigadores) {
    log.debug("update(Long id, List<GrupoLineaInvestigadorInput> grupoLineasInvestigadores) - start");
    List<GrupoLineaInvestigadorOutput> returnValue = converter
        .convertGrupoLineaInvestigadors(service.update(id, converter.convertGrupoLineaInvestigadorInput(
            grupoLineasInvestigadores)));
    log.debug("update(Long id, List<GrupoLineaInvestigadorInput> grupoLineasInvestigadores) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link GrupoEquipo}.
   *
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link GrupoEquipo} paginadas y
   *         filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V')")
  public ResponseEntity<Page<GrupoLineaInvestigadorOutput>> findAll(
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<GrupoLineaInvestigadorOutput> page = converter.convert(service.findAll(query, paging));
    log.debug("findAll(String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

}
