package org.crue.hercules.sgi.csp.controller.publico;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.SolicitudRrhhConverter;
import org.crue.hercules.sgi.csp.converter.SolicitudRrhhRequisitoCategoriaConverter;
import org.crue.hercules.sgi.csp.converter.SolicitudRrhhRequisitoNivelAcademicoConverter;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhInput;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhMemoriaInput;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhMemoriaOutput;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhOutput;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhRequisitoCategoriaOutput;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhRequisitoNivelAcademicoOutput;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhTutorInput;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhTutorOutput;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudRrhh;
import org.crue.hercules.sgi.csp.model.SolicitudRrhhRequisitoCategoria;
import org.crue.hercules.sgi.csp.model.SolicitudRrhhRequisitoNivelAcademico;
import org.crue.hercules.sgi.csp.service.SolicitudRrhhRequisitoCategoriaService;
import org.crue.hercules.sgi.csp.service.SolicitudRrhhRequisitoNivelAcademicoService;
import org.crue.hercules.sgi.csp.service.SolicitudRrhhService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SolicitudRrhhController
 */
@RestController
@RequestMapping(SolicitudRrhhPublicController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
@Validated
public class SolicitudRrhhPublicController {
  public static final String PATH_DELIMITER = "/";
  public static final String PATH_PUBLIC = PATH_DELIMITER + "public";
  public static final String REQUEST_MAPPING = PATH_PUBLIC + PATH_DELIMITER
      + "solicitudes/{solicitudId}/solicitudes-rrhh";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";
  public static final String PATH_MEMORIA = PATH_DELIMITER + "memoria";
  public static final String PATH_REQUISITOS_CATEGORIA = PATH_DELIMITER + "requisitos-categoria";
  public static final String PATH_REQUISITOS_NIVEL_ACADEMICO = PATH_DELIMITER + "requisitos-nivel-academico";
  public static final String PATH_TUTOR = PATH_DELIMITER + "tutor";

  private final SolicitudRrhhService service;
  private final SolicitudRrhhRequisitoCategoriaService requisitoCategoriaService;
  private final SolicitudRrhhRequisitoNivelAcademicoService requisitoNivelAcademicoService;

  private final SolicitudRrhhConverter converter;
  private final SolicitudRrhhRequisitoCategoriaConverter requisitoCategoriaConverter;
  private final SolicitudRrhhRequisitoNivelAcademicoConverter requisitoNivelAcademicoConverter;

  /**
   * Crea nuevo {@link SolicitudRrhh}
   * 
   * @param solicitudRrhh {@link SolicitudRrhh} que se quiere crear.
   * @param solicitudId   Identificador de {@link Solicitud}.
   * @return Nuevo {@link SolicitudRrhh} creado.
   */
  @PostMapping
  public ResponseEntity<SolicitudRrhhOutput> create(@Valid @RequestBody SolicitudRrhhInput solicitudRrhh,
      @PathVariable String solicitudId) {
    log.debug("create(String solicitudId, SolicitudRrhhInput solicitudRrhh) - start");
    SolicitudRrhhOutput returnValue = converter.convert(service.createByExternalUser(
        solicitudId, converter.convert(solicitudRrhh)));
    log.debug("create(String solicitudId, SolicitudRrhhInput solicitudRrhh) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link SolicitudRrhh}.
   * 
   * @param solicitudRrhh {@link SolicitudRrhh} a actualizar.
   * @param solicitudId   Identificador de {@link Solicitud}.
   * @param id            Identificador {@link SolicitudRrhh} a actualizar.
   * @return {@link SolicitudRrhh} actualizado
   */
  @PutMapping(PATH_ID)
  public SolicitudRrhhOutput update(@Valid @RequestBody SolicitudRrhhInput solicitudRrhh,
      @PathVariable String solicitudId,
      @PathVariable Long id) {
    log.debug("update(SolicitudRrhhInput solicitudRrhh, String solicitudId, Long id) - start");
    SolicitudRrhhOutput returnValue = converter
        .convert(service.updateByExternalUser(solicitudId, converter.convert(id, solicitudRrhh)));
    log.debug("update(SolicitudRrhhInput solicitudRrhh, String solicitudId, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link SolicitudRrhhTutorOutput} de {@link SolicitudRrhh} con el
   * id indicado.
   * 
   * @param solicitudId Identificador de {@link Solicitud}.
   * @return {@link SolicitudRrhhTutorOutput} correspondiente al id
   */
  @GetMapping(PATH_TUTOR)
  public ResponseEntity<SolicitudRrhhTutorOutput> findTutor(@PathVariable String solicitudId) {
    log.debug("findTutor(String solicitudId) - start");
    SolicitudRrhh returnValue = service.findBySolicitudPublicId(solicitudId);
    log.debug("findTutor(String solicitudId) - end");
    return returnValue == null ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
        : new ResponseEntity<>(converter.convertRrhhTutorOutput(returnValue), HttpStatus.OK);
  }

  /**
   * Actualiza el tutor de la {@link SolicitudRrhh} con id indicado.
   * 
   * @param solicitudId Identificador de {@link SolicitudRrhh}.
   * @param tutor       el {@link SolicitudRrhhTutorInput}.
   * @return {@link SolicitudRrhhTutorOutput} actualizado.
   */
  @PatchMapping(PATH_TUTOR)
  public SolicitudRrhhTutorOutput updateTutor(@PathVariable String solicitudId,
      @Valid @RequestBody SolicitudRrhhTutorInput tutor) {
    log.debug("updateTutor(Long solicitudId, SolicitudRrhhTutorInput tutor) - start");
    SolicitudRrhhTutorOutput returnValue = converter
        .convertRrhhTutorOutput(service.updateTutorByExternalUser(solicitudId, converter.convert(null, tutor)));
    log.debug("updateTutor(Long solicitudId, SolicitudRrhhTutorInput tutor) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link SolicitudRrhhMemoriaOutput} de {@link SolicitudRrhh} con
   * el id indicado.
   * 
   * @param solicitudId Identificador de {@link SolicitudRrhh}.
   * @return {@link SolicitudRrhhMemoriaOutput} correspondiente al id
   */
  @GetMapping(PATH_MEMORIA)
  public ResponseEntity<SolicitudRrhhMemoriaOutput> findMemoriaBySolicitudRrhhId(@PathVariable String solicitudId) {
    log.debug("findMemoriaBySolicitudRrhhId(String solicitudId) - start");
    SolicitudRrhh returnValue = service.findBySolicitudPublicId(solicitudId);
    log.debug("findMemoriaBySolicitudRrhhId(String solicitudId) - end");
    return returnValue == null ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
        : new ResponseEntity<>(converter.convertRrhhMemoriaOutput(returnValue), HttpStatus.OK);
  }

  /**
   * Actualiza la memoria de la {@link SolicitudRrhh} con id indicado.
   * 
   * @param solicitudId Identificador de {@link SolicitudRrhh}.
   * @param memoria     el {@link SolicitudRrhhMemoriaInput}.
   * @return {@link SolicitudRrhhMemoriaOutput} actualizado.
   */
  @PatchMapping(PATH_MEMORIA)
  public SolicitudRrhhMemoriaOutput updateMemoria(@PathVariable String solicitudId,
      @Valid @RequestBody SolicitudRrhhMemoriaInput memoria) {
    log.debug("updateMemoria(String solicitudId, SolicitudRrhhTutorInput memoria) - start");
    SolicitudRrhhMemoriaOutput returnValue = converter
        .convertRrhhMemoriaOutput(service.updateMemoriaByExternalUser(solicitudId, converter.convert(null, memoria)));
    log.debug("updateMemoria(String solicitudId, SolicitudRrhhTutorInput memoria) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link SolicitudRrhhRequisitoCategoria} del {@link SolicitudRrhh}.
   * 
   * @param solicitudId Identificador del {@link SolicitudRrhh}.
   * @param query       filtro de búsqueda.
   * @param paging      pageable.
   * @return el listado de entidades {@link SolicitudRrhhRequisitoCategoria}
   *         paginadas y filtradas del {@link SolicitudRrhh}.
   */
  @GetMapping(PATH_REQUISITOS_CATEGORIA)
  public ResponseEntity<Page<SolicitudRrhhRequisitoCategoriaOutput>> findAllRequisitosCategoria(
      @PathVariable String solicitudId,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllRequisitosCategoria(String solicitudId, String query, Pageable paging) - start");
    Page<SolicitudRrhhRequisitoCategoriaOutput> page = requisitoCategoriaConverter
        .convert(requisitoCategoriaService.findAllBySolicitudPublicId(solicitudId, query, paging));
    log.debug("findAllRequisitosCategoria(String solicitudId, String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link SolicitudRrhhRequisitoNivelAcademico} del {@link SolicitudRrhh}.
   * 
   * @param solicitudId Identificador del {@link SolicitudRrhh}.
   * @param query       filtro de búsqueda.
   * @param paging      pageable.
   * @return el listado de entidades {@link SolicitudRrhhRequisitoNivelAcademico}
   *         paginadas y filtradas del {@link SolicitudRrhh}.
   */
  @GetMapping(PATH_REQUISITOS_NIVEL_ACADEMICO)
  public ResponseEntity<Page<SolicitudRrhhRequisitoNivelAcademicoOutput>> findAllRequisitosNivelAcedemico(
      @PathVariable String solicitudId,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllRequisitosNivelAcedemico(String solicitudId, String query, Pageable paging) - start");
    Page<SolicitudRrhhRequisitoNivelAcademicoOutput> page = requisitoNivelAcademicoConverter
        .convert(requisitoNivelAcademicoService.findAllBySolicitudPublicId(solicitudId, query, paging));
    log.debug("findAllRequisitosNivelAcedemico(String solicitudId, String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

}
