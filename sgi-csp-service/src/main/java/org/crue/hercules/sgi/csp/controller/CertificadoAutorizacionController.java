package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.CertificadoAutorizacionInput;
import org.crue.hercules.sgi.csp.dto.CertificadoAutorizacionOutput;
import org.crue.hercules.sgi.csp.model.CertificadoAutorizacion;
import org.crue.hercules.sgi.csp.service.CertificadoAutorizacionService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * CertificadoAutorizacionController
 */
@RestController
@RequestMapping(CertificadoAutorizacionController.REQUEST_MAPPING)
@Slf4j
public class CertificadoAutorizacionController {

  public static final String REQUEST_MAPPING = "/certificadosautorizaciones";

  private ModelMapper modelMapper;

  private CertificadoAutorizacionService service;

  public CertificadoAutorizacionController(CertificadoAutorizacionService service, ModelMapper modelMapper) {
    this.service = service;
    this.modelMapper = modelMapper;
  }

  /**
   * Crea nuevo {@link CertificadoAutorizacion}
   * 
   * @param certificadoAutorizacion {@link CertificadoAutorizacion} que se quiere
   *                                crear.
   * @return Nuevo {@link CertificadoAutorizacion}creado.
   */

  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-INV-ER', 'CSP-AUT-E')")
  public ResponseEntity<CertificadoAutorizacionOutput> create(
      @Valid @RequestBody CertificadoAutorizacionInput certificadoAutorizacion) {
    log.debug("create(CertificadoAutorizacion certificadoAutorizacion) - start");

    CertificadoAutorizacionOutput returnValue = convert(service.create(convert(certificadoAutorizacion)));

    log.debug("create(CertificadoAutorizacion certificadoAutorizacion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link CertificadoAutorizacion}.
   * 
   * @param certificadoAutorizacion {@link CertificadoAutorizacion} a actualizar.
   * @param id                      Identificador {@link CertificadoAutorizacion}
   *                                a
   *                                actualizar.
   * @return Proyecto {@link CertificadoAutorizacion} actualizado
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-E','CSP-AUT-INV-ER')")
  public CertificadoAutorizacionOutput update(@Valid @RequestBody CertificadoAutorizacionInput certificadoAutorizacion,
      @PathVariable Long id) {
    log.debug("update(CertificadoAutorizacion certificadoAutorizacion, Long id) - start");
    CertificadoAutorizacionOutput returnValue = convert(service.update(convert(id, certificadoAutorizacion)));
    log.debug("update(CertificadoAutorizacion certificadoAutorizacion, Long id) - end");
    return returnValue;
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-B','CSP-AUT-INV-BR')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  private CertificadoAutorizacionOutput convert(CertificadoAutorizacion certificadoAutorizacion) {
    return modelMapper.map(certificadoAutorizacion, CertificadoAutorizacionOutput.class);
  }

  private CertificadoAutorizacion convert(CertificadoAutorizacionInput certificadoAutorizacionInput) {
    return convert(null, certificadoAutorizacionInput);
  }

  private CertificadoAutorizacion convert(Long id, CertificadoAutorizacionInput certificadoAutorizacionInput) {
    CertificadoAutorizacion certificadoAutorizacion = modelMapper.map(certificadoAutorizacionInput,
        CertificadoAutorizacion.class);
    certificadoAutorizacion.setId(id);
    return certificadoAutorizacion;
  }

}
