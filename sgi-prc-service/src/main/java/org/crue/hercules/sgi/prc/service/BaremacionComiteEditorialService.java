package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.util.function.LongPredicate;

import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.repository.BaremoRepository;
import org.crue.hercules.sgi.prc.repository.CampoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.IndiceImpactoRepository;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionBaremoItemRepository;
import org.crue.hercules.sgi.prc.repository.TipoFuenteImpactoCuartilRepository;
import org.crue.hercules.sgi.prc.repository.ValorCampoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la baremación de cómites editoriales
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class BaremacionComiteEditorialService extends BaremacionCommonService {

  public BaremacionComiteEditorialService(
      ProduccionCientificaRepository produccionCientificaRepository,
      PuntuacionBaremoItemRepository puntuacionBaremoItemRepository,
      BaremoRepository baremoRepository,
      CampoProduccionCientificaRepository campoProduccionCientificaRepository,
      ValorCampoRepository valorCampoRepository,
      IndiceImpactoRepository indiceImpactoRepository,
      TipoFuenteImpactoCuartilRepository tipoFuenteImpactoCuartilRepository,
      ProduccionCientificaCloneService produccionCientificaCloneService,
      ModelMapper modelMapper) {
    super(produccionCientificaRepository, puntuacionBaremoItemRepository, baremoRepository,
        campoProduccionCientificaRepository, valorCampoRepository, indiceImpactoRepository,
        tipoFuenteImpactoCuartilRepository, produccionCientificaCloneService,
        modelMapper);
    loadPredicates();
  }

  protected void loadPredicates() {
    loadComitesEditorialesJCRPredicates();
    loadComitesEditorialesSCIMAGOPredicates();
    loadComitesEditorialesCITECPredicates();
    loadComitesEditorialesSCOPUSPredicates();
    loadComitesEditorialesSCIMAGOPredicates();
    loadComitesEditorialesERIHPredicates();
    loadComitesEditorialesDIALNETPredicates();
    loadComitesEditorialesMIARPredicates();
    loadComitesEditorialesFECYTPredicates();

    // COMITE EDITORIAL
    LongPredicate hasFuenteImpactoOtras = getPredicateHasFuenteImpactoArticulosOrComitesOTRAS();
    LongPredicate isISNNNotEmpty = getPredicateIsISNNNotEmpty();

    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL, isISNNNotEmpty.and(hasFuenteImpactoOtras));
  }

  private void loadComitesEditorialesJCRPredicates() {
    // COMITE_EDITORIAL_JCR_Q1
    LongPredicate hasFuenteImpactoJCR = getPredicateHasFuenteImpactoJCR();
    LongPredicate isISNNNotEmpty = getPredicateIsISNNNotEmpty();

    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_JCR_Q1,
        isISNNNotEmpty.and(hasFuenteImpactoJCR)
            .and(getPredicateHasPosicionRevistaLessEqualThan25AndJCR()));

    // COMITE_EDITORIAL_JCR_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_JCR_Q2,
        isISNNNotEmpty.and(hasFuenteImpactoJCR)
            .and(getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndJCR()));

    // COMITE_EDITORIAL_JCR_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_JCR_Q3,
        isISNNNotEmpty.and(hasFuenteImpactoJCR)
            .and(getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndJCR()));

    // COMITE_EDITORIAL_JCR_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_JCR_Q4,
        isISNNNotEmpty.and(hasFuenteImpactoJCR)
            .and(getPredicateHasPosicionRevistaGreatherThan75AndJCR()));
  }

  private void loadComitesEditorialesSCOPUSPredicates() {
    // COMITE_EDITORIAL_SCOPUS_Q1
    LongPredicate hasFuenteImpactoSCOPUS = getPredicateHasFuenteImpactoSCOPUS();
    LongPredicate isISNNNotEmpty = getPredicateIsISNNNotEmpty();

    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCOPUS_Q1,
        isISNNNotEmpty.and(hasFuenteImpactoSCOPUS)
            .and(getPredicateHasPosicionRevistaLessEqualThan25AndSCOPUS()));

    // COMITE_EDITORIAL_SCOPUS_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCOPUS_Q2,
        isISNNNotEmpty.and(hasFuenteImpactoSCOPUS)
            .and(getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndSCOPUS()));

    // COMITE_EDITORIAL_SCOPUS_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCOPUS_Q3,
        isISNNNotEmpty.and(hasFuenteImpactoSCOPUS)
            .and(getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndSCOPUS()));

    // COMITE_EDITORIAL_SCOPUS_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCOPUS_Q4,
        isISNNNotEmpty.and(hasFuenteImpactoSCOPUS)
            .and(getPredicateHasPosicionRevistaGreatherThan75AndSCOPUS()));
  }

  private void loadComitesEditorialesSCIMAGOPredicates() {
    // COMITE_EDITORIAL_SCIMAGO_Q1
    LongPredicate hasFuenteImpactoSCIMAGO = getPredicateHasFuenteImpactoSCIMAGO();
    LongPredicate isISNNNotEmpty = getPredicateIsISNNNotEmpty();

    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCIMAGO_Q1,
        isISNNNotEmpty.and(hasFuenteImpactoSCIMAGO)
            .and(getPredicateHasPosicionRevistaLessEqualThan25AndSCIMAGO()));

    // COMITE_EDITORIAL_SCIMAGO_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCIMAGO_Q2,
        isISNNNotEmpty.and(hasFuenteImpactoSCIMAGO)
            .and(getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndSCIMAGO()));

    // COMITE_EDITORIAL_SCIMAGO_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCIMAGO_Q3,
        isISNNNotEmpty.and(hasFuenteImpactoSCIMAGO)
            .and(getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndSCIMAGO()));

    // COMITE_EDITORIAL_SCIMAGO_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCIMAGO_Q4,
        isISNNNotEmpty.and(hasFuenteImpactoSCIMAGO)
            .and(getPredicateHasPosicionRevistaGreatherThan75AndSCIMAGO()));
  }

  private void loadComitesEditorialesCITECPredicates() {
    // COMITE_EDITORIAL_CITEC_Q1
    LongPredicate hasFuenteImpactoCITEC = getPredicateHasFuenteImpactoCITEC();
    LongPredicate isISNNNotEmpty = getPredicateIsISNNNotEmpty();

    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_CITEC_Q1,
        isISNNNotEmpty.and(hasFuenteImpactoCITEC)
            .and(getPredicateIsEqualQ1AndCITEC()));

    // COMITE_EDITORIAL_CITEC_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_CITEC_Q2,
        isISNNNotEmpty.and(hasFuenteImpactoCITEC)
            .and(getPredicateIsEqualQ2AndCITEC()));

    // COMITE_EDITORIAL_CITEC_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_CITEC_Q3,
        isISNNNotEmpty.and(hasFuenteImpactoCITEC)
            .and(getPredicateIsEqualQ3AndCITEC()));

    // COMITE_EDITORIAL_CITEC_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_CITEC_Q4,
        isISNNNotEmpty.and(hasFuenteImpactoCITEC)
            .and(getPredicateIsEqualQ4AndCITEC()));
  }

  private void loadComitesEditorialesERIHPredicates() {
    // COMITE_EDITORIAL_ERIH_Q1
    LongPredicate hasFuenteImpactoERIH = getPredicateHasFuenteImpactoERIH();
    LongPredicate isISNNNotEmpty = getPredicateIsISNNNotEmpty();

    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_ERIH_Q1,
        isISNNNotEmpty.and(hasFuenteImpactoERIH)
            .and(getPredicateIsEqualQ1AndERIH()));

    // COMITE_EDITORIAL_ERIH_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_ERIH_Q2,
        isISNNNotEmpty.and(hasFuenteImpactoERIH)
            .and(getPredicateIsEqualQ2AndERIH()));

    // COMITE_EDITORIAL_ERIH_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_ERIH_Q3,
        isISNNNotEmpty.and(hasFuenteImpactoERIH)
            .and(getPredicateIsEqualQ3AndERIH()));

    // COMITE_EDITORIAL_ERIH_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_ERIH_Q4,
        isISNNNotEmpty.and(hasFuenteImpactoERIH)
            .and(getPredicateIsEqualQ4AndERIH()));
  }

  private void loadComitesEditorialesDIALNETPredicates() {
    // COMITE_EDITORIAL_DIALNET_Q1
    LongPredicate hasFuenteImpactoDIALNET = getPredicateHasFuenteImpactoDIALNET();
    LongPredicate isISNNNotEmpty = getPredicateIsISNNNotEmpty();

    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_DIALNET_Q1,
        isISNNNotEmpty.and(hasFuenteImpactoDIALNET)
            .and(getPredicateHasPosicionRevistaLessEqualThan25AndDIALNET()));

    // COMITE_EDITORIAL_DIALNET_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_DIALNET_Q2,
        isISNNNotEmpty.and(hasFuenteImpactoDIALNET)
            .and(getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndDIALNET()));

    // COMITE_EDITORIAL_DIALNET_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_DIALNET_Q3,
        isISNNNotEmpty.and(hasFuenteImpactoDIALNET)
            .and(getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndDIALNET()));

    // COMITE_EDITORIAL_DIALNET_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_DIALNET_Q4,
        isISNNNotEmpty.and(hasFuenteImpactoDIALNET)
            .and(getPredicateHasPosicionRevistaGreatherThan75AndDIALNET()));
  }

  private void loadComitesEditorialesMIARPredicates() {
    // COMITE_EDITORIAL_MIAR_Q1
    LongPredicate hasFuenteImpactoMIAR = getPredicateHasFuenteImpactoMIAR();
    LongPredicate isISNNNotEmpty = getPredicateIsISNNNotEmpty();

    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_MIAR_Q1,
        isISNNNotEmpty.and(hasFuenteImpactoMIAR)
            .and(getPredicateHasPosicionRevistaLessEqualThan25AndMIAR()));

    // COMITE_EDITORIAL_MIAR_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_MIAR_Q2,
        isISNNNotEmpty.and(hasFuenteImpactoMIAR)
            .and(getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndMIAR()));

    // COMITE_EDITORIAL_MIAR_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_MIAR_Q3,
        isISNNNotEmpty.and(hasFuenteImpactoMIAR)
            .and(getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndMIAR()));

    // COMITE_EDITORIAL_MIAR_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_MIAR_Q4,
        isISNNNotEmpty.and(hasFuenteImpactoMIAR)
            .and(getPredicateHasPosicionRevistaGreatherThan75AndMIAR()));
  }

  private void loadComitesEditorialesFECYTPredicates() {
    // COMITE_EDITORIAL_FECYT_Q1
    LongPredicate hasFuenteImpactoFECYT = getPredicateHasFuenteImpactoFECYT();
    LongPredicate isISNNNotEmpty = getPredicateIsISNNNotEmpty();

    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_FECYT_Q1,
        isISNNNotEmpty.and(hasFuenteImpactoFECYT)
            .and(getPredicateHasPosicionRevistaLessEqualThan25AndFECYT()));

    // COMITE_EDITORIAL_FECYT_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_FECYT_Q2,
        isISNNNotEmpty.and(hasFuenteImpactoFECYT)
            .and(getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndFECYT()));

    // COMITE_EDITORIAL_FECYT_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_FECYT_Q3,
        isISNNNotEmpty.and(hasFuenteImpactoFECYT)
            .and(getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndFECYT()));

    // COMITE_EDITORIAL_FECYT_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_FECYT_Q4,
        isISNNNotEmpty.and(hasFuenteImpactoFECYT)
            .and(getPredicateHasPosicionRevistaGreatherThan75AndFECYT()));
  }

  /* -------------------- predicates -------------------- */

  private LongPredicate getPredicateIsISNNNotEmpty() {
    return produccionCientificaId -> isValorCampoNotEmpty(produccionCientificaId, CodigoCVN.ISSN);
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
