package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.SolicitudProyectoResponsableEconomicoInput;
import org.crue.hercules.sgi.csp.dto.SolicitudProyectoResponsableEconomicoOutput;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoResponsableEconomico;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoResponsableEconomicoService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * SolicitudProyectoResponsableEconomicoController
 */
@RestController
@RequestMapping(SolicitudProyectoResponsableEconomicoController.MAPPING)
@Slf4j
public class SolicitudProyectoResponsableEconomicoController {
  public static final String MAPPING = "/solicitudproyectoresponsableseconomicos";

  private ModelMapper modelMapper;

  /** SolicitudProyectoResponsableEconomico service */
  private final SolicitudProyectoResponsableEconomicoService service;

  /**
   * Instancia un nuevo SolicitudProyectoResponsableEconomicoController.
   * 
   * @param modelMapper {@link ModelMapper}.
   * @param service     {@link SolicitudProyectoResponsableEconomicoService}
   */
  public SolicitudProyectoResponsableEconomicoController(ModelMapper modelMapper,
      SolicitudProyectoResponsableEconomicoService service) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Devuelve el {@link SolicitudProyectoResponsableEconomico} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoResponsableEconomico}.
   * @return {@link SolicitudProyectoResponsableEconomico} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-INV-ER')")
  public SolicitudProyectoResponsableEconomicoOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    SolicitudProyectoResponsableEconomico returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  @PatchMapping("/{solicitudProyectoId}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-INV-ER')")
  public ResponseEntity<Page<SolicitudProyectoResponsableEconomicoOutput>> updateSolicituProyectoResponsablesEconomicos(
      @PathVariable Long solicitudProyectoId,
      @Valid @RequestBody List<SolicitudProyectoResponsableEconomicoInput> responsablesEconomicos) {
    log.debug(
        "updateSolicituProyectoResponsablesEconomicos(Long solicitudId, List<SolicitudProyectoResponsableEconomicoInput> responsablesEconomicos) - start");

    Page<SolicitudProyectoResponsableEconomicoOutput> returnValue = convert(
        new PageImpl<>(service.updateSolicitudProyectoResponsableEconomicos(solicitudProyectoId,
            convert(solicitudProyectoId, responsablesEconomicos))));
    log.debug(
        "updateSolicituProyectoResponsablesEconomicos(Long solicitudId, List<SolicitudProyectoResponsableEconomicoInput> responsablesEconomicos) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  private SolicitudProyectoResponsableEconomicoOutput convert(
      SolicitudProyectoResponsableEconomico responsableEconomico) {
    return modelMapper.map(responsableEconomico, SolicitudProyectoResponsableEconomicoOutput.class);
  }

  private Page<SolicitudProyectoResponsableEconomicoOutput> convert(Page<SolicitudProyectoResponsableEconomico> page) {
    List<SolicitudProyectoResponsableEconomicoOutput> content = page.getContent().stream()
        .map(responsableEconomico -> convert(responsableEconomico)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private SolicitudProyectoResponsableEconomico convert(Long solicitudProyectoId,
      SolicitudProyectoResponsableEconomicoInput responsableEconomicoInput) {
    SolicitudProyectoResponsableEconomico responsableEconomico = modelMapper.map(responsableEconomicoInput,
        SolicitudProyectoResponsableEconomico.class);
    responsableEconomico.setSolicitudProyectoId(solicitudProyectoId);
    return responsableEconomico;
  }

  private List<SolicitudProyectoResponsableEconomico> convert(Long solicitudProyectoId,
      List<SolicitudProyectoResponsableEconomicoInput> list) {
    return list.stream().map(responsableEconomico -> convert(solicitudProyectoId, responsableEconomico))
        .collect(Collectors.toList());
  }
}