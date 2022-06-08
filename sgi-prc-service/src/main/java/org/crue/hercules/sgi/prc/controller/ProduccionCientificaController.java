package org.crue.hercules.sgi.prc.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.prc.dto.AcreditacionOutput;
import org.crue.hercules.sgi.prc.dto.ActividadOutput;
import org.crue.hercules.sgi.prc.dto.ActividadResumen;
import org.crue.hercules.sgi.prc.dto.AutorOutput;
import org.crue.hercules.sgi.prc.dto.CampoProduccionCientificaOutput;
import org.crue.hercules.sgi.prc.dto.ComiteEditorialOutput;
import org.crue.hercules.sgi.prc.dto.ComiteEditorialResumen;
import org.crue.hercules.sgi.prc.dto.CongresoOutput;
import org.crue.hercules.sgi.prc.dto.CongresoResumen;
import org.crue.hercules.sgi.prc.dto.DireccionTesisOutput;
import org.crue.hercules.sgi.prc.dto.DireccionTesisResumen;
import org.crue.hercules.sgi.prc.dto.EstadoProduccionCientificaInput;
import org.crue.hercules.sgi.prc.dto.IndiceImpactoOutput;
import org.crue.hercules.sgi.prc.dto.ObraArtisticaOutput;
import org.crue.hercules.sgi.prc.dto.ObraArtisticaResumen;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaOutput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaOutput.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.dto.ProyectoOutput;
import org.crue.hercules.sgi.prc.dto.PublicacionOutput;
import org.crue.hercules.sgi.prc.dto.PublicacionResumen;
import org.crue.hercules.sgi.prc.dto.ValorCampoOutput;
import org.crue.hercules.sgi.prc.exceptions.NoRelatedEntitiesException;
import org.crue.hercules.sgi.prc.model.Acreditacion;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.IndiceImpacto;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.Proyecto;
import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.service.AcreditacionService;
import org.crue.hercules.sgi.prc.service.AutorService;
import org.crue.hercules.sgi.prc.service.CampoProduccionCientificaService;
import org.crue.hercules.sgi.prc.service.IndiceImpactoService;
import org.crue.hercules.sgi.prc.service.ProduccionCientificaService;
import org.crue.hercules.sgi.prc.service.ProyectoService;
import org.crue.hercules.sgi.prc.service.ValorCampoService;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ProduccionCientificaController
 */
@RestController
@RequestMapping(ProduccionCientificaController.MAPPING)
@Slf4j
@RequiredArgsConstructor
public class ProduccionCientificaController {
  public static final String PATH_DELIMITER = "/";
  public static final String MAPPING = PATH_DELIMITER + "producciones-cientificas";
  public static final String PATH_PUBLICACIONES = PATH_DELIMITER + "publicaciones";
  public static final String PATH_COMITES_EDITORIALES = PATH_DELIMITER + "comites-editoriales";
  public static final String PATH_CONGRESOS = PATH_DELIMITER + "congresos";
  public static final String PATH_OBRAS_ARTISTICAS = PATH_DELIMITER + "obras-artisticas";
  public static final String PATH_ACTIVIDADES = PATH_DELIMITER + "actividades";
  public static final String PATH_DIRECCIONES_TESIS = PATH_DELIMITER + "direcciones-tesis";
  public static final String PATH_INDICES_IMPACTO = PATH_DELIMITER + "{id}/indices-impacto";
  public static final String PATH_AUTORES = PATH_DELIMITER + "{id}/autores";
  public static final String PATH_PROYECTOS = PATH_DELIMITER + "{id}/proyectos";
  public static final String PATH_ACREDITACIONES = PATH_DELIMITER + "{id}/acreditaciones";
  public static final String PATH_CAMPOS = PATH_DELIMITER + "{id}/campos";
  public static final String PATH_VALORES = PATH_CAMPOS + PATH_DELIMITER + "{campoId}/valores";
  public static final String PATH_MODIFICABLE = PATH_DELIMITER + "{id}/modificable";

  private final ProduccionCientificaService service;
  private final IndiceImpactoService indiceImpactoService;
  private final AutorService autorService;
  private final ProyectoService proyectoService;
  private final AcreditacionService acreditacionService;
  private final CampoProduccionCientificaService campoProduccionCientificaService;
  private final ValorCampoService valorCampoService;

  private final ModelMapper modelMapper;

  /**
   * Devuelve una lista paginada y filtrada {@link PublicacionOutput}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging la información de la paginación.
   * @return el listado de entidades {@link PublicacionOutput} paginadas y
   *         filtradas.
   */
  @GetMapping(PATH_PUBLICACIONES)
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E', 'PRC-VAL-INV-ER')")
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
   * Devuelve una lista paginada y filtrada {@link ComiteEditorialOutput}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging la información de la paginación.
   * @return el listado de entidades {@link ComiteEditorialOutput} paginadas y
   *         filtradas.
   */
  @GetMapping(PATH_COMITES_EDITORIALES)
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E')")
  public ResponseEntity<Page<ComiteEditorialOutput>> findAllComitesEditoriales(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllComitesEditoriales(String query, Pageable paging) - start");
    Page<ComiteEditorialResumen> page = service.findAllComitesEditoriales(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllComitesEditoriales(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllComitesEditoriales(String query, Pageable paging) - end");
    return new ResponseEntity<>(convertComiteEditorialResumen(page), HttpStatus.OK);
  }

  private ComiteEditorialOutput convertComiteEditorialResumen(ComiteEditorialResumen comiteEditorialResumen) {
    ComiteEditorialOutput output = modelMapper.map(comiteEditorialResumen, ComiteEditorialOutput.class);
    output.setEstado(new EstadoProduccionCientifica());
    output.getEstado().setEstado(comiteEditorialResumen.getEstado());
    return output;
  }

  private Page<ComiteEditorialOutput> convertComiteEditorialResumen(Page<ComiteEditorialResumen> page) {
    List<ComiteEditorialOutput> content = page.getContent().stream()
        .map(this::convertComiteEditorialResumen)
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  /**
   * Devuelve una lista paginada y filtrada {@link CongresoOutput}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging la información de la paginación.
   * @return el listado de entidades {@link CongresoOutput} paginadas y
   *         filtradas.
   */
  @GetMapping(PATH_CONGRESOS)
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E', 'PRC-VAL-INV-ER')")
  public ResponseEntity<Page<CongresoOutput>> findAllCongresos(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllCongresos(String query, Pageable paging) - start");
    Page<CongresoResumen> page = service.findAllCongresos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllCongresos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllCongresos(String query, Pageable paging) - end");
    return new ResponseEntity<>(convertCongresoResumen(page), HttpStatus.OK);
  }

  private CongresoOutput convertCongresoResumen(CongresoResumen congresoResumen) {
    CongresoOutput output = modelMapper.map(congresoResumen, CongresoOutput.class);
    output.setEstado(new EstadoProduccionCientifica());
    output.getEstado().setEstado(congresoResumen.getEstado());
    return output;
  }

  private Page<CongresoOutput> convertCongresoResumen(Page<CongresoResumen> page) {
    List<CongresoOutput> content = page.getContent().stream()
        .map(this::convertCongresoResumen)
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  /**
   * Devuelve una lista paginada y filtrada {@link ObraArtisticaOutput}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging la información de la paginación.
   * @return el listado de entidades {@link ObraArtisticaOutput} paginadas y
   *         filtradas.
   */
  @GetMapping(PATH_OBRAS_ARTISTICAS)
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E')")
  public ResponseEntity<Page<ObraArtisticaOutput>> findAllObrasArtisticas(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllObrasArtisticas(String query, Pageable paging) - start");
    Page<ObraArtisticaResumen> page = service.findAllObrasArtisticas(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllObrasArtisticas(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllObrasArtisticas(String query, Pageable paging) - end");
    return new ResponseEntity<>(convertObraArtisticaResumen(page), HttpStatus.OK);
  }

  private ObraArtisticaOutput convertObraArtisticaResumen(ObraArtisticaResumen obraArtisticaResumen) {
    ObraArtisticaOutput output = modelMapper.map(obraArtisticaResumen, ObraArtisticaOutput.class);
    output.setEstado(new EstadoProduccionCientifica());
    output.getEstado().setEstado(obraArtisticaResumen.getEstado());
    return output;
  }

  private Page<ObraArtisticaOutput> convertObraArtisticaResumen(Page<ObraArtisticaResumen> page) {
    List<ObraArtisticaOutput> content = page.getContent().stream()
        .map(
            this::convertObraArtisticaResumen)
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  /**
   * Devuelve una lista paginada y filtrada {@link ActividadOutput}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging la información de la paginación.
   * @return el listado de entidades {@link ActividadOutput} paginadas y
   *         filtradas.
   */
  @GetMapping(PATH_ACTIVIDADES)
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E')")
  public ResponseEntity<Page<ActividadOutput>> findAllActividades(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllActividades(String query, Pageable paging) - start");
    Page<ActividadResumen> page = service.findAllActividades(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllActividades(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllActividades(String query, Pageable paging) - end");
    return new ResponseEntity<>(convertActividadResumen(page), HttpStatus.OK);
  }

  private ActividadOutput convertActividadResumen(ActividadResumen actividadResumen) {
    ActividadOutput output = modelMapper.map(actividadResumen, ActividadOutput.class);
    output.setEstado(new EstadoProduccionCientifica());
    output.getEstado().setEstado(actividadResumen.getEstado());
    return output;
  }

  private Page<ActividadOutput> convertActividadResumen(Page<ActividadResumen> page) {
    List<ActividadOutput> content = page.getContent().stream()
        .map(this::convertActividadResumen)
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  /**
   * Devuelve una lista paginada y filtrada {@link DireccionTesisOutput}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging la información de la paginación.
   * @return el listado de entidades {@link DireccionTesisOutput} paginadas y
   *         filtradas.
   */
  @GetMapping(PATH_DIRECCIONES_TESIS)
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E')")
  public ResponseEntity<Page<DireccionTesisOutput>> findAllDireccionesTesis(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllDireccionesTesis(String query, Pageable paging) - start");
    Page<DireccionTesisResumen> page = service.findAllDireccionesTesis(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllDireccionesTesis(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllDireccionesTesis(String query, Pageable paging) - end");
    return new ResponseEntity<>(convertDireccionTesisResumen(page), HttpStatus.OK);
  }

  private DireccionTesisOutput convertDireccionTesisResumen(DireccionTesisResumen actividadResumen) {
    DireccionTesisOutput output = modelMapper.map(actividadResumen, DireccionTesisOutput.class);
    output.setEstado(new EstadoProduccionCientifica());
    output.getEstado().setEstado(actividadResumen.getEstado());
    return output;
  }

  private Page<DireccionTesisOutput> convertDireccionTesisResumen(Page<DireccionTesisResumen> page) {
    List<DireccionTesisOutput> content = page.getContent().stream()
        .map(this::convertDireccionTesisResumen)
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  /**
   * Devuelve el {@link ProduccionCientificaOutput} con el id indicado.
   * 
   * @param id Identificador de {@link ProduccionCientifica}.
   * @return {@link ProduccionCientificaOutput} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E', 'PRC-VAL-INV-ER')")
  public ProduccionCientificaOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    service.checkAccesibleByInvestigador(id);

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
  @PreAuthorize("hasAnyAuthority('PRC-VAL-E', 'PRC-VAL-INV-ER')")
  public ProduccionCientificaOutput validar(@PathVariable Long id) {
    log.debug("validar(Long id) - start");
    service.checkAccesibleByInvestigador(id);

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
  @PreAuthorize("hasAnyAuthority('PRC-VAL-E', 'PRC-VAL-INV-ER')")
  public ProduccionCientificaOutput rechazar(@PathVariable Long id,
      @Valid @RequestBody EstadoProduccionCientificaInput estadoProduccionCientifica) {
    log.debug("rechazar(Long id, EstadoProduccionCientificaInput estadoProduccionCientifica) - start");
    service.checkAccesibleByInvestigador(id);

    ProduccionCientifica returnValue = service.cambiarEstado(id, TipoEstadoProduccion.RECHAZADO,
        estadoProduccionCientifica.getComentario());
    log.debug("rechazar(Long id, EstadoProduccionCientificaInput estadoProduccionCientifica) - end");
    return convert(returnValue);
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la
   * {@link ProduccionCientifica} puede ser consultada por un investigador.
   *
   * @param id Id de la {@link ProduccionCientifica}.
   * @return HTTP-200 si puede ser modificada / HTTP-204 si no puede ser
   *         consultada
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthority('PRC-VAL-INV-ER')")
  public ResponseEntity<Void> accesibleByInvestigador(@PathVariable Long id) {
    log.debug("registrable(Long id) - start");
    boolean returnValue = service.accesibleByInvestigador(id);
    log.debug("registrable(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la
   * {@link ProduccionCientifica} puede ser editada por un investigador.
   *
   * @param id Id de la {@link ProduccionCientifica}.
   * @return HTTP-200 si puede ser modificada / HTTP-204 si no puede ser
   *         modificada
   */
  @RequestMapping(path = PATH_MODIFICABLE, method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthority('PRC-VAL-INV-ER')")
  public ResponseEntity<Void> editableByInvestigador(@PathVariable Long id) {
    log.debug("registrable(Long id) - start");
    boolean returnValue = service.editableByInvestigador(id);
    log.debug("registrable(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
        .map(this::convertPublicacionResumen).collect(Collectors.toList());

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
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E', 'PRC-VAL-INV-ER')")
  public ResponseEntity<Page<IndiceImpactoOutput>> findIndicesImpacto(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findIndicesImpacto(Long id, String query, Pageable paging) - start");
    service.checkAccesibleByInvestigador(id);

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
        .map(this::convert)
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
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E', 'PRC-VAL-INV-ER')")
  public ResponseEntity<Page<AutorOutput>> findAutores(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAutores(Long id, String query, Pageable paging) - start");
    service.checkAccesibleByInvestigador(id);

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
        .map(this::convert)
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
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E', 'PRC-VAL-INV-ER')")
  public ResponseEntity<Page<ProyectoOutput>> findProyectos(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findProyectos(Long id, String query, Pageable paging) - start");
    service.checkAccesibleByInvestigador(id);

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
        .map(this::convert)
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
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E', 'PRC-VAL-INV-ER')")
  public ResponseEntity<Page<AcreditacionOutput>> findAcreditaciones(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAcreditaciones(Long id, String query, Pageable paging) - start");
    service.checkAccesibleByInvestigador(id);

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
        .map(this::convert)
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private AcreditacionOutput convert(Acreditacion acreditacion) {
    return modelMapper.map(acreditacion, AcreditacionOutput.class);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link CampoProduccionCientifica}.
   * 
   * @param id     el id de {@link ProduccionCientifica}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link CampoProduccionCientifica}
   *         paginadas y filtradas.
   */
  @GetMapping(PATH_CAMPOS)
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E', 'PRC-VAL-INV-ER')")
  public ResponseEntity<Page<CampoProduccionCientificaOutput>> findCampos(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    service.checkAccesibleByInvestigador(id);

    Page<CampoProduccionCientifica> page = campoProduccionCientificaService.findAllByProduccionCientificaId(id, query,
        paging);
    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  private Page<CampoProduccionCientificaOutput> convert(Page<CampoProduccionCientifica> page) {
    List<CampoProduccionCientificaOutput> content = page.getContent().stream()
        .map(this::convert).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private CampoProduccionCientificaOutput convert(CampoProduccionCientifica campoProduccionCientifica) {
    return modelMapper.map(campoProduccionCientifica, CampoProduccionCientificaOutput.class);
  }

  /**
   * Obtiene todos los {@link ValorCampo} del {@link CampoProduccionCientifica}
   * con el id indicado paginadas y/o filtradas.
   *
   * @param id      el id de {@link ProduccionCientifica}.
   * @param campoId el id de {@link CampoProduccionCientifica}.
   * @param query   la información del filtro.
   * @param paging  la información de la paginación.
   * @return listado de {@link ValorCampo} paginadas y/o filtradas.
   */
  @GetMapping(PATH_VALORES)
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E', 'PRC-VAL-INV-ER')")
  public ResponseEntity<Page<ValorCampoOutput>> findValores(@PathVariable Long id, @PathVariable Long campoId,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findValores(Long id, Long campoId, String query, Pageable paging) - start");
    service.checkAccesibleByInvestigador(id);

    final CampoProduccionCientifica relatedCampoProduccionCientifica = campoProduccionCientificaService
        .findById(campoId);
    if (!id.equals(relatedCampoProduccionCientifica.getProduccionCientificaId())) {
      throw new NoRelatedEntitiesException(CampoProduccionCientifica.class, ProduccionCientifica.class);
    }

    Page<ValorCampo> page = valorCampoService.findAllByCampoProduccionCientificaId(campoId, query, paging);
    if (page.isEmpty()) {
      log.debug("findValores(Long id, Long campoId, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findValores(Long id, Long campoId, String query, Pageable paging) - end");
    return new ResponseEntity<>(convertValorCampo(page), HttpStatus.OK);
  }

  private Page<ValorCampoOutput> convertValorCampo(Page<ValorCampo> page) {
    List<ValorCampoOutput> content = page.getContent().stream()
        .map(this::convert).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private ValorCampoOutput convert(ValorCampo valorCampo) {
    return modelMapper.map(valorCampo, ValorCampoOutput.class);
  }
}
