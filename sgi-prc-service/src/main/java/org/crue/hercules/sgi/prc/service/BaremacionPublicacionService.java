package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.LongPredicate;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.crue.hercules.sgi.prc.enums.TablaMaestraCVN;
import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.repository.AutorGrupoRepository;
import org.crue.hercules.sgi.prc.repository.AutorRepository;
import org.crue.hercules.sgi.prc.repository.BaremoRepository;
import org.crue.hercules.sgi.prc.repository.CampoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.IndiceImpactoRepository;
import org.crue.hercules.sgi.prc.repository.ModuladorRepository;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionBaremoItemRepository;
import org.crue.hercules.sgi.prc.repository.TipoFuenteImpactoCuartilRepository;
import org.crue.hercules.sgi.prc.repository.ValorCampoRepository;
import org.crue.hercules.sgi.prc.service.sgi.GrupoInvestigacionService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiSgpService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la baremación de publicaciones
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class BaremacionPublicacionService extends BaremacionCommonService {
  private final ModuladorRepository moduladorRepository;
  private final AutorRepository autorRepository;
  private final AutorGrupoRepository autorGrupoRepository;
  private final SgiApiSgpService personaService;
  private final GrupoInvestigacionService grupoInvestigacionService;

  @Autowired
  public BaremacionPublicacionService(
      ProduccionCientificaRepository produccionCientificaRepository,
      PuntuacionBaremoItemRepository puntuacionBaremoItemRepository,
      BaremoRepository baremoRepository,
      CampoProduccionCientificaRepository campoProduccionCientificaRepository,
      ValorCampoRepository valorCampoRepository,
      IndiceImpactoRepository indiceImpactoRepository,
      AutorRepository autorRepository,
      AutorGrupoRepository autorGrupoRepository,
      TipoFuenteImpactoCuartilRepository tipoFuenteImpactoCuartilRepository,
      ProduccionCientificaCloneService produccionCientificaCloneService,
      SgiApiSgpService personaService,
      GrupoInvestigacionService grupoInvestigacionService,
      ModelMapper modelMapper,
      ModuladorRepository moduladorRepository) {
    super(produccionCientificaRepository, puntuacionBaremoItemRepository, baremoRepository,
        campoProduccionCientificaRepository, valorCampoRepository, indiceImpactoRepository,
        tipoFuenteImpactoCuartilRepository, produccionCientificaCloneService,
        modelMapper);
    this.moduladorRepository = moduladorRepository;
    this.autorRepository = autorRepository;
    this.autorGrupoRepository = autorGrupoRepository;
    this.personaService = personaService;
    this.grupoInvestigacionService = grupoInvestigacionService;

    loadPredicates();
  }

  protected void loadPredicates() {
    loadLibrosPredicates();
    loadArticulosPredicates();
  }

  private void loadLibrosPredicates() {
    loadLibrosAutoriaPredicates();
    loadLibrosCapituloPredicates();
    loadLibrosEdicionPredicates();
    loadLibrosComentarioPredicates();
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
  }

  private void loadLibrosAutoriaPredicates() {
    // AUTORIA_BCI_EDITORIAL_EXTRANJERA
    LongPredicate hasFuenteImpactoBCI = getPredicateHasFuenteImpactoBCI();

    LongPredicate isTipoProduccionEqualsLibroOrMonografia = produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E060_010_010_010, TablaMaestraCVN.E060_010_010_010_032, produccionCientificaId);

    getHmTipoBaremoPredicates().put(TipoBaremo.AUTORIA_BCI_EDITORIAL_EXTRANJERA,
        getPredicateIsISSNForeign().and(hasFuenteImpactoBCI).and(isTipoProduccionEqualsLibroOrMonografia));

    // AUTORIA_BCI_EDITORIAL_NACIONAL
    getHmTipoBaremoPredicates().put(TipoBaremo.AUTORIA_BCI_EDITORIAL_NACIONAL,
        getPredicateIsISSNNational()
            .and(hasFuenteImpactoBCI)
            .and(isTipoProduccionEqualsLibroOrMonografia));

    // AUTORIA_ICEE_Q1
    LongPredicate hasFuenteImpactoICEE = getPredicateHasFuenteImpactoICEE();

    getHmTipoBaremoPredicates().put(TipoBaremo.AUTORIA_ICEE_Q1,
        hasFuenteImpactoICEE.and(getPredicateHasPosicionRevistaLessEqualThan25AndICEE())
            .and(isTipoProduccionEqualsLibroOrMonografia));

    // AUTORIA_ICEE_RESTO_CUARTILES
    getHmTipoBaremoPredicates().put(TipoBaremo.AUTORIA_ICEE_RESTO_CUARTILES,
        hasFuenteImpactoICEE.and(getPredicateHasPositionRevistaGreatherThan25AndICEE())
            .and(isTipoProduccionEqualsLibroOrMonografia));

    // AUTORIA_DIALNET
    getHmTipoBaremoPredicates().put(TipoBaremo.AUTORIA_DIALNET,
        getPredicateHasFuenteImpactoDIALNET().and(isTipoProduccionEqualsLibroOrMonografia));

    // AUTORIA_OTRAS
    getHmTipoBaremoPredicates().put(TipoBaremo.AUTORIA_OTRAS,
        getPredicateHasFuenteImpactoLibrosOTRAS().and(isTipoProduccionEqualsLibroOrMonografia));
  }

  private void loadLibrosCapituloPredicates() {
    // CAP_LIBRO_BCI_EDITORIAL_EXTRANJERA
    LongPredicate hasFuenteImpactoBCI = getPredicateHasFuenteImpactoBCI();

    LongPredicate isTipoProduccionEqualsCapitulo = produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E060_010_010_010, TablaMaestraCVN.E060_010_010_010_004, produccionCientificaId);

    getHmTipoBaremoPredicates().put(TipoBaremo.CAP_LIBRO_BCI_EDITORIAL_EXTRANJERA,
        getPredicateIsISSNForeign().and(hasFuenteImpactoBCI).and(isTipoProduccionEqualsCapitulo));

    // CAP_LIBRO_BCI_EDITORIAL_NACIONAL
    getHmTipoBaremoPredicates().put(TipoBaremo.CAP_LIBRO_BCI_EDITORIAL_NACIONAL,
        getPredicateIsISSNNational()
            .and(hasFuenteImpactoBCI)
            .and(isTipoProduccionEqualsCapitulo));

    // CAP_LIBRO_ICEE_Q1
    LongPredicate hasFuenteImpactoICEE = getPredicateHasFuenteImpactoICEE();

    getHmTipoBaremoPredicates().put(TipoBaremo.CAP_LIBRO_ICEE_Q1,
        hasFuenteImpactoICEE.and(getPredicateHasPosicionRevistaLessEqualThan25AndICEE())
            .and(isTipoProduccionEqualsCapitulo));

    // CAP_LIBRO_ICEE_RESTO_CUARTILES
    getHmTipoBaremoPredicates().put(TipoBaremo.CAP_LIBRO_ICEE_RESTO_CUARTILES,
        hasFuenteImpactoICEE.and(getPredicateHasPositionRevistaGreatherThan25AndICEE())
            .and(isTipoProduccionEqualsCapitulo));

    // CAP_LIBRO_DIALNET
    getHmTipoBaremoPredicates().put(TipoBaremo.CAP_LIBRO_DIALNET,
        getPredicateHasFuenteImpactoDIALNET().and(isTipoProduccionEqualsCapitulo));

    // CAP_LIBRO_OTRAS
    getHmTipoBaremoPredicates().put(TipoBaremo.CAP_LIBRO_OTRAS,
        getPredicateHasFuenteImpactoLibrosOTRAS().and(isTipoProduccionEqualsCapitulo));
  }

  private void loadLibrosEdicionPredicates() {
    // EDICION_BCI_EDITORIAL_EXTRANJERA
    LongPredicate hasFuenteImpactoBCI = getPredicateHasFuenteImpactoBCI();

    LongPredicate isTipoProduccionEqualsEdicion = produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E060_010_010_010, TablaMaestraCVN.E060_010_010_010_208, produccionCientificaId);

    getHmTipoBaremoPredicates().put(TipoBaremo.EDICION_BCI_EDITORIAL_EXTRANJERA,
        getPredicateIsISSNForeign().and(hasFuenteImpactoBCI).and(isTipoProduccionEqualsEdicion));

    // EDICION_BCI_EDITORIAL_NACIONAL
    getHmTipoBaremoPredicates().put(TipoBaremo.EDICION_BCI_EDITORIAL_NACIONAL,
        getPredicateIsISSNNational().and(hasFuenteImpactoBCI).and(isTipoProduccionEqualsEdicion));

    // EDICION_ICEE_Q1
    LongPredicate hasFuenteImpactoICEE = getPredicateHasFuenteImpactoICEE();

    getHmTipoBaremoPredicates().put(TipoBaremo.EDICION_ICEE_Q1,
        hasFuenteImpactoICEE.and(getPredicateHasPosicionRevistaLessEqualThan25AndICEE())
            .and(isTipoProduccionEqualsEdicion));

    // EDICION_ICEE_RESTO_CUARTILES
    getHmTipoBaremoPredicates().put(TipoBaremo.EDICION_ICEE_RESTO_CUARTILES,
        hasFuenteImpactoICEE.and(getPredicateHasPositionRevistaGreatherThan25AndICEE())
            .and(isTipoProduccionEqualsEdicion));

    // EDICION_DIALNET
    getHmTipoBaremoPredicates().put(TipoBaremo.EDICION_DIALNET,
        getPredicateHasFuenteImpactoDIALNET().and(isTipoProduccionEqualsEdicion));

    // EDICION_OTRAS
    getHmTipoBaremoPredicates().put(TipoBaremo.EDICION_OTRAS,
        getPredicateHasFuenteImpactoLibrosOTRAS().and(isTipoProduccionEqualsEdicion));
  }

  private void loadLibrosComentarioPredicates() {
    // COMENTARIO_BCI_EDITORIAL_EXTRANJERA
    LongPredicate hasFuenteImpactoBCI = getPredicateHasFuenteImpactoBCI();

    LongPredicate isTipoProduccionEqualsComentario = produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E060_010_010_010, TablaMaestraCVN.E060_010_010_010_COMENTARIO_SISTEMATICO_NORMAS,
        produccionCientificaId);

    getHmTipoBaremoPredicates().put(TipoBaremo.COMENTARIO_BCI_EDITORIAL_EXTRANJERA,
        getPredicateIsISSNForeign().and(hasFuenteImpactoBCI).and(isTipoProduccionEqualsComentario));

    // COMENTARIO_BCI_EDITORIAL_NACIONAL
    getHmTipoBaremoPredicates().put(TipoBaremo.COMENTARIO_BCI_EDITORIAL_NACIONAL,
        getPredicateIsISSNNational().and(hasFuenteImpactoBCI).and(isTipoProduccionEqualsComentario));

    // COMENTARIO_ICEE_Q1
    LongPredicate hasFuenteImpactoICEE = getPredicateHasFuenteImpactoICEE();

    getHmTipoBaremoPredicates().put(TipoBaremo.COMENTARIO_ICEE_Q1,
        hasFuenteImpactoICEE.and(getPredicateHasPosicionRevistaLessEqualThan25AndICEE())
            .and(isTipoProduccionEqualsComentario));

    // COMENTARIO_ICEE_RESTO_CUARTILES
    getHmTipoBaremoPredicates().put(TipoBaremo.COMENTARIO_ICEE_RESTO_CUARTILES,
        hasFuenteImpactoICEE.and(getPredicateHasPositionRevistaGreatherThan25AndICEE())
            .and(isTipoProduccionEqualsComentario));

    // COMENTARIO_DIALNET
    getHmTipoBaremoPredicates().put(TipoBaremo.COMENTARIO_DIALNET,
        getPredicateHasFuenteImpactoDIALNET().and(isTipoProduccionEqualsComentario));

    // CAP_LIBRO_OTRAS
    getHmTipoBaremoPredicates().put(TipoBaremo.COMENTARIO_OTRAS,
        getPredicateHasFuenteImpactoLibrosOTRAS().and(isTipoProduccionEqualsComentario));
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

    BigDecimal puntos = new BigDecimal("1.00");

    TipoBaremo tipoBaremo = baremacionInput.getBaremo().getConfiguracionBaremo().getTipoBaremo();

    if (tipoBaremo.equals(TipoBaremo.LIBRO_NUMERO_AUTORES)) {
      puntos = evaluateLibroNumeroAutores(baremacionInput);
    }

    log.debug("evaluateBaremoModulador(baremacionInput) - end");
    return puntos;
  }

  private BigDecimal evaluateLibroNumeroAutores(BaremacionInput baremacionInput) {
    BigDecimal puntos = new BigDecimal("1.00");

    Long produccionCientificaId = baremacionInput.getProduccionCientificaId();
    List<Autor> autoresBaremables = findAutoresBaremables(produccionCientificaId);

    String areaRef = getAreaByAutorBaremable(produccionCientificaId, autoresBaremables);
    if (StringUtils.hasText(areaRef)) {

      // moduladorRepository.findByConvocatoriaBaremacionIdAndAreaRefAndTipoModulador(baremacionInput.getConvocatoriaBaremacionId(),
      // areaRef, TipoModulador.NUMERO_AUTORES).map(modulador ->
      // modulador.getValor3())
      puntos = baremacionInput.getBaremo().getPuntos();

    }

    return puntos;
  }

  private String getAreaByAutorBaremable(Long produccionCientificaId, List<Autor> autoresBaremables) {
    String areaRef = null;

    // Buscamos area si viene informado E060_010_010_390
    areaRef = findValoresByCampoProduccionCientificaId(CodigoCVN.E060_010_010_390,
        produccionCientificaId).stream()
            .filter(valorCampo -> isPersonaRef(valorCampo.getValor()) && isPersonaRefBaremable(valorCampo.getValor()))
            .findFirst()
            .map(valorCampo -> getAreaNodoRaiz()).orElse(null);

    // Buscamos area en la lista de autores baremables
    if (!StringUtils.hasText(areaRef)) {

    }

    return areaRef;
  }

  private String getAreaNodoRaiz() {
    return null;
  }

  protected BigDecimal evaluateBaremoExtra(BaremacionInput baremacionInput) {
    log.debug("evaluateBaremoExtra(baremacionInput) - start");

    BigDecimal puntos = BigDecimal.ZERO;

    TipoBaremo tipoBaremo = baremacionInput.getBaremo().getConfiguracionBaremo().getTipoBaremo();

    boolean isValid = false;
    if (tipoBaremo.equals(TipoBaremo.LIBRO_EDITORIAL_PRESTIGIO)) {
      // TODO cuando esté grupos
      // isValid = evaluateLibroEditorialPrestigio(produccionCientificaId,
      // tipoBaremo);
    }

    if (isValid) {
      puntos = baremacionInput.getBaremo().getPuntos();
    }

    log.debug("evaluateBaremoExtra(baremacionInput) - end");

    return puntos;
  }

  /* -------------------- predicates -------------------- */
  private LongPredicate getPredicateIsISNNNotEmpty() {
    return produccionCientificaId -> isValorCampoNotEmpty(produccionCientificaId, CodigoCVN.E060_010_010_160);
  }

  private LongPredicate getPredicateHasPosicionRevistaLessEqualThan25AndICEE() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.ICEE)).stream()
            .anyMatch(this::isRevista25OrPosicionRevistaLessEqualThan25);
  }

  private LongPredicate getPredicateHasPositionRevistaGreatherThan25AndICEE() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.ICEE)).stream()
            .anyMatch(this::isPosicionRevistaGreatherThan25);
  }

  private LongPredicate getPredicateHasFuenteImpactoLibrosOTRAS() {
    return produccionCientificaId -> hasFuenteImpactoNotIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.BCI, TipoFuenteImpacto.ICEE, TipoFuenteImpacto.DIALNET));
  }

  private LongPredicate getPredicateHasFuenteImpactoBCI() {
    return produccionCientificaId -> hasFuenteImpactoIn(produccionCientificaId, Arrays.asList(TipoFuenteImpacto.BCI));
  }

  private LongPredicate getPredicateHasFuenteImpactoICEE() {
    return produccionCientificaId -> hasFuenteImpactoIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.ICEE));
  }

  private LongPredicate getPredicateIsArticuloCientifico() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E060_010_010_010, TablaMaestraCVN.E060_010_010_010_020, produccionCientificaId);
  }

  protected LongPredicate getPredicateIsISSNNational() {
    return produccionCientificaId -> isValorCampoISBNNational(produccionCientificaId,
        CodigoCVN.E060_010_010_160);
  }

  protected LongPredicate getPredicateIsISSNForeign() {
    return produccionCientificaId -> !isValorCampoISBNNational(produccionCientificaId,
        CodigoCVN.E060_010_010_160);
  }

  protected List<Autor> findAutoresBaremables(Long produccionCientificaId) {
    return autorRepository.findAllByProduccionCientificaIdAndPersonaRefIsNotNull(produccionCientificaId).stream()
        .filter(autor -> isPersonaRef(autor.getPersonaRef()) && isPersonaRefBaremable(autor.getPersonaRef()))
        .collect(Collectors.toList());
  }

  protected Boolean isPersonaRef(String personaRef) {
    return personaService.findById(personaRef).isPresent();
  }

  protected Boolean isPersonaRefBaremable(String personaRef) {
    return grupoInvestigacionService.isBaremable(personaRef, getAnio());
  }
}
