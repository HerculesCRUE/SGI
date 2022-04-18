package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.GrupoConverter;
import org.crue.hercules.sgi.csp.converter.GrupoEquipoConverter;
import org.crue.hercules.sgi.csp.converter.GrupoEspecialInvestigacionConverter;
import org.crue.hercules.sgi.csp.converter.GrupoPalabraClaveConverter;
import org.crue.hercules.sgi.csp.converter.GrupoTipoConverter;
import org.crue.hercules.sgi.csp.dto.GrupoDto;
import org.crue.hercules.sgi.csp.dto.GrupoEquipoOutput;
import org.crue.hercules.sgi.csp.dto.GrupoEspecialInvestigacionOutput;
import org.crue.hercules.sgi.csp.dto.GrupoInput;
import org.crue.hercules.sgi.csp.dto.GrupoOutput;
import org.crue.hercules.sgi.csp.dto.GrupoPalabraClaveInput;
import org.crue.hercules.sgi.csp.dto.GrupoPalabraClaveOutput;
import org.crue.hercules.sgi.csp.dto.GrupoTipoOutput;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.model.GrupoEspecialInvestigacion;
import org.crue.hercules.sgi.csp.model.GrupoPalabraClave;
import org.crue.hercules.sgi.csp.model.GrupoTipo;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.service.GrupoEquipoService;
import org.crue.hercules.sgi.csp.service.GrupoEspecialInvestigacionService;
import org.crue.hercules.sgi.csp.service.GrupoPalabraClaveService;
import org.crue.hercules.sgi.csp.service.GrupoService;
import org.crue.hercules.sgi.csp.service.GrupoTipoService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * GrupoController
 */
@RestController
@RequestMapping(GrupoController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class GrupoController {

  public static final String REQUEST_MAPPING = "/grupos";
  public static final String PATH_ID = "/{id}";
  public static final String PATH_ACTIVAR = PATH_ID + "/activar";
  public static final String PATH_DESACTIVAR = PATH_ID + "/desactivar";
  public static final String PATH_GRUPO_EQUIPO = PATH_ID + "/miembrosequipo";
  public static final String PATH_INVESTIGADORES_PRINCIPALES = PATH_ID + "/investigadoresprincipales";
  public static final String PATH_INVESTIGADORES_PRINCIPALES_MAX_PARTICIPACION = PATH_ID
      + "/investigadoresprincipalesmaxparticipacion";
  public static final String PATH_TODOS = "/todos";
  public static final String PATH_CODIGO_DUPLICADO = "/codigoduplicado";
  public static final String PATH_NEXT_CODIGO = "/nextcodigo";
  public static final String PATH_PALABRAS_CLAVE = PATH_ID + "/palabrasclave";
  public static final String PATH_GRUPO_BAREMABLE_GRUPO_REF_ANIO = "/grupo-baremable/{grupoRef}/{anio}";
  public static final String PATH_BAREMABLES_ANIO = "/baremables/{anio}";
  public static final String PATH_GRUPO_TIPO = PATH_ID + "/tipos";
  public static final String PATH_GRUPO_ESPECIAL_INVESTIGACION = PATH_ID + "/especiales-investigacion";

  // Services
  private final GrupoService service;
  private final GrupoEquipoService grupoEquipoService;
  private final GrupoPalabraClaveService grupoPalabraClaveService;
  private final GrupoTipoService grupoTipoService;
  private final GrupoEspecialInvestigacionService grupoEspecialInvestigacionService;
  // Converters
  private final GrupoConverter converter;
  private final GrupoEquipoConverter grupoEquipoConverter;
  private final GrupoPalabraClaveConverter grupoPalabraClaveConverter;
  private final GrupoTipoConverter grupoTipoConverter;
  private final GrupoEspecialInvestigacionConverter grupoEspecialInvestigacionConverter;

  /**
   * Crea nuevo {@link Grupo}
   * 
   * @param grupo {@link Grupo} que se quiere crear.
   * @return Nuevo {@link Grupo} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-GIN-C')")
  public ResponseEntity<GrupoOutput> create(@Valid @RequestBody GrupoInput grupo) {
    log.debug("create(GrupoInput grupo) - start");
    GrupoOutput returnValue = converter.convert(service.create(converter.convert(grupo)));
    log.debug("create(GrupoInput grupo) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Grupo}.
   * 
   * @param grupo {@link Grupo} a actualizar.
   * @param id    Identificador {@link Grupo} a actualizar.
   * @return {@link Grupo} actualizado
   */
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-GIN-E')")
  public GrupoOutput update(@Valid @RequestBody GrupoInput grupo, @PathVariable Long id) {
    log.debug("update(GrupoInput grupo, Long id) - start");
    GrupoOutput returnValue = converter.convert(service.update(converter.convert(id, grupo)));
    log.debug("update(GrupoInput grupo, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Grupo} con el id indicado.
   * 
   * @param id Identificador de {@link Grupo}.
   * @return {@link Grupo} correspondiente al id
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V','CSP-SOL-C','CSP-SOL-E','CSP-SOL-INV-C')")
  public GrupoOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    GrupoOutput returnValue = converter.convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link Grupo} con el id indicado.
   *
   * @param id Identificador de {@link Grupo}.
   * @return {@link HttpStatus#OK} si existe y {@link HttpStatus#NO_CONTENT} si
   *         no.
   */
  @RequestMapping(path = PATH_ID, method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V')")
  public ResponseEntity<Void> exists(@PathVariable Long id) {
    log.debug("Convocatoria exists(Long id) - start");
    boolean exists = service.existsById(id);
    log.debug("Convocatoria exists(Long id) - end");
    return exists ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Grupo}.
   *
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Grupo} paginadas y
   *         filtradas.
   */
  @GetMapping(PATH_TODOS)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-B', 'CSP-GIN-E', 'CSP-GIN-R', 'CSP-GIN-V')")
  public ResponseEntity<Page<GrupoOutput>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<GrupoOutput> page = converter.convert(service.findAll(query, paging));
    log.debug("findAll(String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Grupo} activos.
   *
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Grupo} paginadas y
   *         filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-PRC-V', 'CSP-SOL-C', 'CSP-SOL-E', 'CSP-SOL-V', 'CSP-SOL-INV-C', 'CSP-SOL-INV-ER')")
  public ResponseEntity<Page<GrupoOutput>> findActivos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<GrupoOutput> page = converter.convert(service.findActivos(query, paging));
    log.debug("findAll(String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Activa la {@link FuenteFinanciacion} con id indicado.
   * 
   * @param id Identificador de {@link FuenteFinanciacion}.
   * @return {@link FuenteFinanciacion} actualizado.
   */
  @PatchMapping(PATH_ACTIVAR)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-GIN-R')")
  public GrupoOutput activar(@PathVariable Long id) {
    log.debug("activar(Long id) - start");
    GrupoOutput returnValue = converter.convert(service.activar(id));
    log.debug("activar(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link FuenteFinanciacion} con id indicado.
   * 
   * @param id Identificador de {@link FuenteFinanciacion}.
   * @return {@link FuenteFinanciacion} desactivada.
   */
  @PatchMapping(PATH_DESACTIVAR)
  @PreAuthorize("hasAuthority('CSP-GIN-B')")
  public GrupoOutput desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    GrupoOutput returnValue = converter.convert(service.desactivar(id));
    log.debug("desactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el siguiente codigo para el {@link Grupo} del departamento indicado.
   *
   * @param departamentoRef departamento para el que se quiere obtener el codigo.
   * @return el siguiente codigo para el {@link Grupo}.
   */
  @GetMapping(PATH_NEXT_CODIGO)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-C','CSP-SOL-E')")
  public String getNextCodigo(@RequestParam String departamentoRef) {
    log.debug("getNextCodigo(String departamentoRef) - start");
    String returnValue = service.getNextCodigo(departamentoRef);
    log.debug("getNextCodigo(String departamentoRef) - end");
    return JSONObject.quote(returnValue);
  }

  /**
   * Comprueba si ya existe un grupo (o otro grupo si se indica un grupoId) con el
   * codigo indicado
   * 
   * @param grupoId Identificador del {@link Grupo}
   * @param codigo  codigo que se quiere validar
   * @return <code>true</code> si ya existe otro grupo con el mismo codigo,
   *         <code>false</code> en caso contrario
   */
  @RequestMapping(path = PATH_CODIGO_DUPLICADO, method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-C', 'CSP-GIN-E')")
  public ResponseEntity<Void> isDuplicatedCodigo(@RequestParam(required = false) Long grupoId,
      @RequestParam String codigo) {
    log.debug("isDuplicatedCodigo(Long grupoId, String departamentoRef) - start");
    boolean returnValue = service.isDuplicatedCodigo(grupoId, codigo);
    log.debug("isDuplicatedCodigo(Long grupoId, String departamentoRef) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link GrupoEquipo} del
   * {@link Grupo}.
   * 
   * @param id     Identificador del {@link GrupoEquipo}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ProyectoEquipo} paginadas y
   *         filtradas del {@link Grupo}.
   */
  @GetMapping(PATH_GRUPO_EQUIPO)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-V', 'CSP-GIN-E')")
  public ResponseEntity<Page<GrupoEquipoOutput>> findAllGrupoEquipo(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllGrupoEquipo(Long id, String query, Pageable paging) - start");
    Page<GrupoEquipoOutput> page = grupoEquipoConverter.convert(grupoEquipoService.findAllByGrupo(id, query, paging));
    log.debug("findAllGrupoEquipo(Long id, String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista filtrada de investigadores principales del
   * {@link Grupo} en el momento actual con mayor porcentaje de particitacion.
   *
   * Son investiador principales los {@link GrupoEquipo} que a fecha actual
   * tiene el {@link RolProyecto} con el flag {@link RolProyecto#rolPrincipal} a
   * <code>true</code>. En caso de existir mas de un {@link GrupoEquipo}, se
   * recupera el que tenga el mayor porcentaje de dedicación al grupo
   * ({@link GrupoEquipo#participacion}) y en caso de que varios tengan la misma
   * participacion se devuelven todos los que coincidan.
   * 
   * @param id Identificador del {@link Grupo}.
   * @return la lista de personaRef de los investigadores principales del
   *         {@link Grupo} en el momento actual.
   */
  @GetMapping(PATH_INVESTIGADORES_PRINCIPALES_MAX_PARTICIPACION)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-V', 'CSP-GIN-E', 'CSP-GIN-PRC-V')")
  public ResponseEntity<List<String>> findPersonaRefInvestigadoresPrincipalesWithMaxParticipacion(
      @PathVariable Long id) {
    log.debug("findPersonaRefInvestigadoresPrincipales(Long id) - start");
    List<String> returnValue = grupoEquipoService.findPersonaRefInvestigadoresPrincipalesWithMaxParticipacion(id);
    log.debug("findPersonaRefInvestigadoresPrincipales(Long id) - end");
    return returnValue.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
        : new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Devuelve una lista filtrada de investigadores principales del {@link Grupo}
   * en el momento actual.
   *
   * Son investiador principales los {@link GrupoEquipo} que a fecha actual
   * tiene el {@link RolProyecto} con el flag {@link RolProyecto#rolPrincipal} a
   * <code>true</code>.
   * 
   * @param id Identificador del {@link Grupo}.
   * @return la lista de personaRef de los investigadores principales del
   *         {@link Grupo} en el momento actual.
   */
  @GetMapping(PATH_INVESTIGADORES_PRINCIPALES)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-EJEC-V', 'CSP-EJEC-E', 'CSP-EJEC-INV-VR')")
  public ResponseEntity<List<String>> findPersonaRefInvestigadoresPrincipales(@PathVariable Long id) {
    log.debug("findPersonaRefInvestigadoresPrincipales(Long id) - start");
    List<String> returnValue = grupoEquipoService.findPersonaRefInvestigadoresPrincipales(id);
    log.debug("findPersonaRefInvestigadoresPrincipales(Long id) - end");
    return returnValue.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
        : new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Devuelve las {@link GrupoPalabraClave} asociadas a la entidad {@link Grupo}
   * con el id indicado
   * 
   * @param id     Identificador de {@link Grupo}
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return {@link GrupoPalabraClave} correspondientes al id de la entidad
   *         {@link Grupo}
   */
  @GetMapping(PATH_PALABRAS_CLAVE)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V', 'CSP-GIN-C')")
  public Page<GrupoPalabraClaveOutput> findPalabrasClave(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findPalabrasClave(@PathVariable Long id, String query, Pageable paging) - start");
    Page<GrupoPalabraClaveOutput> returnValue = grupoPalabraClaveConverter
        .convert(grupoPalabraClaveService.findByGrupoId(id, query, paging));
    log.debug("findPalabrasClave(@PathVariable Long id, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Actualiza la lista de {@link GrupoPalabraClave} asociadas a la entidad
   * {@link Grupo} con el id indicado
   * 
   * @param id            identificador de {@link Grupo}
   * @param palabrasClave nueva lista de {@link GrupoPalabraClave} de
   *                      la entidad {@link Grupo}
   * @return la nueva lista de {@link GrupoPalabraClave} asociadas a la entidad
   *         {@link Grupo}
   */
  @PatchMapping(PATH_PALABRAS_CLAVE)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-C')")
  public ResponseEntity<List<GrupoPalabraClaveOutput>> updatePalabrasClave(@PathVariable Long id,
      @Valid @RequestBody List<GrupoPalabraClaveInput> palabrasClave) {
    log.debug("updatePalabrasClave(Long id, List<GrupoPalabraClaveInput> palabrasClave) - start");
    List<GrupoPalabraClaveOutput> returnValue = grupoPalabraClaveConverter
        .convertGrupoPalabrasClave(grupoPalabraClaveService.updatePalabrasClave(id,
            grupoPalabraClaveConverter.convertGrupoPalabrasClaveInput(palabrasClave)));
    log.debug("updatePalabrasClave(Long id, List<ConvocatoriaPalabraClave> palabrasClave) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Devuelve si grupoRef pertenece a un grupo de investigación con el campo
   * "Grupo especial de investigación" a "No" el 31 de diciembre del
   * año que se esta baremando
   * 
   * @param grupoRef grupoRef
   * @param anio     Año de baremación
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = PATH_GRUPO_BAREMABLE_GRUPO_REF_ANIO, method = RequestMethod.HEAD)
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-csp')) or hasAuthority('CSP-PRO-PRC-V')")
  public ResponseEntity<Void> isGrupoBaremable(@PathVariable Long grupoRef, @PathVariable Integer anio) {
    log.debug("isGrupoBaremable(grupoRef, anio) - start");
    if (service.isGrupoBaremable(grupoRef, anio)) {
      log.debug("isGrupoBaremable(grupoRef, anio) - end");

      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("isGrupoBaremable(grupoRef, anio) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve una lista de {@link GrupoDto} pertenecientes a un determinado
   * grupo y que estén a 31 de diciembre del año de baremación
   *
   * @param anio año de baremación
   * @return lista de {@link GrupoDto}
   */
  @GetMapping(PATH_BAREMABLES_ANIO)
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-csp')) or hasAuthority('CSP-PRO-PRC-V')")
  public ResponseEntity<List<GrupoDto>> findAllByAnio(@PathVariable Integer anio) {
    log.debug("findAllByAnio(anio) - start");
    List<GrupoDto> grupos = service.findAllByAnio(anio);

    if (grupos.isEmpty()) {
      log.debug("findAllByAnio(anio) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllByAnio(anio) - end");
    return new ResponseEntity<>(grupos, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link GrupoTipo} del
   * {@link Grupo}.
   * 
   * @param id     Identificador del {@link GrupoTipo}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link GrupoTipo} paginadas y
   *         filtradas del {@link Grupo}.
   */
  @GetMapping(PATH_GRUPO_TIPO)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-V', 'CSP-GIN-E')")
  public ResponseEntity<Page<GrupoTipoOutput>> findAllGrupoTipo(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllGrupoTipo(Long id, String query, Pageable paging) - start");
    Page<GrupoTipoOutput> page = grupoTipoConverter.convert(grupoTipoService.findAllByGrupo(id, query, paging));
    log.debug("findAllGrupoTipo(Long id, String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link GrupoEspecialInvestigacion}
   * del
   * {@link Grupo}.
   * 
   * @param id     Identificador del {@link GrupoEspecialInvestigacion}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link GrupoEspecialInvestigacion} paginadas
   *         y
   *         filtradas del {@link Grupo}.
   */
  @GetMapping(PATH_GRUPO_ESPECIAL_INVESTIGACION)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-V', 'CSP-GIN-E')")
  public ResponseEntity<Page<GrupoEspecialInvestigacionOutput>> findAllGrupoEspecialInvestigacion(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllGrupoEspecialInvestigacion(Long id, String query, Pageable paging) - start");
    Page<GrupoEspecialInvestigacionOutput> page = grupoEspecialInvestigacionConverter
        .convert(grupoEspecialInvestigacionService.findAllByGrupo(id, query, paging));
    log.debug("findAllGrupoEspecialInvestigacion(Long id, String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Hace las comprobaciones necesarias para determinar si el {@link Grupo}
   * puede ser modificado.
   * 
   * @param id Id del {@link Grupo}.
   * @return HTTP-200 Si se permite modificación / HTTP-204 Si no se permite
   *         modificación
   */
  @RequestMapping(path = "/{id}/modificable", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-V', 'CSP-GIN-C', 'CSP-GIN-E', 'CSP-GIN-B')")
  public ResponseEntity<Void> modificable(@PathVariable Long id) {
    log.debug("modificable(Long id) - start");
    boolean returnValue = service.modificable();
    log.debug("modificable(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
