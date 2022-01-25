package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.dto.RequisitoIPCategoriaProfesionalInput;
import org.crue.hercules.sgi.csp.dto.RequisitoIPCategoriaProfesionalOutput;
import org.crue.hercules.sgi.csp.dto.RequisitoIPNivelAcademicoInput;
import org.crue.hercules.sgi.csp.dto.RequisitoIPNivelAcademicoOutput;
import org.crue.hercules.sgi.csp.exceptions.NoRelatedEntitiesException;
import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * RequisitoIPController
 */
@RestController
@RequestMapping(RequisitoIPController.MAPPING)
@Slf4j
public class RequisitoIPController {
  public static final String MAPPING = "/convocatoria-requisitoips";
  public static final String PATH_NIVELES = "/{id}/niveles";
  public static final String PATH_CATEGORIAS_PROFESIONALES_REQUISITOS_EQUIPO = "/{id}/categoriasprofesionalesrequisitosequipo";

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
  public RequisitoIPController(ModelMapper modelMapper, RequisitoIPService service,
      RequisitoIPNivelAcademicoService requisitoIPNivelAcademicoService,
      RequisitoIPCategoriaProfesionalService requisitoIPCategoriaProfesionalService) {
    this.modelMapper = modelMapper;
    this.service = service;
    this.requisitoIPNivelAcademicoService = requisitoIPNivelAcademicoService;
    this.requisitoIPCategoriaProfesionalService = requisitoIPCategoriaProfesionalService;
  }

  /**
   * Crea un nuevo {@link RequisitoIP}.
   * 
   * @param requisitoIP {@link RequisitoIP} que se quiere crear.
   * @return Nuevo {@link RequisitoIP} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C', 'CSP-CON-E')")
  public ResponseEntity<RequisitoIP> create(@Valid @RequestBody RequisitoIP requisitoIP) {
    log.debug("create(RequisitoIP requisitoIP) - start");
    RequisitoIP returnValue = service.create(requisitoIP);
    log.debug("create(RequisitoIP requisitoIP) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link RequisitoIP} con el id indicado.
   * 
   * @param requisitoIP {@link RequisitoIP} a actualizar.
   * @param id          identificador de la {@link Convocatoria} a actualizar.
   * @return {@link RequisitoIP} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public RequisitoIP update(@Validated({ Update.class, Default.class }) @RequestBody RequisitoIP requisitoIP,
      @PathVariable Long id) {
    log.debug("update(RequisitoIP requisitoIP, Long id) - start");
    RequisitoIP returnValue = service.update(requisitoIP, id);
    log.debug("update(RequisitoIP requisitoIP, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link RequisitoIP} de la {@link Convocatoria}.
   * 
   * @param id Identificador de la {@link Convocatoria}.
   * @return el {@link RequisitoIP}
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E', 'CSP-CON-V', 'CSP-CON-INV-V')")
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E', 'CSP-CON-V', 'CSP-CON-INV-V')")
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E', 'CSP-CON-V', 'CSP-CON-INV-V')")
  public List<RequisitoIPCategoriaProfesionalOutput> findCategoriasProfesionales(@PathVariable Long id) {
    log.debug("findCategoriasProfesionales(@PathVariable Long id) - start");
    List<RequisitoIPCategoriaProfesionalOutput> returnValue = convertRequisitoIPCategoriaProfesionales(
        requisitoIPCategoriaProfesionalService.findByRequisitoIP(id));
    log.debug("findCategoriasProfesionales(@PathVariable Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza la lista de {@link RequisitoIPNivelAcademico} asociados al
   * {@link RequisitoIP} con el id indicado
   * 
   * @param id                identificador del {@link RequisitoIP}
   * @param nivelesAcademicos nueva lista de {@link RequisitoIPNivelAcademico} del
   *                          {@link RequisitoIP}
   * @return la nueva lista de {@link RequisitoIPNivelAcademico} asociados al
   *         {@link RequisitoIP}
   */
  @PatchMapping(PATH_NIVELES)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public ResponseEntity<List<RequisitoIPNivelAcademicoOutput>> updateNivelesAcademicos(@PathVariable Long id,
      @Valid @RequestBody List<RequisitoIPNivelAcademicoInput> nivelesAcademicos) {
    log.debug("updateNivelesAcademicos(Long id, List<RequisitoIPNivelAcademico> nivelesAcademicos) - start");

    nivelesAcademicos.stream().forEach(nivelesAcademico -> {
      if (!nivelesAcademico.getRequisitoIPId().equals(id)) {
        throw new NoRelatedEntitiesException(RequisitoIPNivelAcademico.class, RequisitoIP.class);
      }
    });

    List<RequisitoIPNivelAcademicoOutput> returnValue = convertRequisitoIPNivelAcademicos(
        requisitoIPNivelAcademicoService.updateNivelesAcademicos(id,
            convertRequisitoIPNivelAcademicoInputs(nivelesAcademicos)));
    log.debug("updateNivelesAcademicos(Long id, List<RequisitoIPNivelAcademico> nivelesAcademicos) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Actualiza la lista de {@link RequisitoIPCategoriaProfesional} asociadas al
   * {@link RequisitoIP} con el id indicado
   * 
   * @param id                      identificador del {@link RequisitoIP}
   * @param categoriasProfesionales nueva lista de
   *                                {@link RequisitoIPCategoriaProfesional} del
   *                                {@link RequisitoIP}
   * @return la nueva lista de {@link RequisitoIPCategoriaProfesional} asociadas
   *         al {@link RequisitoIP}
   */
  @PatchMapping(PATH_CATEGORIAS_PROFESIONALES_REQUISITOS_EQUIPO)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public ResponseEntity<List<RequisitoIPCategoriaProfesionalOutput>> updateCategoriasProfesionales(
      @PathVariable Long id, @Valid @RequestBody List<RequisitoIPCategoriaProfesionalInput> categoriasProfesionales) {
    log.debug(
        "updateCategoriasProfesionales(Long id, List<RequisitoIPCategoriaProfesionalInput> categoriasProfesionales) - start");

    categoriasProfesionales.stream().forEach(categoriaProfesional -> {
      if (!categoriaProfesional.getRequisitoIPId().equals(id)) {
        throw new NoRelatedEntitiesException(RequisitoIPCategoriaProfesional.class, RequisitoIP.class);
      }
    });

    List<RequisitoIPCategoriaProfesionalOutput> returnValue = convertRequisitoIPCategoriaProfesionales(
        requisitoIPCategoriaProfesionalService.updateCategoriasProfesionales(id,
            convertRequisitoIPCategoriaProfesionalInputs(categoriasProfesionales)));
    log.debug(
        "updateCategoriasProfesionales(Long id, List<RequisitoIPCategoriaProfesionalInput> categoriasProfesionales) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  private RequisitoIPCategoriaProfesionalOutput convert(RequisitoIPCategoriaProfesional entity) {
    return modelMapper.map(entity, RequisitoIPCategoriaProfesionalOutput.class);
  }

  private RequisitoIPNivelAcademicoOutput convert(RequisitoIPNivelAcademico entity) {
    return modelMapper.map(entity, RequisitoIPNivelAcademicoOutput.class);
  }

  private RequisitoIPNivelAcademico convert(RequisitoIPNivelAcademicoInput input) {
    return convert(null, input);
  }

  private RequisitoIPCategoriaProfesional convert(RequisitoIPCategoriaProfesionalInput input) {
    return convert(null, input);
  }

  private List<RequisitoIPNivelAcademico> convertRequisitoIPNivelAcademicoInputs(
      List<RequisitoIPNivelAcademicoInput> inputs) {
    return inputs.stream().map(input -> convert(input)).collect(Collectors.toList());
  }

  private List<RequisitoIPCategoriaProfesional> convertRequisitoIPCategoriaProfesionalInputs(
      List<RequisitoIPCategoriaProfesionalInput> inputs) {
    return inputs.stream().map(input -> convert(input)).collect(Collectors.toList());
  }

  private RequisitoIPNivelAcademico convert(Long id, RequisitoIPNivelAcademicoInput input) {
    RequisitoIPNivelAcademico entity = modelMapper.map(input, RequisitoIPNivelAcademico.class);
    entity.setId(id);
    return entity;
  }

  private RequisitoIPCategoriaProfesional convert(Long id, RequisitoIPCategoriaProfesionalInput input) {
    RequisitoIPCategoriaProfesional entity = modelMapper.map(input, RequisitoIPCategoriaProfesional.class);
    entity.setId(id);
    return entity;
  }

  private List<RequisitoIPNivelAcademicoOutput> convertRequisitoIPNivelAcademicos(
      List<RequisitoIPNivelAcademico> entities) {
    return entities.stream().map(entity -> convert(entity)).collect(Collectors.toList());
  }

  private List<RequisitoIPCategoriaProfesionalOutput> convertRequisitoIPCategoriaProfesionales(
      List<RequisitoIPCategoriaProfesional> entities) {
    return entities.stream().map(entity -> convert(entity)).collect(Collectors.toList());
  }
}
