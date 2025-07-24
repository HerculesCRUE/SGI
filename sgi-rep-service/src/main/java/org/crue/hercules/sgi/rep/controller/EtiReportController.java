package org.crue.hercules.sgi.rep.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.eti.InformeEvaluacionReportInput;
import org.crue.hercules.sgi.rep.service.InformeActaReportService;
import org.crue.hercules.sgi.rep.service.InformeEvaluacionFavorableModificacionReportService;
import org.crue.hercules.sgi.rep.service.InformeEvaluacionFavorableNuevaReportService;
import org.crue.hercules.sgi.rep.service.InformeEvaluacionFavorableRatificacionReportService;
import org.crue.hercules.sgi.rep.service.InformeEvaluacionReportService;
import org.crue.hercules.sgi.rep.service.InformeEvaluacionRetrospectivaReportService;
import org.crue.hercules.sgi.rep.service.InformeEvaluadorReportService;
import org.crue.hercules.sgi.rep.service.InformeMemoriaReportService;
import org.crue.hercules.sgi.rep.util.SgiReportContextHolder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller de informes de ETI
 */
@RestController
@RequestMapping(EtiReportController.MAPPING)
@RequiredArgsConstructor
@Slf4j
public class EtiReportController {
  public static final String MAPPING = "/report/eti";
  private static final OutputType OUTPUT_TYPE_PDF = OutputType.PDF;

  private final InformeMemoriaReportService mxxReportService;
  private final InformeEvaluacionReportService informeEvaluacionReportService;
  private final InformeEvaluadorReportService informeEvaluadorReportService;
  private final InformeEvaluacionFavorableNuevaReportService informeFavorableMemoriaReportService;
  private final InformeActaReportService informeActaReportService;
  private final InformeEvaluacionRetrospectivaReportService informeEvaluacionRetrospectivaReportService;
  private final InformeEvaluacionFavorableModificacionReportService informeFavorableModificacionReportService;
  private final InformeEvaluacionFavorableRatificacionReportService informeFavorableRatificacionReportService;

  /**
   * Devuelve un informe M10, M20 o M30, Seguimiento anual, final y retrospectiva
   *
   * @param idMemoria    Identificador de la memoria
   * @param idFormulario Identificador del formulario
   * @param lang         Code del language
   * @return Resource
   */
  @GetMapping("/informe-mxx/{idMemoria}/{idFormulario}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-ESCR', 'ETI-MEM-INV-ERTR')")
  public ResponseEntity<Resource> getInformeMXX(@PathVariable Long idMemoria, @PathVariable Long idFormulario,
      @RequestParam(name = "l", required = false) String lang) {

    log.debug("getInformeMXX({}, {}) - start", idMemoria, idFormulario);

    SgiReportContextHolder.setLanguage(Language.fromCode(lang));

    byte[] reportContent = mxxReportService.getReport(idMemoria, idFormulario);
    ByteArrayResource archivo = new ByteArrayResource(reportContent);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, OUTPUT_TYPE_PDF.getType());
    headers.add(HttpHeaders.CONTENT_LANGUAGE, SgiReportContextHolder.getLanguage().getCode());

    log.debug("getInformeMXX({}, {}) - end", idMemoria, idFormulario);
    return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
  }

  /**
   * Devuelve un informe de evaluación
   *
   * @param idEvaluacion Identificador de la evaluación
   * @return Resource
   */
  @GetMapping("/informe-evaluacion/{idEvaluacion}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-INV-EVALR', 'ETI-EVC-EVALR')")
  public ResponseEntity<Resource> getInformeEvaluacion(@PathVariable Long idEvaluacion) {

    log.debug("getInformeEvaluacion(idEvaluacion) - start");

    byte[] reportContent = informeEvaluacionReportService.getReport(idEvaluacion);
    ByteArrayResource archivo = new ByteArrayResource(reportContent);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, OUTPUT_TYPE_PDF.getType());

    log.debug("getInformeEvaluacion(idEvaluacion) - end");
    return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
  }

  /**
   * Devuelve un informe de evaluador
   *
   * @param idEvaluacion Identificador de la evaluación
   * @return Resource
   */
  @GetMapping("/informe-ficha-evaluador/{idEvaluacion}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-INV-EVALR', 'ETI-EVC-EVALR')")
  public ResponseEntity<Resource> getInformeEvaluador(@PathVariable Long idEvaluacion) {

    log.debug("getInformeEvaluador(idEvaluacion) - start");

    byte[] reportContent = informeEvaluadorReportService.getReport(idEvaluacion);
    ByteArrayResource archivo = new ByteArrayResource(reportContent);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, OUTPUT_TYPE_PDF.getType());

    log.debug("getInformeEvaluador(idEvaluacion) - end");
    return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
  }

  /**
   * Devuelve un informe favorable memoria
   *
   * @param idEvaluacion Identificador de la evaluación
   * 
   * @return Resource
   */
  @GetMapping("/informe-favorable-memoria/{idEvaluacion}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-INV-EVALR', 'ETI-EVC-EVALR')")
  public ResponseEntity<Resource> getInformeFavorableMemoria(@PathVariable Long idEvaluacion) {

    log.debug("getInformeFavorableMemoria(idEvaluacion) - start");

    byte[] reportContent = informeFavorableMemoriaReportService.getReport(idEvaluacion);
    ByteArrayResource archivo = new ByteArrayResource(reportContent);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, OUTPUT_TYPE_PDF.getType());
    log.debug("getInformeFavorableMemoria(idEvaluacion) - end");

    return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
  }

  /**
   * Devuelve un informe acta
   *
   * @param idActa Identificador de la evaluación
   * @param lang   code language
   * @return Resource
   */
  @GetMapping("/informe-acta/{idActa}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-DES', 'ETI-ACT-DESR', 'ETI-ACT-INV-DESR')")
  public ResponseEntity<Resource> getInformeActa(@PathVariable Long idActa,
      @RequestParam(name = "l", required = false) String lang) {

    log.debug("getInformeActa(idActa) - start");

    SgiReportContextHolder.setLanguage(Language.fromCode(lang));

    byte[] reportContent = informeActaReportService.getReport(idActa);
    ByteArrayResource archivo = new ByteArrayResource(reportContent);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, OUTPUT_TYPE_PDF.getType());

    log.debug("getInformeActa(idActa) - end");
    return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
  }

  /**
   * Devuelve un informe evaluación retrospectiva
   *
   * @param input InformeEvaluacionReportInput con Identificador de la evaluación
   *              y fecha del informe
   * @return Resource
   */
  @PostMapping("/informe-evaluacion-retrospectiva")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL','ETI-MEM-INV-ERTR')")
  public ResponseEntity<Resource> getInformeEvaluacionRetrospectiva(
      @Valid @RequestBody InformeEvaluacionReportInput input) {

    log.debug("getInformeEvaluacionRetrospectiva(input) - start");

    byte[] reportContent = informeEvaluacionRetrospectivaReportService.getReport(
        input.getIdEvaluacion());
    ByteArrayResource archivo = new ByteArrayResource(reportContent);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, OUTPUT_TYPE_PDF.getType());

    log.debug("getInformeEvaluacionRetrospectiva(input) - end");
    return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
  }

  /**
   * Devuelve un informe favorable modificación
   *
   * @param idEvaluacion Identificador de la evaluación
   * 
   * @return Resource
   */
  @GetMapping("/informe-favorable-modificacion/{idEvaluacion}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-INV-EVALR')")
  public ResponseEntity<Resource> getInformeFavorableModificacion(@PathVariable Long idEvaluacion) {

    log.debug("getInformeFavorableModificacion(idEvaluacion) - start");

    byte[] reportContent = informeFavorableModificacionReportService.getReport(
        idEvaluacion);
    ByteArrayResource archivo = new ByteArrayResource(reportContent);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, OUTPUT_TYPE_PDF.getType());

    log.debug("getInformeFavorableModificacion(idEvaluacion) - end");
    return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
  }

  /**
   * Devuelve un informe favorable ratificación
   *
   * @param idEvaluacion Identificador de la evaluación
   * 
   * @return Resource
   */
  @GetMapping("/informe-favorable-ratificacion/{idEvaluacion}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-INV-EVALR')")
  public ResponseEntity<Resource> getInformeFavorableRatificacion(@PathVariable Long idEvaluacion) {

    log.debug("getInformeFavorableRatificacion(idEvaluacion) - start");

    byte[] reportContent = informeFavorableRatificacionReportService.getReport(
        idEvaluacion);
    ByteArrayResource archivo = new ByteArrayResource(reportContent);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, OUTPUT_TYPE_PDF.getType());

    log.debug("getInformeFavorableRatificacion(idEvaluacion) - end");
    return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
  }

}