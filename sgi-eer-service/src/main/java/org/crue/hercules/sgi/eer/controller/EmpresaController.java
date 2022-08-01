package org.crue.hercules.sgi.eer.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eer.converter.EmpresaAdministracionSociedadConverter;
import org.crue.hercules.sgi.eer.converter.EmpresaComposicionSociedadConverter;
import org.crue.hercules.sgi.eer.converter.EmpresaConverter;
import org.crue.hercules.sgi.eer.converter.EmpresaDocumentoConverter;
import org.crue.hercules.sgi.eer.converter.EmpresaEquipoEmprendedorConverter;
import org.crue.hercules.sgi.eer.dto.EmpresaAdministracionSociedadOutput;
import org.crue.hercules.sgi.eer.dto.EmpresaComposicionSociedadOutput;
import org.crue.hercules.sgi.eer.dto.EmpresaDocumentoOutput;
import org.crue.hercules.sgi.eer.dto.EmpresaEquipoEmprendedorOutput;
import org.crue.hercules.sgi.eer.dto.EmpresaInput;
import org.crue.hercules.sgi.eer.dto.EmpresaOutput;
import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.model.EmpresaAdministracionSociedad;
import org.crue.hercules.sgi.eer.model.EmpresaComposicionSociedad;
import org.crue.hercules.sgi.eer.model.EmpresaDocumento;
import org.crue.hercules.sgi.eer.model.EmpresaEquipoEmprendedor;
import org.crue.hercules.sgi.eer.service.EmpresaAdministracionSociedadService;
import org.crue.hercules.sgi.eer.service.EmpresaComposicionSociedadService;
import org.crue.hercules.sgi.eer.service.EmpresaDocumentoService;
import org.crue.hercules.sgi.eer.service.EmpresaEquipoEmprendedorService;
import org.crue.hercules.sgi.eer.service.EmpresaService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
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
 * EmpresaController
 */
@RestController
@RequestMapping(EmpresaController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class EmpresaController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "empresas";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";
  public static final String PATH_DESACTIVAR = PATH_ID + PATH_DELIMITER + "desactivar";
  public static final String PATH_EMPRESA_EQUIPO_EMPRENDEDOR = PATH_ID + PATH_DELIMITER + "equipos-emprendedores";
  public static final String PATH_DOCUMENTOS = PATH_ID + PATH_DELIMITER + "documentos";
  public static final String PATH_EMPRESA_COMPOSICION_SOCIEDAD = PATH_ID + PATH_DELIMITER + "composiciones-sociedades";
  public static final String PATH_EMPRESA_ADMINISTRACION_SOCIEDAD = PATH_ID + PATH_DELIMITER
      + "administraciones-sociedades";
  public static final String PATH_MODIFICADOS_IDS = PATH_DELIMITER + "modificados-ids";

  // Services
  private final EmpresaService service;
  private final EmpresaEquipoEmprendedorService empresaEquipoEmprendedorService;
  private final EmpresaDocumentoService empresaDocumentoService;
  private final EmpresaComposicionSociedadService empresaComposicionSociedadService;
  private final EmpresaAdministracionSociedadService empresaAdministracionSociedadService;
  // Converters
  private final EmpresaConverter converter;
  private final EmpresaEquipoEmprendedorConverter empresaEquipoEmprendedorConverter;
  private final EmpresaDocumentoConverter empresaDocumentoConverter;
  private final EmpresaComposicionSociedadConverter empresaComposicionSociedadConverter;
  private final EmpresaAdministracionSociedadConverter empresaAdministracionSociedadConverter;

  /**
   * Crea nuevo {@link Empresa}
   * 
   * @param empresa {@link Empresa} que se quiere crear.
   * @return Nuevo {@link Empresa} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('EER-EER-C')")
  public ResponseEntity<EmpresaOutput> create(@Valid @RequestBody EmpresaInput empresa) {
    log.debug("create(EmpresaInput empresa) - start");
    EmpresaOutput returnValue = converter.convert(service.create(converter.convert(empresa)));
    log.debug("create(EmpresaInput empresa) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Empresa}.
   * 
   * @param empresa {@link Empresa} a actualizar.
   * @param id      Identificador {@link Empresa} a actualizar.
   * @return {@link Empresa} actualizado
   */
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('EER-EER-E')")
  public EmpresaOutput update(@Valid @RequestBody EmpresaInput empresa, @PathVariable Long id) {
    log.debug("update(EmpresaInput empresa, Long id) - start");
    EmpresaOutput returnValue = converter.convert(service.update(converter.convert(id, empresa)));
    log.debug("update(EmpresaInput empresa, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Empresa} con el id indicado.
   * 
   * @param id Identificador de {@link Empresa}.
   * @return {@link Empresa} correspondiente al id
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('EER-EER-E', 'EER-EER-V')")
  public EmpresaOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    EmpresaOutput returnValue = converter.convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link Empresa} con el id indicado.
   *
   * @param id Identificador de {@link Empresa}.
   * @return {@link HttpStatus#OK} si existe y {@link HttpStatus#NO_CONTENT} si
   *         no.
   */
  @RequestMapping(path = PATH_ID, method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('EER-EER-E', 'EER-EER-V')")
  public ResponseEntity<Void> exists(@PathVariable Long id) {
    log.debug("Convocatoria exists(Long id) - start");
    boolean exists = service.existsById(id);
    log.debug("Convocatoria exists(Long id) - end");
    return exists ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Empresa} activos.
   *
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Empresa} paginadas y
   *         filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('EER-EER-B', 'EER-EER-E', 'EER-EER-R', 'EER-EER-V')")
  public ResponseEntity<Page<EmpresaOutput>> findActivos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<EmpresaOutput> page = converter.convert(service.findActivos(query, paging));
    log.debug("findAll(String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Desactiva la {@link Empresa} con id indicado.
   * 
   * @param id Identificador de {@link Empresa}.
   * @return {@link Empresa} desactivada.
   */
  @PatchMapping(PATH_DESACTIVAR)
  @PreAuthorize("hasAuthority('EER-EER-B')")
  public EmpresaOutput desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    EmpresaOutput returnValue = converter.convert(service.desactivar(id));
    log.debug("desactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link EmpresaEquipoEmprendedor}
   * de la {@link Empresa}.
   * 
   * @param id     Identificador del {@link EmpresaEquipoEmprendedor}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link EmpresaEquipoEmprendedor} paginadas
   *         y
   *         filtradas de la {@link Empresa}.
   */
  @GetMapping(PATH_EMPRESA_EQUIPO_EMPRENDEDOR)
  @PreAuthorize("hasAnyAuthorityForAnyUO('EER-EER-V', 'EER-EER-E')")
  public ResponseEntity<Page<EmpresaEquipoEmprendedorOutput>> findAllEmpresaEquipoEmprendedor(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllEmpresaEquipoEmprendedor(Long id, String query, Pageable paging) - start");
    Page<EmpresaEquipoEmprendedorOutput> page = empresaEquipoEmprendedorConverter
        .convert(empresaEquipoEmprendedorService.findAllByEmpresa(id, query, paging));
    log.debug("findAllEmpresaEquipoEmprendedor(Long id, String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link EmpresaDocumento} activos que
   * pertenezcan a la {@link Empresa} con id indicado.
   *
   * @param id     Identificador de {@link Empresa}.
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link EmpresaDocumento} paginadas y
   *         filtradas.
   */
  @GetMapping(PATH_DOCUMENTOS)
  @PreAuthorize("hasAnyAuthorityForAnyUO('EER-EER-E', 'EER-EER-V')")
  public ResponseEntity<Page<EmpresaDocumentoOutput>> findDocumentos(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findDocumentos(Long id, String query, Pageable paging) - start");
    Page<EmpresaDocumentoOutput> page = empresaDocumentoConverter
        .convert(empresaDocumentoService.findAllByEmpresaId(id, query, paging));
    log.debug("findDocumentos(Long id, String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link EmpresaComposicionSociedad}
   * de la {@link Empresa}.
   * 
   * @param id     Identificador del {@link EmpresaComposicionSociedad}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link EmpresaComposicionSociedad} paginadas
   *         y
   *         filtradas de la {@link Empresa}.
   */
  @GetMapping(PATH_EMPRESA_COMPOSICION_SOCIEDAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('EER-EER-V', 'EER-EER-E')")
  public ResponseEntity<Page<EmpresaComposicionSociedadOutput>> findAllEmpresaComposicionSociedad(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllEmpresaComposicionSociedad(Long id, String query, Pageable paging) - start");
    Page<EmpresaComposicionSociedadOutput> page = empresaComposicionSociedadConverter
        .convert(empresaComposicionSociedadService.findAllByEmpresa(id, query, paging));
    log.debug("findAllEmpresaComposicionSociedad(Long id, String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link EmpresaAdministracionSociedad}
   * de la {@link Empresa}.
   * 
   * @param id     Identificador del {@link EmpresaAdministracionSociedad}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link EmpresaAdministracionSociedad}
   *         paginadas
   *         y
   *         filtradas de la {@link Empresa}.
   */
  @GetMapping(PATH_EMPRESA_ADMINISTRACION_SOCIEDAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('EER-EER-V', 'EER-EER-E')")
  public ResponseEntity<Page<EmpresaAdministracionSociedadOutput>> findAllEmpresaAdministracionSociedad(
      @PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllEmpresaAdministracionSociedad(Long id, String query, Pageable paging) - start");
    Page<EmpresaAdministracionSociedadOutput> page = empresaAdministracionSociedadConverter
        .convert(empresaAdministracionSociedadService.findAllByEmpresa(id, query, paging));
    log.debug("findAllEmpresaAdministracionSociedad(Long id, String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene los ids de {@link Empresa} modificados que cumplan las condiciones
   * indicadas en el filtro de búsqueda
   *
   * @param query información del filtro.
   * @return el listado de ids de {@link Empresa}.
   */
  @GetMapping(PATH_MODIFICADOS_IDS)
  @PreAuthorize("hasAnyAuthorityForAnyUO('EER-EER-V', 'EER-EER-E')")
  public ResponseEntity<List<Long>> findIdsEmpresaModificados(
      @RequestParam(name = "q", required = false) String query) {
    log.debug("findIdsEmpresaModificados(String query) - start");
    List<Long> returnValue = service.findIdsEmpresaModificados(query);
    log.debug("findIdsEmpresaModificados(String query) - end");
    return returnValue.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
        : new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

}
