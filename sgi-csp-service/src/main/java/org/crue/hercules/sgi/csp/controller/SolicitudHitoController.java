package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.SolicitudHitoInput;
import org.crue.hercules.sgi.csp.dto.SolicitudHitoOutput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.model.SolicitudHito;
import org.crue.hercules.sgi.csp.model.SolicitudHitoAviso;
import org.crue.hercules.sgi.csp.service.SolicitudHitoService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
 * SolicitudHitoController
 */
@RestController
@RequestMapping("/solicitudhitos")
@Slf4j
public class SolicitudHitoController {

  /** SolicitudHitoService service */
  private final SolicitudHitoService service;
  /** Model mapper */
  private ModelMapper modelMapper;

  /**
   * Instancia un nuevo SolicitudHitoController.
   * 
   * @param solicitudHitoService {@link SolicitudHitoService}.
   * @param modelMapper          {@link ModelMapper}
   */
  public SolicitudHitoController(SolicitudHitoService solicitudHitoService, ModelMapper modelMapper) {
    this.service = solicitudHitoService;
    this.modelMapper = modelMapper;
  }

  /**
   * Crea nuevo {@link SolicitudHito}
   * 
   * @param solicitudHito {@link SolicitudHitoInput}. que se quiere crear.
   * @return Nuevo {@link SolicitudHito} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  public ResponseEntity<SolicitudHitoOutput> create(@Valid @RequestBody SolicitudHitoInput solicitudHito) {
    log.debug("create(SolicitudHito solicitudHito) - start");
    SolicitudHitoOutput returnValue = this.convert(service.create(solicitudHito));
    log.debug("create(SolicitudHito solicitudHito) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link SolicitudHito}.
   * 
   * @param solicitudHito  {@link SolicitudHitoInput} a actualizar.
   * @param id             Identificador {@link SolicitudHito} a actualizar.
   * @param authentication Datos autenticación.
   * @return SolicitudHito {@link SolicitudHito} actualizado
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  public SolicitudHitoOutput update(@Valid @RequestBody SolicitudHitoInput solicitudHito, @PathVariable Long id,
      Authentication authentication) {
    log.debug("update(SolicitudHito solicitudHito, Long id) - start");

    SolicitudHitoOutput returnValue = this.convert(service.update(id, solicitudHito));
    log.debug("update(SolicitudHito solicitudHito, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link SolicitudHito} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitudHito}.
   * @return SolicitudHito {@link SolicitudHito} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public SolicitudHitoOutput findById(@PathVariable Long id) {
    log.debug("SolicitudHito findById(Long id) - start");
    SolicitudHitoOutput returnValue = this.convert(service.findById(id));
    log.debug("SolicitudHito findById(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link SolicitudHito} con id indicado.
   * 
   * @param id Identificador de {@link SolicitudHito}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Retorna el listado de destinatarios para incluir en el envio de email de una
   * {@link SolicitudHito}, en base a los flags de
   * {@link SolicitudHitoAviso}
   * 
   * @param id identificador de {@link SolicitudHito}
   * @return Listado de {@link Recipient}
   */
  @GetMapping("/{id}/deferrable-recipients")
  // @PreAuthorize("isClient() and hasAuthority('SCOPE_sgi-csp')")
  public List<Recipient> resolveDeferrableRecipients(@PathVariable Long id) {
    log.debug("resolveDeferrableRecipients(Long id) - start");
    List<Recipient> recipients = service.getDeferredRecipients(id);
    log.debug("resolveDeferrableRecipients(Long id) - end");
    return recipients;
  }

  private SolicitudHitoOutput convert(SolicitudHito solicitudHito) {
    return modelMapper.map(solicitudHito, SolicitudHitoOutput.class);
  }
}