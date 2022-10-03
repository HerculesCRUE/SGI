package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.LongPredicate;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.crue.hercules.sgi.prc.dto.sgp.DireccionTesisDto;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.enums.TablaMaestraCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
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
import org.crue.hercules.sgi.prc.repository.specification.ProduccionCientificaSpecifications;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiCspService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiSgpService;
import org.crue.hercules.sgi.prc.util.ProduccionCientificaFieldFormatUtil;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
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

  public static final String PREFIX_TESIS = "TES_";

  private static final EpigrafeCVN EPIGRAFE_CVN_DIRECCION_TESIS = EpigrafeCVN.E030_040_000_000;
  private static final String TRUE = "true";
  private static final String FALSE = "false";

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

  @Override
  protected TipoPuntuacion getTipoPuntuacion() {
    return TipoPuntuacion.DIRECCION_TESIS;
  }

  @Override
  protected void loadPredicates() {

    // DIRECCION_TESIS_TESIS
    getHmTipoBaremoPredicates().put(TipoBaremo.DIRECCION_TESIS_TESIS, getPredicateIsTesisDoctoral());

    // DIRECCION_TESIS_TESINA_O_DEA_O_TFM
    LongPredicate isNotTesis = getPredicateIsTesina().or(getPredicateIsTFM()).or(getPredicateIsDEA())
        .or(getPredicateIsOther());

    getHmTipoBaremoPredicates().put(TipoBaremo.DIRECCION_TESIS_TESINA_O_DEA_O_TFM, isNotTesis);

    loadPredicatesExtra();
  }

  private void loadPredicatesExtra() {
    getHmTipoBaremoPredicates().put(TipoBaremo.DIRECCION_TESIS_MENCION_INDUSTRIAL,
        getPredicateIsTesisDoctoral().and(getPredicateIsMencionIndustrial()));

    getHmTipoBaremoPredicates().put(TipoBaremo.DIRECCION_TESIS_MENCION_INTERNACIONAL,
        getPredicateIsTesisDoctoral().and(getPredicateIsMencionInternacional()));
  }

  public void copyTesis(Integer anioInicio, Integer anioFin) {
    log.debug("copyTesis(anioInicio, anioFin) - start");

    // Delete all tesis
    Specification<ProduccionCientifica> specs = ProduccionCientificaSpecifications
        .byEpigrafeCVNIn(Arrays.asList(EPIGRAFE_CVN_DIRECCION_TESIS))
        .and(ProduccionCientificaSpecifications.byConvocatoriaBaremacionIsNull())
        .and(ProduccionCientificaSpecifications.byProduccionCientificaRefStartWith(PREFIX_TESIS));

    getProduccionCientificaRepository().findAll(specs)
        .forEach(getProduccionCientificaBuilderService()::deleteProduccionCientifica);

    try {
      IntStream.range(anioInicio, anioFin)
          .forEach(anio -> {

            List<DireccionTesisDto> direccionTesisAnio = getSgiApiSgpService().findTesisByAnio(anio);

            List<String> personasEquipo = getAllPersonasInGruposBaremablesByAnio(anio);

            personasEquipo.forEach(personaRef -> {
              Optional<DireccionTesisDto> optTesis = direccionTesisAnio.stream()
                  .filter(tesis -> isDirectorOrCoDirectorTesis(tesis, personaRef))
                  .findFirst();
              if (optTesis.isPresent()) {
                saveTesis(optTesis.get());
              }
            });
          });
    } catch (Exception e) {
      log.error(e.getMessage());
    }

    log.debug("copyTesis(anioInicio, anioFin) - end");
  }

  private void saveTesis(DireccionTesisDto tesis) {
    try {
      String tesisId = tesis.getId();
      String produccionCientificaRef = PREFIX_TESIS + tesisId;

      ProduccionCientifica produccionCientifica = ProduccionCientifica.builder()
          .epigrafeCVN(EPIGRAFE_CVN_DIRECCION_TESIS)
          .produccionCientificaRef(produccionCientificaRef)
          .build();

      Long produccionCientificaId = getProduccionCientificaBuilderService().addProduccionCientificaAndEstado(
          produccionCientifica, TipoEstadoProduccion.VALIDADO);

      addCampoTesisTitulo(tesis, produccionCientificaId);
      addCampoTesisFecha(tesis, produccionCientificaId);
      addCampoTesisAlumno(tesis, produccionCientificaId);
      addCampoTesisTipoProyecto(tesis, produccionCientificaId);
      addCampoTesisMencionCalidad(tesis, produccionCientificaId);
      addCampoTesisFechaMencionCalidad(tesis, produccionCientificaId);
      addCampoTesisDoctoradoEuropeo(tesis, produccionCientificaId);
      addCampoTesisFechaMencionDoctoradoEuropeo(tesis, produccionCientificaId);
      addCampoTesisMencionInternacional(tesis, produccionCientificaId);
      addCampoTesisMencionIndustrial(tesis, produccionCientificaId);

      if (StringUtils.hasText(tesis.getPersonaRef())) {
        getProduccionCientificaBuilderService().addAutorByPersonaRefAndIp(produccionCientificaId,
            tesis.getPersonaRef(), Boolean.FALSE);
      }

      if (StringUtils.hasText(tesis.getCoDirectorTesisRef())) {
        getProduccionCientificaBuilderService().addAutorByPersonaRefAndIp(produccionCientificaId,
            tesis.getCoDirectorTesisRef(), Boolean.FALSE);
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  private boolean isDirectorOrCoDirectorTesis(DireccionTesisDto tesis, String personaRef) {
    return (StringUtils.hasText(tesis.getPersonaRef()) && tesis.getPersonaRef().equals(personaRef)) ||
        (StringUtils.hasText(tesis.getPersonaRef()) && tesis.getCoDirectorTesisRef().equals(personaRef));
  }

  private void addCampoTesisTitulo(DireccionTesisDto tesis, Long produccionCientificaId) {
    if (StringUtils.hasText(tesis.getTituloTrabajo())) {
      getProduccionCientificaBuilderService()
          .addCampoProduccionCientificaAndValor(produccionCientificaId, CodigoCVN.E030_040_000_030,
              tesis.getTituloTrabajo());
    }
  }

  private void addCampoTesisFecha(DireccionTesisDto tesis, Long produccionCientificaId) {
    if (StringUtils.hasText(tesis.getFechaDefensa())) {
      getProduccionCientificaBuilderService().addCampoProduccionCientificaAndValor(produccionCientificaId,
          CodigoCVN.E030_040_000_140,
          ProduccionCientificaFieldFormatUtil.formatDate(tesis.getFechaDefensa(),
              getSgiConfigProperties().getTimeZone()));
    }
  }

  private void addCampoTesisAlumno(DireccionTesisDto tesis, Long produccionCientificaId) {
    if (StringUtils.hasText(tesis.getAlumno())) {
      getProduccionCientificaBuilderService()
          .addCampoProduccionCientificaAndValor(produccionCientificaId, CodigoCVN.E030_040_000_120, tesis.getAlumno());
    }
  }

  private void addCampoTesisTipoProyecto(DireccionTesisDto tesis, Long produccionCientificaId) {
    getProduccionCientificaBuilderService()
        .addCampoProduccionCientificaAndValor(produccionCientificaId, CodigoCVN.E030_040_000_010,
            tesis.getTipoProyecto().getId());
  }

  private void addCampoTesisMencionCalidad(DireccionTesisDto tesis, Long produccionCientificaId) {
    String mencionCalidad = Boolean.TRUE
        .equals(ObjectUtils.defaultIfNull(tesis.getMencionCalidad(), Boolean.FALSE)) ? TRUE : FALSE;
    getProduccionCientificaBuilderService()
        .addCampoProduccionCientificaAndValor(produccionCientificaId, CodigoCVN.E030_040_000_170,
            mencionCalidad);
  }

  private void addCampoTesisFechaMencionCalidad(DireccionTesisDto tesis, Long produccionCientificaId) {
    if (StringUtils.hasText(tesis.getFechaMencionCalidad())) {
      getProduccionCientificaBuilderService().addCampoProduccionCientificaAndValor(produccionCientificaId,
          CodigoCVN.E030_040_000_170,
          ProduccionCientificaFieldFormatUtil.formatDate(tesis.getFechaMencionCalidad(),
              getSgiConfigProperties().getTimeZone()));
    }
  }

  private void addCampoTesisDoctoradoEuropeo(DireccionTesisDto tesis, Long produccionCientificaId) {
    String doctoradoEuropeo = Boolean.TRUE
        .equals(ObjectUtils.defaultIfNull(tesis.getDoctoradoEuropeo(), Boolean.FALSE)) ? TRUE : FALSE;
    getProduccionCientificaBuilderService()
        .addCampoProduccionCientificaAndValor(produccionCientificaId, CodigoCVN.E030_040_000_190,
            doctoradoEuropeo);
  }

  private void addCampoTesisFechaMencionDoctoradoEuropeo(DireccionTesisDto tesis, Long produccionCientificaId) {
    if (null != tesis.getFechaMencionDoctoradoEuropeo()) {
      getProduccionCientificaBuilderService().addCampoProduccionCientificaAndValor(produccionCientificaId,
          CodigoCVN.E030_040_000_160,
          ProduccionCientificaFieldFormatUtil.formatDate(tesis.getFechaMencionDoctoradoEuropeo(),
              getSgiConfigProperties().getTimeZone()));
    }
  }

  private void addCampoTesisMencionInternacional(DireccionTesisDto tesis, Long produccionCientificaId) {
    String mencionInternacional = Boolean.TRUE
        .equals(ObjectUtils.defaultIfNull(tesis.getMencionInternacional(), Boolean.FALSE)) ? TRUE : FALSE;
    getProduccionCientificaBuilderService()
        .addCampoProduccionCientificaAndValor(produccionCientificaId, CodigoCVN.MENCION_INTERNACIONAL,
            mencionInternacional);
  }

  private void addCampoTesisMencionIndustrial(DireccionTesisDto tesis, Long produccionCientificaId) {
    String mencionIndustrial = Boolean.TRUE
        .equals(ObjectUtils.defaultIfNull(tesis.getMencionIndustrial(), Boolean.FALSE)) ? TRUE : FALSE;
    getProduccionCientificaBuilderService()
        .addCampoProduccionCientificaAndValor(produccionCientificaId, CodigoCVN.MENCION_INDUSTRIAL,
            mencionIndustrial);
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
    return produccionCientificaId -> isValorEqualsStringValue(CodigoCVN.MENCION_INDUSTRIAL, TRUE,
        produccionCientificaId);
  }

  private LongPredicate getPredicateIsMencionInternacional() {
    return produccionCientificaId -> isValorEqualsStringValue(CodigoCVN.MENCION_INTERNACIONAL, TRUE,
        produccionCientificaId);
  }

  @Override
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
