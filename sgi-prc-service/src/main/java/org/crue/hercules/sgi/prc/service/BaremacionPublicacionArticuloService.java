package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.LongPredicate;

import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.crue.hercules.sgi.prc.enums.TablaMaestraCVN;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.model.Modulador.TipoModulador;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador.TipoPuntuacion;
import org.crue.hercules.sgi.prc.repository.AliasEnumeradoRepository;
import org.crue.hercules.sgi.prc.repository.AutorGrupoRepository;
import org.crue.hercules.sgi.prc.repository.AutorRepository;
import org.crue.hercules.sgi.prc.repository.BaremoRepository;
import org.crue.hercules.sgi.prc.repository.CampoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.IndiceExperimentalidadRepository;
import org.crue.hercules.sgi.prc.repository.IndiceImpactoRepository;
import org.crue.hercules.sgi.prc.repository.ModuladorRepository;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionBaremoItemRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionItemInvestigadorRepository;
import org.crue.hercules.sgi.prc.repository.TipoFuenteImpactoCuartilRepository;
import org.crue.hercules.sgi.prc.repository.ValorCampoRepository;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiCspService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiSgoService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiSgpService;
import org.crue.hercules.sgi.prc.util.ProduccionCientificaFieldFormatUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la baremación de publicaciones de artículos
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class BaremacionPublicacionArticuloService extends BaremacionPublicacionAndComiteService {

  @Autowired
  public BaremacionPublicacionArticuloService(
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
      TipoFuenteImpactoCuartilRepository tipoFuenteImpactoCuartilRepository,
      ProduccionCientificaBuilderService produccionCientificaBuilderService,
      SgiApiSgpService sgiApiSgpService,
      SgiApiCspService sgiApiCspService,
      ConvocatoriaBaremacionLogService convocatoriaBaremacionLogService,
      ModelMapper modelMapper,
      SgiConfigProperties sgiConfigProperties,
      SgiApiSgoService sgiApiSgoService,
      ModuladorRepository moduladorRepository) {
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
        tipoFuenteImpactoCuartilRepository,
        produccionCientificaBuilderService,
        sgiApiSgpService,
        sgiApiCspService,
        convocatoriaBaremacionLogService,
        modelMapper,
        sgiConfigProperties,
        sgiApiSgoService,
        moduladorRepository);

    loadPredicates();
  }

  protected TipoPuntuacion getTipoPuntuacion() {
    return TipoPuntuacion.ARTICULOS;
  }

  protected void loadPredicates() {
    loadArticulosPredicates();
  }

  private void loadArticulosPredicates() {
    loadArticulosJCRPredicates();
    loadArticulosSCIMAGOPredicates();
    loadArticulosCITECPredicates();
    loadArticulosSCOPUSPredicates();
    loadArticulosSCIMAGOPredicates();
    loadArticulosERIHPredicates();
    loadArticulosDIALNETPredicates();
    loadArticulosMIARPredicates();
    loadArticulosFECYTPredicates();

    // ARTICULO
    LongPredicate hasFuenteImpactoOtras = getPredicateHasFuenteImpactoArticulosOrComitesOTRAS();
    LongPredicate isISNNNotEmpty = getPredicateIsISNNNotEmpty();
    LongPredicate isTipoProduccionEqualsArticuloCientifico = getPredicateIsArticuloCientifico();

    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO, isISNNNotEmpty.and(hasFuenteImpactoOtras)
        .and(isTipoProduccionEqualsArticuloCientifico));

    loadArticulosExtraPredicates();
  }

  private void loadArticulosExtraPredicates() {

    // ARTICULO_JCR_Q1_DECIL1
    LongPredicate hasFuenteImpactoJCR = getPredicateHasFuenteImpactoJCR();
    LongPredicate isISNNNotEmpty = getPredicateIsISNNNotEmpty();
    LongPredicate isTipoProduccionEqualsArticuloCientifico = getPredicateIsArticuloCientifico();

    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_JCR_Q1_DECIL1,
        isISNNNotEmpty.and(hasFuenteImpactoJCR)
            .and(getPredicateHasPosicionRevistaLessEqualThan10AndJCR())
            .and(isTipoProduccionEqualsArticuloCientifico));

    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_NATURE_O_SCIENCE,
        getPredicateIsISSNNature().or(getPredicateIsISSNScience()));
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_INDICE_NORMALIZADO,
        getPredicateIsIndiceNormalizadoGreatherThan1());
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_LIDERAZGO, getPredicateHasLiderazgo());
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_EXCELENCIA, getPredicateIsPublicacionRelevante());
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_OPEN_ACCESS_ALL, getPredicateIsTipoOpenAccessAll());
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_OPEN_ACCESS_GOLD, getPredicateIsTipoOpenAccessGold());
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_OPEN_ACCESS_HYBRID_GOLD,
        getPredicateIsTipoOpenAccessHybridGold());
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_OPEN_ACCESS_BRONZE, getPredicateIsTipoOpenAccessBronze());
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_OPEN_ACCESS_GREEN, getPredicateIsTipoOpenAccessGreen());
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_INTERNACIONALIZACION, getPredicateIsInternacional());
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_INTERDISCIPLINARIEDAD, getPredicateIsInterdisciplinar());
  }

  private void loadArticulosJCRPredicates() {
    // ARTICULO_JCR_Q1
    LongPredicate hasFuenteImpactoJCR = getPredicateHasFuenteImpactoJCR();
    LongPredicate isISNNNotEmpty = getPredicateIsISNNNotEmpty();
    LongPredicate isTipoProduccionEqualsArticuloCientifico = getPredicateIsArticuloCientifico();

    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_JCR_Q1,
        isISNNNotEmpty.and(hasFuenteImpactoJCR)
            .and(getPredicateHasPosicionRevistaLessEqualThan25AndJCR())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_JCR_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_JCR_Q2,
        isISNNNotEmpty.and(hasFuenteImpactoJCR)
            .and(getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndJCR())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_JCR_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_JCR_Q3,
        isISNNNotEmpty.and(hasFuenteImpactoJCR)
            .and(getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndJCR())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_JCR_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_JCR_Q4,
        isISNNNotEmpty.and(hasFuenteImpactoJCR)
            .and(getPredicateHasPosicionRevistaGreatherThan75AndJCR())
            .and(isTipoProduccionEqualsArticuloCientifico));
  }

  private void loadArticulosSCOPUSPredicates() {
    // ARTICULO_SCOPUS_Q1
    LongPredicate hasFuenteImpactoSCOPUS = getPredicateHasFuenteImpactoSCOPUS();
    LongPredicate isISNNNotEmpty = getPredicateIsISNNNotEmpty();
    LongPredicate isTipoProduccionEqualsArticuloCientifico = getPredicateIsArticuloCientifico();

    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_SCOPUS_Q1,
        isISNNNotEmpty.and(hasFuenteImpactoSCOPUS)
            .and(getPredicateHasPosicionRevistaLessEqualThan25AndSCOPUS())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_SCOPUS_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_SCOPUS_Q2,
        isISNNNotEmpty.and(hasFuenteImpactoSCOPUS)
            .and(getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndSCOPUS())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_SCOPUS_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_SCOPUS_Q3,
        isISNNNotEmpty.and(hasFuenteImpactoSCOPUS)
            .and(getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndSCOPUS())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_SCOPUS_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_SCOPUS_Q4,
        isISNNNotEmpty.and(hasFuenteImpactoSCOPUS)
            .and(getPredicateHasPosicionRevistaGreatherThan75AndSCOPUS())
            .and(isTipoProduccionEqualsArticuloCientifico));
  }

  private void loadArticulosSCIMAGOPredicates() {
    // ARTICULO_SCIMAGO_Q1
    LongPredicate hasFuenteImpactoSCIMAGO = getPredicateHasFuenteImpactoSCIMAGO();
    LongPredicate isISNNNotEmpty = getPredicateIsISNNNotEmpty();
    LongPredicate isTipoProduccionEqualsArticuloCientifico = getPredicateIsArticuloCientifico();

    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_SCIMAGO_Q1,
        isISNNNotEmpty.and(hasFuenteImpactoSCIMAGO)
            .and(getPredicateHasPosicionRevistaLessEqualThan25AndSCIMAGO())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_SCIMAGO_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_SCIMAGO_Q2,
        isISNNNotEmpty.and(hasFuenteImpactoSCIMAGO)
            .and(getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndSCIMAGO())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_SCIMAGO_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_SCIMAGO_Q3,
        isISNNNotEmpty.and(hasFuenteImpactoSCIMAGO)
            .and(getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndSCIMAGO())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_SCIMAGO_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_SCIMAGO_Q4,
        isISNNNotEmpty.and(hasFuenteImpactoSCIMAGO)
            .and(getPredicateHasPosicionRevistaGreatherThan75AndSCIMAGO())
            .and(isTipoProduccionEqualsArticuloCientifico));
  }

  private void loadArticulosCITECPredicates() {
    // ARTICULO_CITEC_Q1
    LongPredicate hasFuenteImpactoCITEC = getPredicateHasFuenteImpactoCITEC();
    LongPredicate isISNNNotEmpty = getPredicateIsISNNNotEmpty();
    LongPredicate isTipoProduccionEqualsArticuloCientifico = getPredicateIsArticuloCientifico();

    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_CITEC_Q1,
        isISNNNotEmpty.and(hasFuenteImpactoCITEC)
            .and(getPredicateIsEqualQ1AndCITEC())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_CITEC_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_CITEC_Q2,
        isISNNNotEmpty.and(hasFuenteImpactoCITEC)
            .and(getPredicateIsEqualQ2AndCITEC())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_CITEC_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_CITEC_Q3,
        isISNNNotEmpty.and(hasFuenteImpactoCITEC)
            .and(getPredicateIsEqualQ3AndCITEC())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_CITEC_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_CITEC_Q4,
        isISNNNotEmpty.and(hasFuenteImpactoCITEC)
            .and(getPredicateIsEqualQ4AndCITEC())
            .and(isTipoProduccionEqualsArticuloCientifico));
  }

  private void loadArticulosERIHPredicates() {
    // ARTICULO_ERIH_Q1
    LongPredicate hasFuenteImpactoERIH = getPredicateHasFuenteImpactoERIH();
    LongPredicate isISNNNotEmpty = getPredicateIsISNNNotEmpty();
    LongPredicate isTipoProduccionEqualsArticuloCientifico = getPredicateIsArticuloCientifico();

    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_ERIH_Q1,
        isISNNNotEmpty.and(hasFuenteImpactoERIH)
            .and(getPredicateIsEqualQ1AndERIH())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_ERIH_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_ERIH_Q2,
        isISNNNotEmpty.and(hasFuenteImpactoERIH)
            .and(getPredicateIsEqualQ2AndERIH())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_ERIH_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_ERIH_Q3,
        isISNNNotEmpty.and(hasFuenteImpactoERIH)
            .and(getPredicateIsEqualQ3AndERIH())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_ERIH_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_ERIH_Q4,
        isISNNNotEmpty.and(hasFuenteImpactoERIH)
            .and(getPredicateIsEqualQ4AndERIH())
            .and(isTipoProduccionEqualsArticuloCientifico));
  }

  private void loadArticulosDIALNETPredicates() {
    // ARTICULO_DIALNET_Q1
    LongPredicate hasFuenteImpactoDIALNET = getPredicateHasFuenteImpactoDIALNET();
    LongPredicate isISNNNotEmpty = getPredicateIsISNNNotEmpty();
    LongPredicate isTipoProduccionEqualsArticuloCientifico = getPredicateIsArticuloCientifico();

    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_DIALNET_Q1,
        isISNNNotEmpty.and(hasFuenteImpactoDIALNET)
            .and(getPredicateHasPosicionRevistaLessEqualThan25AndDIALNET())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_DIALNET_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_DIALNET_Q2,
        isISNNNotEmpty.and(hasFuenteImpactoDIALNET)
            .and(getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndDIALNET())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_DIALNET_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_DIALNET_Q3,
        isISNNNotEmpty.and(hasFuenteImpactoDIALNET)
            .and(getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndDIALNET())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_DIALNET_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_DIALNET_Q4,
        isISNNNotEmpty.and(hasFuenteImpactoDIALNET)
            .and(getPredicateHasPosicionRevistaGreatherThan75AndDIALNET())
            .and(isTipoProduccionEqualsArticuloCientifico));
  }

  private void loadArticulosMIARPredicates() {
    // ARTICULO_MIAR_Q1
    LongPredicate hasFuenteImpactoMIAR = getPredicateHasFuenteImpactoMIAR();
    LongPredicate isISNNNotEmpty = getPredicateIsISNNNotEmpty();
    LongPredicate isTipoProduccionEqualsArticuloCientifico = getPredicateIsArticuloCientifico();

    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_MIAR_Q1,
        isISNNNotEmpty.and(hasFuenteImpactoMIAR)
            .and(getPredicateHasPosicionRevistaLessEqualThan25AndMIAR())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_MIAR_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_MIAR_Q2,
        isISNNNotEmpty.and(hasFuenteImpactoMIAR)
            .and(getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndMIAR())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_MIAR_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_MIAR_Q3,
        isISNNNotEmpty.and(hasFuenteImpactoMIAR)
            .and(getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndMIAR())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_MIAR_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_MIAR_Q4,
        isISNNNotEmpty.and(hasFuenteImpactoMIAR)
            .and(getPredicateHasPosicionRevistaGreatherThan75AndMIAR())
            .and(isTipoProduccionEqualsArticuloCientifico));
  }

  private void loadArticulosFECYTPredicates() {
    // ARTICULO_FECYT_Q1
    LongPredicate hasFuenteImpactoFECYT = getPredicateHasFuenteImpactoFECYT();
    LongPredicate isISNNNotEmpty = getPredicateIsISNNNotEmpty();
    LongPredicate isTipoProduccionEqualsArticuloCientifico = getPredicateIsArticuloCientifico();

    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_FECYT_Q1,
        isISNNNotEmpty.and(hasFuenteImpactoFECYT)
            .and(getPredicateHasPosicionRevistaLessEqualThan25AndFECYT())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_FECYT_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_FECYT_Q2,
        isISNNNotEmpty.and(hasFuenteImpactoFECYT)
            .and(getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndFECYT())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_FECYT_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_FECYT_Q3,
        isISNNNotEmpty.and(hasFuenteImpactoFECYT)
            .and(getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndFECYT())
            .and(isTipoProduccionEqualsArticuloCientifico));

    // ARTICULO_FECYT_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.ARTICULO_FECYT_Q4,
        isISNNNotEmpty.and(hasFuenteImpactoFECYT)
            .and(getPredicateHasPosicionRevistaGreatherThan75AndFECYT())
            .and(isTipoProduccionEqualsArticuloCientifico));
  }

  protected BigDecimal evaluateBaremoModulador(BaremacionInput baremacionInput) {
    log.debug("evaluateBaremoModulador(baremacionInput) - start");

    BigDecimal puntos = BigDecimal.ZERO;

    TipoBaremo tipoBaremo = baremacionInput.getBaremo().getConfiguracionBaremo().getTipoBaremo();

    Long produccionCientificaId = baremacionInput.getProduccionCientificaId();
    boolean isTipoProduccionEqualsArticuloCientifico = getPredicateIsArticuloCientifico().test(produccionCientificaId);

    if (isTipoProduccionEqualsArticuloCientifico && tipoBaremo.equals(TipoBaremo.ARTICULO_NUMERO_AUTORES)) {
      return evaluateModuladorByTipo(baremacionInput, TipoModulador.NUMERO_AUTORES);
    }

    if (isTipoProduccionEqualsArticuloCientifico && tipoBaremo.equals(TipoBaremo.ARTICULO_AREAS)) {
      return evaluateModuladorByTipo(baremacionInput, TipoModulador.AREAS);
    }

    log.debug("evaluateBaremoModulador(baremacionInput) - end");
    return puntos;
  }

  protected BigDecimal evaluateBaremoExtra(BaremacionInput baremacionInput) {
    log.debug("evaluateBaremoExtra(baremacionInput) - start");

    BigDecimal puntos = BigDecimal.ZERO;

    TipoBaremo tipoBaremo = baremacionInput.getBaremo().getConfiguracionBaremo().getTipoBaremo();

    Long produccionCientificaId = baremacionInput.getProduccionCientificaId();
    boolean isTipoProduccionEqualsArticuloCientifico = getPredicateIsArticuloCientifico().test(produccionCientificaId);

    if (isTipoProduccionEqualsArticuloCientifico
        && evaluateProduccionCientificaByTipoBaremo(baremacionInput, tipoBaremo)) {
      puntos = baremacionInput.getBaremo().getPuntos();
    }

    log.debug("evaluateBaremoExtra(baremacionInput) - end");

    return puntos;
  }

  /* -------------------- predicates -------------------- */
  private LongPredicate getPredicateIsISNNNotEmpty() {
    return produccionCientificaId -> isValorCampoNotEmpty(produccionCientificaId, CodigoCVN.E060_010_010_160);
  }

  private LongPredicate getPredicateIsArticuloCientifico() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E060_010_010_010, TablaMaestraCVN.E060_010_010_010_020, produccionCientificaId);
  }

  private LongPredicate getPredicateIsISSNNature() {
    return produccionCientificaId -> isValorEqualsStringValue(
        CodigoCVN.E060_010_010_160, "0028-0836", produccionCientificaId)
        || isValorEqualsStringValue(CodigoCVN.E060_010_010_160, "1476-4687", produccionCientificaId);
  }

  private LongPredicate getPredicateIsISSNScience() {
    return produccionCientificaId -> isValorEqualsStringValue(
        CodigoCVN.E060_010_010_160, "1095-9203", produccionCientificaId);
  }

  private LongPredicate getPredicateIsIndiceNormalizadoGreatherThan1() {
    return produccionCientificaId -> isValorGreatherThanIntegerValue(
        CodigoCVN.INDICE_NORMALIZADO, "1", produccionCientificaId);
  }

  protected boolean isValorGreatherThanIntegerValue(CodigoCVN codigoCVN, String numberValue,
      Long produccionCientificaId) {
    return findValoresByCampoProduccionCientificaId(codigoCVN, produccionCientificaId).stream()
        .anyMatch(valorCampo -> valorCampo.getValor()
            .compareTo(ProduccionCientificaFieldFormatUtil.formatNumber(numberValue)) > 0);
  }

  private LongPredicate getPredicateHasLiderazgo() {
    return this::hasAutorBaremable;
  }

  private Boolean hasAutorBaremable(Long produccionCientificaId) {
    // Buscamos si viene informado E060_010_010_390 y pertenece a la universidad
    if (findValoresByCampoProduccionCientificaId(
        CodigoCVN.E060_010_010_390, produccionCientificaId).stream()
        .anyMatch(valorCampo -> isPersonaRefAndBaremable(valorCampo.getValor()))) {
      return Boolean.TRUE;
    }

    List<Autor> autoresBaremables = findAutoresBaremables(produccionCientificaId);

    if (!autoresBaremables.isEmpty()) {
      // Buscamos area en la lista de autores baremables el autor en primera posición
      if (Boolean.TRUE.equals(isPersonaRefAndBaremable(autoresBaremables.get(0).getPersonaRef()))) {
        return Boolean.TRUE;
      }

      // Buscamos area en la lista de autores baremables el autor en última posición
      int numAutores = autoresBaremables.size();
      return numAutores > 1
          && Boolean.TRUE.equals(isPersonaRefAndBaremable(autoresBaremables.get(numAutores - 1).getPersonaRef()));
    }
    return Boolean.FALSE;
  }

  private LongPredicate getPredicateIsPublicacionRelevante() {
    return produccionCientificaId -> isValorEqualsStringValue(
        CodigoCVN.E060_010_010_300, "true", produccionCientificaId)
        || isValorEqualsStringValue(CodigoCVN.PUBLICACION_MUY_RELEVANTE, "true", produccionCientificaId);
  }

  private LongPredicate getPredicateIsTipoOpenAccessAll() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.TIPO_OPEN_ACCESS, TablaMaestraCVN.TIPO_OPEN_ACCESS_ALL, produccionCientificaId);
  }

  private LongPredicate getPredicateIsTipoOpenAccessGold() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.TIPO_OPEN_ACCESS, TablaMaestraCVN.TIPO_OPEN_ACCESS_GOLD, produccionCientificaId);
  }

  private LongPredicate getPredicateIsTipoOpenAccessHybridGold() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.TIPO_OPEN_ACCESS, TablaMaestraCVN.TIPO_OPEN_ACCESS_HYBRID_GOLD, produccionCientificaId);
  }

  private LongPredicate getPredicateIsTipoOpenAccessBronze() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.TIPO_OPEN_ACCESS, TablaMaestraCVN.TIPO_OPEN_ACCESS_BRONZE, produccionCientificaId);
  }

  private LongPredicate getPredicateIsTipoOpenAccessGreen() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.TIPO_OPEN_ACCESS, TablaMaestraCVN.TIPO_OPEN_ACCESS_GREEN, produccionCientificaId);
  }

  private LongPredicate getPredicateIsInternacional() {
    return produccionCientificaId -> isValorEqualsStringValue(CodigoCVN.INTERNACIONAL, "true", produccionCientificaId);
  }

  private LongPredicate getPredicateIsInterdisciplinar() {
    return produccionCientificaId -> isValorEqualsStringValue(CodigoCVN.INTERDISCIPLINAR, "true",
        produccionCientificaId);
  }

}
