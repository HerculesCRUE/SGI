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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la baremación de dirección de tesis
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class BaremacionDireccionTesisService extends BaremacionCommonService {

  public BaremacionDireccionTesisService(
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
    return TipoPuntuacion.DIRECCION_TESIS;
  }

  protected void loadPredicates() {

    // DIRECCION_TESIS_TESIS
    getHmTipoBaremoPredicates().put(TipoBaremo.DIRECCION_TESIS_TESIS, getPredicateIsTesisDoctoral());

    // DIRECCION_TESIS_TESINA_O_DEA_O_TFM
    LongPredicate isNotTesis = getPredicateIsTesina().or(getPredicateIsTFM()).or(getPredicateIsDEA())
        .or(getPredicateIsOther());

    getHmTipoBaremoPredicates().put(TipoBaremo.DIRECCION_TESIS_TESINA_O_DEA_O_TFM, isNotTesis);

    loadPredicatesExtra();
  }

  protected void loadPredicatesExtra() {
    getHmTipoBaremoPredicates().put(TipoBaremo.DIRECCION_TESIS_MENCION_INDUSTRIAL,
        getPredicateIsTesisDoctoral().and(getPredicateIsMencionIndustrial()));

    getHmTipoBaremoPredicates().put(TipoBaremo.DIRECCION_TESIS_MENCION_INTERNACIONAL,
        getPredicateIsTesisDoctoral().and(getPredicateIsMencionInternacional()));
  }

  /* -------------------- predicates -------------------- */

  private LongPredicate getPredicateIsTesisDoctoral() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E030_040_000_010, TablaMaestraCVN.E030_040_000_010_067, produccionCientificaId);
  }

  private LongPredicate getPredicateIsTesina() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E030_040_000_010, TablaMaestraCVN.E030_040_000_010_066, produccionCientificaId);
  }

  private LongPredicate getPredicateIsTFM() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E030_040_000_010, TablaMaestraCVN.E030_040_000_010_055, produccionCientificaId);
  }

  private LongPredicate getPredicateIsDEA() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E030_040_000_010, TablaMaestraCVN.E030_040_000_010_071, produccionCientificaId);
  }

  private LongPredicate getPredicateIsOther() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E030_040_000_010, TablaMaestraCVN.E030_040_000_010_OTHERS, produccionCientificaId);
  }

  private LongPredicate getPredicateIsMencionIndustrial() {
    return produccionCientificaId -> isValorEqualsStringValue(CodigoCVN.MENCION_INDUSTRIAL, "true",
        produccionCientificaId);
  }

  private LongPredicate getPredicateIsMencionInternacional() {
    return produccionCientificaId -> isValorEqualsStringValue(CodigoCVN.MENCION_INTERNACIONAL, "true",
        produccionCientificaId);
  }

  protected BigDecimal evaluateBaremoModulador(BaremacionInput baremacionInput) {
    log.debug("evaluateBaremoModulador(baremacionInput) - start");

    log.debug("evaluateBaremoModulador(baremacionInput) - end");
    return new BigDecimal("1.00");
  }

  protected BigDecimal evaluateBaremoExtra(BaremacionInput baremacionInput) {
    log.debug("evaluateBaremoExtra(baremacionInput) - start");
    BigDecimal puntos = BigDecimal.ZERO;

    TipoBaremo tipoBaremo = baremacionInput.getBaremo().getConfiguracionBaremo().getTipoBaremo();

    if (evaluateProduccionCientificaByTipoBaremo(baremacionInput, tipoBaremo)) {
      puntos = baremacionInput.getBaremo().getPuntos();
    }

    log.debug("evaluateBaremoExtra(baremacionInput) - end");
    return puntos;
  }

}
