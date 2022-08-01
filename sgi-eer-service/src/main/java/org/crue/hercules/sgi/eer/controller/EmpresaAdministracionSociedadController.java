package org.crue.hercules.sgi.eer.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eer.converter.EmpresaAdministracionSociedadConverter;
import org.crue.hercules.sgi.eer.dto.EmpresaAdministracionSociedadInput;
import org.crue.hercules.sgi.eer.dto.EmpresaAdministracionSociedadOutput;
import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.model.EmpresaAdministracionSociedad;
import org.crue.hercules.sgi.eer.service.EmpresaAdministracionSociedadService;
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
 * EmpresaAdministracionSociedadController
 */
@RestController
@RequestMapping(EmpresaAdministracionSociedadController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
@Validated
public class EmpresaAdministracionSociedadController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "empresasadministracionessociedades";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final EmpresaAdministracionSociedadService service;
  private final EmpresaAdministracionSociedadConverter converter;

  /**
   * Devuelve el {@link EmpresaAdministracionSociedad} con el id indicado.
   * 
   * @param id Identificador de {@link EmpresaAdministracionSociedad}.
   * @return {@link EmpresaAdministracionSociedad} correspondiente al id
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('EER-EER-E', 'EER-EER-V')")
  public EmpresaAdministracionSociedadOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    EmpresaAdministracionSociedadOutput returnValue = converter.convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link EmpresaAdministracionSociedad} del
   * {@link Empresa}
   * con el
   * listado empresaAdministracionSociedades añadiendo, editando o eliminando los
   * elementos segun
   * proceda.
   * 
   * @param id                              Id del {@link Empresa}.
   * @param empresaAdministracionSociedades lista con los nuevos
   *                                        {@link EmpresaAdministracionSociedad}
   *                                        a
   *                                        guardar.
   * @return Lista actualizada con los {@link EmpresaAdministracionSociedad}.
   */
  @PatchMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('EER-EER-E')")
  public ResponseEntity<List<EmpresaAdministracionSociedadOutput>> update(@PathVariable Long id,
      @RequestBody List<@Valid EmpresaAdministracionSociedadInput> empresaAdministracionSociedades) {
    log.debug("update(List<EmpresaAdministracionSociedadInput> empresaAdministracionSociedades, empresaId) - start");
    List<EmpresaAdministracionSociedadOutput> returnValue = converter
        .convertEmpresaAdministracionSociedades(service.update(id, converter.convertEmpresaAdministracionSociedadInput(
            empresaAdministracionSociedades)));
    log.debug("update(List<EmpresaAdministracionSociedadInput> empresaAdministracionSociedades, empresaId) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

}
