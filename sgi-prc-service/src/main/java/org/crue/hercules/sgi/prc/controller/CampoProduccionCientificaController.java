package org.crue.hercules.sgi.prc.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.prc.dto.CampoProduccionCientificaInput;
import org.crue.hercules.sgi.prc.dto.CampoProduccionCientificaOutput;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.service.CampoProduccionCientificaService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * CampoProduccionCientificaController
 */
@RestController
@RequestMapping(CampoProduccionCientificaController.MAPPING)
@Slf4j
public class CampoProduccionCientificaController {
  public static final String PATH_DELIMITER = "/";
  public static final String MAPPING = PATH_DELIMITER + "campos-producciones-cientificas";
  public static final String PATH_VALORES = PATH_DELIMITER + "{id}/valores";

  private ModelMapper modelMapper;

  /** CampoProduccionCientifica service */
  private final CampoProduccionCientificaService service;

  /**
   * Instancia un nuevo CampoProduccionCientificaController.
   * 
   * @param modelMapper {@link ModelMapper}
   * @param service     {@link CampoProduccionCientificaService}
   */
  public CampoProduccionCientificaController(
      ModelMapper modelMapper,
      CampoProduccionCientificaService service) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link CampoProduccionCientifica}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link CampoProduccionCientifica}
   *         paginadas y filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E')")
  public ResponseEntity<Page<CampoProduccionCientificaOutput>> findAll(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<CampoProduccionCientifica> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve el {@link CampoProduccionCientifica} con el id indicado.
   * 
   * @param id Identificador de {@link CampoProduccionCientifica}.
   * @return {@link CampoProduccionCientifica} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public CampoProduccionCientificaOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    CampoProduccionCientifica returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Crea un nuevo {@link CampoProduccionCientifica}.
   * 
   * @param campoProduccionCientifica {@link CampoProduccionCientifica} que se
   *                                  quiere crear.
   * @return Nuevo {@link CampoProduccionCientifica} creado.
   */
  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<CampoProduccionCientificaOutput> create(
      @Valid @RequestBody CampoProduccionCientificaInput campoProduccionCientifica) {
    log.debug("create(CampoProduccionCientifica campoProduccionCientifica) - start");
    CampoProduccionCientifica returnValue = service.create(convert(campoProduccionCientifica));
    log.debug("create(CampoProduccionCientifica campoProduccionCientifica) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link CampoProduccionCientifica} con
   * campoProduccionCientificaRef id
   * indicado.
   * 
   * @param campoProduccionCientifica {@link CampoProduccionCientifica} a
   *                                  actualizar.
   * @param id                        id {@link CampoProduccionCientifica} a
   *                                  actualizar.
   * @return {@link CampoProduccionCientifica} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public CampoProduccionCientificaOutput update(
      @Valid @RequestBody CampoProduccionCientificaInput campoProduccionCientifica,
      @PathVariable Long id) {
    log.debug("update(CampoProduccionCientifica campoProduccionCientifica, Long id) - start");
    CampoProduccionCientifica returnValue = service.update(convert(id, campoProduccionCientifica));
    log.debug("update(CampoProduccionCientifica campoProduccionCientifica, Long id) - end");
    return convert(returnValue);
  }

  /**
   * Elimina el {@link CampoProduccionCientifica} con campoProduccionCientificaRef
   * indicado.
   * 
   * @param id id de {@link CampoProduccionCientifica}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteByCampoProduccionCientificaRef(Long id) - start");
    service.delete(id);
    log.debug("deleteByCampoProduccionCientificaRef(Long id) - end");
  }

  private CampoProduccionCientifica convert(Long id,
      CampoProduccionCientificaInput campoProduccionCientificaInput) {
    CampoProduccionCientifica campoProduccionCientifica = convert(campoProduccionCientificaInput);
    campoProduccionCientifica.setId(id);
    return campoProduccionCientifica;
  }

  private CampoProduccionCientificaOutput convert(CampoProduccionCientifica campoProduccionCientifica) {
    return modelMapper.map(campoProduccionCientifica, CampoProduccionCientificaOutput.class);
  }

  private CampoProduccionCientifica convert(CampoProduccionCientificaInput campoProduccionCientificaInput) {
    return modelMapper.map(campoProduccionCientificaInput, CampoProduccionCientifica.class);
  }

  private Page<CampoProduccionCientificaOutput> convert(Page<CampoProduccionCientifica> page) {
    List<CampoProduccionCientificaOutput> content = page.getContent().stream()
        .map(this::convert).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }
}