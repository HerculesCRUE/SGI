package org.crue.hercules.sgi.pii.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.pii.dto.InformePatentabilidadOutput;
import org.crue.hercules.sgi.pii.dto.InvencionAreaConocimientoInput;
import org.crue.hercules.sgi.pii.dto.InvencionAreaConocimientoOutput;
import org.crue.hercules.sgi.pii.dto.InvencionDocumentoOutput;
import org.crue.hercules.sgi.pii.dto.InvencionGastoOutput;
import org.crue.hercules.sgi.pii.dto.InvencionInput;
import org.crue.hercules.sgi.pii.dto.InvencionInventorInput;
import org.crue.hercules.sgi.pii.dto.InvencionInventorOutput;
import org.crue.hercules.sgi.pii.dto.InvencionOutput;
import org.crue.hercules.sgi.pii.dto.InvencionSectorAplicacionInput;
import org.crue.hercules.sgi.pii.dto.InvencionSectorAplicacionOutput;
import org.crue.hercules.sgi.pii.dto.SolicitudProteccionOutput;
import org.crue.hercules.sgi.pii.exceptions.NoRelatedEntitiesException;
import org.crue.hercules.sgi.pii.model.InformePatentabilidad;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.InvencionAreaConocimiento;
import org.crue.hercules.sgi.pii.model.InvencionDocumento;
import org.crue.hercules.sgi.pii.model.InvencionGasto;
import org.crue.hercules.sgi.pii.model.InvencionInventor;
import org.crue.hercules.sgi.pii.model.InvencionSectorAplicacion;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion;
import org.crue.hercules.sgi.pii.service.InformePatentabilidadService;
import org.crue.hercules.sgi.pii.service.InvencionAreaConocimientoService;
import org.crue.hercules.sgi.pii.service.InvencionDocumentoService;
import org.crue.hercules.sgi.pii.service.InvencionGastoService;
import org.crue.hercules.sgi.pii.service.InvencionInventorService;
import org.crue.hercules.sgi.pii.service.InvencionSectorAplicacionService;
import org.crue.hercules.sgi.pii.service.InvencionService;
import org.crue.hercules.sgi.pii.service.SolicitudProteccionService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

import lombok.extern.slf4j.Slf4j;

/**
 * InvencionController {
 * 
 */
@RestController
@RequestMapping(InvencionController.MAPPING)
@Slf4j
public class InvencionController {
  public static final String MAPPING = "/invenciones";
  public static final String PATH_SECTORES = "/{id}/sectoresaplicacion";
  public static final String PATH_AREAS = "/{id}/areasconocimiento";
  public static final String PATH_INFORMESPATENTABILIDAD = "/{id}/informespatentabilidad";
  public static final String PATH_INVENCION_INVENTOR = "/{invencionId}/invencion-inventores";
  public static final String PATH_INVENCION_GASTO = "/{invencionId}/gastos";

  private ModelMapper modelMapper;

  /** Invencion service */
  private final InvencionService service;
  /** InvencionSectorAplicacion service */
  private final InvencionSectorAplicacionService invencionSectorAplicacionService;
  private final InvencionDocumentoService invencionDocumentoService;
  private final SolicitudProteccionService solicitudProteccionService;
  /** InvencionAreaConocimientoService service */
  private final InvencionAreaConocimientoService invencionAreaConocimientoService;

  /** InformePatentabilidadService service */
  private final InformePatentabilidadService informePatentabilidadService;

  /** Invencion service */
  private final InvencionInventorService invencionInventorService;

  /** InvencionGasto service */
  private final InvencionGastoService invencionGastoService;

  public InvencionController(ModelMapper modelMapper, InvencionService invencionService,
      InvencionSectorAplicacionService invencionSectorAplicacionService,
      InvencionDocumentoService invencionDocumentoService,
      InvencionAreaConocimientoService invencionAreaConocimientoService,
      InformePatentabilidadService informePatentabilidadService,
      final SolicitudProteccionService solicitudProteccionService, InvencionInventorService invencionInventorService,
      InvencionGastoService invencionGastoService) {
    this.modelMapper = modelMapper;
    this.service = invencionService;
    this.invencionInventorService = invencionInventorService;
    this.invencionSectorAplicacionService = invencionSectorAplicacionService;
    this.invencionDocumentoService = invencionDocumentoService;
    this.invencionAreaConocimientoService = invencionAreaConocimientoService;
    this.informePatentabilidadService = informePatentabilidadService;
    this.solicitudProteccionService = solicitudProteccionService;
    this.invencionGastoService = invencionGastoService;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Invencion} activos.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return la lista de entidades {@link Invencion} paginadas y/o filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthority('PII-INV-V', 'PII-INV-C', 'PII-INV-E', 'PII-INV-B', 'PII-INV-R')")
  ResponseEntity<Page<InvencionOutput>> findActivos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<Invencion> page = service.findActivos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Invencion}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return la lista de entidades {@link Invencion} paginadas y/o filtradas.
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthority('PII-INV-V', 'PII-INV-C', 'PII-INV-E', 'PII-INV-B', 'PII-INV-R')")
  ResponseEntity<Page<InvencionOutput>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(String query, Pageable paging) - start");
    Page<Invencion> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodos(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve la {@link Invencion} con el id indicado.
   * 
   * @param id Identificador de {@link Invencion}.
   * @return {@link Invencion} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('PII-INV-V', 'PII-INV-C', 'PII-INV-E', 'PII-INV-B', 'PII-INV-R')")
  InvencionOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    Invencion returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Crea un nuevo {@link Invencion}.
   * 
   * @param invencion {@link Invencion} que se quiere crear.
   * @return Nuevo {@link Invencion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('PII-INV-C')")
  ResponseEntity<InvencionOutput> create(@Valid @RequestBody InvencionInput invencion) {
    log.debug("create(Invencion invencion) - start");
    Invencion returnValue = service.create(convert(invencion));
    log.debug("create(Invencion invencion) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza la {@link Invencion} con el id indicado.
   * 
   * @param invencion {@link Invencion} a actualizar.
   * @param id        id {@link Invencion} a actualizar.
   * @return {@link Invencion} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-INV-E')")
  InvencionOutput update(@Valid @RequestBody InvencionInput invencion, @PathVariable Long id) {
    log.debug("update(Invencion invencion, Long id) - start");
    Invencion returnValue = service.update(convert(id, invencion));
    log.debug("update(Invencion invencion, Long id) - end");
    return convert(returnValue);
  }

  /**
   * Activa la {@link Invencion} con id indicado.
   * 
   * @param id Identificador de {@link Invencion}.
   * @return {@link Invencion} actualizada.
   */
  @PatchMapping("/{id}/activar")
  @PreAuthorize("hasAuthority('PII-INV-R')")
  InvencionOutput activar(@PathVariable Long id) {
    log.debug("activar(Long id) - start");
    Invencion returnValue = service.activar(id);
    log.debug("activar(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Desactiva la {@link Invencion} con id indicado.
   * 
   * @param id Identificador de {@link Invencion}.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAuthority('PII-INV-B')")
  InvencionOutput desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    Invencion returnValue = service.desactivar(id);
    log.debug("desactivar(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Comprueba la existencia de la {@link Invencion} con el id indicado.
   * 
   * @param id Identificador de {@link Invencion}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthority('PII-INV-V', 'PII-INV-E')")
  public ResponseEntity<?> exists(@PathVariable Long id) {
    log.debug("Invencion exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("Invencion exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("Invencion exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve las {@link InvencionSectorAplicacion} asociadas a la
   * {@link Invencion} con el id indicado
   * 
   * @param id Identificador de {@link Invencion}
   * @return {@link InvencionSectorAplicacion} correspondiente al id de la
   *         {@link Invencion}
   */
  @GetMapping(PATH_SECTORES)
  @PreAuthorize("hasAnyAuthority('PII-INV-E', 'PII-INV-V', 'PII-INV-C')")
  public List<InvencionSectorAplicacionOutput> findSectoresAplicacion(@PathVariable Long id) {
    log.debug("findSectoresAplicacion(@PathVariable Long id) - start");
    List<InvencionSectorAplicacionOutput> returnValue = convertInvencionSectoresAplicacion(
        invencionSectorAplicacionService.findByInvencion(id));
    log.debug("findSectoresAplicacion(@PathVariable Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza la lista de {@link InvencionSectorAplicacion} asociadas a la
   * {@link Invencion} con el id indicado
   * 
   * @param id                 identificador de la {@link Invencion}
   * @param sectoresAplicacion nueva lista de {@link InvencionSectorAplicacion} de
   *                           la {@link Invencion}
   * @return la nueva lista de {@link InvencionSectorAplicacion} asociadas a la
   *         {@link Invencion}
   */
  @PatchMapping(PATH_SECTORES)
  @PreAuthorize("hasAnyAuthority('PII-INV-E', 'PII-INV-C')")
  public ResponseEntity<List<InvencionSectorAplicacionOutput>> updateSectoresAplicacion(@PathVariable Long id,
      @Valid @RequestBody List<InvencionSectorAplicacionInput> sectoresAplicacion) {
    log.debug("updateSectoresAplicacion(Long id, List<InvencionSectorAplicacionInput> sectoresAplicacion) - start");

    sectoresAplicacion.stream().forEach(sectorAplicacion -> {
      if (!sectorAplicacion.getInvencionId().equals(id)) {
        throw new NoRelatedEntitiesException(InvencionSectorAplicacion.class, Invencion.class);
      }
    });

    List<InvencionSectorAplicacionOutput> returnValue = convertInvencionSectoresAplicacion(
        invencionSectorAplicacionService.updateSectoresAplicacion(id,
            convertInvencionSectorAplicacionInputs(sectoresAplicacion)));
    log.debug("updateSectoresAplicacion(Long id, List<InvencionSectorAplicacionInput> sectoresAplicacion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Devuelve las {@link InvencionAreaConocimiento} asociadas a la
   * {@link Invencion} con el id indicado
   * 
   * @param id Identificador de {@link Invencion}
   * @return {@link InvencionAreaConocimiento} correspondiente al id de la
   *         {@link Invencion}
   */
  @GetMapping(PATH_AREAS)
  @PreAuthorize("hasAnyAuthority('PII-INV-E', 'PII-INV-V', 'PII-INV-C')")
  public List<InvencionAreaConocimientoOutput> findAreasConocimiento(@PathVariable Long id) {
    log.debug("findAreasConocimiento(@PathVariable Long id) - start");
    List<InvencionAreaConocimientoOutput> returnValue = convertInvencionAreasConocimiento(
        invencionAreaConocimientoService.findByInvencion(id));
    log.debug("findAreasConocimiento(@PathVariable Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza la lista de {@link InvencionAreaConocimiento} asociadas a la
   * {@link Invencion} con el id indicado
   * 
   * @param id                identificador de la {@link Invencion}
   * @param areasConocimiento nueva lista de {@link InvencionAreaConocimiento} de
   *                          la {@link Invencion}
   * @return la nueva lista de {@link InvencionAreaConocimiento} asociadas a la
   *         {@link Invencion}
   */
  @PatchMapping(PATH_AREAS)
  @PreAuthorize("hasAnyAuthority('PII-INV-E', 'PII-INV-C')")
  public ResponseEntity<List<InvencionAreaConocimientoOutput>> updateAreasConocimiento(@PathVariable Long id,
      @Valid @RequestBody List<InvencionAreaConocimientoInput> areasConocimiento) {
    log.debug("updateAreasConocimiento(Long id, List<InvencionAreaConocimientoInput> areasConocimiento) - start");

    areasConocimiento.stream().forEach(areaConocimiento -> {
      if (!areaConocimiento.getInvencionId().equals(id)) {
        throw new NoRelatedEntitiesException(InvencionAreaConocimiento.class, Invencion.class);
      }
    });

    List<InvencionAreaConocimientoOutput> returnValue = convertInvencionAreasConocimiento(
        invencionAreaConocimientoService.updateAreasConocimiento(id,
            convertInvencionAreaConocimientoInputs(areasConocimiento)));
    log.debug("updateAreasConocimiento(Long id, List<InvencionAreaConocimientoInput> areasConocimiento) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Devuelve una lista de {@link InvencionDocumento} relacionados a una invencion
   * 
   * @param id Identificador de {@link Invencion}.
   * @return la lista de entidades {@link InvencionDocumento}
   */
  @GetMapping("/{invencionId}/invenciondocumentos")
  @PreAuthorize("hasAnyAuthority('PII-INV-V', 'PII-INV-E')")
  ResponseEntity<Page<InvencionDocumentoOutput>> findInvencionDocumentosByInvencionId(@PathVariable Long invencionId,
      @RequestPageable(sort = "s") Pageable paging) {

    Page<InvencionDocumentoOutput> page = convertToPage(
        invencionDocumentoService.findByInvencionId(invencionId, paging));
    return page.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(page);
  }

  /**
   * Devuelve una lista paginada de {@link InvencionInventor} relacionados a la
   * {@link Invencion} pasada por parámetros Devuelve una lista paginada de
   * {@link InvencionInventor} activos relacionados a la {@link Invencion} pasada
   * por parámetros
   * 
   * @param id Identificador de {@link Invencion}.
   * @return Lista de entidades {@link InvencionInventor} paginadas
   */
  @GetMapping(PATH_INVENCION_INVENTOR)
  @PreAuthorize("hasAnyAuthority('PII-INV-V', 'PII-INV-C', 'PII-INV-E')")
  ResponseEntity<Page<InvencionInventorOutput>> findInvencionInventoresByInvencionId(@PathVariable Long invencionId,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug(
        "findInvencionInventoresByInvencionId(@PathVariable Long invencionId, @RequestParam(name = 'q', required = false) String query, @RequestPageable(sort = 's') Pageable paging) - start");

    Page<InvencionInventorOutput> page = convertInventorInvencion(
        invencionInventorService.findActivosByInvencion(invencionId, query, paging));

    log.debug(
        "findInvencionInventoresByInvencionId(@PathVariable Long invencionId, @RequestParam(name = 'q', required = false) String query, @RequestPageable(sort = 's') Pageable paging) - end");
    return page.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(page);
  }

  /**
   * Devuelve los {@link InformePatentabilidad} asociados a la {@link Invencion}
   * con el id indicado
   * 
   * @param id Identificador de {@link Invencion}
   * @return {@link InformePatentabilidad} correspondientes al id de la
   *         {@link Invencion}
   */
  @GetMapping(PATH_INFORMESPATENTABILIDAD)
  @PreAuthorize("hasAnyAuthority('PII-INV-E', 'PII-INV-V')")

  public List<InformePatentabilidadOutput> findInformesPatentabilidad(@PathVariable Long id) {
    log.debug("findInformesPatentabilidad(@PathVariable Long id) - start");
    List<InformePatentabilidadOutput> returnValue = convertInformesPatentabilidad(
        informePatentabilidadService.findByInvencion(id));
    log.debug("findInformesPatentabilidad(@PathVariable Long id) - end");
    return returnValue;
  }

  @PatchMapping(PATH_INVENCION_INVENTOR)
  @PreAuthorize("hasAnyAuthority('PII-INV-C', 'PII-INV-E')")
  ResponseEntity<Page<InvencionInventorOutput>> updateInventoresByInvencionId(@PathVariable Long invencionId,
      @Valid @RequestBody List<InvencionInventorInput> invencionInventoresInput,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {

    this.service.findById(invencionId);
    List<InvencionInventor> inventores = convertInvencionInventoresInput(invencionId, invencionInventoresInput);
    this.invencionInventorService.saveUpdateOrDeleteBatchMode(invencionId, inventores);

    return this.findInvencionInventoresByInvencionId(invencionId, query, paging);
  }

  /**
   * Devuelve una lista de {@link SolicitudProteccion} relacionados a una
   * invencion
   * 
   * @param id Identificador de {@link Invencion}.
   * @return la lista de entidades {@link SolicitudProteccion}
   */
  @GetMapping("/{invencionId}/solicitudesproteccion")
  @PreAuthorize("hasAuthority('PII-INV-E')")
  ResponseEntity<Page<SolicitudProteccionOutput>> findSolicitudesProteccionByInvencionId(@PathVariable Long invencionId,
      @RequestPageable(sort = "s") Pageable paging) {

    Page<SolicitudProteccionOutput> page = convertToPageSolicitudProteccion(
        solicitudProteccionService.findByInvencionId(invencionId, paging));

    return page.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(page);
  }

  /**
   * Devuelve los {@link InvencionGasto} asociados a la {@link Invencion} con el
   * id indicado
   * 
   * @param invencionId Identificador de {@link Invencion}
   * @return {@link InvencionGasto} correspondientes al id de la {@link Invencion}
   */
  @GetMapping(PATH_INVENCION_GASTO)
  @PreAuthorize("hasAnyAuthority('PII-INV-V', 'PII-INV-E')")
  public List<InvencionGastoOutput> findInvencionGastosByInvencionId(@PathVariable Long invencionId) {
    log.debug("findInvencionGastosByInvencionId(@PathVariable Long id) - start");
    List<InvencionGastoOutput> returnValue = convertInvencionGasto(
        invencionGastoService.findByInvencionId(invencionId));
    log.debug("findInvencionGastosByInvencionId(@PathVariable Long id) - end");
    return returnValue;
  }

  private InvencionOutput convert(Invencion invencion) {
    return modelMapper.map(invencion, InvencionOutput.class);
  }

  private InvencionInventorOutput convert(InvencionInventor invencionInventor) {
    return modelMapper.map(invencionInventor, InvencionInventorOutput.class);
  }

  private Invencion convert(InvencionInput invencionInput) {
    return convert(null, invencionInput);
  }

  private Invencion convert(Long id, InvencionInput invencionInput) {
    Invencion invencion = modelMapper.map(invencionInput, Invencion.class);
    invencion.setId(id);
    return invencion;
  }

  private Page<InvencionOutput> convert(Page<Invencion> page) {
    List<InvencionOutput> content = page.getContent().stream().map((invencion) -> convert(invencion))
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private InvencionSectorAplicacion convert(Long id, InvencionSectorAplicacionInput input) {
    InvencionSectorAplicacion entity = modelMapper.map(input, InvencionSectorAplicacion.class);
    entity.setId(id);
    return entity;
  }

  private InvencionSectorAplicacionOutput convert(InvencionSectorAplicacion entity) {
    return modelMapper.map(entity, InvencionSectorAplicacionOutput.class);
  }

  private InvencionSectorAplicacion convert(InvencionSectorAplicacionInput input) {
    return convert(null, input);
  }

  private List<InvencionSectorAplicacionOutput> convertInvencionSectoresAplicacion(
      List<InvencionSectorAplicacion> entities) {
    return entities.stream().map((entity) -> convert(entity)).collect(Collectors.toList());
  }

  private List<InvencionSectorAplicacion> convertInvencionSectorAplicacionInputs(
      List<InvencionSectorAplicacionInput> inputs) {
    return inputs.stream().map((input) -> convert(input)).collect(Collectors.toList());
  }

  private InvencionDocumentoOutput convert(InvencionDocumento invencionDocumento) {
    return modelMapper.map(invencionDocumento, InvencionDocumentoOutput.class);
  }

  private Page<InvencionDocumentoOutput> convertToPage(Page<InvencionDocumento> page) {
    List<InvencionDocumentoOutput> content = page.getContent().stream()
        .map((invencionDocumento) -> convert(invencionDocumento)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private InvencionAreaConocimientoOutput convert(InvencionAreaConocimiento entity) {
    return modelMapper.map(entity, InvencionAreaConocimientoOutput.class);
  }

  private InvencionAreaConocimiento convert(Long id, InvencionAreaConocimientoInput input) {
    InvencionAreaConocimiento entity = modelMapper.map(input, InvencionAreaConocimiento.class);
    entity.setId(id);
    return entity;
  }

  private InvencionAreaConocimiento convert(InvencionAreaConocimientoInput input) {
    return convert(null, input);
  }

  private List<InvencionAreaConocimientoOutput> convertInvencionAreasConocimiento(
      List<InvencionAreaConocimiento> entities) {
    return entities.stream().map((entity) -> convert(entity)).collect(Collectors.toList());
  }

  private List<InvencionAreaConocimiento> convertInvencionAreaConocimientoInputs(
      List<InvencionAreaConocimientoInput> inputs) {
    return inputs.stream().map((input) -> convert(input)).collect(Collectors.toList());
  }

  private List<InformePatentabilidadOutput> convertInformesPatentabilidad(List<InformePatentabilidad> entities) {
    return entities.stream().map((entity) -> convert(entity)).collect(Collectors.toList());
  }

  private InformePatentabilidadOutput convert(InformePatentabilidad entity) {
    return modelMapper.map(entity, InformePatentabilidadOutput.class);
  }

  private SolicitudProteccionOutput convert(SolicitudProteccion solicitudProteccion) {
    return modelMapper.map(solicitudProteccion, SolicitudProteccionOutput.class);
  }

  private Page<SolicitudProteccionOutput> convertToPageSolicitudProteccion(Page<SolicitudProteccion> page) {
    List<SolicitudProteccionOutput> content = page.getContent().stream()
        .map((solicitudProteccion) -> convert(solicitudProteccion)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private Page<InvencionInventorOutput> convertInventorInvencion(Page<InvencionInventor> page) {
    List<InvencionInventorOutput> content = page.getContent().stream().map((invencion) -> convert(invencion))
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private InvencionInventor convert(Long id, InvencionInventorInput input) {
    InvencionInventor entity = modelMapper.map(input, InvencionInventor.class);
    entity.setInvencionId(id);
    return entity;
  }

  private List<InvencionInventor> convertInvencionInventoresInput(Long id, List<InvencionInventorInput> inputs) {
    return inputs.stream().map((input) -> convert(id, input)).collect(Collectors.toList());
  }

  private List<InvencionGastoOutput> convertInvencionGasto(List<InvencionGasto> entities) {
    return entities.stream().map((entity) -> convert(entity)).collect(Collectors.toList());
  }

  private InvencionGastoOutput convert(InvencionGasto entity) {
    return modelMapper.map(entity, InvencionGastoOutput.class);
  }
}
