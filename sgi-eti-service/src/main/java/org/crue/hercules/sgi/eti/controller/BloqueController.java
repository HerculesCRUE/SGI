package org.crue.hercules.sgi.eti.controller;

import org.crue.hercules.sgi.eti.dto.ApartadoTreeOutput;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.service.ApartadoService;
import org.crue.hercules.sgi.eti.service.BloqueService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * BloqueController
 */
@RestController
@RequestMapping(BloqueController.REQUEST_MAPPING)
@Slf4j
public class BloqueController {

  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "bloques";
  public static final String PATH_COMENTARIOS_GENERALES = PATH_DELIMITER + "comentarios-generales";

  public static final String PATH_ID = PATH_DELIMITER + "{id}";
  public static final String PATH_APARTADOS = PATH_ID + PATH_DELIMITER + "apartados";
  public static final String PATH_APARTADOS_TREE = PATH_ID + PATH_DELIMITER + "apartados-tree";
  public static final String PATH_FORMULARIO = PATH_DELIMITER + "{idFormulario}" + PATH_DELIMITER + "formulario";

  /** Bloque service */
  private final BloqueService service;

  /** Apartado service */
  private ApartadoService apartadoService;

  /**
   * Instancia un nuevo BloqueController.
   * 
   * @param service         BloqueService
   * @param apartadoService ApartadoService
   */
  public BloqueController(BloqueService service, ApartadoService apartadoService) {
    log.debug("BloqueController(BloqueService service, ApartadoService apartadoService) - start");
    this.service = service;
    this.apartadoService = apartadoService;
    log.debug("BloqueController(BloqueService service, ApartadoService apartadoService) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Bloque}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   * @return el listado de entidades {@link Bloque} paginadas y filtradas.
   */
  @GetMapping()
  public ResponseEntity<Page<Bloque>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<Bloque> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link Bloque} con el id indicado.
   * 
   * @param id Identificador de {@link Bloque}.
   * @return {@link Bloque} correspondiente al id.
   */
  @GetMapping(PATH_ID)
  public Bloque one(@PathVariable Long id) {
    log.debug("Bloque one(Long id) - start");
    Bloque returnValue = service.findById(id);
    log.debug("Bloque one(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene las entidades {@link Apartado} por el id de su {@link Bloque}.
   * 
   * @param id     El id de la entidad {@link Bloque}.
   * @param paging pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  @GetMapping(PATH_APARTADOS)
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR', 'ETI-MEM-INV-ER')")
  public ResponseEntity<Page<Apartado>> getApartados(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("getApartados(Long id, Pageable paging - start");
    Page<Apartado> page = apartadoService.findByBloqueId(id, paging);
    log.debug("getApartados(Long id, Pageable paging - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve los bloques a través del id del formulario.
   * 
   * @deprecated Usar {@link FormularioController#getBloques(Long, Pageable)}
   * 
   * @param idFormulario Identificador de {@link Formulario}.
   * @param paging       pageable
   * @return el listado de entidades {@link Bloque} paginadas.
   */
  @GetMapping(PATH_FORMULARIO)
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-eti')) or hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-PEV-INV-VR')")
  @Deprecated
  public Page<Bloque> findByFormularioId(@PathVariable Long idFormulario,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("Bloque findByFormularioId(Long idFormulario, Pageable paging) - start");
    Page<Bloque> returnValue = service.findByFormularioId(idFormulario, paging);
    log.debug("Bloque findByFormularioId(Long idFormulario, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene el {@link Bloque} de comentarios generales.
   *
   * @return el {@link Bloque}.
   */
  @GetMapping(PATH_COMENTARIOS_GENERALES)
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-eti')) or hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-PEV-INV-C', 'ETI-PEV-INV-ER')")
  public Bloque getBloqueComentariosGenerales() {
    log.debug("getBloqueComentariosGenerales() - start");
    Bloque returnValue = service.getBloqueComentariosGenerales();
    log.debug("getBloqueComentariosGenerales() - end");
    return returnValue;
  }

  /**
   * Obtiene las entidades {@link Apartado} por el id de su {@link Bloque}.
   * 
   * @param id     El id de la entidad {@link Bloque}.
   * @param paging pageable
   * @return el listado de entidades {@link Apartado} paginadas.
   */
  @GetMapping(PATH_APARTADOS_TREE)
  @PreAuthorize("isClient() and hasAuthority('SCOPE_sgi-eti')")
  public ResponseEntity<Page<ApartadoTreeOutput>> getApartadosTree(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("getApartados(Long id, Pageable paging) - start");
    Page<ApartadoTreeOutput> page = apartadoService.findApartadosTreeByBloqueId(id, paging);
    log.debug("getApartados(Long id, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

}
