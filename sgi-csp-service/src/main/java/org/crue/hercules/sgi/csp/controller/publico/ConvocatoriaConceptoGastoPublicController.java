package org.crue.hercules.sgi.csp.controller.publico;

import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.service.ConvocatoriaConceptoGastoCodigoEcService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaConceptoGastoService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ConvocatoriaConceptoGastoPublicController
 */
@RestController
@RequestMapping(ConvocatoriaConceptoGastoPublicController.REQUEST_MAPPING)
@Slf4j
public class ConvocatoriaConceptoGastoPublicController {
  public static final String PATH_DELIMITER = "/";
  public static final String PATH_PUBLIC = PATH_DELIMITER + "public";
  public static final String REQUEST_MAPPING = PATH_PUBLIC + PATH_DELIMITER + "convocatoriaconceptogastos";

  public static final String PATH_ID = PATH_DELIMITER + "{id}";
  public static final String PATH_CODIGOS_ECONOMICOS = PATH_ID + PATH_DELIMITER + "convocatoriagastocodigoec";

  /** ConvocatoriaConceptoGasto service */
  private final ConvocatoriaConceptoGastoService service;
  /** ConvocatoriaConceptoGastoCodigoEc service */
  private final ConvocatoriaConceptoGastoCodigoEcService convocatoriaConceptoGastoCodigoEcService;

  /**
   * Instancia un nuevo ConvocatoriaConceptoGastoController.
   * 
   * @param service                                  {@link ConvocatoriaConceptoGastoService}
   * @param convocatoriaConceptoGastoCodigoEcService {@link ConvocatoriaConceptoGastoCodigoEcService}
   */
  public ConvocatoriaConceptoGastoPublicController(ConvocatoriaConceptoGastoService service,
      ConvocatoriaConceptoGastoCodigoEcService convocatoriaConceptoGastoCodigoEcService) {
    this.service = service;
    this.convocatoriaConceptoGastoCodigoEcService = convocatoriaConceptoGastoCodigoEcService;
  }

  /**
   * Devuelve el {@link ConvocatoriaConceptoGasto} con el id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaConceptoGasto}.
   * @return {@link ConvocatoriaConceptoGasto} correspondiente al id.
   */
  @GetMapping(PATH_ID)
  public ConvocatoriaConceptoGasto findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ConvocatoriaConceptoGasto returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ConvocatoriaConceptoGastoCodigoEc} permitidos de la
   * {@link ConvocatoriaConceptoGasto}.
   * 
   * @param id     Identificador de {@link ConvocatoriaConceptoGasto}.
   * @param paging pageable.
   * @return el listado de entidades {@link ConvocatoriaConceptoGastoCodigoEc}
   *         paginadas y filtradas de la {@link ConvocatoriaConceptoGasto}.
   */
  @GetMapping(PATH_CODIGOS_ECONOMICOS)
  public ResponseEntity<Page<ConvocatoriaConceptoGastoCodigoEc>> findAllConvocatoriaGastosCodigoEc(
      @PathVariable Long id, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaGastosCodigoEc(Long id, Pageable paging) - start");
    Page<ConvocatoriaConceptoGastoCodigoEc> page = convocatoriaConceptoGastoCodigoEcService
        .findAllByConvocatoriaConceptoGasto(id, paging);
    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaGastosCodigoEc(Long id, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaGastosCodigoEc(Long id, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}
