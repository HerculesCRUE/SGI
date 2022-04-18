package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.function.LongPredicate;

import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.crue.hercules.sgi.prc.enums.TablaMaestraCVN;
import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.model.Modulador.TipoModulador;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador.TipoPuntuacion;
import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.repository.AliasEnumeradoRepository;
import org.crue.hercules.sgi.prc.repository.AutorGrupoRepository;
import org.crue.hercules.sgi.prc.repository.AutorRepository;
import org.crue.hercules.sgi.prc.repository.BaremoRepository;
import org.crue.hercules.sgi.prc.repository.CampoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.EditorialPrestigioRepository;
import org.crue.hercules.sgi.prc.repository.IndiceExperimentalidadRepository;
import org.crue.hercules.sgi.prc.repository.IndiceImpactoRepository;
import org.crue.hercules.sgi.prc.repository.ModuladorRepository;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionBaremoItemRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionGrupoInvestigadorRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionGrupoRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionItemInvestigadorRepository;
import org.crue.hercules.sgi.prc.repository.TipoFuenteImpactoCuartilRepository;
import org.crue.hercules.sgi.prc.repository.ValorCampoRepository;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiCspService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiSgoService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiSgpService;
import org.crue.hercules.sgi.prc.util.ProduccionCientificaFieldFormatUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la baremación de publicaciones de libros
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class BaremacionPublicacionLibroService extends BaremacionPublicacionAndComiteService {
  private final EditorialPrestigioRepository editorialPrestigioRepository;

  @Autowired
  public BaremacionPublicacionLibroService(
      AliasEnumeradoRepository aliasEnumeradoRepository,
      ProduccionCientificaRepository produccionCientificaRepository,
      PuntuacionBaremoItemRepository puntuacionBaremoItemRepository,
      PuntuacionItemInvestigadorRepository puntuacionItemInvestigadorRepository,
      PuntuacionGrupoRepository puntuacionGrupoRepository,
      PuntuacionGrupoInvestigadorRepository puntuacionGrupoInvestigadorRepository,
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
      EditorialPrestigioRepository editorialPrestigioRepository,
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
    this.editorialPrestigioRepository = editorialPrestigioRepository;

    loadPredicates();
  }

  protected TipoPuntuacion getTipoPuntuacion() {
    return TipoPuntuacion.LIBROS;
  }

  protected void loadPredicates() {
    loadLibrosPredicates();
  }

  private void loadLibrosPredicates() {
    loadLibrosAutoriaPredicates();
    loadLibrosCapituloPredicates();
    loadLibrosEdicionPredicates();
    loadLibrosComentarioPredicates();
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

  protected BigDecimal evaluateBaremoModulador(BaremacionInput baremacionInput) {
    log.debug("evaluateBaremoModulador(baremacionInput) - start");

    BigDecimal puntos = BigDecimal.ZERO;

    TipoBaremo tipoBaremo = baremacionInput.getBaremo().getConfiguracionBaremo().getTipoBaremo();

    Long produccionCientificaId = baremacionInput.getProduccionCientificaId();
    boolean isTipoProduccionEqualsLibro = getPredicateIsArticuloCientifico().negate().test(produccionCientificaId);

    if (isTipoProduccionEqualsLibro && tipoBaremo.equals(TipoBaremo.LIBRO_NUMERO_AUTORES)) {
      puntos = evaluateModuladorByTipo(baremacionInput, TipoModulador.NUMERO_AUTORES);
    }

    log.debug("evaluateBaremoModulador(baremacionInput) - end");
    return puntos;
  }

  protected BigDecimal evaluateBaremoExtra(BaremacionInput baremacionInput) {
    log.debug("evaluateBaremoExtra(baremacionInput) - start");

    BigDecimal puntos = BigDecimal.ZERO;

    TipoBaremo tipoBaremo = baremacionInput.getBaremo().getConfiguracionBaremo().getTipoBaremo();

    Long produccionCientificaId = baremacionInput.getProduccionCientificaId();
    boolean isTipoProduccionEqualsLibro = getPredicateIsArticuloCientifico().negate().test(produccionCientificaId);

    if ((isTipoProduccionEqualsLibro
        && tipoBaremo.equals(TipoBaremo.LIBRO_EDITORIAL_PRESTIGIO)
        && evaluateEditorialPrestigio(baremacionInput))) {
      puntos = baremacionInput.getBaremo().getPuntos();
    }

    log.debug("evaluateBaremoExtra(baremacionInput) - end");

    return puntos;
  }

  private Boolean evaluateEditorialPrestigio(BaremacionInput baremacionInput) {
    log.debug("evaluateModuladorEditorialPrestigio(baremacionInput) - start");

    Boolean isValid = Boolean.FALSE;
    Long produccionCientificaId = baremacionInput.getProduccionCientificaId();

    List<ValorCampo> valores = findValoresByCampoProduccionCientificaId(CodigoCVN.E060_010_010_100,
        produccionCientificaId);

    if (!valores.isEmpty()) {
      String editorialField = valores.get(0).getValor();

      List<Autor> autoresBaremables = findAutoresBaremables(produccionCientificaId);

      if (!autoresBaremables.isEmpty()) {
        List<String> areas = getAreaByAutorBaremable(baremacionInput, autoresBaremables);
        if (!CollectionUtils.isEmpty(areas)) {

          Pair<Instant, Instant> pairFechasBaremacion = ProduccionCientificaFieldFormatUtil
              .calculateFechasInicioFinBaremacionByAnio(
                  getAnio(), getSgiConfigProperties().getTimeZone());

          isValid = areas.stream().anyMatch(areaRef -> editorialPrestigioRepository
              .findByAreaRefAndAnioBaremacion(areaRef,
                  pairFechasBaremacion.getFirst(), pairFechasBaremacion.getSecond())
              .stream()
              .anyMatch(
                  editorial -> ProduccionCientificaFieldFormatUtil.normalizeString(editorial.getNombre().toUpperCase())
                      .equals(ProduccionCientificaFieldFormatUtil.normalizeString(editorialField.toUpperCase()))));
        }
      }

      log.debug("evaluateModuladorEditorialPrestigio(baremacionInput) - end");
    }
    return isValid;
  }

  /* -------------------- predicates -------------------- */
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

  private LongPredicate getPredicateIsISSNNational() {
    return produccionCientificaId -> isValorCampoISBNNational(produccionCientificaId,
        CodigoCVN.E060_010_010_160);
  }

  private LongPredicate getPredicateIsISSNForeign() {
    return produccionCientificaId -> !isValorCampoISBNNational(produccionCientificaId,
        CodigoCVN.E060_010_010_160);
  }
}
