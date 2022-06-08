package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.util.function.LongPredicate;

import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.enums.TablaMaestraCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class BaremacionComiteEditorialService extends BaremacionPublicacionAndComiteService {

  @Autowired
  public BaremacionComiteEditorialService(
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
        autorRepository,
        autorGrupoRepository,
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
    return TipoPuntuacion.COMITES_EDITORIALES;
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

    loadExtraPredicates();
  }

  protected void loadExtraPredicates() {
    loadComitesEditorialesJCREditorPredicates();
    loadComitesEditorialesSCIMAGOEditorPredicates();
    loadComitesEditorialesCITECEditorPredicates();
    loadComitesEditorialesSCOPUSEditorPredicates();
    loadComitesEditorialesSCIMAGOEditorPredicates();
    loadComitesEditorialesERIHEditorPredicates();
    loadComitesEditorialesDIALNETEditorPredicates();
    loadComitesEditorialesMIAREditorPredicates();
    loadComitesEditorialesFECYTEditorPredicates();
  }

  private void loadComitesEditorialesJCREditorPredicates() {
    // COMITE_EDITORIAL_JCR_Q1_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_JCR_Q1_EDITOR,
        getPredicatesJCRQ1().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_JCR_Q2_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_JCR_Q2_EDITOR,
        getPredicatesJCRQ2().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_JCR_Q3_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_JCR_Q3_EDITOR,
        getPredicatesJCRQ3().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_JCR_Q4_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_JCR_Q4_EDITOR,
        getPredicatesJCRQ4().and(getPredicateIsEditorOrDirector()));
  }

  private void loadComitesEditorialesJCRPredicates() {
    // COMITE_EDITORIAL_JCR_Q1
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_JCR_Q1, getPredicatesJCRQ1());

    // COMITE_EDITORIAL_JCR_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_JCR_Q2, getPredicatesJCRQ2());

    // COMITE_EDITORIAL_JCR_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_JCR_Q3, getPredicatesJCRQ3());

    // COMITE_EDITORIAL_JCR_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_JCR_Q4, getPredicatesJCRQ4());
  }

  private void loadComitesEditorialesSCOPUSPredicates() {
    // COMITE_EDITORIAL_SCOPUS_Q1
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCOPUS_Q1, getPredicatesSCOPUSQ1());

    // COMITE_EDITORIAL_SCOPUS_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCOPUS_Q2, getPredicatesSCOPUSQ2());

    // COMITE_EDITORIAL_SCOPUS_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCOPUS_Q3, getPredicatesSCOPUSQ3());

    // COMITE_EDITORIAL_SCOPUS_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCOPUS_Q4, getPredicatesSCOPUSQ4());
  }

  private void loadComitesEditorialesSCOPUSEditorPredicates() {
    // COMITE_EDITORIAL_SCOPUS_Q1_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCOPUS_Q1_EDITOR,
        getPredicatesSCOPUSQ1().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_SCOPUS_Q2_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCOPUS_Q2_EDITOR,
        getPredicatesSCOPUSQ2().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_SCOPUS_Q3_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCOPUS_Q3_EDITOR,
        getPredicatesSCOPUSQ3().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_SCOPUS_Q4_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCOPUS_Q4_EDITOR,
        getPredicatesSCOPUSQ4().and(getPredicateIsEditorOrDirector()));
  }

  private void loadComitesEditorialesSCIMAGOPredicates() {
    // COMITE_EDITORIAL_SCIMAGO_Q1
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCIMAGO_Q1, getPredicatesSCIMAGOQ1());

    // COMITE_EDITORIAL_SCIMAGO_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCIMAGO_Q2, getPredicatesSCIMAGOQ2());

    // COMITE_EDITORIAL_SCIMAGO_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCIMAGO_Q3, getPredicatesSCIMAGOQ3());

    // COMITE_EDITORIAL_SCIMAGO_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCIMAGO_Q4, getPredicatesSCIMAGOQ4());
  }

  private void loadComitesEditorialesSCIMAGOEditorPredicates() {
    // COMITE_EDITORIAL_SCIMAGO_Q1_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCIMAGO_Q1_EDITOR,
        getPredicatesSCIMAGOQ1().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_SCIMAGO_Q2_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCIMAGO_Q2_EDITOR,
        getPredicatesSCIMAGOQ2().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_SCIMAGO_Q3_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCIMAGO_Q3_EDITOR,
        getPredicatesSCIMAGOQ3().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_SCIMAGO_Q4_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_SCIMAGO_Q4_EDITOR,
        getPredicatesSCIMAGOQ4().and(getPredicateIsEditorOrDirector()));
  }

  private void loadComitesEditorialesCITECPredicates() {
    // COMITE_EDITORIAL_CITEC_Q1
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_CITEC_Q1, getPredicatesCITECQ1());

    // COMITE_EDITORIAL_CITEC_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_CITEC_Q2, getPredicatesCITECQ2());

    // COMITE_EDITORIAL_CITEC_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_CITEC_Q3, getPredicatesCITECQ3());

    // COMITE_EDITORIAL_CITEC_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_CITEC_Q4, getPredicatesCITECQ4());
  }

  private void loadComitesEditorialesCITECEditorPredicates() {
    // COMITE_EDITORIAL_CITEC_Q1_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_CITEC_Q1_EDITOR,
        getPredicatesCITECQ1().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_CITEC_Q2_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_CITEC_Q2_EDITOR,
        getPredicatesCITECQ2().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_CITEC_Q3_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_CITEC_Q3_EDITOR,
        getPredicatesCITECQ3().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_CITEC_Q4_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_CITEC_Q4_EDITOR,
        getPredicatesCITECQ4().and(getPredicateIsEditorOrDirector()));
  }

  private void loadComitesEditorialesERIHPredicates() {
    // COMITE_EDITORIAL_ERIH_Q1
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_ERIH_Q1, getPredicatesERIHQ1());

    // COMITE_EDITORIAL_ERIH_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_ERIH_Q2, getPredicatesERIHQ2());

    // COMITE_EDITORIAL_ERIH_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_ERIH_Q3, getPredicatesERIHQ3());

    // COMITE_EDITORIAL_ERIH_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_ERIH_Q4, getPredicatesERIHQ4());
  }

  private void loadComitesEditorialesERIHEditorPredicates() {
    // COMITE_EDITORIAL_ERIH_Q1_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_ERIH_Q1_EDITOR,
        getPredicatesERIHQ1().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_ERIH_Q2_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_ERIH_Q2_EDITOR,
        getPredicatesERIHQ2().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_ERIH_Q3_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_ERIH_Q3_EDITOR,
        getPredicatesERIHQ3().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_ERIH_Q4_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_ERIH_Q4_EDITOR,
        getPredicatesERIHQ4().and(getPredicateIsEditorOrDirector()));
  }

  private void loadComitesEditorialesDIALNETPredicates() {
    // COMITE_EDITORIAL_DIALNET_Q1
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_DIALNET_Q1, getPredicatesDIALNETQ1());

    // COMITE_EDITORIAL_DIALNET_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_DIALNET_Q2, getPredicatesDIALNETQ2());

    // COMITE_EDITORIAL_DIALNET_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_DIALNET_Q3, getPredicatesDIALNETQ3());

    // COMITE_EDITORIAL_DIALNET_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_DIALNET_Q4, getPredicatesDIALNETQ4());
  }

  private void loadComitesEditorialesDIALNETEditorPredicates() {
    // COMITE_EDITORIAL_DIALNET_Q1_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_DIALNET_Q1_EDITOR,
        getPredicatesDIALNETQ1().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_DIALNET_Q2_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_DIALNET_Q2_EDITOR,
        getPredicatesDIALNETQ2().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_DIALNET_Q3_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_DIALNET_Q3_EDITOR,
        getPredicatesDIALNETQ3().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_DIALNET_Q4_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_DIALNET_Q4_EDITOR,
        getPredicatesDIALNETQ4().and(getPredicateIsEditorOrDirector()));
  }

  private void loadComitesEditorialesMIARPredicates() {
    // COMITE_EDITORIAL_MIAR_Q1
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_MIAR_Q1, getPredicatesMIARQ1());

    // COMITE_EDITORIAL_MIAR_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_MIAR_Q2, getPredicatesMIARQ2());

    // COMITE_EDITORIAL_MIAR_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_MIAR_Q3, getPredicatesMIARQ3());

    // COMITE_EDITORIAL_MIAR_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_MIAR_Q4, getPredicatesMIARQ4());
  }

  private void loadComitesEditorialesMIAREditorPredicates() {
    // COMITE_EDITORIAL_MIAR_Q1_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_MIAR_Q1_EDITOR,
        getPredicatesMIARQ1().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_MIAR_Q2_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_MIAR_Q2_EDITOR,
        getPredicatesMIARQ2().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_MIAR_Q3_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_MIAR_Q3_EDITOR,
        getPredicatesMIARQ3().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_MIAR_Q4_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_MIAR_Q4_EDITOR,
        getPredicatesMIARQ4().and(getPredicateIsEditorOrDirector()));
  }

  private void loadComitesEditorialesFECYTPredicates() {
    // COMITE_EDITORIAL_FECYT_Q1
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_FECYT_Q1, getPredicatesFECYTQ1());

    // COMITE_EDITORIAL_FECYT_Q2
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_FECYT_Q2, getPredicatesFECYTQ2());

    // COMITE_EDITORIAL_FECYT_Q3
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_FECYT_Q3, getPredicatesFECYTQ3());

    // COMITE_EDITORIAL_FECYT_Q4
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_FECYT_Q4, getPredicatesFECYTQ4());
  }

  private void loadComitesEditorialesFECYTEditorPredicates() {
    // COMITE_EDITORIAL_FECYT_Q1_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_FECYT_Q1_EDITOR,
        getPredicatesFECYTQ1().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_FECYT_Q2_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_FECYT_Q2_EDITOR,
        getPredicatesFECYTQ2().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_FECYT_Q3_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_FECYT_Q3_EDITOR,
        getPredicatesFECYTQ3().and(getPredicateIsEditorOrDirector()));

    // COMITE_EDITORIAL_FECYT_Q4_EDITOR
    getHmTipoBaremoPredicates().put(TipoBaremo.COMITE_EDITORIAL_FECYT_Q4_EDITOR,
        getPredicatesFECYTQ4().and(getPredicateIsEditorOrDirector()));
  }

  /* -------------------- predicates -------------------- */

  private LongPredicate getPredicateIsISNNNotEmpty() {
    return produccionCientificaId -> isValorCampoNotEmpty(produccionCientificaId, CodigoCVN.ISSN);
  }

  private LongPredicate getPredicateIsEditorOrDirector() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E060_030_030_100, TablaMaestraCVN.E060_030_030_100_EDITOR, produccionCientificaId) ||
        isValorEqualsTablaMaestraCVN(CodigoCVN.E060_030_030_100, TablaMaestraCVN.E060_030_030_100_DIRECTOR,
            produccionCientificaId);
  }

  private LongPredicate getPredicatesJCRQ1() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoJCR())
        .and(getPredicateHasPosicionRevistaLessEqualThan25AndJCR());
  }

  private LongPredicate getPredicatesJCRQ2() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoJCR())
        .and(getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndJCR());
  }

  private LongPredicate getPredicatesJCRQ3() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoJCR())
        .and(getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndJCR());
  }

  private LongPredicate getPredicatesJCRQ4() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoJCR())
        .and(getPredicateHasPosicionRevistaGreatherThan75AndJCR());
  }

  private LongPredicate getPredicatesSCOPUSQ1() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoSCOPUS())
        .and(getPredicateHasPosicionRevistaLessEqualThan25AndSCOPUS());
  }

  private LongPredicate getPredicatesSCOPUSQ2() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoSCOPUS())
        .and(getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndSCOPUS());
  }

  private LongPredicate getPredicatesSCOPUSQ3() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoSCOPUS())
        .and(getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndSCOPUS());
  }

  private LongPredicate getPredicatesSCOPUSQ4() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoSCOPUS())
        .and(getPredicateHasPosicionRevistaGreatherThan75AndSCOPUS());
  }

  private LongPredicate getPredicatesSCIMAGOQ1() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoSCIMAGO())
        .and(getPredicateHasPosicionRevistaLessEqualThan25AndSCIMAGO());
  }

  private LongPredicate getPredicatesSCIMAGOQ2() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoSCIMAGO())
        .and(getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndSCIMAGO());
  }

  private LongPredicate getPredicatesSCIMAGOQ3() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoSCIMAGO())
        .and(getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndSCIMAGO());
  }

  private LongPredicate getPredicatesSCIMAGOQ4() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoSCIMAGO())
        .and(getPredicateHasPosicionRevistaGreatherThan75AndSCIMAGO());
  }

  private LongPredicate getPredicatesCITECQ1() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoCITEC())
        .and(getPredicateIsEqualQ1AndCITEC());
  }

  private LongPredicate getPredicatesCITECQ2() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoCITEC())
        .and(getPredicateIsEqualQ2AndCITEC());
  }

  private LongPredicate getPredicatesCITECQ3() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoCITEC())
        .and(getPredicateIsEqualQ3AndCITEC());
  }

  private LongPredicate getPredicatesCITECQ4() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoCITEC())
        .and(getPredicateIsEqualQ4AndCITEC());
  }

  private LongPredicate getPredicatesERIHQ1() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoERIH())
        .and(getPredicateIsEqualQ1AndERIH());
  }

  private LongPredicate getPredicatesERIHQ2() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoERIH())
        .and(getPredicateIsEqualQ2AndERIH());
  }

  private LongPredicate getPredicatesERIHQ3() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoERIH())
        .and(getPredicateIsEqualQ3AndERIH());
  }

  private LongPredicate getPredicatesERIHQ4() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoERIH())
        .and(getPredicateIsEqualQ4AndERIH());
  }

  private LongPredicate getPredicatesDIALNETQ1() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoDIALNET())
        .and(getPredicateHasPosicionRevistaLessEqualThan25AndDIALNET());
  }

  private LongPredicate getPredicatesDIALNETQ2() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoDIALNET())
        .and(getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndDIALNET());
  }

  private LongPredicate getPredicatesDIALNETQ3() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoDIALNET())
        .and(getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndDIALNET());
  }

  private LongPredicate getPredicatesDIALNETQ4() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoDIALNET())
        .and(getPredicateHasPosicionRevistaGreatherThan75AndDIALNET());
  }

  private LongPredicate getPredicatesMIARQ1() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoMIAR())
        .and(getPredicateHasPosicionRevistaLessEqualThan25AndMIAR());
  }

  private LongPredicate getPredicatesMIARQ2() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoMIAR())
        .and(getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndMIAR());
  }

  private LongPredicate getPredicatesMIARQ3() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoMIAR())
        .and(getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndMIAR());
  }

  private LongPredicate getPredicatesMIARQ4() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoMIAR())
        .and(getPredicateHasPosicionRevistaGreatherThan75AndMIAR());
  }

  private LongPredicate getPredicatesFECYTQ1() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoFECYT())
        .and(getPredicateHasPosicionRevistaLessEqualThan25AndFECYT());
  }

  private LongPredicate getPredicatesFECYTQ2() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoFECYT())
        .and(getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndFECYT());
  }

  private LongPredicate getPredicatesFECYTQ3() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoFECYT())
        .and(getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndFECYT());
  }

  private LongPredicate getPredicatesFECYTQ4() {
    return getPredicateIsISNNNotEmpty()
        .and(getPredicateHasFuenteImpactoFECYT())
        .and(getPredicateHasPosicionRevistaGreatherThan75AndFECYT());
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
