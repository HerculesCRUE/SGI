package org.crue.hercules.sgi.prc.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.prc.converter.ModuladorConverter;
import org.crue.hercules.sgi.prc.dto.ModuladorInput;
import org.crue.hercules.sgi.prc.dto.ModuladorOutput;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.Modulador;
import org.crue.hercules.sgi.prc.model.Modulador.TipoModulador;
import org.crue.hercules.sgi.prc.service.ModuladorService;
import org.springframework.data.domain.Page;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ModuladorController
 */
@RestController
@RequestMapping(ModuladorController.MAPPING)
@Slf4j
@RequiredArgsConstructor
public class ModuladorController {
  public static final String PATH_DELIMITER = "/";
  public static final String MAPPING = PATH_DELIMITER + "moduladores";
  public static final String PATH_FIND_CONVOCATORIA_TIPO = PATH_DELIMITER + "{convocatoriaBaremacionId}/{tipo}";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final ModuladorService service;
  private final ModuladorConverter converter;

  /**
   * Devuelve una lista paginada y filtrada {@link Modulador}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link Modulador}
   *         paginadas y filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('PRC-CON-C', 'PRC-CON-E')")
  public ResponseEntity<Page<ModuladorOutput>> findAll(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<Modulador> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(converter.convert(page), HttpStatus.OK);
  }

  /**
   * Obtiene todos los {@link Modulador} por su convocatoriaBaremacionId y tipo
   *
   * @param convocatoriaBaremacionId el id de {@link ConvocatoriaBaremacion}.
   * @param tipo                     {@link TipoModulador}.
   * @return listado de {@link Modulador}.
   */
  @GetMapping(PATH_FIND_CONVOCATORIA_TIPO)
  @PreAuthorize("hasAnyAuthorityForAnyUO('PRC-CON-C', 'PRC-CON-E')")
  public List<ModuladorOutput> findByConvocatoriaBaremacionIdAndTipo(@PathVariable Long convocatoriaBaremacionId,
      @PathVariable TipoModulador tipo) {
    log.debug("findByConvocatoriaBaremacionIdAndTipo({},{})  - start", convocatoriaBaremacionId, tipo);

    List<Modulador> returnValue = service.findByConvocatoriaBaremacionIdAndTipo(convocatoriaBaremacionId, tipo);
    log.debug("findByConvocatoriaBaremacionIdAndTipo({},{})  - end", convocatoriaBaremacionId, tipo);

    return converter.convert(returnValue);
  }

  /**
   * Devuelve el {@link Modulador} con el id indicado.
   * 
   * @param id Identificador de {@link Modulador}.
   * @return {@link Modulador} correspondiente al id.
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('PRC-CON-C', 'PRC-CON-E')")
  public ModuladorOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    Modulador returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return converter.convert(returnValue);
  }

  /**
   * Crea un nuevo {@link Modulador}.
   * 
   * @param modulador {@link Modulador} que se quiere crear.
   * @return Nuevo {@link Modulador} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('PRC-CON-C', 'PRC-CON-E')")
  public ResponseEntity<ModuladorOutput> create(
      @Valid @RequestBody ModuladorInput modulador) {
    log.debug("create(Modulador modulador) - start");
    Modulador returnValue = service.create(converter.convert(modulador));
    log.debug("create(Modulador modulador) - end");
    return new ResponseEntity<>(converter.convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link Modulador}
   * 
   * @param modulador {@link Modulador} a actualizar.
   * @param id        id {@link Modulador} a actualizar.
   * @return {@link Modulador} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('PRC-CON-C', 'PRC-CON-E')")
  public ModuladorOutput update(
      @Valid @RequestBody ModuladorInput modulador,
      @PathVariable Long id) {
    log.debug("update(Modulador modulador, Long id) - start");
    Modulador returnValue = service.update(converter.convert(id, modulador));
    log.debug("update(Modulador modulador, Long id) - end");
    return converter.convert(returnValue);
  }

  /**
   * Elimina el {@link Modulador} con id* indicado.
   * 
   * @param id id de {@link Modulador}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('PRC-CON-C', 'PRC-CON-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteByModuladorRef(Long id) - start");
    service.delete(id);
    log.debug("deleteByModuladorRef(Long id) - end");
  }

}