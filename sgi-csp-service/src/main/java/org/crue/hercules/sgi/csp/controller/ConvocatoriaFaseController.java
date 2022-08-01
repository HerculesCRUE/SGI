package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.converter.ConvocatoriaFaseConverter;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaFaseInput;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaFaseOutput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFaseAviso;
import org.crue.hercules.sgi.csp.service.ConvocatoriaFaseService;
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
 * ConvocatoriaFaseController
 */
@RestController
@RequestMapping("/convocatoriafases")
@Slf4j
public class ConvocatoriaFaseController {

  /** ConvocatoriaFase service */
  private final ConvocatoriaFaseService service;
  private final ConvocatoriaFaseConverter convocatoriaFaseConverter;

  /**
   * Instancia un nuevo ConvocatoriaFaseController.
   * 
   * @param service                   {@link ConvocatoriaFaseService}
   * @param convocatoriaFaseConverter {@link ConvocatoriaFaseConverter}
   */
  public ConvocatoriaFaseController(ConvocatoriaFaseService service,
      ConvocatoriaFaseConverter convocatoriaFaseConverter) {
    this.service = service;
    this.convocatoriaFaseConverter = convocatoriaFaseConverter;
  }

  /**
   * Devuelve el {@link ConvocatoriaFase} con el id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaFase}.
   * @return {@link ConvocatoriaFaseOutput} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public ConvocatoriaFaseOutput findById(@PathVariable Long id) {
    return convocatoriaFaseConverter.convert(service.findById(id));
  }

  /**
   * Crea un nuevo {@link ConvocatoriaFase}.
   * 
   * @param convocatoriaFaseInput {@link ConvocatoriaFaseInput} que se quiere
   *                              crear.
   * @return Nuevo {@link ConvocatoriaFaseOutput} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C','CSP-CON-E')")
  public ResponseEntity<ConvocatoriaFaseOutput> create(
      @Valid @RequestBody ConvocatoriaFaseInput convocatoriaFaseInput) {
    log.debug("create(ConvocatoriaFase convocatoriaFase) - start");
    ConvocatoriaFaseOutput returnValue = convocatoriaFaseConverter.convert(service.create(convocatoriaFaseInput));
    log.debug("create(ConvocatoriaFase convocatoriaFase) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ConvocatoriaFase} con el id indicado.
   * 
   * @param convocatoriaFaseInput {@link ConvocatoriaFaseInput} a actualizar.
   * @param id                    id {@link ConvocatoriaFase} a actualizar.
   * @return {@link ConvocatoriaFaseOutput} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public ResponseEntity<ConvocatoriaFaseOutput> update(
      @Validated({ Update.class, Default.class }) @RequestBody ConvocatoriaFaseInput convocatoriaFaseInput,
      @PathVariable Long id) {
    log.debug("update(ConvocatoriaFase convocatoriaFase, Long id) - start");

    ConvocatoriaFaseOutput returnValue = convocatoriaFaseConverter.convert(service.update(id, convocatoriaFaseInput));
    log.debug("update(ConvocatoriaFase convocatoriaFase, Long id) - end");
    return ResponseEntity.ok(returnValue);
  }

  /**
   * Desactiva el {@link ConvocatoriaFase} con id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaFase}.
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
   * {@link ConvocatoriaFase}, en base a los flags de
   * {@link ConvocatoriaFaseAviso}
   * 
   * @param id identificador de {@link ConvocatoriaFase}
   * @return Listado de {@link Recipient}
   */
  @GetMapping("/{id}/deferrable-recipients")
  @PreAuthorize("isClient() and hasAuthority('SCOPE_sgi-csp')")
  public List<Recipient> resolveDeferrableRecipients(@PathVariable Long id) {
    return service.getDeferredRecipients(id);
  }

}
