package org.crue.hercules.sgi.csp.controller;

import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.SolicitudHito;
import org.crue.hercules.sgi.csp.service.SolicitudHitoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.CollectionUtils;
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

  /**
   * Instancia un nuevo SolicitudHitoController.
   * 
   * @param solicitudHitoService {@link SolicitudHitoService}.
   */
  public SolicitudHitoController(SolicitudHitoService solicitudHitoService) {
    this.service = solicitudHitoService;
  }

  /**
   * Crea nuevo {@link SolicitudHito}
   * 
   * @param solicitudHito {@link SolicitudHito}. que se quiere crear.
   * @return Nuevo {@link SolicitudHito} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  public ResponseEntity<SolicitudHito> create(@Valid @RequestBody SolicitudHito solicitudHito) {
    log.debug("create(SolicitudHito solicitudHito) - start");
    SolicitudHito returnValue = service.create(solicitudHito);
    log.debug("create(SolicitudHito solicitudHito) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link SolicitudHito}.
   * 
   * @param solicitudHito  {@link SolicitudHito} a actualizar.
   * @param id             Identificador {@link SolicitudHito} a actualizar.
   * @param authentication Datos autenticación.
   * @return SolicitudHito {@link SolicitudHito} actualizado
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  public SolicitudHito update(@Valid @RequestBody SolicitudHito solicitudHito, @PathVariable Long id,
      Authentication authentication) {
    log.debug("update(SolicitudHito solicitudHito, Long id) - start");

    Boolean isAdministradorOrGestor = !CollectionUtils.isEmpty(
        authentication.getAuthorities().stream().filter(authority -> authority.getAuthority().startsWith("CSP-SOL-C"))
            .filter(Objects::nonNull).distinct().collect(Collectors.toList()));

    solicitudHito.setId(id);
    SolicitudHito returnValue = service.update(solicitudHito, isAdministradorOrGestor);
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
  SolicitudHito findById(@PathVariable Long id) {
    log.debug("SolicitudHito findById(Long id) - start");
    SolicitudHito returnValue = service.findById(id);
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
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

}