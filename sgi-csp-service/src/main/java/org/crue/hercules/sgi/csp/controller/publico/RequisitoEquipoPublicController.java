package org.crue.hercules.sgi.csp.controller.publico;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.dto.RequisitoEquipoCategoriaProfesionalOutput;
import org.crue.hercules.sgi.csp.dto.RequisitoEquipoNivelAcademicoOutput;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.crue.hercules.sgi.csp.model.RequisitoEquipoCategoriaProfesional;
import org.crue.hercules.sgi.csp.model.RequisitoEquipoNivelAcademico;
import org.crue.hercules.sgi.csp.service.RequisitoEquipoCategoriaProfesionalService;
import org.crue.hercules.sgi.csp.service.RequisitoEquipoNivelAcademicoService;
import org.crue.hercules.sgi.csp.service.RequisitoEquipoService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * RequisitoEquipoPublicController
 */
@RestController
@RequestMapping(RequisitoEquipoPublicController.REQUEST_MAPPING)
@Slf4j
public class RequisitoEquipoPublicController {
  public static final String PATH_DELIMITER = "/";
  public static final String PATH_PUBLIC = PATH_DELIMITER + "public";
  public static final String REQUEST_MAPPING = PATH_PUBLIC + PATH_DELIMITER + "convocatoria-requisitoequipos";

  public static final String PATH_ID = PATH_DELIMITER + "{id}";
  public static final String PATH_NIVELES = PATH_ID + "/niveles";
  public static final String PATH_CATEGORIAS_PROFESIONALES_REQUISITOS_EQUIPO = PATH_ID
      + "/categoriasprofesionalesrequisitosequipo";

  private ModelMapper modelMapper;

  /** RequisitoEquipo service */
  private final RequisitoEquipoService service;
  /** RequisitoEquipoNivelAcademicoService */
  private final RequisitoEquipoNivelAcademicoService requisitoEquipoNivelAcademicoService;
  /** RequisitoEquipoCategoriaProfesionalService */
  private final RequisitoEquipoCategoriaProfesionalService requisitoEquipoCategoriaProfesionalService;

  /**
   * Instancia un nuevo RequisitoEquipoController.
   * 
   * @param modelMapper                                {@link ModelMapper}
   * @param service                                    {@link RequisitoEquipoService}
   * @param requisitoEquipoNivelAcademicoService       {@link RequisitoEquipoNivelAcademicoService}
   * @param requisitoEquipoCategoriaProfesionalService {@link RequisitoEquipoCategoriaProfesionalService}
   */
  public RequisitoEquipoPublicController(ModelMapper modelMapper, RequisitoEquipoService service,
      RequisitoEquipoNivelAcademicoService requisitoEquipoNivelAcademicoService,
      RequisitoEquipoCategoriaProfesionalService requisitoEquipoCategoriaProfesionalService) {
    this.modelMapper = modelMapper;
    this.service = service;
    this.requisitoEquipoNivelAcademicoService = requisitoEquipoNivelAcademicoService;
    this.requisitoEquipoCategoriaProfesionalService = requisitoEquipoCategoriaProfesionalService;
  }

  /**
   * Devuelve el {@link RequisitoEquipo} de la {@link Convocatoria}.
   * 
   * @param id Identificador de {@link Convocatoria}.
   * @return el {@link RequisitoEquipo}
   */
  @GetMapping(PATH_ID)
  public ResponseEntity<RequisitoEquipo> findByConvocatoriaId(@PathVariable Long id) {
    log.debug("RequisitoEquipo findByConvocatoriaId(Long id) - start");
    RequisitoEquipo returnValue = service.findByConvocatoriaId(id);

    if (returnValue == null) {
      log.debug("RequisitoEquipo findByConvocatoriaId(Long id) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("RequisitoEquipo findByConvocatoriaId(Long id) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Devuelve los {@link RequisitoEquipoNivelAcademico} asociados al
   * {@link RequisitoEquipo} con el id indicado
   * 
   * @param id Identificador de {@link RequisitoEquipoNivelAcademico}
   * @return RequisitoEquipoNivelAcademico {@link RequisitoEquipoNivelAcademico}
   *         correspondiente al id
   */
  @GetMapping(PATH_NIVELES)
  public List<RequisitoEquipoNivelAcademicoOutput> findNivelesAcademicos(@PathVariable Long id) {
    log.debug("findNivelesAcademicos(@PathVariable Long id) - start");
    List<RequisitoEquipoNivelAcademicoOutput> returnValue = convertRequisitoEquipoNivelAcademicos(
        requisitoEquipoNivelAcademicoService.findByRequisitoEquipo(id));
    log.debug("findNivelesAcademicos(@PathVariable Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve las {@link RequisitoEquipoCategoriaProfesional} asociadas al
   * {@link RequisitoEquipo} con el id indicado
   * 
   * @param id Identificador de {@link RequisitoEquipoCategoriaProfesional}
   * @return RequisitoEquipoCategoriaProfesional
   *         {@link RequisitoEquipoCategoriaProfesional} correspondiente al id
   */
  @GetMapping(PATH_CATEGORIAS_PROFESIONALES_REQUISITOS_EQUIPO)
  public List<RequisitoEquipoCategoriaProfesionalOutput> findCategoriasProfesionales(@PathVariable Long id) {
    log.debug("findCategoriasProfesionales(@PathVariable Long id) - start");
    List<RequisitoEquipoCategoriaProfesionalOutput> returnValue = convertRequisitoEquipoCategoriaProfesionales(
        requisitoEquipoCategoriaProfesionalService.findByRequisitoEquipo(id));
    log.debug("findCategoriasProfesionales(@PathVariable Long id) - end");
    return returnValue;
  }

  private RequisitoEquipoCategoriaProfesionalOutput convert(RequisitoEquipoCategoriaProfesional entity) {
    return modelMapper.map(entity, RequisitoEquipoCategoriaProfesionalOutput.class);
  }

  private RequisitoEquipoNivelAcademicoOutput convert(RequisitoEquipoNivelAcademico entity) {
    return modelMapper.map(entity, RequisitoEquipoNivelAcademicoOutput.class);
  }

  private List<RequisitoEquipoNivelAcademicoOutput> convertRequisitoEquipoNivelAcademicos(
      List<RequisitoEquipoNivelAcademico> entities) {
    return entities.stream().map(entity -> convert(entity)).collect(Collectors.toList());
  }

  private List<RequisitoEquipoCategoriaProfesionalOutput> convertRequisitoEquipoCategoriaProfesionales(
      List<RequisitoEquipoCategoriaProfesional> entities) {
    return entities.stream().map(entity -> convert(entity)).collect(Collectors.toList());
  }
}
