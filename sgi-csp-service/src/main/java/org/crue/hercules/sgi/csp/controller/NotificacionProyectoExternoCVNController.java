package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.dto.NotificacionProyectoExternoCVNOutput;
import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN;
import org.crue.hercules.sgi.csp.service.NotificacionProyectoExternoCVNService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * NotificacionProyectoExternoCVNController
 */
@RestController
@RequestMapping(NotificacionProyectoExternoCVNController.MAPPING)
@Slf4j
public class NotificacionProyectoExternoCVNController {
  public static final String MAPPING = "/notificacionesproyectosexternoscvn";
  private ModelMapper modelMapper;

  private final NotificacionProyectoExternoCVNService service;

  public NotificacionProyectoExternoCVNController(
      NotificacionProyectoExternoCVNService notificacionProyectoExternoCVNService,
      ModelMapper modelMapper) {
    this.service = notificacionProyectoExternoCVNService;
    this.modelMapper = modelMapper;
  }

  /**
   * Devuelve una lista paginada y/o filtrada
   * {@link NotificacionProyectoExternoCVN}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return la lista de entidades {@link NotificacionProyectoExternoCVN}
   *         paginadas y/o filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CVPR-V', 'CSP-CVPR-E')")
  public ResponseEntity<Page<NotificacionProyectoExternoCVNOutput>> findAll(
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<NotificacionProyectoExternoCVN> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  private Page<NotificacionProyectoExternoCVNOutput> convert(Page<NotificacionProyectoExternoCVN> page) {
    List<NotificacionProyectoExternoCVNOutput> content = page.getContent().stream()
        .map((notificacionProyectoExternoCVN) -> convert(notificacionProyectoExternoCVN))
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private NotificacionProyectoExternoCVNOutput convert(NotificacionProyectoExternoCVN notificacionProyectoExternoCVN) {
    return modelMapper.map(notificacionProyectoExternoCVN, NotificacionProyectoExternoCVNOutput.class);
  }
}
