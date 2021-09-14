package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.dto.RequisitoEquipoCategoriaProfesionalInput;
import org.crue.hercules.sgi.csp.dto.RequisitoEquipoCategoriaProfesionalOutput;
import org.crue.hercules.sgi.csp.dto.RequisitoEquipoNivelAcademicoInput;
import org.crue.hercules.sgi.csp.dto.RequisitoEquipoNivelAcademicoOutput;
import org.crue.hercules.sgi.csp.exceptions.NoRelatedEntitiesException;
import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
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
 * RequisitoEquipoController
 */
@RestController
@RequestMapping(RequisitoEquipoController.MAPPING)
@Slf4j
public class RequisitoEquipoController {
  public static final String MAPPING = "/convocatoria-requisitoequipos";
  public static final String PATH_NIVELES = "/{id}/niveles";
  public static final String PATH_CATEGORIAS = "/{id}/categorias";

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
  public RequisitoEquipoController(ModelMapper modelMapper, RequisitoEquipoService service,
      RequisitoEquipoNivelAcademicoService requisitoEquipoNivelAcademicoService,
      RequisitoEquipoCategoriaProfesionalService requisitoEquipoCategoriaProfesionalService) {
    this.modelMapper = modelMapper;
    this.service = service;
    this.requisitoEquipoNivelAcademicoService = requisitoEquipoNivelAcademicoService;
    this.requisitoEquipoCategoriaProfesionalService = requisitoEquipoCategoriaProfesionalService;
  }

  /**
   * Crea un nuevo {@link RequisitoEquipo}.
   * 
   * @param requisitoEquipo {@link RequisitoEquipo} que se quiere crear.
   * @return Nuevo {@link RequisitoEquipo} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C','CSP-CON-E')")
  ResponseEntity<RequisitoEquipo> create(@Valid @RequestBody RequisitoEquipo requisitoEquipo) {
    log.debug("create(RequisitoEquipo requisitoEquipo) - start");
    RequisitoEquipo returnValue = service.create(requisitoEquipo);
    log.debug("create(RequisitoEquipo requisitoEquipo) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link RequisitoEquipo} con el id de {@link Convocatoria}
   * indicado.
   * 
   * @param requisitoEquipo {@link RequisitoEquipo} a actualizar.
   * @param id              Identificador de la {@link Convocatoria}.
   * @return {@link RequisitoEquipo} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  RequisitoEquipo update(@Validated({ Update.class, Default.class }) @RequestBody RequisitoEquipo requisitoEquipo,
      @PathVariable Long id) {
    log.debug("update(RequisitoEquipo requisitoEquipo, Long id) - start");
    RequisitoEquipo returnValue = service.update(requisitoEquipo, id);
    log.debug("update(RequisitoEquipo requisitoEquipo, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link RequisitoEquipo} de la {@link Convocatoria}.
   * 
   * @param id Identificador de {@link Convocatoria}.
   * @return el {@link RequisitoEquipo}
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E', 'CSP-CON-V', 'CSP-CON-INV-V')")
  ResponseEntity<RequisitoEquipo> findByConvocatoriaId(@PathVariable Long id) {
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E', 'CSP-CON-V', 'CSP-CON-INV-V')")
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
  @GetMapping(PATH_CATEGORIAS)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E', 'CSP-CON-V', 'CSP-CON-INV-V')")
  public List<RequisitoEquipoCategoriaProfesionalOutput> findCategoriasProfesionales(@PathVariable Long id) {
    log.debug("findCategoriasProfesionales(@PathVariable Long id) - start");
    List<RequisitoEquipoCategoriaProfesionalOutput> returnValue = convertRequisitoEquipoCategoriaProfesionales(
        requisitoEquipoCategoriaProfesionalService.findByRequisitoEquipo(id));
    log.debug("findCategoriasProfesionales(@PathVariable Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza la lista de {@link RequisitoEquipoNivelAcademico} asociados al
   * {@link RequisitoEquipo} con el id indicado
   * 
   * @param id                identificador del {@link RequisitoEquipo}
   * @param nivelesAcademicos nueva lista de {@link RequisitoEquipoNivelAcademico}
   *                          del {@link RequisitoEquipo}
   * @return la nueva lista de {@link RequisitoEquipoNivelAcademico} asociados al
   *         {@link RequisitoEquipo}
   */
  @PatchMapping(PATH_NIVELES)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public ResponseEntity<List<RequisitoEquipoNivelAcademicoOutput>> updateNivelesAcademicos(@PathVariable Long id,
      @Valid @RequestBody List<RequisitoEquipoNivelAcademicoInput> nivelesAcademicos) {
    log.debug("updateNivelesAcademicos(Long id, List<RequisitoEquipoNivelAcademico> nivelesAcademicos) - start");

    nivelesAcademicos.stream().forEach(nivelesAcademico -> {
      if (!nivelesAcademico.getRequisitoEquipoId().equals(id)) {
        throw new NoRelatedEntitiesException(RequisitoEquipoNivelAcademico.class, RequisitoEquipo.class);
      }
    });

    List<RequisitoEquipoNivelAcademicoOutput> returnValue = convertRequisitoEquipoNivelAcademicos(
        requisitoEquipoNivelAcademicoService.updateNivelesAcademicos(id,
            convertRequisitoEquipoNivelAcademicoInputs(nivelesAcademicos)));
    log.debug("updateNivelesAcademicos(Long id, List<RequisitoEquipoNivelAcademico> nivelesAcademicos) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Actualiza la lista de {@link RequisitoEquipoCategoriaProfesional} asociadas
   * al {@link RequisitoEquipo} con el id indicado
   * 
   * @param id                      identificador del {@link RequisitoEquipo}
   * @param categoriasProfesionales nueva lista de
   *                                {@link RequisitoEquipoCategoriaProfesional}
   *                                del {@link RequisitoEquipo}
   * @return la nueva lista de {@link RequisitoEquipoCategoriaProfesional}
   *         asociadas al {@link RequisitoEquipo}
   */
  @PatchMapping(PATH_CATEGORIAS)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public ResponseEntity<List<RequisitoEquipoCategoriaProfesionalOutput>> updateCategoriasProfesionales(
      @PathVariable Long id,
      @Valid @RequestBody List<RequisitoEquipoCategoriaProfesionalInput> categoriasProfesionales) {
    log.debug(
        "updateCategoriasProfesionales(Long id, List<RequisitoEquipoCategoriaProfesionalInput> categoriasProfesionales) - start");

    categoriasProfesionales.stream().forEach(categoriaProfesional -> {
      if (!categoriaProfesional.getRequisitoEquipoId().equals(id)) {
        throw new NoRelatedEntitiesException(RequisitoEquipoCategoriaProfesional.class, RequisitoEquipo.class);
      }
    });

    List<RequisitoEquipoCategoriaProfesionalOutput> returnValue = convertRequisitoEquipoCategoriaProfesionales(
        requisitoEquipoCategoriaProfesionalService.updateCategoriasProfesionales(id,
            convertRequisitoEquipoCategoriaProfesionalInputs(categoriasProfesionales)));
    log.debug(
        "updateCategoriasProfesionales(Long id, List<RequisitoEquipoCategoriaProfesionalInput> categoriasProfesionales) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  private RequisitoEquipoCategoriaProfesionalOutput convert(RequisitoEquipoCategoriaProfesional entity) {
    return modelMapper.map(entity, RequisitoEquipoCategoriaProfesionalOutput.class);
  }

  private RequisitoEquipoNivelAcademicoOutput convert(RequisitoEquipoNivelAcademico entity) {
    return modelMapper.map(entity, RequisitoEquipoNivelAcademicoOutput.class);
  }

  private RequisitoEquipoNivelAcademico convert(RequisitoEquipoNivelAcademicoInput input) {
    return convert(null, input);
  }

  private RequisitoEquipoCategoriaProfesional convert(RequisitoEquipoCategoriaProfesionalInput input) {
    return convert(null, input);
  }

  private List<RequisitoEquipoNivelAcademico> convertRequisitoEquipoNivelAcademicoInputs(
      List<RequisitoEquipoNivelAcademicoInput> inputs) {
    return inputs.stream().map((input) -> convert(input)).collect(Collectors.toList());
  }

  private List<RequisitoEquipoCategoriaProfesional> convertRequisitoEquipoCategoriaProfesionalInputs(
      List<RequisitoEquipoCategoriaProfesionalInput> inputs) {
    return inputs.stream().map((input) -> convert(input)).collect(Collectors.toList());
  }

  private RequisitoEquipoNivelAcademico convert(Long id, RequisitoEquipoNivelAcademicoInput input) {
    RequisitoEquipoNivelAcademico entity = modelMapper.map(input, RequisitoEquipoNivelAcademico.class);
    entity.setId(id);
    return entity;
  }

  private RequisitoEquipoCategoriaProfesional convert(Long id, RequisitoEquipoCategoriaProfesionalInput input) {
    RequisitoEquipoCategoriaProfesional entity = modelMapper.map(input, RequisitoEquipoCategoriaProfesional.class);
    entity.setId(id);
    return entity;
  }

  private List<RequisitoEquipoNivelAcademicoOutput> convertRequisitoEquipoNivelAcademicos(
      List<RequisitoEquipoNivelAcademico> entities) {
    return entities.stream().map((entity) -> convert(entity)).collect(Collectors.toList());
  }

  private List<RequisitoEquipoCategoriaProfesionalOutput> convertRequisitoEquipoCategoriaProfesionales(
      List<RequisitoEquipoCategoriaProfesional> entities) {
    return entities.stream().map((entity) -> convert(entity)).collect(Collectors.toList());
  }
}
