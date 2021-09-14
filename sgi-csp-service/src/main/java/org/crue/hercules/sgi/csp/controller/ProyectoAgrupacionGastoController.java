package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.AgrupacionGastoConceptoOutput;
import org.crue.hercules.sgi.csp.dto.ProyectoAgrupacionGastoInput;
import org.crue.hercules.sgi.csp.dto.ProyectoAgrupacionGastoOutput;
import org.crue.hercules.sgi.csp.model.AgrupacionGastoConcepto;
import org.crue.hercules.sgi.csp.model.ProyectoAgrupacionGasto;
import org.crue.hercules.sgi.csp.service.AgrupacionGastoConceptoService;
import org.crue.hercules.sgi.csp.service.ProyectoAgrupacionGastoService;
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
@RequestMapping(ProyectoAgrupacionGastoController.REQUEST_MAPPING)
@Slf4j
public class ProyectoAgrupacionGastoController {

  /** El path que gestiona este controlador */
  public static final String REQUEST_MAPPING = "/agrupaciongastos";

  /** Proyecto service */
  private final ProyectoAgrupacionGastoService service;

  /** Agrupacion Gasto Concepto Service */
  private final AgrupacionGastoConceptoService agrupacionGastoConceptoService;

  ModelMapper modelMapper;

  public ProyectoAgrupacionGastoController(ModelMapper modelMapper,
      AgrupacionGastoConceptoService agrupacionGastoConceptoService, ProyectoAgrupacionGastoService service) {
    this.modelMapper = modelMapper;
    this.agrupacionGastoConceptoService = agrupacionGastoConceptoService;
    this.service = service;
  }

  /**
   * Crea nuevo {@link ProyectoAgrupacionGasto}
   * 
   * @param proyectoAgrupacionGasto {@link ProyectoAgrupacionGasto} que se quiere
   *                                crear.
   * @return Nuevo {@link ProyectoAgrupacionGasto} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-C')")
  public ResponseEntity<ProyectoAgrupacionGastoOutput> create(
      @Valid @RequestBody ProyectoAgrupacionGastoInput proyectoAgrupacionGasto) {
    log.debug("create(ProyectoAgrupacionGasto proyectoAgrupacionGasto) - start");
    ProyectoAgrupacionGasto returnValue = service.create(convert(proyectoAgrupacionGasto));
    log.debug("create(ProyectoAgrupacionGasto proyectoAgrupacionGasto) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link ProyectoAgrupacionGasto}.
   * 
   * @param proyectoAgrupacionGasto {@link ProyectoAgrupacionGasto} a actualizar.
   * @param id                      Identificador {@link ProyectoAgrupacionGasto}
   *                                a actualizar.
   * @return Proyecto {@link ProyectoAgrupacionGasto} actualizado
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ProyectoAgrupacionGastoOutput update(@Valid @RequestBody ProyectoAgrupacionGastoInput proyectoAgrupacionGasto,
      @PathVariable Long id) {
    log.debug("update(ProyectoAgrupacionGasto proyectoAgrupacionGasto, Long id) - start");
    ProyectoAgrupacionGasto returnValue = service.update(convert(id, proyectoAgrupacionGasto));
    log.debug("update(ProyectoAgrupacionGasto proyectoAgrupacionGasto, Long id) - end");
    return convert(returnValue);
  }

  /**
   * Comprueba la existencia del {@link ProyectoAgrupacionGasto} con el id
   * indicado.
   * 
   * @param id Identificador de {@link ProyectoAgrupacionGasto}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<?> exists(@PathVariable Long id) {
    log.debug("exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve el {@link ProyectoAgrupacionGasto} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoAgrupacionGastoOutput}.
   * @return {@link ProyectoAgrupacionGastoOutput} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ProyectoAgrupacionGastoOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProyectoAgrupacionGastoOutput returnValue = convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ProyectoAgrupacionGasto} con id indicado.
   * 
   * @param id Identificador de {@link ProyectoAgrupacionGasto}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve todos los {@link AgrupacionGastoConceptoOutput} de un
   * {@link ProyectoAgrupacionGasto}
   * 
   * @param query
   * @param paging
   * @param id     del {@link ProyectoAgrupacionGasto}.
   * @return {@link ResponseEntity} con la lista de
   *         {@link AgrupacionGastoConceptoOutput} y el estado de la conexion.
   */
  @GetMapping("/{id}/agrupaciongastoconceptos")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ResponseEntity<Page<AgrupacionGastoConceptoOutput>> findAllById(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<AgrupacionGastoConcepto> page = agrupacionGastoConceptoService.findAllByAgrupacionId(id, query, paging);
    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convertAgrupacionGastoConcepto(page), HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoAgrupacionGasto}
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-EJEC-E', 'CSP-EJEC-V', 'CSP-EJEC-INV-VR')")
  ResponseEntity<Page<ProyectoAgrupacionGastoOutput>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<ProyectoAgrupacionGasto> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  private ProyectoAgrupacionGastoOutput convert(ProyectoAgrupacionGasto proyectoAgrupacionGasto) {
    return modelMapper.map(proyectoAgrupacionGasto, ProyectoAgrupacionGastoOutput.class);
  }

  private ProyectoAgrupacionGasto convert(ProyectoAgrupacionGastoInput proyectoAgrupacionGastoInput) {
    return convert(null, proyectoAgrupacionGastoInput);
  }

  private Page<ProyectoAgrupacionGastoOutput> convert(Page<ProyectoAgrupacionGasto> page) {
    List<ProyectoAgrupacionGastoOutput> content = page.getContent().stream()
        .map((proyectoAgrupacion) -> convert(proyectoAgrupacion)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private ProyectoAgrupacionGasto convert(Long id, ProyectoAgrupacionGastoInput proyectoAgrupacionGastoInput) {
    ProyectoAgrupacionGasto proyectoAgrupacionGasto = modelMapper.map(proyectoAgrupacionGastoInput,
        ProyectoAgrupacionGasto.class);
    proyectoAgrupacionGasto.setId(id);
    return proyectoAgrupacionGasto;
  }

  private AgrupacionGastoConceptoOutput convertAgrupacionGastoConcepto(
      AgrupacionGastoConcepto agrupacionGastoConcepto) {
    return modelMapper.map(agrupacionGastoConcepto, AgrupacionGastoConceptoOutput.class);
  }

  private Page<AgrupacionGastoConceptoOutput> convertAgrupacionGastoConcepto(Page<AgrupacionGastoConcepto> page) {
    List<AgrupacionGastoConceptoOutput> content = page.getContent().stream()
        .map((agrupacionGastoConcepto) -> convertAgrupacionGastoConcepto(agrupacionGastoConcepto))
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

}
