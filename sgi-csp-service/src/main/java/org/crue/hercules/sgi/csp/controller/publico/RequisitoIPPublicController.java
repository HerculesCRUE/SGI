package org.crue.hercules.sgi.csp.controller.publico;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.dto.RequisitoIPCategoriaProfesionalOutput;
import org.crue.hercules.sgi.csp.dto.RequisitoIPNivelAcademicoOutput;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.model.RequisitoIPCategoriaProfesional;
import org.crue.hercules.sgi.csp.model.RequisitoIPNivelAcademico;
import org.crue.hercules.sgi.csp.service.RequisitoIPCategoriaProfesionalService;
import org.crue.hercules.sgi.csp.service.RequisitoIPNivelAcademicoService;
import org.crue.hercules.sgi.csp.service.RequisitoIPService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * RequisitoIPPublicController
 */
@RestController
@RequestMapping(RequisitoIPPublicController.REQUEST_MAPPING)
@Slf4j
public class RequisitoIPPublicController {
  public static final String PATH_DELIMITER = "/";
  public static final String PATH_PUBLIC = PATH_DELIMITER + "public";
  public static final String REQUEST_MAPPING = PATH_PUBLIC + PATH_DELIMITER + "convocatoria-requisitoips";

  public static final String PATH_ID = PATH_DELIMITER + "{id}";
  public static final String PATH_NIVELES = PATH_ID + "/niveles";
  public static final String PATH_CATEGORIAS_PROFESIONALES_REQUISITOS_EQUIPO = PATH_ID
      + "/categoriasprofesionales";

  private ModelMapper modelMapper;

  /** RequisitoIP service */
  private final RequisitoIPService service;
  /** RequisitoIPNivelAcademicoService */
  private final RequisitoIPNivelAcademicoService requisitoIPNivelAcademicoService;
  /** RequisitoIPCategoriaProfesionalService */
  private final RequisitoIPCategoriaProfesionalService requisitoIPCategoriaProfesionalService;

  /**
   * Instancia un nuevo RequisitoIPController.
   * 
   * @param modelMapper                            {@link ModelMapper}
   * @param service                                {@link RequisitoIPService}
   * @param requisitoIPNivelAcademicoService       {@link RequisitoIPNivelAcademicoService}
   * @param requisitoIPCategoriaProfesionalService {@link RequisitoIPCategoriaProfesionalService}
   */
  public RequisitoIPPublicController(ModelMapper modelMapper, RequisitoIPService service,
      RequisitoIPNivelAcademicoService requisitoIPNivelAcademicoService,
      RequisitoIPCategoriaProfesionalService requisitoIPCategoriaProfesionalService) {
    this.modelMapper = modelMapper;
    this.service = service;
    this.requisitoIPNivelAcademicoService = requisitoIPNivelAcademicoService;
    this.requisitoIPCategoriaProfesionalService = requisitoIPCategoriaProfesionalService;
  }

  /**
   * Devuelve el {@link RequisitoIP} de la {@link Convocatoria}.
   * 
   * @param id Identificador de la {@link Convocatoria}.
   * @return el {@link RequisitoIP}
   */
  @GetMapping(PATH_ID)
  public ResponseEntity<RequisitoIP> findByConvocatoriaId(@PathVariable Long id) {
    log.debug("RequisitoIP findByConvocatoriaId(Long id) - start");
    RequisitoIP returnValue = service.findByConvocatoria(id);

    if (returnValue == null) {
      log.debug("RequisitoIP findByConvocatoriaId(Long id) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("RequisitoIP findByConvocatoriaId(Long id) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Devuelve los {@link RequisitoIPNivelAcademico} asociados al
   * {@link RequisitoIP} con el id indicado
   * 
   * @param id Identificador de {@link RequisitoIPNivelAcademico}
   * @return RequisitoIPNivelAcademico {@link RequisitoIPNivelAcademico}
   *         correspondiente al id
   */
  @GetMapping(PATH_NIVELES)
  public List<RequisitoIPNivelAcademicoOutput> findNivelesAcademicos(@PathVariable Long id) {
    log.debug("findNivelesAcademicos(@PathVariable Long id) - start");
    List<RequisitoIPNivelAcademicoOutput> returnValue = convertRequisitoIPNivelAcademicos(
        requisitoIPNivelAcademicoService.findByRequisitoIP(id));
    log.debug("findNivelesAcademicos(@PathVariable Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve las {@link RequisitoIPCategoriaProfesional} asociadas al
   * {@link RequisitoIP} con el id indicado
   * 
   * @param id Identificador de {@link RequisitoIPCategoriaProfesional}
   * @return RequisitoIPCategoriaProfesional
   *         {@link RequisitoIPCategoriaProfesional} correspondiente al id
   */
  @GetMapping(PATH_CATEGORIAS_PROFESIONALES_REQUISITOS_EQUIPO)
  public List<RequisitoIPCategoriaProfesionalOutput> findCategoriasProfesionales(@PathVariable Long id) {
    log.debug("findCategoriasProfesionales(@PathVariable Long id) - start");
    List<RequisitoIPCategoriaProfesionalOutput> returnValue = convertRequisitoIPCategoriaProfesionales(
        requisitoIPCategoriaProfesionalService.findByRequisitoIP(id));
    log.debug("findCategoriasProfesionales(@PathVariable Long id) - end");
    return returnValue;
  }

  private RequisitoIPCategoriaProfesionalOutput convert(RequisitoIPCategoriaProfesional entity) {
    return modelMapper.map(entity, RequisitoIPCategoriaProfesionalOutput.class);
  }

  private RequisitoIPNivelAcademicoOutput convert(RequisitoIPNivelAcademico entity) {
    return modelMapper.map(entity, RequisitoIPNivelAcademicoOutput.class);
  }

  private List<RequisitoIPNivelAcademicoOutput> convertRequisitoIPNivelAcademicos(
      List<RequisitoIPNivelAcademico> entities) {
    return entities.stream().map(this::convert).collect(Collectors.toList());
  }

  private List<RequisitoIPCategoriaProfesionalOutput> convertRequisitoIPCategoriaProfesionales(
      List<RequisitoIPCategoriaProfesional> entities) {
    return entities.stream().map(this::convert).collect(Collectors.toList());
  }

}
