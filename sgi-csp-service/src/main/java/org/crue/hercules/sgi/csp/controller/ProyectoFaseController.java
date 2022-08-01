package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.converter.ProyectoFaseConverter;
import org.crue.hercules.sgi.csp.dto.ProyectoFaseInput;
import org.crue.hercules.sgi.csp.dto.ProyectoFaseOutput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.ProyectoFase;
import org.crue.hercules.sgi.csp.model.ProyectoFaseAviso;
import org.crue.hercules.sgi.csp.service.ProyectoFaseAvisoService;
import org.crue.hercules.sgi.csp.service.ProyectoFaseService;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoFaseController
 */
@RestController
@RequestMapping("/proyectofases")
@Slf4j
@RequiredArgsConstructor
public class ProyectoFaseController {

  private final ProyectoFaseService service;
  private final ProyectoFaseConverter proyectoFaseConverter;
  private final ProyectoFaseAvisoService proyectoFaseAvisoService;

  /**
   * Crea un nuevo {@link ProyectoFase}.
   * 
   * @param proyectoFaseInput {@link ProyectoFaseInput} que se quiere crear.
   * @return Nuevo {@link ProyectoFaseOutput} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<ProyectoFaseOutput> create(@Valid @RequestBody ProyectoFaseInput proyectoFaseInput) {

    return new ResponseEntity<>(proyectoFaseConverter.convert(service.create(proyectoFaseInput)), HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ProyectoFase} con el id indicado.
   * 
   * @param proyectoFaseInput {@link ProyectoFaseInput} a actualizar.
   * @param id                id {@link ProyectoFase} a actualizar.
   * @return {@link ProyectoFaseOutput} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<ProyectoFaseOutput> update(
      @Validated({ Update.class, Default.class }) @RequestBody ProyectoFaseInput proyectoFaseInput,
      @PathVariable Long id) {

    return ResponseEntity.ok(proyectoFaseConverter.convert(service.update(id, proyectoFaseInput)));
  }

  /**
   * Desactiva el {@link ProyectoFase} con id indicado.
   * 
   * @param id Identificador de {@link ProyectoFase}.
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
   * Devuelve el {@link ProyectoFase} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoFase}.
   * @return {@link ProyectoFaseOutput} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public ResponseEntity<ProyectoFaseOutput> findById(@PathVariable Long id) {

    return ResponseEntity.ok(proyectoFaseConverter.convert(service.findById(id)));
  }

  /**
   * Retorna el listado de destinatarios para incluir en el envio de email de una
   * {@link ProyectoFase}, en base a los flags de
   * {@link ProyectoFaseAviso}
   * 
   * @param id identificador de {@link ConvocatoriaFase}
   * @return Listado de {@link Recipient}
   */
  @GetMapping("/{id}/deferrable-recipients")
  @PreAuthorize("isClient() and hasAuthority('SCOPE_sgi-csp')")
  public List<Recipient> resolveDeferrableRecipients(@PathVariable Long id) {
    return proyectoFaseAvisoService.getDeferredRecipients(id);
  }

}
