package org.crue.hercules.sgi.eer.controller;

import org.crue.hercules.sgi.eer.converter.TipoDocumentoConverter;
import org.crue.hercules.sgi.eer.dto.TipoDocumentoOutput;
import org.crue.hercules.sgi.eer.model.TipoDocumento;
import org.crue.hercules.sgi.eer.service.TipoDocumentoService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TipoDocumentoController
 */
@RestController
@RequestMapping(TipoDocumentoController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class TipoDocumentoController {
  public static final String REQUEST_MAPPING = "/tiposdocumentos";
  public static final String PATH_ID = "/{id}";
  public static final String PATH_SUBTIPOS = PATH_ID + "/subtipos";

  // Services
  private final TipoDocumentoService service;
  // Converters
  private final TipoDocumentoConverter converter;

  /**
   * Devuelve una lista paginada y filtrada {@link TipoDocumento} activos.
   *
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link TipoDocumento} paginadas y
   *         filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthority('EER-EER-E', 'EER-EER-V')")
  public ResponseEntity<Page<TipoDocumentoOutput>> findTiposActivos(
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<TipoDocumentoOutput> page = converter.convert(service.findTiposActivos(query, paging));
    log.debug("findAll(String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoDocumento} activos y cuyo
   * padre es el {@link TipoDocumento} con id indicado.
   *
   * @param id     Identificador del {@link TipoDocumento} padre
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link TipoDocumento}.
   */
  @GetMapping(PATH_SUBTIPOS)
  @PreAuthorize("hasAnyAuthority('EER-EER-E', 'EER-EER-V')")
  public ResponseEntity<Page<TipoDocumentoOutput>> findSubtipos(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findSubtipos(Long id, String query, Pageable paging) - start");
    Page<TipoDocumentoOutput> page = converter.convert(service.findSubtiposActivos(id, query, paging));
    log.debug("findSubtipos(Long id, String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }
}
