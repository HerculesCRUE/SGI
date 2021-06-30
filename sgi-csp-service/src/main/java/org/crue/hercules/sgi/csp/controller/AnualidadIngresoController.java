package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.AnualidadIngresoInput;
import org.crue.hercules.sgi.csp.dto.AnualidadIngresoOutput;
import org.crue.hercules.sgi.csp.model.AnualidadIngreso;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad;
import org.crue.hercules.sgi.csp.service.AnualidadIngresoService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * AnualidadIngresoController
 */

@RestController
@RequestMapping("/anualidadingreso")
@Slf4j
public class AnualidadIngresoController {

  private ModelMapper modelMapper;

  /** AnualidadIngreso service */
  private final AnualidadIngresoService service;

  public AnualidadIngresoController(ModelMapper modelMapper, AnualidadIngresoService anualidadIngresoService) {
    log.debug("AnualidadIngresoController(AnualidadIngresoService anualidadIngresoService) - start");
    this.modelMapper = modelMapper;
    this.service = anualidadIngresoService;
    log.debug("AnualidadIngresoController(AnualidadIngresoService anualidadIngresoService) - end");
  }

  /**
   * Actualiza el listado de {@link AnualidadIngreso} de la
   * {@link ProyectoAnualidad} con el listado anualidadIngresos añadiendo,
   * editando o eliminando los elementos segun proceda.
   * 
   * @param proyectoAnualidadId Id del {@link ProyectoAnualidad}.
   * @param anualidadIngreso    lista con los nuevos {@link AnualidadIngreso} a
   *                            guardar.
   * @return Lista actualizada con los {@link AnualidadIngreso}.
   */
  @Transactional
  @PatchMapping("/{proyectoAnualidadId}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<List<AnualidadIngresoOutput>> update(@PathVariable Long proyectoAnualidadId,
      @Valid @RequestBody List<AnualidadIngresoInput> anualidadIngreso) {
    log.debug("update(Long convocatoriaId, List<AnualidadIngreso> anualidadIngresoes) - start");

    List<AnualidadIngreso> returnValue = service.update(proyectoAnualidadId,
        convertListAnualidadIngreso(anualidadIngreso));
    log.debug("update(Long convocatoriaId, List<AnualidadIngreso> anualidadIngresoes) - end");
    return new ResponseEntity<>(convertListAnualidadIngresoOutput(returnValue), HttpStatus.CREATED);
  }

  private List<AnualidadIngresoOutput> convertListAnualidadIngresoOutput(List<AnualidadIngreso> anualidadesIngreso) {
    List<AnualidadIngresoOutput> anualidadesIngresoOutput = anualidadesIngreso.stream().map((anualidadIngreso) -> {
      return modelMapper.map(anualidadIngreso, AnualidadIngresoOutput.class);
    }).collect(Collectors.toList());

    return anualidadesIngresoOutput;
  }

  private List<AnualidadIngreso> convertListAnualidadIngreso(List<AnualidadIngresoInput> anualidadesIngresoInput) {
    List<AnualidadIngreso> anualidadesIngreso = anualidadesIngresoInput.stream().map((anualidadIngreso) -> {
      return modelMapper.map(anualidadIngreso, AnualidadIngreso.class);
    }).collect(Collectors.toList());

    return anualidadesIngreso;
  }
}
