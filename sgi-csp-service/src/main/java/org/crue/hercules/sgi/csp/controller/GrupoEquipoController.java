package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.GrupoEquipoConverter;
import org.crue.hercules.sgi.csp.dto.GrupoEquipoCreateInput;
import org.crue.hercules.sgi.csp.dto.GrupoEquipoDto;
import org.crue.hercules.sgi.csp.dto.GrupoEquipoOutput;
import org.crue.hercules.sgi.csp.dto.GrupoEquipoUpdateInput;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigador;
import org.crue.hercules.sgi.csp.service.GrupoEquipoService;
import org.crue.hercules.sgi.csp.service.GrupoLineaInvestigadorService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * GrupoEquipoController
 */
@RestController
@RequestMapping(GrupoEquipoController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
@Validated
public class GrupoEquipoController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "gruposequipos";

  public static final String PATH_PERSONA_BAREMABLE_PERSONA_REF_ANIO = PATH_DELIMITER
      + "persona-baremable/{personaRef}/{anio}";
  public static final String PATH_GRUPOS_PERSONA_REF_ANIO = PATH_DELIMITER + "/{personaRef}/{anio}";
  public static final String PATH_BAREMABLES_GRUPO_REF_ANIO = PATH_DELIMITER + "baremables/{grupoRef}/{anio}";
  public static final String PATH_MIEMBROS_EQUIPO_INVESTIGADOR = PATH_DELIMITER + "investigador";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";
  public static final String PATH_GRUPO_LINEA_INVESTIGADOR = PATH_ID + PATH_DELIMITER + "gruposlineasinvestigadores";

  private final GrupoEquipoService service;
  private final GrupoEquipoConverter converter;
  private final GrupoLineaInvestigadorService grupoLineaInevestigadorService;

  /**
   * Crea nuevo {@link GrupoEquipo}
   * 
   * @param grupoEquipo {@link GrupoEquipo} que se quiere crear.
   * @return Nuevo {@link GrupoEquipo} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-C', 'CSP-GIN-E')")
  public ResponseEntity<GrupoEquipoOutput> create(@Valid @RequestBody GrupoEquipoCreateInput grupoEquipo) {
    log.debug("create(GrupoEquipoCreateInput grupoEquipo) - start");
    GrupoEquipoOutput returnValue = converter.convert(service.create(converter.convert(grupoEquipo)));
    log.debug("create(GrupoEquipoCreateInput grupoEquipo) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Devuelve el {@link GrupoEquipo} con el id indicado.
   * 
   * @param id Identificador de {@link GrupoEquipo}.
   * @return {@link GrupoEquipo} correspondiente al id
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V')")
  public GrupoEquipoOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    GrupoEquipoOutput returnValue = converter.convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba si personaRef pertenece a un grupo de investigación con un rol con
   * el flag de baremable a true a fecha 31 de diciembre del año que se esta
   * baremando y el grupo al que pertenecen los autores (tabla Grupo) este activo
   * y el campo "Grupo especial de investigación" a "No" el 31 de diciembre del
   * año que se esta baremando
   * 
   * @param personaRef PersonaRef
   * @param anio       Año de baremación
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = PATH_PERSONA_BAREMABLE_PERSONA_REF_ANIO, method = RequestMethod.HEAD)
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-csp')) or hasAuthority('CSP-PRO-PRC-V')")
  public ResponseEntity<Void> isPersonaBaremable(@PathVariable String personaRef, @PathVariable Integer anio) {
    log.debug("isPersonaBaremable(personaRef, anio) - start");
    if (service.isPersonaBaremable(personaRef, anio)) {
      log.debug("isPersonaBaremable(personaRef, anio) - end");

      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("isPersonaBaremable(personaRef, anio) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve una lista de {@link GrupoEquipoDto} pertenecientes a un
   * determinado grupo y que estén a 31 de diciembre del año de baremación
   *
   * @param grupoRef grupoRef
   * @param anio     año de baremación
   * @return lista de {@link GrupoEquipoDto}
   */
  @GetMapping(PATH_BAREMABLES_GRUPO_REF_ANIO)
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-csp')) or hasAuthority('CSP-PRO-PRC-V')")
  public ResponseEntity<List<GrupoEquipoDto>> findByGrupoIdAndAnio(@PathVariable Long grupoRef,
      @PathVariable Integer anio) {
    log.debug("findByGrupoIdAndAnio(grupoRef, anio) - start");
    List<GrupoEquipoDto> gruposEquipos = service.findByGrupoIdAndAnio(grupoRef, anio);

    if (gruposEquipos.isEmpty()) {
      log.debug("findByGrupoIdAndAnio(grupoRef, anio) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findByGrupoIdAndAnio(grupoRef, anio) - end");

    return new ResponseEntity<>(gruposEquipos, HttpStatus.OK);
  }

  /**
   * Devuelve una lista de ids {@link GrupoEquipo} pertenecientes a un
   * determinado personaRef y que estén a 31 de diciembre del año de baremación
   *
   * @param personaRef personaRef
   * @param anio       año de baremación
   * @return lista de ids {@link GrupoEquipo}
   */
  @GetMapping(PATH_GRUPOS_PERSONA_REF_ANIO)
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-csp')) or hasAuthority('CSP-PRO-PRC-V')")
  public ResponseEntity<List<Long>> findGrupoEquipoByPersonaRefAndFechaBaremacion(@PathVariable String personaRef,
      @PathVariable Integer anio) {
    log.debug("findGrupoEquipoByPersonaRefAndFechaBaremacion({},{}) - start", personaRef, anio);
    List<Long> gruposEquipos = service.findGrupoEquipoByPersonaRefAndFechaBaremacion(personaRef, anio);

    if (gruposEquipos.isEmpty()) {
      log.debug("findGrupoEquipoByPersonaRefAndFechaBaremacion({},{}) - end", personaRef, anio);

      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findGrupoEquipoByPersonaRefAndFechaBaremacion({},{}) - end", personaRef, anio);

    return new ResponseEntity<>(gruposEquipos, HttpStatus.OK);
  }

  /**
   * Actualiza el listado de {@link GrupoEquipo} del {@link Grupo} con el
   * listado grupoEquipos añadiendo, editando o eliminando los elementos segun
   * proceda.
   * 
   * @param id           Id del {@link Grupo}.
   * @param grupoEquipos lista con los nuevos {@link GrupoEquipo} a guardar.
   * @return Lista actualizada con los {@link GrupoEquipo}.
   */
  @PatchMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V')")
  public ResponseEntity<List<GrupoEquipoOutput>> update(@PathVariable Long id,
      @RequestBody List<@Valid GrupoEquipoUpdateInput> grupoEquipos) {
    log.debug("update(List<GrupoEquipoUpdateInput> grupoEquipos, grupoId) - start");
    List<GrupoEquipoOutput> returnValue = converter
        .convertGrupoEquipos(service.update(id, converter.convertGrupoEquipoInput(
            grupoEquipos)));
    log.debug("update(List<GrupoEquipoUpdateInput> grupoEquipos, grupoId) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link GrupoEquipo} cuya persona
   * Ref sea el usuario actual o formen parte de un grupo en el que la persona sea
   * un investigador principal.
   * 
   * @return el listado de personaRef de los {@link GrupoEquipo}.
   */
  @GetMapping(PATH_MIEMBROS_EQUIPO_INVESTIGADOR)
  @PreAuthorize("hasAuthority('PRC-INF-INV-GR')")
  public ResponseEntity<List<String>> findMiembrosEquipoUsuario() {
    log.debug("findMiembrosEquipoUsuario() - start");
    List<String> result = service.findMiembrosEquipoUsuario();
    log.debug("findMiembrosEquipoUsuario() - end");
    return result.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(result, HttpStatus.OK);
  }

  /**
   * Comprueba la existencia de un Grupo Equipo adscrito al
   * {@link GrupoLineaInvestigador} con el id en las fechas del grupo equipo
   *
   * @param id Identificador de Grupo Equipo
   * @return {@link HttpStatus#OK} si existe y {@link HttpStatus#NO_CONTENT} si
   *         no.
   */
  @RequestMapping(path = PATH_GRUPO_LINEA_INVESTIGADOR, method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V')")
  public ResponseEntity<Void> existsLineaInvestigadorInFechasGrupoEquipo(@PathVariable Long id) {
    log.debug("GrupoEquipo existsLineaInvestigadorInFechasGrupoEquipo(Long id) - start");
    boolean exists = grupoLineaInevestigadorService.existsLineaInvestigadorInFechasGrupoEquipo(id);
    log.debug("GrupoEquipo existsLineaInvestigadorInFechasGrupoEquipo(Long id) - end");
    return exists ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link GrupoEquipo}.
   *
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link GrupoEquipo} paginadas y
   *         filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-C', 'CSP-GIN-E')")
  public ResponseEntity<Page<GrupoEquipoOutput>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<GrupoEquipoOutput> page = converter.convert(service.findAll(query, paging));
    log.debug("findAll(String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }
}
