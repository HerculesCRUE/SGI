package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.util.function.LongPredicate;

import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.crue.hercules.sgi.prc.enums.TablaMaestraCVN;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador.TipoPuntuacion;
import org.crue.hercules.sgi.prc.repository.AliasEnumeradoRepository;
import org.crue.hercules.sgi.prc.repository.AutorGrupoRepository;
import org.crue.hercules.sgi.prc.repository.AutorRepository;
import org.crue.hercules.sgi.prc.repository.BaremoRepository;
import org.crue.hercules.sgi.prc.repository.CampoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.IndiceExperimentalidadRepository;
import org.crue.hercules.sgi.prc.repository.IndiceImpactoRepository;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionBaremoItemRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionItemInvestigadorRepository;
import org.crue.hercules.sgi.prc.repository.ValorCampoRepository;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiCspService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiSgpService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la baremación de obras artísticas
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class BaremacionObraArtisticaService extends BaremacionCommonService {

  @Autowired
  public BaremacionObraArtisticaService(
      AliasEnumeradoRepository aliasEnumeradoRepository,
      ProduccionCientificaRepository produccionCientificaRepository,
      PuntuacionBaremoItemRepository puntuacionBaremoItemRepository,
      PuntuacionItemInvestigadorRepository puntuacionItemInvestigadorRepository,
      IndiceExperimentalidadRepository indiceExperimentalidadRepository,
      BaremoRepository baremoRepository,
      AutorRepository autorRepository,
      AutorGrupoRepository autorGrupoRepository,
      CampoProduccionCientificaRepository campoProduccionCientificaRepository,
      ValorCampoRepository valorCampoRepository,
      IndiceImpactoRepository indiceImpactoRepository,
      ProduccionCientificaBuilderService produccionCientificaBuilderService,
      SgiApiSgpService sgiApiSgpService,
      SgiApiCspService sgiApiCspService,
      ConvocatoriaBaremacionLogService convocatoriaBaremacionLogService,
      ModelMapper modelMapper,
      SgiConfigProperties sgiConfigProperties) {
    super(aliasEnumeradoRepository,
        produccionCientificaRepository,
        puntuacionBaremoItemRepository,
        puntuacionItemInvestigadorRepository,
        indiceExperimentalidadRepository,
        baremoRepository,
        autorRepository, autorGrupoRepository,
        campoProduccionCientificaRepository,
        valorCampoRepository,
        indiceImpactoRepository,
        produccionCientificaBuilderService,
        sgiApiSgpService,
        sgiApiCspService,
        convocatoriaBaremacionLogService,
        modelMapper,
        sgiConfigProperties);

    loadPredicates();
  }

  protected TipoPuntuacion getTipoPuntuacion() {
    return TipoPuntuacion.OBRAS_ARTISTICAS;
  }

  protected void loadPredicates() {

    LongPredicate isExposicion = getPredicateIsExposicion();
    LongPredicate isDisenio = getPredicateIsDisenio();
    LongPredicate isEspania = getPredicateIsPaisEspania();
    LongPredicate isNotEspania = getPredicateIsPaisEspania().negate();
    LongPredicate isCatalogo = getPredicateIsCatalogo();
    LongPredicate isComisarioExposicion = getPredicateIsComisarioExposicion();
    LongPredicate isNotCatalogo = isCatalogo.negate();
    LongPredicate isNotComisarioExposicion = isComisarioExposicion.negate();
    LongPredicate isCatalogoOrComisarioExposicion = isCatalogo.or(isComisarioExposicion);
    LongPredicate isColectiva = getPredicateIsColectiva();
    LongPredicate isNotColectiva = getPredicateIsNotColectiva();

    // OBRA_ARTISTICA_EXP_GRUPO1_INDIVIDUAL
    getHmTipoBaremoPredicates().put(TipoBaremo.OBRA_ARTISTICA_EXP_GRUPO1_INDIVIDUAL,
        isExposicion
            .and(isNotEspania)
            .and(isCatalogoOrComisarioExposicion)
            .and(isNotColectiva));

    // OBRA_ARTISTICA_EXP_GRUPO1_COLECTIVA
    getHmTipoBaremoPredicates().put(TipoBaremo.OBRA_ARTISTICA_EXP_GRUPO1_COLECTIVA, isExposicion
        .and(isNotEspania)
        .and(isCatalogoOrComisarioExposicion)
        .and(isColectiva));

    // OBRA_ARTISTICA_EXP_GRUPO2_INDIVIDUAL
    getHmTipoBaremoPredicates().put(TipoBaremo.OBRA_ARTISTICA_EXP_GRUPO2_INDIVIDUAL, isExposicion
        .and(isEspania)
        .and(isCatalogoOrComisarioExposicion)
        .and(isNotColectiva));

    // OBRA_ARTISTICA_EXP_GRUPO2_COLECTIVA
    getHmTipoBaremoPredicates().put(TipoBaremo.OBRA_ARTISTICA_EXP_GRUPO2_COLECTIVA, isExposicion
        .and(isEspania)
        .and(isCatalogoOrComisarioExposicion)
        .and(isColectiva));

    // OBRA_ARTISTICA_EXP_GRUPO3
    getHmTipoBaremoPredicates().put(TipoBaremo.OBRA_ARTISTICA_EXP_GRUPO3, isExposicion
        .and(isNotCatalogo)
        .and(isNotComisarioExposicion)
        .and(isColectiva));

    // OBRA_ARTISTICA_EXP_GRUPO4
    getHmTipoBaremoPredicates().put(TipoBaremo.OBRA_ARTISTICA_EXP_GRUPO4, isExposicion
        .and(isNotCatalogo)
        .and(isNotComisarioExposicion)
        .and(isNotColectiva));

    // OBRA_ARTISTICA_DISENIO_GRUPO1
    LongPredicate isComunidadMurcia = getPredicateIsComunidadMurcia();
    LongPredicate isNotComunidadMurcia = isComunidadMurcia.negate();
    LongPredicate isNotEspaniaOrNotComunidadMurcia = isNotEspania.or(isEspania.and(isNotComunidadMurcia));

    getHmTipoBaremoPredicates().put(TipoBaremo.OBRA_ARTISTICA_DISENIO_GRUPO1, isDisenio
        .and(isNotEspaniaOrNotComunidadMurcia)
        .and(isCatalogo));

    // OBRA_ARTISTICA_DISENIO_GRUPO2
    getHmTipoBaremoPredicates().put(TipoBaremo.OBRA_ARTISTICA_DISENIO_GRUPO2, isDisenio
        .and(isEspania)
        .and(isComunidadMurcia)
        .and(isCatalogo));

    // OBRA_ARTISTICA_DISENIO_GRUPO3
    getHmTipoBaremoPredicates().put(TipoBaremo.OBRA_ARTISTICA_DISENIO_GRUPO3, isDisenio
        .and(isEspania)
        .and(isComunidadMurcia)
        .and(isCatalogo));

    getHmTipoBaremoPredicates().put(TipoBaremo.OBRA_ARTISTICA_DISENIO_GRUPO3, isDisenio
        .and(isNotCatalogo));
  }

  /* -------------------- predicates -------------------- */

  private LongPredicate getPredicateIsExposicion() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.TIPO_OBRA, TablaMaestraCVN.TIPO_OBRA_EXPOSICION, produccionCientificaId);
  }

  private LongPredicate getPredicateIsDisenio() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.TIPO_OBRA, TablaMaestraCVN.TIPO_OBRA_DISENO, produccionCientificaId);
  }

  private LongPredicate getPredicateIsPaisEspania() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E050_020_030_040, TablaMaestraCVN.PAIS_724, produccionCientificaId);
  }

  private LongPredicate getPredicateIsComunidadMurcia() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E050_020_030_050, TablaMaestraCVN.COMUNIDAD_ES62, produccionCientificaId);
  }

  private LongPredicate getPredicateIsColectiva() {
    return produccionCientificaId -> isValorEqualsStringValue(CodigoCVN.COLECTIVA, "true",
        produccionCientificaId);
  }

  private LongPredicate getPredicateIsNotColectiva() {
    return produccionCientificaId -> isValorEqualsStringValue(CodigoCVN.COLECTIVA, "false",
        produccionCientificaId);
  }

  private LongPredicate getPredicateIsCatalogo() {
    return produccionCientificaId -> isValorEqualsStringValue(CodigoCVN.E050_020_030_100, "true",
        produccionCientificaId);
  }

  private LongPredicate getPredicateIsComisarioExposicion() {
    return produccionCientificaId -> isValorEqualsStringValue(CodigoCVN.E050_020_030_110, "true",
        produccionCientificaId);
  }

  protected BigDecimal evaluateBaremoModulador(BaremacionInput baremacionInput) {
    log.debug("evaluateBaremoModulador(baremacionInput) - start");

    log.debug("evaluateBaremoModulador(baremacionInput) - end");
    return new BigDecimal("1.00");
  }

  protected BigDecimal evaluateBaremoExtra(BaremacionInput baremacionInput) {
    log.debug("evaluateBaremoExtra(baremacionInput) - start");

    log.debug("evaluateBaremoExtra(baremacionInput) - end");
    return BigDecimal.ZERO;
  }

}
