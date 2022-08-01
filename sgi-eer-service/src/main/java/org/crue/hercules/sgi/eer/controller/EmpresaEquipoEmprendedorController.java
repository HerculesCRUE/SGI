package org.crue.hercules.sgi.eer.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eer.converter.EmpresaEquipoEmprendedorConverter;
import org.crue.hercules.sgi.eer.dto.EmpresaEquipoEmprendedorInput;
import org.crue.hercules.sgi.eer.dto.EmpresaEquipoEmprendedorOutput;
import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.model.EmpresaEquipoEmprendedor;
import org.crue.hercules.sgi.eer.service.EmpresaEquipoEmprendedorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * EmpresaEquipoEmprendedorController
 */
@RestController
@RequestMapping(EmpresaEquipoEmprendedorController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
@Validated
public class EmpresaEquipoEmprendedorController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "empresasequiposemprendedores";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final EmpresaEquipoEmprendedorService service;
  private final EmpresaEquipoEmprendedorConverter converter;

  /**
   * Devuelve el {@link EmpresaEquipoEmprendedor} con el id indicado.
   * 
   * @param id Identificador de {@link EmpresaEquipoEmprendedor}.
   * @return {@link EmpresaEquipoEmprendedor} correspondiente al id
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('EER-EER-E', 'EER-EER-V')")
  public EmpresaEquipoEmprendedorOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    EmpresaEquipoEmprendedorOutput returnValue = converter.convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link EmpresaEquipoEmprendedor} del {@link Empresa}
   * con el
   * listado grupoEquipoEmprendedors añadiendo, editando o eliminando los
   * elementos segun
   * proceda.
   * 
   * @param id                      Id del {@link Empresa}.
   * @param grupoEquipoEmprendedors lista con los nuevos
   *                                {@link EmpresaEquipoEmprendedor} a guardar.
   * @return Lista actualizada con los {@link EmpresaEquipoEmprendedor}.
   */
  @PatchMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('EER-EER-E')")
  public ResponseEntity<List<EmpresaEquipoEmprendedorOutput>> update(@PathVariable Long id,
      @RequestBody List<@Valid EmpresaEquipoEmprendedorInput> grupoEquipoEmprendedors) {
    log.debug("update(List<EmpresaEquipoEmprendedorInput> grupoEquipoEmprendedors, grupoId) - start");
    List<EmpresaEquipoEmprendedorOutput> returnValue = converter
        .convertEmpresaEquipoEmprendedors(service.update(id, converter.convertEmpresaEquipoEmprendedorInput(
            grupoEquipoEmprendedors)));
    log.debug("update(List<EmpresaEquipoEmprendedorInput> grupoEquipoEmprendedors, grupoId) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

}
