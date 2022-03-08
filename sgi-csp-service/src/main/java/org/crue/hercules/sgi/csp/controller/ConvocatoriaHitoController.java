package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.ConvocatoriaHitoInput;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaHitoOutput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHito;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHitoAviso;
import org.crue.hercules.sgi.csp.service.ConvocatoriaHitoService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ConvocatoriaHitoController
 */
@RestController
@RequestMapping("/convocatoriahitos")
@Slf4j
public class ConvocatoriaHitoController {

  /** ConvocatoriaHito service */
  private final ConvocatoriaHitoService service;
  /** Model mapper */
  private ModelMapper modelMapper;

  /**
   * Instancia un nuevo ConvocatoriaHitoController.
   * 
   * @param service     {@link ConvocatoriaHitoService}
   * @param modelMapper {@link ModelMapper}
   */
  public ConvocatoriaHitoController(ConvocatoriaHitoService service, ModelMapper modelMapper) {
    this.service = service;
    this.modelMapper = modelMapper;
  }

  /**
   * Devuelve el {@link ConvocatoriaHito} con el id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaHito}.
   * @return {@link ConvocatoriaHito} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public ConvocatoriaHitoOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ConvocatoriaHitoOutput returnValue = this.convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea un nuevo {@link ConvocatoriaHito}.
   * 
   * @param convocatoriaHito {@link ConvocatoriaHito} que se quiere crear.
   * @return Nuevo {@link ConvocatoriaHito} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C', 'CSP-CON-E')")
  public ResponseEntity<ConvocatoriaHitoOutput> create(@Valid @RequestBody ConvocatoriaHitoInput convocatoriaHito) {
    log.debug("create(ConvocatoriaHito convocatoriaHito) - start");
    ConvocatoriaHitoOutput returnValue = convert(service.create(convocatoriaHito));
    log.debug("create(ConvocatoriaHito convocatoriaHito) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ConvocatoriaHito} con el id indicado.
   * 
   * @param convocatoriaHito {@link ConvocatoriaHito} a actualizar.
   * @param id               id {@link ConvocatoriaHito} a actualizar.
   * @return {@link ConvocatoriaHito} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public ConvocatoriaHitoOutput update(
      @PathVariable Long id,
      @Valid @RequestBody ConvocatoriaHitoInput convocatoriaHito) {
    log.debug("update(ConvocatoriaHito convocatoriaHito, Long id) - start");

    ConvocatoriaHitoOutput returnValue = convert(service.update(id, convocatoriaHito));
    log.debug("update(ConvocatoriaHito convocatoriaHito, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ConvocatoriaHito} con id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaHito}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Retorna el listado de destinatarios para incluir en el envio de email de una
   * {@link ConvocatoriaHito}, en base a los flags de
   * {@link ConvocatoriaHitoAviso}
   * 
   * @param id identificador de {@link ConvocatoriaHito}
   * @return Listado de {@link Recipient}
   */
  @GetMapping("/{id}/deferrable-recipients")
  @PreAuthorize("isClient() and hasAuthority('SCOPE_sgi-csp')")
  public List<Recipient> resolveDeferrableRecipients(@PathVariable Long id) {
    log.debug("resolveDeferrableRecipients(Long id) - start");
    List<Recipient> recipients = service.getDeferredRecipients(id);
    log.debug("resolveDeferrableRecipients(Long id) - end");
    return recipients;
  }

  private ConvocatoriaHitoOutput convert(ConvocatoriaHito convocatoriaHito) {
    return modelMapper.map(convocatoriaHito, ConvocatoriaHitoOutput.class);
  }
}
