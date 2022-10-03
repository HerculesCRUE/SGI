package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.LongPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.crue.hercules.sgi.prc.dto.sgp.PersonaDto.AreaConocimientoDto;
import org.crue.hercules.sgi.prc.dto.sgp.PersonaDto.VinculacionDto;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.IndiceImpacto;
import org.crue.hercules.sgi.prc.model.Modulador;
import org.crue.hercules.sgi.prc.model.Modulador.TipoModulador;
import org.crue.hercules.sgi.prc.model.TipoFuenteImpactoCuartil.Cuartil;
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
import org.crue.hercules.sgi.prc.repository.specification.IndiceImpactoSpecifications;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiCspService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiSgoService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiSgpService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
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
public abstract class BaremacionPublicacionAndComiteService extends BaremacionCommonService {
  private static final String NUMBER_100_00 = "100.00";
  private static final String POSICION_REVISTA_10_00 = "10.00";
  private static final String POSICION_REVISTA_25_00 = "25.00";
  private static final String POSICION_REVISTA_50_00 = "50.00";
  private static final String POSICION_REVISTA_75_00 = "75.00";
  private static final String LOG_AREAS_NUM_AUTORES_AUTORES = "Area[%s] NumAutores[%d] Autor[%s]";

  private final TipoFuenteImpactoCuartilRepository tipoFuenteImpactoCuartilRepository;
  private final ModuladorRepository moduladorRepository;
  private final SgiApiSgoService sgiApiSgoService;

  @Autowired
  protected BaremacionPublicacionAndComiteService(
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
        produccionCientificaBuilderService,
        sgiApiSgpService,
        sgiApiCspService,
        convocatoriaBaremacionLogService,
        modelMapper,
        sgiConfigProperties);
    this.tipoFuenteImpactoCuartilRepository = tipoFuenteImpactoCuartilRepository;
    this.moduladorRepository = moduladorRepository;
    this.sgiApiSgoService = sgiApiSgoService;
  }

  protected BigDecimal evaluateModuladorByTipo(BaremacionInput baremacionInput, TipoModulador tipoModulador) {
    log.debug("evaluateModuladorByTipo(baremacionInput, tipoModulador) - start");

    BigDecimal puntos = BigDecimal.ZERO;

    Long produccionCientificaId = baremacionInput.getProduccionCientificaId();
    List<Autor> autoresBaremables = findAutoresBaremables(produccionCientificaId);

    if (!autoresBaremables.isEmpty()) {
      Integer numAutores = TipoModulador.AREAS.equals(tipoModulador) ? 1 : autoresBaremables.size();

      List<String> areas = getAreaByAutorBaremable(baremacionInput, autoresBaremables);
      if (!CollectionUtils.isEmpty(areas)) {
        String areaRef = areas.get(areas.size() - 1);
        puntos = moduladorRepository
            .findByConvocatoriaBaremacionIdAndAreaRefAndTipo(baremacionInput.getConvocatoriaBaremacionId(),
                areaRef, tipoModulador)
            .map(modulador -> getValorModuladorByNumAutores(modulador, numAutores))
            .orElse(puntos);

        if (null != puntos) {
          String optionalMessage = String.format("Area[%s] NumAutores[%d] TipoModulador[%s] %s",
              areaRef, numAutores, tipoModulador, puntos.toString());
          traceLog(baremacionInput, optionalMessage);
        }
      }
    }

    log.debug("evaluateModuladorByTipo(baremacionInput, tipoModulador) - end");
    return puntos;
  }

  private BigDecimal getValorModuladorByNumAutores(Modulador modulador, Integer numAutores) {
    log.debug("getValorModuladorByNumAutores(modulador, numAutores) - start");

    if (numAutores == 1) {
      return modulador.getValor1();
    }
    if (numAutores >= 1 && numAutores <= 3) {
      return modulador.getValor2();
    }
    if (numAutores >= 5 && numAutores <= 5) {
      return modulador.getValor3();
    }
    if (numAutores >= 6 && numAutores <= 7) {
      return modulador.getValor4();
    }
    if (numAutores >= 8) {
      return modulador.getValor5();
    }
    log.debug("getValorModuladorByNumAutores(modulador, numAutores) - end");
    return null;
  }

  protected List<String> getAreaByAutorBaremable(BaremacionInput baremacionInput, List<Autor> autoresBaremables) {
    log.debug("getAreaByAutorBaremable(produccionCientificaId, autoresBaremables) - start");

    // Buscamos area si viene informado E060_010_010_390
    List<String> areas = findValoresByCampoProduccionCientificaId(CodigoCVN.E060_010_010_390,
        baremacionInput.getProduccionCientificaId()).stream()
        .filter(valorCampo -> isPersonaRefAndBaremable(valorCampo.getValor()))
        .findFirst()
        .map(valorCampo -> getAreaNodoRaizByPersonaRef(valorCampo.getValor())).orElse(null);

    // Buscamos area en la lista de autores baremables el autor en primera posición
    if (CollectionUtils.isEmpty(areas)) {
      areas = getAreaRefByAutor(autoresBaremables.get(0));

      if (!CollectionUtils.isEmpty(areas)) {
        String optionalMessage = String.format(LOG_AREAS_NUM_AUTORES_AUTORES,
            areas, autoresBaremables.size(), "Primer Autor");
        traceLog(baremacionInput, optionalMessage);
      }

    } else {
      String optionalMessage = String.format(LOG_AREAS_NUM_AUTORES_AUTORES,
          areas, autoresBaremables.size(), "E060_010_010_390");
      traceLog(baremacionInput, optionalMessage);
    }

    Integer numAutores = autoresBaremables.size();

    // Buscamos area en la lista de autores baremables el autor en última posición
    if (CollectionUtils.isEmpty(areas) && numAutores > 1) {
      areas = getAreaRefByAutor(autoresBaremables.get(numAutores - 1));

      if (!CollectionUtils.isEmpty(areas)) {
        String optionalMessage = String.format(LOG_AREAS_NUM_AUTORES_AUTORES,
            areas, autoresBaremables.size(), "Autor en última posición");
        traceLog(baremacionInput, optionalMessage);
      }
    }

    log.debug("getAreaByAutorBaremable(produccionCientificaId, autoresBaremables) - end");
    return areas;
  }

  private List<String> getAreaRefByAutor(Autor autorBaremable) {
    return Stream.of(autorBaremable)
        .filter(autor -> isPersonaRefAndBaremable(autor.getPersonaRef()))
        .findFirst()
        .map(autor -> getAreaNodoRaizByPersonaRef(autor.getPersonaRef())).orElse(null);
  }

  private List<String> getAreaNodoRaizByPersonaRef(String personaRef) {
    List<String> areas = new ArrayList<>();
    AreaConocimientoDto areaRefPersona = getSgiApiSgpService().findVinculacionByPersonaId(personaRef)
        .map(VinculacionDto::getAreaConocimiento)
        .orElse(null);

    if (null != areaRefPersona && null != areaRefPersona.getId()) {
      List<AreaConocimientoDto> areasDto = new ArrayList<>();
      areasDto.add(areaRefPersona);
      areas = getAreaNodoRaizById(areasDto).stream().map(AreaConocimientoDto::getId)
          .collect(Collectors.toList());
    }
    return areas;
  }

  private List<AreaConocimientoDto> getAreaNodoRaizById(List<AreaConocimientoDto> areas) {
    AreaConocimientoDto areaRefPersona = areas.get(areas.size() - 1);
    if (StringUtils.hasText(areaRefPersona.getPadreId())) {
      List<AreaConocimientoDto> areaPadre = sgiApiSgoService
          .findAllAreasConocimiento("id==" + areaRefPersona.getPadreId());
      if (!CollectionUtils.isEmpty(areaPadre)) {
        areas.add(areaPadre.get(0));
        areas = getAreaNodoRaizById(areas);
      }
    }
    return areas;
  }

  /* -------------------- Common predicates -------------------- */
  protected LongPredicate getPredicateHasFuenteImpactoJCR() {
    return produccionCientificaId -> hasFuenteImpactoIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.WOS_JCR));
  }

  protected LongPredicate getPredicateHasFuenteImpactoSCIMAGO() {
    return produccionCientificaId -> hasFuenteImpactoIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.SCIMAGO));
  }

  protected LongPredicate getPredicateHasFuenteImpactoCITEC() {
    return produccionCientificaId -> hasFuenteImpactoIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.CITEC));
  }

  protected LongPredicate getPredicateHasFuenteImpactoSCOPUS() {
    return produccionCientificaId -> hasFuenteImpactoIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.SCOPUS_SJR));
  }

  protected LongPredicate getPredicateHasFuenteImpactoERIH() {
    return produccionCientificaId -> hasFuenteImpactoIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.ERIH));
  }

  protected LongPredicate getPredicateHasFuenteImpactoMIAR() {
    return produccionCientificaId -> hasFuenteImpactoIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.MIAR));
  }

  protected LongPredicate getPredicateHasFuenteImpactoFECYT() {
    return produccionCientificaId -> hasFuenteImpactoIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.FECYT));
  }

  protected LongPredicate getPredicateHasFuenteImpactoDIALNET() {
    return produccionCientificaId -> hasFuenteImpactoIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.DIALNET));
  }

  // JCR
  protected LongPredicate getPredicateHasPosicionRevistaLessEqualThan10AndJCR() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.WOS_JCR)).stream()
        .anyMatch(this::isRevista25AndPosicionRevistaLessEqualThan10);
  }

  protected LongPredicate getPredicateHasPosicionRevistaLessEqualThan25AndJCR() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.WOS_JCR)).stream()
        .anyMatch(this::isRevista25OrPosicionRevistaLessEqualThan25);
  }

  protected LongPredicate getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndJCR() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.WOS_JCR)).stream()
        .anyMatch(this::isPosicionRevistaGreatherThan25AndLessThanEqual50);
  }

  protected LongPredicate getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndJCR() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.WOS_JCR)).stream()
        .anyMatch(this::isPosicionRevistaGreatherThan50AndLessThanEqual75);
  }

  protected LongPredicate getPredicateHasPosicionRevistaGreatherThan75AndJCR() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.WOS_JCR)).stream()
        .anyMatch(this::isPosicionRevistaGreatherThan75);
  }

  // SCOPUS
  protected LongPredicate getPredicateHasPosicionRevistaLessEqualThan25AndSCOPUS() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.SCOPUS_SJR)).stream()
        .anyMatch(this::isRevista25OrPosicionRevistaLessEqualThan25);
  }

  protected LongPredicate getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndSCOPUS() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.SCOPUS_SJR)).stream()
        .anyMatch(this::isPosicionRevistaGreatherThan25AndLessThanEqual50);
  }

  protected LongPredicate getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndSCOPUS() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.SCOPUS_SJR)).stream()
        .anyMatch(this::isPosicionRevistaGreatherThan50AndLessThanEqual75);
  }

  protected LongPredicate getPredicateHasPosicionRevistaGreatherThan75AndSCOPUS() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.SCOPUS_SJR)).stream()
        .anyMatch(this::isPosicionRevistaGreatherThan75);
  }

  // SCIMAGO
  protected LongPredicate getPredicateHasPosicionRevistaLessEqualThan25AndSCIMAGO() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.SCIMAGO)).stream()
        .anyMatch(this::isRevista25OrPosicionRevistaLessEqualThan25);
  }

  protected LongPredicate getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndSCIMAGO() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.SCIMAGO)).stream()
        .anyMatch(this::isPosicionRevistaGreatherThan25AndLessThanEqual50);
  }

  protected LongPredicate getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndSCIMAGO() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.SCIMAGO)).stream()
        .anyMatch(this::isPosicionRevistaGreatherThan50AndLessThanEqual75);
  }

  protected LongPredicate getPredicateHasPosicionRevistaGreatherThan75AndSCIMAGO() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.SCIMAGO)).stream()
        .anyMatch(this::isPosicionRevistaGreatherThan75);
  }

  // DIALNET
  protected LongPredicate getPredicateHasPosicionRevistaLessEqualThan25AndDIALNET() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.DIALNET)).stream()
        .anyMatch(this::isRevista25OrPosicionRevistaLessEqualThan25);
  }

  protected LongPredicate getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndDIALNET() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.DIALNET)).stream()
        .anyMatch(this::isPosicionRevistaGreatherThan25AndLessThanEqual50);
  }

  protected LongPredicate getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndDIALNET() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.DIALNET)).stream()
        .anyMatch(this::isPosicionRevistaGreatherThan50AndLessThanEqual75);
  }

  protected LongPredicate getPredicateHasPosicionRevistaGreatherThan75AndDIALNET() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.DIALNET)).stream()
        .anyMatch(this::isPosicionRevistaGreatherThan75);
  }

  // MIAR
  protected LongPredicate getPredicateHasPosicionRevistaLessEqualThan25AndMIAR() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.MIAR)).stream()
        .anyMatch(this::isRevista25OrPosicionRevistaLessEqualThan25);
  }

  protected LongPredicate getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndMIAR() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.MIAR)).stream()
        .anyMatch(this::isPosicionRevistaGreatherThan25AndLessThanEqual50);
  }

  protected LongPredicate getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndMIAR() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.MIAR)).stream()
        .anyMatch(this::isPosicionRevistaGreatherThan50AndLessThanEqual75);
  }

  protected LongPredicate getPredicateHasPosicionRevistaGreatherThan75AndMIAR() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.MIAR)).stream()
        .anyMatch(this::isPosicionRevistaGreatherThan75);
  }

  // FECYT
  protected LongPredicate getPredicateHasPosicionRevistaLessEqualThan25AndFECYT() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.FECYT)).stream()
        .anyMatch(this::isRevista25OrPosicionRevistaLessEqualThan25);
  }

  protected LongPredicate getPredicateHasPosicionRevistaGreatherThan25AndLessThanEqual50AndFECYT() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.FECYT)).stream()
        .anyMatch(this::isPosicionRevistaGreatherThan25AndLessThanEqual50);
  }

  protected LongPredicate getPredicateHasPosicionRevistaGreatherThan50AndLessThanEqual75AndFECYT() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.FECYT)).stream()
        .anyMatch(this::isPosicionRevistaGreatherThan50AndLessThanEqual75);
  }

  protected LongPredicate getPredicateHasPosicionRevistaGreatherThan75AndFECYT() {
    return produccionCientificaId -> findFuentesImpactoByTipoFuenteIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.FECYT)).stream()
        .anyMatch(this::isPosicionRevistaGreatherThan75);
  }

  private boolean isRevista25AndPosicionRevistaLessEqualThan10(IndiceImpacto indiceImpacto) {
    if (Boolean.TRUE.equals(indiceImpacto.getRevista25()) && !Objects.isNull(indiceImpacto.getPosicionPublicacion())
        && !Objects.isNull(indiceImpacto.getNumeroRevistas())) {
      return indiceImpacto.getPosicionPublicacion().divide(indiceImpacto.getNumeroRevistas(), 2, RoundingMode.HALF_UP)
          .compareTo(new BigDecimal(POSICION_REVISTA_10_00)) <= 0;
    }
    return false;
  }

  protected boolean isRevista25OrPosicionRevistaLessEqualThan25(IndiceImpacto indiceImpacto) {
    if (Boolean.TRUE.equals(indiceImpacto.getRevista25())) {
      return true;
    }
    if (!Objects.isNull(indiceImpacto.getPosicionPublicacion())
        && !Objects.isNull(indiceImpacto.getNumeroRevistas())) {
      return indiceImpacto.getPosicionPublicacion().divide(indiceImpacto.getNumeroRevistas(), 2, RoundingMode.HALF_UP)
          .multiply(new BigDecimal(NUMBER_100_00))
          .compareTo(new BigDecimal(POSICION_REVISTA_25_00)) <= 0;
    }
    return false;
  }

  protected boolean isPosicionRevistaGreatherThan25(IndiceImpacto indiceImpacto) {
    if (!Objects.isNull(indiceImpacto.getPosicionPublicacion())
        && !Objects.isNull(indiceImpacto.getNumeroRevistas())) {
      return indiceImpacto.getPosicionPublicacion().divide(indiceImpacto.getNumeroRevistas(), 2, RoundingMode.HALF_UP)
          .multiply(new BigDecimal(NUMBER_100_00))
          .compareTo(new BigDecimal(POSICION_REVISTA_25_00)) > 0;
    }
    return false;
  }

  private boolean isPosicionRevistaGreatherThan25AndLessThanEqual50(IndiceImpacto indiceImpacto) {
    if (!Objects.isNull(indiceImpacto.getPosicionPublicacion())
        && !Objects.isNull(indiceImpacto.getNumeroRevistas())) {
      BigDecimal posicionRevista = indiceImpacto.getPosicionPublicacion().divide(indiceImpacto.getNumeroRevistas(), 2,
          RoundingMode.HALF_UP)
          .multiply(new BigDecimal(NUMBER_100_00));
      return posicionRevista.compareTo(new BigDecimal(POSICION_REVISTA_25_00)) > 0 && posicionRevista
          .compareTo(new BigDecimal(POSICION_REVISTA_50_00)) <= 0;
    }
    return false;
  }

  private boolean isPosicionRevistaGreatherThan50AndLessThanEqual75(IndiceImpacto indiceImpacto) {
    if (!Objects.isNull(indiceImpacto.getPosicionPublicacion())
        && !Objects.isNull(indiceImpacto.getNumeroRevistas())) {
      BigDecimal posicionRevista = indiceImpacto.getPosicionPublicacion().divide(indiceImpacto.getNumeroRevistas(), 2,
          RoundingMode.HALF_UP)
          .multiply(new BigDecimal(NUMBER_100_00));
      return posicionRevista.compareTo(new BigDecimal(POSICION_REVISTA_50_00)) > 0 && posicionRevista
          .compareTo(new BigDecimal(POSICION_REVISTA_75_00)) <= 0;
    }
    return false;
  }

  private boolean isPosicionRevistaGreatherThan75(IndiceImpacto indiceImpacto) {
    if (!Objects.isNull(indiceImpacto.getPosicionPublicacion())
        && !Objects.isNull(indiceImpacto.getNumeroRevistas())) {
      return indiceImpacto.getPosicionPublicacion().divide(indiceImpacto.getNumeroRevistas(), 2, RoundingMode.HALF_UP)
          .multiply(new BigDecimal(NUMBER_100_00))
          .compareTo(new BigDecimal(POSICION_REVISTA_75_00)) > 0;
    }
    return false;
  }

  protected LongPredicate getPredicateHasFuenteImpactoArticulosOrComitesOTRAS() {
    return produccionCientificaId -> hasFuenteImpactoNotIn(produccionCientificaId,
        Arrays.asList(TipoFuenteImpacto.WOS_JCR, TipoFuenteImpacto.SCOPUS_SJR, TipoFuenteImpacto.CITEC,
            TipoFuenteImpacto.SCIMAGO, TipoFuenteImpacto.ERIH, TipoFuenteImpacto.DIALNET, TipoFuenteImpacto.MIAR,
            TipoFuenteImpacto.FECYT));
  }

  protected boolean hasFuenteImpactoIn(Long produccionCientificaId, List<TipoFuenteImpacto> fuentesImpacto) {
    Specification<IndiceImpacto> specs = IndiceImpactoSpecifications
        .byProduccionCientificaId(produccionCientificaId)
        .and(IndiceImpactoSpecifications.byAnio(getAnio()).or(IndiceImpactoSpecifications.byAnioIsNull()))
        .and(IndiceImpactoSpecifications.tipoFuenteImpactoIn(fuentesImpacto));
    PageRequest page = PageRequest.of(0, 1, sortIndiceImpactoByAnio());
    return getIndiceImpactoRepository().findAll(specs, page).hasContent();
  }

  protected boolean hasFuenteImpactoNotIn(Long produccionCientificaId, List<TipoFuenteImpacto> fuentesImpacto) {
    Specification<IndiceImpacto> specs = IndiceImpactoSpecifications
        .byProduccionCientificaId(produccionCientificaId)
        .and(IndiceImpactoSpecifications.byAnio(getAnio()).or(IndiceImpactoSpecifications.byAnioIsNull()))
        .and(IndiceImpactoSpecifications.tipoFuenteImpactoNotIn(fuentesImpacto));
    PageRequest page = PageRequest.of(0, 1, sortIndiceImpactoByAnio());
    return getIndiceImpactoRepository().findAll(specs, page).hasContent();
  }

  protected LongPredicate getPredicateIsEqualQ1AndCITEC() {
    return produccionCientificaId -> tipoFuenteImpactoCuartilRepository
        .existsByFuenteImpactoAndAnioAndCuartil(TipoFuenteImpacto.CITEC, getAnio(), Cuartil.Q1);
  }

  protected LongPredicate getPredicateIsEqualQ2AndCITEC() {
    return produccionCientificaId -> tipoFuenteImpactoCuartilRepository
        .existsByFuenteImpactoAndAnioAndCuartil(TipoFuenteImpacto.CITEC, getAnio(), Cuartil.Q2);
  }

  protected LongPredicate getPredicateIsEqualQ3AndCITEC() {
    return produccionCientificaId -> tipoFuenteImpactoCuartilRepository
        .existsByFuenteImpactoAndAnioAndCuartil(TipoFuenteImpacto.CITEC, getAnio(), Cuartil.Q3);
  }

  protected LongPredicate getPredicateIsEqualQ4AndCITEC() {
    return produccionCientificaId -> tipoFuenteImpactoCuartilRepository
        .existsByFuenteImpactoAndAnioAndCuartil(TipoFuenteImpacto.CITEC, getAnio(), Cuartil.Q4);
  }

  protected LongPredicate getPredicateIsEqualQ1AndERIH() {
    return produccionCientificaId -> tipoFuenteImpactoCuartilRepository
        .existsByFuenteImpactoAndAnioAndCuartil(TipoFuenteImpacto.ERIH, getAnio(), Cuartil.Q1);
  }

  protected LongPredicate getPredicateIsEqualQ2AndERIH() {
    return produccionCientificaId -> tipoFuenteImpactoCuartilRepository
        .existsByFuenteImpactoAndAnioAndCuartil(TipoFuenteImpacto.ERIH, getAnio(), Cuartil.Q2);
  }

  protected LongPredicate getPredicateIsEqualQ3AndERIH() {
    return produccionCientificaId -> tipoFuenteImpactoCuartilRepository
        .existsByFuenteImpactoAndAnioAndCuartil(TipoFuenteImpacto.ERIH, getAnio(), Cuartil.Q3);
  }

  protected LongPredicate getPredicateIsEqualQ4AndERIH() {
    return produccionCientificaId -> tipoFuenteImpactoCuartilRepository
        .existsByFuenteImpactoAndAnioAndCuartil(TipoFuenteImpacto.ERIH, getAnio(), Cuartil.Q4);
  }
}
