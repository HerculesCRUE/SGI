package org.crue.hercules.sgi.eer.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eer.converter.EmpresaComposicionSociedadConverter;
import org.crue.hercules.sgi.eer.dto.EmpresaComposicionSociedadInput;
import org.crue.hercules.sgi.eer.dto.EmpresaComposicionSociedadOutput;
import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.model.EmpresaComposicionSociedad;
import org.crue.hercules.sgi.eer.service.EmpresaComposicionSociedadService;
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
 * EmpresaComposicionSociedadController
 */
@RestController
@RequestMapping(EmpresaComposicionSociedadController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
@Validated
public class EmpresaComposicionSociedadController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "empresascomposicionessociedades";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final EmpresaComposicionSociedadService service;
  private final EmpresaComposicionSociedadConverter converter;

  /**
   * Devuelve el {@link EmpresaComposicionSociedad} con el id indicado.
   * 
   * @param id Identificador de {@link EmpresaComposicionSociedad}.
   * @return {@link EmpresaComposicionSociedad} correspondiente al id
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('EER-EER-E', 'EER-EER-V')")
  public EmpresaComposicionSociedadOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    EmpresaComposicionSociedadOutput returnValue = converter.convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link EmpresaComposicionSociedad} del
   * {@link Empresa}
   * con el
   * listado empresaComposicionSociedades añadiendo, editando o eliminando los
   * elementos segun
   * proceda.
   * 
   * @param id                           Id del {@link Empresa}.
   * @param empresaComposicionSociedades lista con los nuevos
   *                                     {@link EmpresaComposicionSociedad} a
   *                                     guardar.
   * @return Lista actualizada con los {@link EmpresaComposicionSociedad}.
   */
  @PatchMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('EER-EER-E')")
  public ResponseEntity<List<EmpresaComposicionSociedadOutput>> update(@PathVariable Long id,
      @RequestBody List<@Valid EmpresaComposicionSociedadInput> empresaComposicionSociedades) {
    log.debug("update(List<EmpresaComposicionSociedadInput> empresaComposicionSociedades, empresaId) - start");
    List<EmpresaComposicionSociedadOutput> returnValue = converter
        .convertEmpresaComposicionSociedades(service.update(id, converter.convertEmpresaComposicionSociedadInput(
            empresaComposicionSociedades)));
    log.debug("update(List<EmpresaComposicionSociedadInput> empresaComposicionSociedades, empresaId) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

}
