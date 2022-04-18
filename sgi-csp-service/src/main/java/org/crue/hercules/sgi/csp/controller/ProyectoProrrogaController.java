package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.ProrrogaDocumento;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.service.ProrrogaDocumentoService;
import org.crue.hercules.sgi.csp.service.ProyectoProrrogaService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoProrrogaController
 */
@RestController
@RequestMapping("/proyecto-prorrogas")
@Slf4j
public class ProyectoProrrogaController {

  /** ProyectoProrroga service */
  private final ProyectoProrrogaService service;

  /** ProrrogaDocumento service */
  private final ProrrogaDocumentoService prorrogaDocumentoService;

  /**
   * Instancia un nuevo ProyectoProrrogaController.
   * 
   * @param service                  {@link ProyectoProrrogaService}
   * @param prorrogaDocumentoService {@link ProrrogaDocumentoService}
   */
  public ProyectoProrrogaController(ProyectoProrrogaService service,
      ProrrogaDocumentoService prorrogaDocumentoService) {
    this.service = service;
    this.prorrogaDocumentoService = prorrogaDocumentoService;
  }

  /**
   * Crea un nuevo {@link ProyectoProrroga}.
   * 
   * @param proyectoProrroga {@link ProyectoProrroga} que se quiere crear.
   * @return Nuevo {@link ProyectoProrroga} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<ProyectoProrroga> create(@Valid @RequestBody ProyectoProrroga proyectoProrroga) {
    log.debug("create(ProyectoProrroga proyectoProrroga) - start");
    ProyectoProrroga returnValue = service.create(proyectoProrroga);
    log.debug("create(ProyectoProrroga proyectoProrroga) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ProyectoProrroga} con el id indicado.
   * 
   * @param proyectoProrroga {@link ProyectoProrroga} a actualizar.
   * @param id               id {@link ProyectoProrroga} a actualizar.
   * @return {@link ProyectoProrroga} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ProyectoProrroga update(
      @Validated({ Update.class, Default.class }) @RequestBody ProyectoProrroga proyectoProrroga,
      @PathVariable Long id) {
    log.debug("update(ProyectoProrroga proyectoProrroga, Long id) - start");
    proyectoProrroga.setId(id);
    ProyectoProrroga returnValue = service.update(proyectoProrroga);
    log.debug("update(ProyectoProrroga proyectoProrroga, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ProyectoProrroga} con id indicado.
   * 
   * @param id Identificador de {@link ProyectoProrroga}.
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
   * Comprueba la existencia del {@link ProyectoProrroga} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoProrroga}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<Void> exists(@PathVariable Long id) {
    log.debug("ProyectoProrroga exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("ProyectoProrroga exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("ProyectoProrroga exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve el {@link ProyectoProrroga} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoProrroga}.
   * @return {@link ProyectoProrroga} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ProyectoProrroga findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProyectoProrroga returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * 
   * PRORROGA DOCUMENTO
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ProrrogaDocumento} de la
   * {@link ProyectoProrroga}.
   * 
   * @param id     Identificador de {@link ProyectoProrroga}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ProrrogaDocumento}
   *         paginados y filtrados.
   */
  @GetMapping("/{id}/prorrogadocumentos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E', 'CSP-PRO-V')")
  public ResponseEntity<Page<ProrrogaDocumento>> findAllProrrogaDocumento(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProrrogaDocumento(Long id, String query, Pageable paging) - start");
    Page<ProrrogaDocumento> page = prorrogaDocumentoService.findAllByProyectoProrroga(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProrrogaDocumento(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProrrogaDocumento(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}
