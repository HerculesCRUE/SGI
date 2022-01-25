package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.AgrupacionGastoConceptoInput;
import org.crue.hercules.sgi.csp.dto.AgrupacionGastoConceptoOutput;
import org.crue.hercules.sgi.csp.model.AgrupacionGastoConcepto;
import org.crue.hercules.sgi.csp.service.AgrupacionGastoConceptoService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(AgrupacionGastoConceptoController.REQUEST_MAPPING)
@Slf4j
public class AgrupacionGastoConceptoController {

  /** El path que gestiona este controlador */
  public static final String REQUEST_MAPPING = "/agrupaciongastoconceptos";

  /** Proyecto service */
  private final AgrupacionGastoConceptoService service;

  ModelMapper modelMapper;

  public AgrupacionGastoConceptoController(ModelMapper modelMapper, AgrupacionGastoConceptoService service) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Crea nuevo {@link AgrupacionGastoConcepto}
   * 
   * @param agrupacionGastoConcepto {@link AgrupacionGastoConcepto} que se quiere
   *                                crear.
   * @return Nuevo {@link AgrupacionGastoConcepto} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-C')")
  public ResponseEntity<AgrupacionGastoConceptoOutput> create(
      @Valid @RequestBody AgrupacionGastoConceptoInput agrupacionGastoConcepto) {
    log.debug("create(AgrupacionGastoConcepto agrupacionGastoConcepto) - start");
    AgrupacionGastoConcepto returnValue = service.create(convert(agrupacionGastoConcepto));
    log.debug("create(AgrupacionGastoConcepto agrupacionGastoConcepto) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link AgrupacionGastoConcepto}.
   * 
   * @param agrupacionGastoConcepto {@link AgrupacionGastoConcepto} a actualizar.
   * @param id                      Identificador {@link AgrupacionGastoConcepto}
   *                                a actualizar.
   * @return Proyecto {@link AgrupacionGastoConcepto} actualizado
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public AgrupacionGastoConceptoOutput update(@Valid @RequestBody AgrupacionGastoConceptoInput agrupacionGastoConcepto,
      @PathVariable Long id) {
    log.debug("update(AgrupacionGastoConcepto agrupacionGastoConcepto, Long id) - start");
    AgrupacionGastoConcepto returnValue = service.update(convert(id, agrupacionGastoConcepto));
    log.debug("update(AgrupacionGastoConcepto agrupacionGastoConcepto, Long id) - end");
    return convert(returnValue);
  }

  /**
   * Comprueba la existencia del {@link AgrupacionGastoConcepto} con el id
   * indicado.
   * 
   * @param id Identificador de {@link AgrupacionGastoConcepto}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<Void> exists(@PathVariable Long id) {
    log.debug("exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve el {@link AgrupacionGastoConcepto} con el id indicado.
   * 
   * @param id Identificador de {@link AgrupacionGastoConcepto}.
   * @return {@link AgrupacionGastoConcepto} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public AgrupacionGastoConceptoOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    AgrupacionGastoConceptoOutput returnValue = convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link AgrupacionGastoConcepto} con id indicado.
   * 
   * @param id Identificador de {@link AgrupacionGastoConcepto}.
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
   * Devuelve todas las entidades {@link AgrupacionGastoConcepto} activos
   * paginadas
   *
   * @param query  la información del filtro.
   * @param paging la información de la paginación.
   * @return la lista de entidades {@link AgrupacionGastoConcepto} paginadas
   */
  @GetMapping()
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<Page<AgrupacionGastoConceptoOutput>> findAll(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<AgrupacionGastoConcepto> page = service.findAll(paging);
    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  private AgrupacionGastoConceptoOutput convert(AgrupacionGastoConcepto agrupacionGastoConcepto) {
    return modelMapper.map(agrupacionGastoConcepto, AgrupacionGastoConceptoOutput.class);
  }

  private AgrupacionGastoConcepto convert(AgrupacionGastoConceptoInput agrupacionGastoConcepto) {
    return convert(null, agrupacionGastoConcepto);
  }

  private AgrupacionGastoConcepto convert(Long id, AgrupacionGastoConceptoInput agrupacionGastoConceptoInput) {
    AgrupacionGastoConcepto agrupacionGastoConcepto = modelMapper.map(agrupacionGastoConceptoInput,
        AgrupacionGastoConcepto.class);
    agrupacionGastoConcepto.setId(id);
    return agrupacionGastoConcepto;
  }

  private Page<AgrupacionGastoConceptoOutput> convert(Page<AgrupacionGastoConcepto> page) {
    List<AgrupacionGastoConceptoOutput> content = page.getContent().stream()
        .map(agrupacionGastoConcepto -> convert(agrupacionGastoConcepto)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

}
