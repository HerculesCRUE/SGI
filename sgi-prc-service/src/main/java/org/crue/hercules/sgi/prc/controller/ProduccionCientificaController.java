package org.crue.hercules.sgi.prc.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.prc.dto.AcreditacionOutput;
import org.crue.hercules.sgi.prc.dto.AutorOutput;
import org.crue.hercules.sgi.prc.dto.EstadoProduccionCientificaInput;
import org.crue.hercules.sgi.prc.dto.IndiceImpactoOutput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaOutput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaOutput.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.dto.ProyectoOutput;
import org.crue.hercules.sgi.prc.dto.PublicacionOutput;
import org.crue.hercules.sgi.prc.dto.PublicacionResumen;
import org.crue.hercules.sgi.prc.model.Acreditacion;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.IndiceImpacto;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.Proyecto;
import org.crue.hercules.sgi.prc.service.AcreditacionService;
import org.crue.hercules.sgi.prc.service.AutorService;
import org.crue.hercules.sgi.prc.service.IndiceImpactoService;
import org.crue.hercules.sgi.prc.service.ProduccionCientificaService;
import org.crue.hercules.sgi.prc.service.ProyectoService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ProduccionCientificaController
 */
@RestController
@RequestMapping(ProduccionCientificaController.MAPPING)
@Slf4j
public class ProduccionCientificaController {
  public static final String MAPPING = "/producciones-cientificas";
  public static final String PATH_INDICES_IMPACTO = "/{id}/indices-impacto";
  public static final String PATH_AUTORES = "/{id}/autores";
  public static final String PATH_PROYECTOS = "/{id}/proyectos";
  public static final String PATH_ACREDITACIONES = "/{id}/acreditaciones";

  private ModelMapper modelMapper;

  /** ProduccionCientifica service */
  private final ProduccionCientificaService service;
  /** IndiceImpacto service */
  private final IndiceImpactoService indiceImpactoService;
  /** Autor service */
  private final AutorService autorService;
  /** Proyecto service */
  private final ProyectoService proyectoService;
  /** Acreditacion service */
  private final AcreditacionService acreditacionService;

  /**
   * Instancia un nuevo ProduccionCientificaController.
   * 
   * @param modelMapper {@link ModelMapper}
   * @param service     {@link ProduccionCientificaService}
   */
  /**
   * 
   * @param modelMapper          {@link ModelMapper}
   * @param service              {@link ProduccionCientificaService}
   * @param indiceImpactoService {@link IndiceImpactoService}
   * @param autorService         {@link AutorService}
   * @param proyectoService      {@link ProyectoService}
   * @param acreditacionService  {@link AcreditacionService}
   */
  public ProduccionCientificaController(
      ModelMapper modelMapper,
      ProduccionCientificaService service,
      IndiceImpactoService indiceImpactoService,
      AutorService autorService,
      ProyectoService proyectoService,
      AcreditacionService acreditacionService) {
    this.modelMapper = modelMapper;
    this.service = service;
    this.indiceImpactoService = indiceImpactoService;
    this.autorService = autorService;
    this.proyectoService = proyectoService;
    this.acreditacionService = acreditacionService;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link PublicacionOutput}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging la información de la paginación.
   * @return el listado de entidades {@link PublicacionOutput} paginadas y
   *         filtradas.
   */
  @GetMapping("/publicaciones")
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E')")
  public ResponseEntity<Page<PublicacionOutput>> findAllPublicaciones(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllPublicaciones(String query, Pageable paging) - start");
    Page<PublicacionResumen> page = service.findAllPublicaciones(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllPublicaciones(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllPublicaciones(String query, Pageable paging) - end");
    return new ResponseEntity<>(convertPublicacionResumen(page), HttpStatus.OK);
  }

  /**
   * Devuelve el {@link ProduccionCientificaOutput} con el id indicado.
   * 
   * @param id Identificador de {@link ProduccionCientifica}.
   * @return {@link ProduccionCientificaOutput} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E')")
  public ProduccionCientificaOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProduccionCientifica returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Marcar la {@link ProduccionCientifica} con id indicado como validada.
   * 
   * @param id Identificador de {@link ProduccionCientifica}.
   * @return {@link ProduccionCientifica} actualizada.
   */
  @PatchMapping("/{id}/validar")
  @PreAuthorize("hasAuthority('PRC-VAL-E')")
  ProduccionCientificaOutput validar(@PathVariable Long id) {
    log.debug("validar(Long id) - start");
    ProduccionCientifica returnValue = service.cambiarEstado(id, TipoEstadoProduccion.VALIDADO, null);
    log.debug("validar(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Marcar la {@link ProduccionCientifica} con id indicado como rechazada.
   * 
   * @param id                         Identificador de
   *                                   {@link ProduccionCientifica}.
   * @param estadoProduccionCientifica Objeto con el comentario del motivo del
   *                                   rechazo
   * @return {@link ProduccionCientifica} actualizada.
   */
  @PatchMapping("/{id}/rechazar")
  @PreAuthorize("hasAuthority('PRC-VAL-E')")
  ProduccionCientificaOutput rechazar(@PathVariable Long id,
      @Valid @RequestBody EstadoProduccionCientificaInput estadoProduccionCientifica) {
    log.debug("rechazar(Long id, EstadoProduccionCientificaInput estadoProduccionCientifica) - start");
    ProduccionCientifica returnValue = service.cambiarEstado(id, TipoEstadoProduccion.RECHAZADO,
        estadoProduccionCientifica.getComentario());
    log.debug("rechazar(Long id, EstadoProduccionCientificaInput estadoProduccionCientifica) - end");
    return convert(returnValue);
  }

  private ProduccionCientificaOutput convert(ProduccionCientifica produccionCientifica) {
    return modelMapper.map(produccionCientifica, ProduccionCientificaOutput.class);
  }

  private PublicacionOutput convertPublicacionResumen(PublicacionResumen publicacionResumen) {
    PublicacionOutput output = modelMapper.map(publicacionResumen, PublicacionOutput.class);
    output.setEstado(new EstadoProduccionCientifica());
    output.getEstado().setEstado(publicacionResumen.getEstado());
    return output;
  }

  private Page<PublicacionOutput> convertPublicacionResumen(Page<PublicacionResumen> page) {
    List<PublicacionOutput> content = page.getContent().stream()
        .map(produccionCientifica -> convertPublicacionResumen(produccionCientifica)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  /**
   * Obtiene todos los {@link IndiceImpacto} del {@link ProduccionCientifica} con
   * el id indicado paginadas y/o filtradas.
   *
   * @param id     el id de {@link ProduccionCientifica}.
   * @param query  la información del filtro.
   * @param paging la información de la paginación.
   * @return listado de {@link IndiceImpacto} paginadas y/o filtradas.
   */
  @GetMapping(PATH_INDICES_IMPACTO)
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E')")
  public ResponseEntity<Page<IndiceImpactoOutput>> findIndicesImpacto(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findIndicesImpacto(Long id, String query, Pageable paging) - start");
    Page<IndiceImpacto> page = indiceImpactoService.findAllByProduccionCientificaId(id, query, paging);
    if (page.isEmpty()) {
      log.debug("findIndicesImpacto(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findIndicesImpacto(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(convertIndiceImpacto(page), HttpStatus.OK);
  }

  private Page<IndiceImpactoOutput> convertIndiceImpacto(Page<IndiceImpacto> page) {
    List<IndiceImpactoOutput> content = page.getContent().stream()
        .map((indiceImpacto) -> convert(indiceImpacto))
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private IndiceImpactoOutput convert(IndiceImpacto indiceImpacto) {
    return modelMapper.map(indiceImpacto, IndiceImpactoOutput.class);
  }

  /**
   * Obtiene todos los {@link Autor} del {@link ProduccionCientifica} con
   * el id indicado paginadas y/o filtradas.
   *
   * @param id     el id de {@link ProduccionCientifica}.
   * @param query  la información del filtro.
   * @param paging la información de la paginación.
   * @return listado de {@link Autor} paginadas y/o filtradas.
   */
  @GetMapping(PATH_AUTORES)
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E')")
  public ResponseEntity<Page<AutorOutput>> findAutores(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAutores(Long id, String query, Pageable paging) - start");
    Page<Autor> page = autorService.findAllByProduccionCientificaId(id, query, paging);
    if (page.isEmpty()) {
      log.debug("findAutores(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAutores(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(convertAutor(page), HttpStatus.OK);
  }

  private Page<AutorOutput> convertAutor(Page<Autor> page) {
    List<AutorOutput> content = page.getContent().stream()
        .map((autor) -> convert(autor))
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private AutorOutput convert(Autor autor) {
    return modelMapper.map(autor, AutorOutput.class);
  }

  /**
   * Obtiene todos los {@link Proyecto} del {@link ProduccionCientifica} con
   * el id indicado paginadas y/o filtradas.
   *
   * @param id     el id de {@link ProduccionCientifica}.
   * @param query  la información del filtro.
   * @param paging la información de la paginación.
   * @return listado de {@link Proyecto} paginadas y/o filtradas.
   */
  @GetMapping(PATH_PROYECTOS)
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E')")
  public ResponseEntity<Page<ProyectoOutput>> findProyectos(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findProyectos(Long id, String query, Pageable paging) - start");
    Page<Proyecto> page = proyectoService.findAllByProduccionCientificaId(id, query, paging);
    if (page.isEmpty()) {
      log.debug("findProyectos(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findProyectos(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(convertProyecto(page), HttpStatus.OK);
  }

  private Page<ProyectoOutput> convertProyecto(Page<Proyecto> page) {
    List<ProyectoOutput> content = page.getContent().stream()
        .map((proyecto) -> convert(proyecto))
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private ProyectoOutput convert(Proyecto proyecto) {
    return modelMapper.map(proyecto, ProyectoOutput.class);
  }

  /**
   * Obtiene todos las {@link Acreditacion} del {@link ProduccionCientifica} con
   * el id indicado paginadas y/o filtradas.
   *
   * @param id     el id de {@link ProduccionCientifica}.
   * @param query  la información del filtro.
   * @param paging la información de la paginación.
   * @return listado de {@link Acreditacion} paginadas y/o filtradas.
   */
  @GetMapping(PATH_ACREDITACIONES)
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E')")
  public ResponseEntity<Page<AcreditacionOutput>> findAcreditaciones(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAcreditaciones(Long id, String query, Pageable paging) - start");
    Page<Acreditacion> page = acreditacionService.findAllByProduccionCientificaId(id, query, paging);
    if (page.isEmpty()) {
      log.debug("findAcreditaciones(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAcreditaciones(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(convertAcreditacion(page), HttpStatus.OK);
  }

  private Page<AcreditacionOutput> convertAcreditacion(Page<Acreditacion> page) {
    List<AcreditacionOutput> content = page.getContent().stream()
        .map((acreditacion) -> convert(acreditacion))
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private AcreditacionOutput convert(Acreditacion acreditacion) {
    return modelMapper.map(acreditacion, AcreditacionOutput.class);
  }
}
