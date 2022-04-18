package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.LongPredicate;

import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.crue.hercules.sgi.prc.enums.TablaMaestraCVN;
import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.model.IndiceImpacto;
import org.crue.hercules.sgi.prc.model.IndiceImpacto.TipoRanking;
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
 * Servicio para la baremación de congresos
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class BaremacionCongresoService extends BaremacionCommonService {

  public BaremacionCongresoService(
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
    return TipoPuntuacion.CONGRESOS;
  }

  protected void loadPredicates() {

    LongPredicate isCongreso = getPredicateIsCongreso();
    LongPredicate isJornada = getPredicateIsJornada();
    LongPredicate isCongresoOrJornada = isCongreso.or(isJornada);

    // CONGRESO_GRUPO1_O_CORE_A_POR
    getHmTipoBaremoPredicates().put(TipoBaremo.CONGRESO_GRUPO1_O_CORE_A_POR, isCongresoOrJornada.and(
        getPredicateGIIorCOREwithRankingAPor()));

    // CONGRESO_GRUPO1_O_CORE_A
    getHmTipoBaremoPredicates().put(TipoBaremo.CONGRESO_GRUPO1_O_CORE_A, isCongresoOrJornada.and(
        getPredicateGIIorCOREwithRankingA()));

    // CONGRESO_INTERNACIONAL_POSTER_O_CARTEL
    LongPredicate isAmbitoUEOrInternacionalNoUE = getPredicateIsAmbitoUE()
        .or(getPredicateIsAmbitoInternacionalNoUE());

    getHmTipoBaremoPredicates().put(TipoBaremo.CONGRESO_INTERNACIONAL_POSTER_O_CARTEL,
        isCongresoOrJornada.and(isAmbitoUEOrInternacionalNoUE)
            .and(getPredicateIsTipoParticipacionParticipativoPoster()));

    // CONGRESO_INTERNACIONAL_PONENCIA_ORAL_O_ESCRITA
    getHmTipoBaremoPredicates().put(TipoBaremo.CONGRESO_INTERNACIONAL_PONENCIA_ORAL_O_ESCRITA, isCongresoOrJornada.and(
        isAmbitoUEOrInternacionalNoUE).and(getPredicateIsTipoParticipacionParticipativoPonenciaOral()));

    // CONGRESO_INTERNACIONAL_PLENARIA_O_KEYNOTE
    LongPredicate isTipoParticipacionPonenciaInvitadaOrPlenaria = getPredicateIsTipoParticipacionParticipativoPlenaria()
        .or(getPredicateIsTipoParticipacionParticipativoPonenciaInvitada());

    getHmTipoBaremoPredicates().put(TipoBaremo.CONGRESO_INTERNACIONAL_PLENARIA_O_KEYNOTE, isCongresoOrJornada.and(
        isAmbitoUEOrInternacionalNoUE).and(isTipoParticipacionPonenciaInvitadaOrPlenaria));

    // CONGRESO_NACIONAL_POSTER_O_CARTEL
    LongPredicate isAmbitoAutonomicoOrNacional = getPredicateIsAmbitoAutonomico().or(getPredicateIsAmbitoNacional());

    getHmTipoBaremoPredicates().put(TipoBaremo.CONGRESO_NACIONAL_POSTER_O_CARTEL, isCongresoOrJornada.and(
        isAmbitoAutonomicoOrNacional).and(getPredicateIsTipoParticipacionParticipativoPoster()));

    // CONGRESO_NACIONAL_PONENCIA_ORAL_O_ESCRITA
    getHmTipoBaremoPredicates().put(TipoBaremo.CONGRESO_NACIONAL_PONENCIA_ORAL_O_ESCRITA, isCongresoOrJornada.and(
        isAmbitoAutonomicoOrNacional).and(getPredicateIsTipoParticipacionParticipativoPonenciaOral()));

    // CONGRESO_NACIONAL_PLENARIA_O_KEYNOTE
    getHmTipoBaremoPredicates().put(TipoBaremo.CONGRESO_NACIONAL_PLENARIA_O_KEYNOTE, isCongresoOrJornada.and(
        isAmbitoAutonomicoOrNacional).and(isTipoParticipacionPonenciaInvitadaOrPlenaria));

    loadPredicatesExtra();
  }

  protected void loadPredicatesExtra() {
    getHmTipoBaremoPredicates().put(TipoBaremo.CONGRESO_RESUMEN_O_ABSTRACT, getPredicateIsResumenEnRevista());

    getHmTipoBaremoPredicates().put(TipoBaremo.CONGRESO_INTERNACIONAL_OBRA_COLECTIVA, getPredicateIsISSNForeign());

    getHmTipoBaremoPredicates().put(TipoBaremo.CONGRESO_NACIONAL_OBRA_COLECTIVA, getPredicateIsISBNNational());
  }

  /* -------------------- predicates -------------------- */
  private LongPredicate getPredicateIsAmbitoUE() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E060_010_020_080, TablaMaestraCVN.AMBITO_020, produccionCientificaId);
  }

  private LongPredicate getPredicateIsAmbitoInternacionalNoUE() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E060_010_020_080, TablaMaestraCVN.AMBITO_030, produccionCientificaId);
  }

  private LongPredicate getPredicateIsAmbitoAutonomico() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E060_010_020_080, TablaMaestraCVN.AMBITO_000, produccionCientificaId);
  }

  private LongPredicate getPredicateIsAmbitoNacional() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E060_010_020_080, TablaMaestraCVN.AMBITO_010, produccionCientificaId);
  }

  private LongPredicate getPredicateIsTipoParticipacionParticipativoPoster() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E060_010_020_050, TablaMaestraCVN.E060_010_020_050_970, produccionCientificaId);
  }

  private LongPredicate getPredicateIsTipoParticipacionParticipativoPonenciaOral() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E060_010_020_050, TablaMaestraCVN.E060_010_020_050_960, produccionCientificaId);
  }

  private LongPredicate getPredicateIsTipoParticipacionParticipativoPlenaria() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E060_010_020_050, TablaMaestraCVN.E060_010_020_050_080, produccionCientificaId);
  }

  private LongPredicate getPredicateIsTipoParticipacionParticipativoPonenciaInvitada() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E060_010_020_050, TablaMaestraCVN.E060_010_020_050_730, produccionCientificaId);
  }

  private LongPredicate getPredicateIsResumenEnRevista() {
    return produccionCientificaId -> isValorEqualsStringValue(CodigoCVN.RESUMEN_REVISTA, "true",
        produccionCientificaId);
  }

  private LongPredicate getPredicateIsCongreso() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E060_010_020_010, TablaMaestraCVN.E060_010_020_010_008, produccionCientificaId);
  }

  private LongPredicate getPredicateIsJornada() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E060_010_020_010, TablaMaestraCVN.E060_010_020_010_031, produccionCientificaId);
  }

  private LongPredicate getPredicateGIIorCOREwithRankingAPor() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.GII_GRIN_SCIE, TipoFuenteImpacto.CORE)).stream()
        .anyMatch(indiceImpacto -> isGIIWithClase1orCOREwithAPor(indiceImpacto, TipoRanking.A_POR));
  }

  private LongPredicate getPredicateGIIorCOREwithRankingA() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.GII_GRIN_SCIE, TipoFuenteImpacto.CORE)).stream()
        .anyMatch(indiceImpacto -> isGIIWithClase1orCOREwithAPor(indiceImpacto, TipoRanking.A));
  }

  protected boolean isGIIWithClase1orCOREwithAPor(IndiceImpacto indiceImpacto, TipoRanking tipoRankingCORE) {
    return ((indiceImpacto.getFuenteImpacto().equals(TipoFuenteImpacto.GII_GRIN_SCIE)
        && indiceImpacto.getRanking().equals(TipoRanking.CLASE1)) ||
        (indiceImpacto.getFuenteImpacto().equals(TipoFuenteImpacto.CORE)
            && indiceImpacto.getRanking().equals(tipoRankingCORE)));
  }

  protected LongPredicate getPredicateIsISBNNational() {
    return produccionCientificaId -> isValorCampoISBNNational(produccionCientificaId,
        CodigoCVN.E060_010_020_320);
  }

  protected LongPredicate getPredicateIsISSNForeign() {
    return produccionCientificaId -> !isValorCampoISBNNational(produccionCientificaId,
        CodigoCVN.E060_010_020_320);
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
