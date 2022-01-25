package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.AnualidadGastoInput;
import org.crue.hercules.sgi.csp.dto.AnualidadGastoOutput;
import org.crue.hercules.sgi.csp.model.AnualidadGasto;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad;
import org.crue.hercules.sgi.csp.service.AnualidadGastoService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * AnualidadGastoController
 */
@RestController
@RequestMapping(AnualidadGastoController.MAPPING)
@Slf4j
public class AnualidadGastoController {
  public static final String MAPPING = "/anualidadgasto";

  private ModelMapper modelMapper;

  /** AnualidadGasto service */
  private final AnualidadGastoService service;

  /**
   * Instancia un nuevo AnualidadGastoController.
   * 
   * @param modelMapper {@link ModelMapper}.
   * @param service     {@link AnualidadGastoService}
   */
  public AnualidadGastoController(ModelMapper modelMapper, AnualidadGastoService service) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Actualiza el listado de {@link AnualidadGasto} de la
   * {@link ProyectoAnualidad} con el listado codigosEconomicos añadiendo,
   * editando o eliminando los elementos segun proceda.
   * 
   * @param anualidadGasto      {@link AnualidadGasto} a actualizar.
   * @param proyectoAnualidadId id {@link ProyectoAnualidad} a actualizar.
   * @return {@link AnualidadGasto} actualizado.
   */
  @PatchMapping("/{proyectoAnualidadId}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<List<AnualidadGastoOutput>> update(@PathVariable Long proyectoAnualidadId,
      @Valid @RequestBody List<AnualidadGastoInput> anualidadGasto) {
    log.debug("update(Long convocatoriaId, List<AnualidadGastoInput> anualidadGasto) - start");
    List<AnualidadGasto> returnValue = service.update(proyectoAnualidadId, convertListAnualidadGasto(anualidadGasto));
    log.debug("update(Long convocatoriaId, List<AnualidadGastoInput> anualidadGasto) - end");
    return new ResponseEntity<>(convertListAnualidadGastoOutput(returnValue), HttpStatus.CREATED);
  }

  private List<AnualidadGasto> convertListAnualidadGasto(List<AnualidadGastoInput> anualidadesGastoInput) {
    return anualidadesGastoInput.stream().map(anualidadGasto -> modelMapper.map(anualidadGasto, AnualidadGasto.class))
        .collect(Collectors.toList());
  }

  private List<AnualidadGastoOutput> convertListAnualidadGastoOutput(List<AnualidadGasto> anualidadesGasto) {
    return anualidadesGasto.stream().map(anualidadGasto -> modelMapper.map(anualidadGasto, AnualidadGastoOutput.class))
        .collect(Collectors.toList());
  }

}