package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.ProyectoResponsableEconomicoInput;
import org.crue.hercules.sgi.csp.dto.ProyectoResponsableEconomicoOutput;
import org.crue.hercules.sgi.csp.model.ProyectoResponsableEconomico;
import org.crue.hercules.sgi.csp.service.ProyectoResponsableEconomicoService;
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
 * ProyectoResponsableEconomicoController
 */
@RestController
@RequestMapping(ProyectoResponsableEconomicoController.MAPPING)
@Slf4j
public class ProyectoResponsableEconomicoController {
  public static final String MAPPING = "/proyectoresponsableseconomicos";

  private ModelMapper modelMapper;

  /** ProyectoResponsableEconomico service */
  private final ProyectoResponsableEconomicoService service;

  /**
   * Instancia un nuevo ProyectoResponsableEconomicoController.
   * 
   * @param modelMapper {@link ModelMapper}.
   * @param service     {@link ProyectoResponsableEconomicoService}
   */
  public ProyectoResponsableEconomicoController(ModelMapper modelMapper, ProyectoResponsableEconomicoService service) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Devuelve el {@link ProyectoResponsableEconomico} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoResponsableEconomico}.
   * @return {@link ProyectoResponsableEconomico} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ProyectoResponsableEconomicoOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProyectoResponsableEconomico returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  @PatchMapping("/{proyectoId}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<Page<ProyectoResponsableEconomicoOutput>> updateProyectoResponsablesEconomicos(
      @PathVariable Long proyectoId,
      @Valid @RequestBody List<ProyectoResponsableEconomicoInput> responsablesEconomicos) {
    log.debug(
        "updateProyectoResponsablesEconomicos(Long proyectoId, List<ProyectoResponsableEconomicoInput> responsablesEconomicos) - start");

    Page<ProyectoResponsableEconomicoOutput> returnValue = convert(new PageImpl<>(
        service.updateProyectoResponsableEconomicos(proyectoId, convert(proyectoId, responsablesEconomicos))));
    log.debug(
        "updateProyectoResponsablesEconomicos(Long proyectoId, List<ProyectoResponsableEconomicoInput> responsablesEconomicos) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  private ProyectoResponsableEconomicoOutput convert(ProyectoResponsableEconomico responsableEconomico) {
    return modelMapper.map(responsableEconomico, ProyectoResponsableEconomicoOutput.class);
  }

  private Page<ProyectoResponsableEconomicoOutput> convert(Page<ProyectoResponsableEconomico> page) {
    List<ProyectoResponsableEconomicoOutput> content = page.getContent().stream()
        .map((responsableEconomico) -> convert(responsableEconomico)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private ProyectoResponsableEconomico convert(Long proyectoId,
      ProyectoResponsableEconomicoInput responsableEconomicoInput) {
    ProyectoResponsableEconomico responsableEconomico = modelMapper.map(responsableEconomicoInput,
        ProyectoResponsableEconomico.class);
    responsableEconomico.setProyectoId(proyectoId);
    return responsableEconomico;
  }

  private List<ProyectoResponsableEconomico> convert(Long proyectoId, List<ProyectoResponsableEconomicoInput> list) {
    List<ProyectoResponsableEconomico> content = list.stream()
        .map((responsableEconomico) -> convert(proyectoId, responsableEconomico)).collect(Collectors.toList());

    return content;
  }
}