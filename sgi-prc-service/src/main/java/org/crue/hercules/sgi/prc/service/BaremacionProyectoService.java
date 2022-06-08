package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.LongPredicate;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.crue.hercules.sgi.prc.dto.csp.ProyectoDto;
import org.crue.hercules.sgi.prc.dto.csp.ProyectoEquipoDto;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.Baremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionCampo;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.MapeoTipos;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador.TipoPuntuacion;
import org.crue.hercules.sgi.prc.repository.AliasEnumeradoRepository;
import org.crue.hercules.sgi.prc.repository.AutorGrupoRepository;
import org.crue.hercules.sgi.prc.repository.AutorRepository;
import org.crue.hercules.sgi.prc.repository.BaremoRepository;
import org.crue.hercules.sgi.prc.repository.CampoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.ConfiguracionCampoRepository;
import org.crue.hercules.sgi.prc.repository.IndiceExperimentalidadRepository;
import org.crue.hercules.sgi.prc.repository.IndiceImpactoRepository;
import org.crue.hercules.sgi.prc.repository.MapeoTiposRepository;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionBaremoItemRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionItemInvestigadorRepository;
import org.crue.hercules.sgi.prc.repository.ValorCampoRepository;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiCspService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiSgpService;
import org.crue.hercules.sgi.prc.util.ProduccionCientificaFieldFormatUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la baremación de proyectos
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class BaremacionProyectoService extends BaremacionCommonService {
  private static final String AMBITO_LOCAL = "000";
  private static final String AMBITO_NACIONAL = "010";
  private static final String AMBITO_EU = "020";
  private static final String AMBITO_INTERNACIONAL_NO_EU = "030";
  private static final String AMBITO_RESTO = "OTHERS";

  public static final String PREFIX_PROYECTOS = "PRO_";

  public static final EpigrafeCVN EPIGRAFE_CVN_PROYECTO = EpigrafeCVN.E050_020_010_000;
  public static final EpigrafeCVN EPIGRAFE_CVN_CONTRATO = EpigrafeCVN.E050_020_020_000;

  private final MapeoTiposRepository mapeoTiposRepository;
  private final ConfiguracionCampoRepository configuracionCampoRepository;

  @Autowired
  public BaremacionProyectoService(
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
      SgiConfigProperties sgiConfigProperties,
      MapeoTiposRepository mapeoTiposRepository,
      ConfiguracionCampoRepository configuracionCampoRepository) {
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

    this.mapeoTiposRepository = mapeoTiposRepository;
    this.configuracionCampoRepository = configuracionCampoRepository;

    loadPredicates();
  }

  protected TipoPuntuacion getTipoPuntuacion() {
    return TipoPuntuacion.PROYECTOS_INVESTIGACION;
  }

  protected void loadPredicates() {

    // PROYECTO
    getHmTipoBaremoPredicates().put(TipoBaremo.PROYECTO_INVESTIGACION_REGIONAL, getPredicateAmbitoIsRegional());
    getHmTipoBaremoPredicates().put(TipoBaremo.PROYECTO_INVESTIGACION_NACIONAL, getPredicateAmbitoIsNacional());
    getHmTipoBaremoPredicates().put(TipoBaremo.PROYECTO_INVESTIGACION_EUROPEO, getPredicateAmbitoEuropeo());
    getHmTipoBaremoPredicates().put(TipoBaremo.PROYECTO_INVESTIGACION_RESTO, getPredicateAmbitoIsResto());

    loadPredicatesExtra();
  }

  private void loadPredicatesExtra() {
    getHmTipoBaremoPredicates().put(TipoBaremo.PROYECTO_INVESTIGACION_REGIONAL_EXCELENCIA,
        getPredicateAmbitoIsRegional().and(getPredicateConvocatoriaIsExcelencia()));
    getHmTipoBaremoPredicates().put(TipoBaremo.PROYECTO_INVESTIGACION_NACIONAL_EXCELENCIA,
        getPredicateAmbitoIsNacional().and(getPredicateConvocatoriaIsExcelencia()));
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

  @Override
  protected List<Long> getProduccionCientificaIdsByEpigrafeCVNAndAnio(BaremacionInput baremacionInput) {
    List<Long> result = new ArrayList<>();
    Optional<ConfiguracionCampo> optFechaInicio = configuracionCampoRepository
        .findByEpigrafeCVNAndFechaReferenciaInicioIsTrue(EPIGRAFE_CVN_PROYECTO);
    Optional<ConfiguracionCampo> optFechaFin = configuracionCampoRepository
        .findByEpigrafeCVNAndFechaReferenciaFinIsTrue(EPIGRAFE_CVN_PROYECTO);

    if (optFechaInicio.isPresent() && optFechaFin.isPresent()) {
      CodigoCVN codigoCVNFechaInicio = optFechaInicio.get().getCodigoCVN();
      CodigoCVN codigoCVNFechaFin = optFechaFin.get().getCodigoCVN();
      result = getProduccionCientificaRepository().findAllBaremacionByFechaInicioAndFechaFin(baremacionInput,
          codigoCVNFechaInicio, codigoCVNFechaFin);
    }
    return result;
  }

  @Override
  protected void evaluateAutores(BaremacionInput baremacionInput, BigDecimal puntos, Long newProduccionCientificaId) {
    log.debug("evaluateAutores(baremacionInput, puntos, newProduccionCientificaId) - start");

    String optionalMessage;
    List<Autor> autoresBaremables = findAutoresBaremablesByAnio(newProduccionCientificaId, baremacionInput.getAnio());

    if (!autoresBaremables.isEmpty()) {
      int numAutores = autoresBaremables.size();
      BigDecimal puntosInvestigador = puntos.divide(new BigDecimal(numAutores), 2, RoundingMode.HALF_UP);

      optionalMessage = String.format("AUTORES BAREMABLES %d PUNTOS TOTALES=%s --> %s",
          numAutores, puntos.toString(), puntosInvestigador.toString());
      traceLog(baremacionInput, optionalMessage);

      Map<Baremo, BigDecimal> mapPuntuacionBaremo = new HashMap<>();
      autoresBaremables.stream()
          .filter(autor -> isAutorGrupoValidado(autor.getId(), newProduccionCientificaId))
          .forEach(
              autor -> {
                BaremacionInput baremacionInputNew = getModelMapper().map(baremacionInput, BaremacionInput.class);
                baremacionInputNew.setProduccionCientificaId(newProduccionCientificaId);

                Baremo baremoEvaluated = evaluateAutorTipoBaremoPrincipalAdditional(baremacionInputNew, autor.getIp());

                BigDecimal puntosInvestigadorAdicional = puntosInvestigador;
                if (null != baremoEvaluated) {
                  saveOrUpdateMapPuntuacionBaremo(mapPuntuacionBaremo, baremoEvaluated);
                  puntosInvestigadorAdicional = puntosInvestigadorAdicional.add(baremoEvaluated.getPuntos());
                }

                evaluatePuntuacionItemInvestigador(baremacionInputNew, puntosInvestigadorAdicional, autor);
              });

      mapPuntuacionBaremo
          .forEach((baremo, puntosBaremo) -> savePuntacionBaremoAutores(baremacionInput, baremo, puntosBaremo));
    }

    log.debug("evaluateAutores(baremacionInput, puntos, newProduccionCientificaId) - end");
  }

  private void savePuntacionBaremoAutores(BaremacionInput baremacionInput, Baremo baremo, BigDecimal puntosBaremo) {
    Long baremoId = baremo.getId();
    String optionalMessagePuntuacionBaremo = String.format("PuntuacionBaremoItem PROYECTO IP Baremo[%d] Puntos[%s]",
        baremoId, puntosBaremo.toString());
    traceLog(baremacionInput, optionalMessagePuntuacionBaremo);

    savePuntuacionBaremoItem(baremoId, baremacionInput.getProduccionCientificaId(), puntosBaremo,
        baremacionInput.getAnio());
  }

  private void saveOrUpdateMapPuntuacionBaremo(Map<Baremo, BigDecimal> mapPuntuacionBaremo, Baremo baremoEvaluated) {
    if (mapPuntuacionBaremo.containsKey(baremoEvaluated)) {
      BigDecimal puntosOld = mapPuntuacionBaremo.get(baremoEvaluated);
      BigDecimal puntosNew = puntosOld.add(baremoEvaluated.getPuntos());
      mapPuntuacionBaremo.replace(baremoEvaluated, puntosOld, puntosNew);
    } else {
      mapPuntuacionBaremo.put(baremoEvaluated, baremoEvaluated.getPuntos());
    }
  }

  private Baremo evaluateAutorTipoBaremoPrincipalAdditional(BaremacionInput baremacionInput, Boolean ip) {
    if (Boolean.TRUE.equals(ip)) {
      Long produccionCientificaId = baremacionInput.getProduccionCientificaId();

      Baremo baremo = findBaremoByTipoBaremoActivo(baremacionInput, TipoBaremo.PROYECTO_INVESTIGACION_REGIONAL_IP);
      if (null != baremo && getPredicateAmbitoIsRegional().test(produccionCientificaId)) {
        return baremo;
      }

      baremo = findBaremoByTipoBaremoActivo(baremacionInput, TipoBaremo.PROYECTO_INVESTIGACION_NACIONAL_IP);
      if (null != baremo && getPredicateAmbitoIsNacional().test(produccionCientificaId)) {
        return baremo;
      }

      baremo = findBaremoByTipoBaremoActivo(baremacionInput, TipoBaremo.PROYECTO_INVESTIGACION_EUROPEO_IP);
      if (null != baremo && getPredicateAmbitoEuropeo().test(produccionCientificaId)) {
        return baremo;
      }

      baremo = findBaremoByTipoBaremoActivo(baremacionInput, TipoBaremo.PROYECTO_INVESTIGACION_RESTO_IP);
      if (null != baremo && getPredicateAmbitoIsResto().test(produccionCientificaId)) {
        return baremo;
      }
    }

    return null;
  }

  /* -------------------- predicates -------------------- */

  private LongPredicate getPredicateAmbitoIsRegional() {
    return produccionCientificaId -> isValorEqualsStringValue(CodigoCVN.E050_020_010_040,
        AMBITO_LOCAL,
        produccionCientificaId);
  }

  private LongPredicate getPredicateAmbitoIsNacional() {
    return produccionCientificaId -> isValorEqualsStringValue(CodigoCVN.E050_020_010_040,
        AMBITO_NACIONAL,
        produccionCientificaId);
  }

  private LongPredicate getPredicateAmbitoIsEuropeo() {
    return produccionCientificaId -> isValorEqualsStringValue(CodigoCVN.E050_020_010_040,
        AMBITO_EU,
        produccionCientificaId);
  }

  private LongPredicate getPredicateAmbitoIsInternacionalNoEuropeo() {
    return produccionCientificaId -> isValorEqualsStringValue(CodigoCVN.E050_020_010_040,
        AMBITO_INTERNACIONAL_NO_EU,
        produccionCientificaId);
  }

  private LongPredicate getPredicateAmbitoEuropeo() {
    return getPredicateAmbitoIsEuropeo().or(
        getPredicateAmbitoIsInternacionalNoEuropeo());
  }

  private LongPredicate getPredicateAmbitoIsResto() {
    return produccionCientificaId -> isValorEqualsStringValue(CodigoCVN.E050_020_010_040, AMBITO_RESTO,
        produccionCientificaId);
  }

  private LongPredicate getPredicateConvocatoriaIsExcelencia() {
    return produccionCientificaId -> isValorEqualsStringValue(CodigoCVN.CONVOCATORIA_EXCELENCIA, "true",
        produccionCientificaId);
  }

  /* -------------------- Copy proyectos -------------------- */

  public void copyProyectos(Integer anioInicio, Integer anioFin) {
    log.debug("copyProyectos(anioInicio, anioFin) - start");

    // Delete all proyectos and contratos
    getProduccionCientificaRepository().findByEpigrafeCVNAndConvocatoriaBaremacionIdIsNull(EPIGRAFE_CVN_PROYECTO)
        .forEach(getProduccionCientificaBuilderService()::deleteProduccionCientifica);
    getProduccionCientificaRepository().findByEpigrafeCVNAndConvocatoriaBaremacionIdIsNull(EPIGRAFE_CVN_CONTRATO)
        .forEach(getProduccionCientificaBuilderService()::deleteProduccionCientifica);

    getSgiApiCspService().findProyectosProduccionCientifica(anioInicio, anioFin - 1).stream().forEach(proyecto -> {
      Long proyectoId = proyecto.getId();
      String produccionCientificaRef = PREFIX_PROYECTOS + proyectoId;

      EpigrafeCVN epigrafeCVN = isContrato(proyecto) ? EPIGRAFE_CVN_CONTRATO : EPIGRAFE_CVN_PROYECTO;
      ProduccionCientifica produccionCientifica = ProduccionCientifica.builder()
          .epigrafeCVN(epigrafeCVN)
          .produccionCientificaRef(produccionCientificaRef)
          .build();

      Long produccionCientificaId = getProduccionCientificaBuilderService().addProduccionCientificaAndEstado(
          produccionCientifica, TipoEstadoProduccion.VALIDADO);

      addCampoProyectoTitulo(proyecto, produccionCientificaId);
      addCampoProyectoFechaInicio(proyecto, produccionCientificaId);
      addCampoProyectoFechaFin(proyecto, produccionCientificaId);
      addCampoProyectoAmbitoGeografico(proyecto, produccionCientificaId);
      addCampoProyectoTotalImporteConcedido(proyecto, produccionCientificaId);
      addCampoProyectoConvocatoriaExcelencia(proyecto, produccionCientificaId);
      addCampoProyectoImporteConcedido(proyecto, produccionCientificaId);

      getSgiApiCspService().findProyectoEquipoByProyectoId(proyectoId).forEach(
          proyectoEquipo -> getProduccionCientificaBuilderService()
              .addAutor(convertProyectoEquipoToAutor(produccionCientificaId, proyectoEquipo)));
    });
    log.debug("copyProyectos(anioInicio, anioFin) - end");

  }

  private boolean isContrato(ProyectoDto proyecto) {
    return null != proyecto.getContrato() && proyecto.getContrato().equals(Boolean.TRUE);
  }

  private Autor convertProyectoEquipoToAutor(Long produccionCientificaId, ProyectoEquipoDto proyectoEquipo) {
    return Autor.builder()
        .produccionCientificaId(produccionCientificaId)
        .personaRef(proyectoEquipo.getPersonaRef())
        .ip(ObjectUtils.defaultIfNull(proyectoEquipo.getIp(), Boolean.FALSE))
        .fechaInicio(proyectoEquipo.getFechaInicio())
        .fechaFin(proyectoEquipo.getFechaFin())
        .build();
  }

  private void addCampoProyectoFechaFin(ProyectoDto proyecto, Long produccionCientificaId) {
    Instant fechaFin = ObjectUtils.defaultIfNull(proyecto.getFechaFinDefinitiva(), proyecto.getFechaFin());
    if (null != fechaFin) {
      CodigoCVN codigoCVN = !isContrato(proyecto) ? CodigoCVN.E050_020_010_410 : CodigoCVN.FECHA_FIN_CONTRATO;
      getProduccionCientificaBuilderService().addCampoProduccionCientificaAndValor(produccionCientificaId, codigoCVN,
          fechaFin.toString());
    }
  }

  private void addCampoProyectoAmbitoGeografico(ProyectoDto proyecto, Long produccionCientificaId) {
    if (!isContrato(proyecto)) {
      CodigoCVN codigoCVN = CodigoCVN.E050_020_010_040;
      Optional<MapeoTipos> optMapeoTipos = mapeoTiposRepository.findByCodigoCVNAndIdTipoRef(codigoCVN,
          proyecto.getAmbitoGeograficoId());
      if (optMapeoTipos.isPresent()) {
        getProduccionCientificaBuilderService().addCampoProduccionCientificaAndValor(produccionCientificaId, codigoCVN,
            optMapeoTipos.get().getValor());
      }
    }
  }

  private void addCampoProyectoImporteConcedido(ProyectoDto proyecto, Long produccionCientificaId) {
    CodigoCVN codigoCVN = !isContrato(proyecto)
        ? CodigoCVN.CUANTIA_COSTES_INDIRECTOS_PROYECTO
        : CodigoCVN.CUANTIA_COSTES_INDIRECTOS_CONTRATO;

    if (null == proyecto.getImporteConcedidoCostesIndirectos()) {
      proyecto.setImporteConcedidoCostesIndirectos(
          getSgiApiCspService().getTotalImporteConcedidoAnualidadGastoCostesIndirectos(proyecto.getId()));
    }

    if (null != proyecto.getImporteConcedidoCostesIndirectos()) {
      String importeConcedido = ProduccionCientificaFieldFormatUtil
          .formatNumber(proyecto.getImporteConcedidoCostesIndirectos().toString());
      getProduccionCientificaBuilderService().addCampoProduccionCientificaAndValor(produccionCientificaId, codigoCVN,
          importeConcedido);
    }
  }

  private void addCampoProyectoTotalImporteConcedido(ProyectoDto proyecto, Long produccionCientificaId) {
    CodigoCVN codigoCVN = !isContrato(proyecto) ? CodigoCVN.E050_020_010_290 : CodigoCVN.E050_020_020_200;

    if (null == proyecto.getTotalImporteConcedido()) {
      proyecto.setTotalImporteConcedido(getSgiApiCspService().getTotalImporteConcedidoAnualidadGasto(proyecto.getId()));
    }

    if (null != proyecto.getTotalImporteConcedido()) {
      String totalImporteConcedido = ProduccionCientificaFieldFormatUtil
          .formatNumber(proyecto.getTotalImporteConcedido().toString());
      getProduccionCientificaBuilderService()
          .addCampoProduccionCientificaAndValor(produccionCientificaId, codigoCVN, totalImporteConcedido);
    }
  }

  private void addCampoProyectoFechaInicio(ProyectoDto proyecto, Long produccionCientificaId) {
    if (null != proyecto.getFechaInicio()) {
      CodigoCVN codigoCVN = !isContrato(proyecto) ? CodigoCVN.E050_020_010_270 : CodigoCVN.E050_020_020_180;
      getProduccionCientificaBuilderService().addCampoProduccionCientificaAndValor(produccionCientificaId, codigoCVN,
          proyecto.getFechaInicio().toString());
    }
  }

  private void addCampoProyectoTitulo(ProyectoDto proyecto, Long produccionCientificaId) {
    CodigoCVN codigoCVN = !isContrato(proyecto) ? CodigoCVN.E050_020_010_010 : CodigoCVN.E050_020_020_010;

    getProduccionCientificaBuilderService()
        .addCampoProduccionCientificaAndValor(produccionCientificaId, codigoCVN, proyecto.getTitulo());
  }

  private void addCampoProyectoConvocatoriaExcelencia(ProyectoDto proyecto, Long produccionCientificaId) {
    if (!isContrato(proyecto)) {
      CodigoCVN codigoCVN = CodigoCVN.CONVOCATORIA_EXCELENCIA;
      String excelencia = Boolean.TRUE
          .equals(ObjectUtils.defaultIfNull(proyecto.getConvocatoriaExcelencia(), Boolean.FALSE)) ? "true" : "false";
      getProduccionCientificaBuilderService()
          .addCampoProduccionCientificaAndValor(produccionCientificaId, codigoCVN, excelencia);
    }
  }
}
