package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.ProyectoEntidadConvocanteDto;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante;
import org.crue.hercules.sgi.csp.service.ProyectoEntidadConvocanteService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoEntidadConvocanteController
 */

@RestController
@RequestMapping(ProyectoEntidadConvocanteController.REQUEST_MAPPING)
@Slf4j
public class ProyectoEntidadConvocanteController {

  @Autowired
  private ModelMapper modelMapper;

  /** ProyectoEntidadConvocante service */
  private final ProyectoEntidadConvocanteService service;

  /** El path que gestiona este controlador */
  public static final String REQUEST_MAPPING = ProyectoController.REQUEST_MAPPING + "/{id}/entidadconvocantes";
  public static final String PATH_ENTIDADCONVOCANTE = "/{entidadConvocanteId}";
  public static final String PATH_ENTIDADCONVOCANTE_PROGRAMA = PATH_ENTIDADCONVOCANTE + "/programa";

  public ProyectoEntidadConvocanteController(ProyectoEntidadConvocanteService proyectoEntidadConvocanteService) {
    log.debug(
        "ProyectoEntidadConvocanteController(ProyectoEntidadConvocanteService proyectoEntidadConvocanteService) - start");
    this.service = proyectoEntidadConvocanteService;
    log.debug(
        "ProyectoEntidadConvocanteController(ProyectoEntidadConvocanteService proyectoEntidadConvocanteService) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ProyectoEntidadConvocanteDto} del {@link Proyecto}.
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ProyectoEntidadConvocanteDto}
   *         paginados y filtrados.
   */
  @GetMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E')")
  public ResponseEntity<Page<ProyectoEntidadConvocanteDto>> findAllEntidadConvocantes(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllEntidadConvocantes(Long id, String query, Pageable paging) - start");

    Page<ProyectoEntidadConvocanteDto> page = convert(service.findAllByProyecto(id, query, paging));

    if (page.isEmpty()) {
      log.debug("findAllEntidadConvocantes(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllEntidadConvocantes(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Actualiza el listado de {@link ProyectoEntidadConvocante} del
   * {@link Proyecto} con el listado entidadesConvocantes
   * creando, editando o eliminando los elementos segun proceda.
   *
   * @param id                   Id del {@link Proyecto}.
   * @param entidadesConvocantes lista con los nuevos
   *                             {@link ProyectoEntidadConvocante} a guardar.
   * @return la lista de entidades {@link ProyectoEntidadConvocante} persistida.
   */
  @PatchMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<List<ProyectoEntidadConvocanteDto>> updateEntidadesConvocantesProyecto(@PathVariable Long id,
      @RequestBody List<ProyectoEntidadConvocanteDto> entidadesConvocantes) {
    log.debug(
        "updateEntidadesConvocantesProyecto(Long id, List<ProyectoEntidadConvocanteDto> entidadesConvocantes) - start");
    List<ProyectoEntidadConvocanteDto> list = convert(
        service.updateEntidadesConvocantesProyecto(id, convert(id, entidadesConvocantes)));
    log.debug(
        "updateEntidadesConvocantesProyecto(Long id, List<ProyectoEntidadConvocanteDto> entidadesConvocantes) - end");
    return new ResponseEntity<>(list, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link ProyectoEntidadConvocante}.
   * 
   * @param id                identificador del {@link Proyecto} al que se añade
   *                          el {@link ProyectoEntidadConvocanteDto}
   * @param entidadConvocante {@link ProyectoEntidadConvocanteDto}. que se quiere
   *                          crear.
   * @return Nuevo {@link ProyectoEntidadConvocanteDto} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<ProyectoEntidadConvocanteDto> createEntidadConvocante(@PathVariable Long id,
      @Valid @RequestBody ProyectoEntidadConvocanteDto entidadConvocante) {
    log.debug("createEntidadConvocante(ProyectoEntidadConvocanteDto entidadConvocante) - start");
    ProyectoEntidadConvocanteDto returnValue = convert(service.create(convert(id, entidadConvocante)));
    log.debug("createEntidadConvocante(ProyectoEntidadConvocanteDto entidadConvocante) - start");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link Programa} del {@link ProyectoEntidadConvocante} con el id
   * indicado.
   * 
   * @param id                  id {@link ProyectoEntidadConvocante} a actualizar.
   * @param entidadConvocanteId Identificador de
   *                            {@link ProyectoEntidadConvocante}.
   * @param programa            {@link Programa} a fijar.
   * @return {@link ProyectoEntidadConvocante} actualizado.
   */
  @PatchMapping(ProyectoEntidadConvocanteController.PATH_ENTIDADCONVOCANTE_PROGRAMA)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ProyectoEntidadConvocante setPrograma(@PathVariable Long id, @PathVariable Long entidadConvocanteId,
      @RequestBody(required = false) Programa programa) {
    log.debug("update(ProyectoEntidadConvocante proyectoEntidadConvocante, Long id) - start");
    ProyectoEntidadConvocante returnValue = service.setPrograma(entidadConvocanteId, programa);
    log.debug("update(ProyectoEntidadConvocante proyectoEntidadConvocante, Long id) - end");
    return returnValue;
  }

  /**
   * Borra el {@link ProyectoEntidadConvocante} con id indicado.
   * 
   * @param id                  Identificador de
   *                            {@link Proyecto}.
   * @param entidadConvocanteId Identificador de
   *                            {@link ProyectoEntidadConvocante}.
   */
  @DeleteMapping(ProyectoEntidadConvocanteController.PATH_ENTIDADCONVOCANTE)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id, @PathVariable Long entidadConvocanteId) {
    log.debug("deleteById(Long id) - start");
    service.delete(entidadConvocanteId);
    log.debug("deleteById(Long id) - end");
  }

  private ProyectoEntidadConvocanteDto convert(ProyectoEntidadConvocante entidadConvocante) {
    return modelMapper.map(entidadConvocante, ProyectoEntidadConvocanteDto.class);
  }

  private ProyectoEntidadConvocante convert(Long idProyecto, ProyectoEntidadConvocanteDto entidadConvocante) {
    ProyectoEntidadConvocante proyectoEntidadConvocante = modelMapper.map(entidadConvocante,
        ProyectoEntidadConvocante.class);
    proyectoEntidadConvocante.setProyectoId(idProyecto);
    return proyectoEntidadConvocante;
  }

  private Page<ProyectoEntidadConvocanteDto> convert(Page<ProyectoEntidadConvocante> page) {
    List<ProyectoEntidadConvocanteDto> content = page.getContent().stream()
        .map(this::convert).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private List<ProyectoEntidadConvocanteDto> convert(List<ProyectoEntidadConvocante> list) {
    return list.stream().map(this::convert).collect(Collectors.toList());
  }

  private List<ProyectoEntidadConvocante> convert(Long idProyecto, List<ProyectoEntidadConvocanteDto> list) {
    return list.stream().map(entidad -> convert(idProyecto, entidad)).collect(Collectors.toList());
  }

}
