package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.converter.TipoRequerimientoConverter;
import org.crue.hercules.sgi.csp.dto.TipoRequerimientoOutput;
import org.crue.hercules.sgi.csp.model.TipoRequerimiento;
import org.crue.hercules.sgi.csp.service.TipoRequerimientoService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TipoRequerimientoController
 */
@RestController
@RequestMapping(TipoRequerimientoController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class TipoRequerimientoController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "tiposrequerimientos";

  private final TipoRequerimientoService service;
  private final TipoRequerimientoConverter converter;

  /**
   * Devuelve una lista paginada y filtrada {@link TipoRequerimiento} activos.
   *
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link TipoRequerimiento} paginadas y
   *         filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SJUS-V', 'CSP-SJUS-E')")
  public ResponseEntity<Page<TipoRequerimientoOutput>> findActivos(
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findActivos(String query, Pageable paging) - start");
    Page<TipoRequerimientoOutput> page = converter.convert(service.findActivos(query, paging));
    log.debug("findActivos(String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }
}
