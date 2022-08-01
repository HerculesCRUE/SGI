package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.dto.ProyectoHitoInput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.ProyectoHito;
import org.crue.hercules.sgi.csp.model.ProyectoHitoAviso;
import org.crue.hercules.sgi.csp.service.ProyectoHitoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
 * ProyectoHitoController
 */
@RestController
@RequestMapping("/proyectohitos")
@Slf4j
public class ProyectoHitoController {

  /** ProyectoHito service */
  private final ProyectoHitoService service;

  /**
   * Instancia un nuevo ProyectoHitoController.
   * 
   * @param service {@link ProyectoHitoService}
   */
  public ProyectoHitoController(ProyectoHitoService service) {
    this.service = service;
  }

  /**
   * Crea un nuevo {@link ProyectoHito}.
   * 
   * @param proyectoHito {@link ProyectoHito} que se quiere crear.
   * @return Nuevo {@link ProyectoHito} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<ProyectoHito> create(@Valid @RequestBody ProyectoHitoInput proyectoHito) {
    log.debug("create(ProyectoHito proyectoHito) - start");
    ProyectoHito returnValue = service.create(proyectoHito);
    log.debug("create(ProyectoHito proyectoHito) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ProyectoHito} con el id indicado.
   * 
   * @param proyectoHito {@link ProyectoHito} a actualizar.
   * @param id           id {@link ProyectoHito} a actualizar.
   * @return {@link ProyectoHito} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ProyectoHito update(@PathVariable Long id,
      @Validated({ Update.class, Default.class }) @RequestBody ProyectoHitoInput proyectoHito) {
    log.debug("update(ProyectoHito proyectoHito, Long id) - start");
    ProyectoHito returnValue = service.update(id, proyectoHito);
    log.debug("update(ProyectoHito proyectoHito, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ProyectoHito} con id indicado.
   * 
   * @param id Identificador de {@link ProyectoHito}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve el {@link ProyectoHito} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoHito}.
   * @return {@link ProyectoHito} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public ProyectoHito findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProyectoHito returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Retorna el listado de destinatarios para incluir en el envio de email de un
   * {@link ProyectoHito}, en base a los flags de
   * {@link ProyectoHitoAviso}
   * 
   * @param id identificador de {@link ProyectoHito}
   * @return Listado de {@link Recipient}
   */
  @GetMapping("/{id}/deferrable-recipients")
  public List<Recipient> resolveDeferrableRecipients(@PathVariable Long id) {

    return service.getDeferredRecipients(id);
  }
}
