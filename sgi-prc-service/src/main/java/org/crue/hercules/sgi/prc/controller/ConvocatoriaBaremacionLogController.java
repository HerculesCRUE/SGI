package org.crue.hercules.sgi.prc.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.prc.dto.ConvocatoriaBaremacionLogOutput;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacionLog;
import org.crue.hercules.sgi.prc.service.ConvocatoriaBaremacionLogService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ConvocatoriaBaremacionLogController
 */
@RestController
@RequestMapping(ConvocatoriaBaremacionLogController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class ConvocatoriaBaremacionLogController {

  public static final String REQUEST_MAPPING = "/convocatoriabaremacionlog";

  private final ConvocatoriaBaremacionLogService convocatoriaBaremacionLogService;

  private final ModelMapper modelMapper;

  /**
   * Devuelve una lista paginada y filtrada {@link ConvocatoriaBaremacionLog}.
   *
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link ConvocatoriaBaremacionLog} paginadas y
   *         filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAuthority('PRC-CON-BAR')")
  public ResponseEntity<Page<ConvocatoriaBaremacionLogOutput>> findAll(
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<ConvocatoriaBaremacionLogOutput> page = convert(convocatoriaBaremacionLogService.findAll(query, paging));
    log.debug("findAll(String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Elimina el {@link ConvocatoriaBaremacionLog} con convocatoriaBaremacionId
   * indicado.
   *
   * @param convocatoriaBaremacionId convocatoriaBaremacionId de
   *                                 {@link ConvocatoriaBaremacionLog}.
   */
  @DeleteMapping("/{convocatoriaBaremacionId}")
  @PreAuthorize("isAuthenticated()")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteByConvocatoriaBaremacionId(@PathVariable Long convocatoriaBaremacionId) {
    log.debug("deleteByProduccionCientificaRef(String convocatoriaBaremacionId) -  start");

    convocatoriaBaremacionLogService.deleteByConvocatoriaBaremacionId(convocatoriaBaremacionId);

    log.debug("deleteByConvocatoriaBaremacionId(String convocatoriaBaremacionId) -  end");
  }

  private ConvocatoriaBaremacionLogOutput convert(ConvocatoriaBaremacionLog responsableEconomico) {
    return modelMapper.map(responsableEconomico, ConvocatoriaBaremacionLogOutput.class);
  }

  private Page<ConvocatoriaBaremacionLogOutput> convert(Page<ConvocatoriaBaremacionLog> page) {
    List<ConvocatoriaBaremacionLogOutput> content = page.getContent().stream()
        .map(this::convert).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

}
