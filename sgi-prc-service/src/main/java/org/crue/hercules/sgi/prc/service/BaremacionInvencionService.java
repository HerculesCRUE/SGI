package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.function.LongPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.CollectionUtils;
import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.crue.hercules.sgi.prc.dto.BaremacionInput.BaremoInput;
import org.crue.hercules.sgi.prc.dto.csp.ProyectoProyectoSgeDto;
import org.crue.hercules.sgi.prc.dto.pi.InvencionDto;
import org.crue.hercules.sgi.prc.dto.pi.InvencionDto.SolicitudProteccionDto;
import org.crue.hercules.sgi.prc.dto.rel.RelacionOutput;
import org.crue.hercules.sgi.prc.dto.rel.RelacionOutput.TipoEntidad;
import org.crue.hercules.sgi.prc.dto.sgepii.IngresoColumnaDefDto;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.Baremo;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.MapeoTipos;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador.TipoPuntuacion;
import org.crue.hercules.sgi.prc.model.Rango;
import org.crue.hercules.sgi.prc.model.Rango.TipoRango;
import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.repository.AliasEnumeradoRepository;
import org.crue.hercules.sgi.prc.repository.AutorGrupoRepository;
import org.crue.hercules.sgi.prc.repository.AutorRepository;
import org.crue.hercules.sgi.prc.repository.BaremoRepository;
import org.crue.hercules.sgi.prc.repository.CampoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.IndiceExperimentalidadRepository;
import org.crue.hercules.sgi.prc.repository.IndiceImpactoRepository;
import org.crue.hercules.sgi.prc.repository.MapeoTiposRepository;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionBaremoItemRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionItemInvestigadorRepository;
import org.crue.hercules.sgi.prc.repository.RangoRepository;
import org.crue.hercules.sgi.prc.repository.ValorCampoRepository;
import org.crue.hercules.sgi.prc.repository.specification.RangoSpecifications;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiCnfService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiCspService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiPiiService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiRelService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiSgePiiService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiSgpService;
import org.crue.hercules.sgi.prc.util.ProduccionCientificaFieldFormatUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la baremación de invenciones
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class BaremacionInvencionService extends BaremacionCommonService {
  public static final String PREFIX_INVENCIONES = "INV_";
  public static final String TIPO_PROTECCION_PATENTE = "141";
  public static final String TIPO_PROTECCION_MOD_UTILIDAD = "126";
  public static final String TIPO_PROTECCION_DISE_SECR_IND = "109";
  public static final String TIPO_PROTECCION_MARCA = "122";
  public static final String TIPO_PROTECCION_PROPIEDAD_INTELECTUAL = "OTHERS";

  private static final EpigrafeCVN EPIGRAFE_CVN_INVENCION = EpigrafeCVN.E050_030_010_000;
  private static final CodigoCVN CODIGO_CVN_TIPO_PROTECCCION = CodigoCVN.E050_030_010_030;
  private static final CodigoCVN CODIGO_CVN_AMBITO_EUROPA = CodigoCVN.E050_030_010_170;
  private static final CodigoCVN CODIGO_CVN_AMBITO_ESPANIA = CodigoCVN.E050_030_010_160;

  private final MapeoTiposRepository mapeoTiposRepository;
  private final RangoRepository rangoRepository;
  private final SgiApiPiiService sgiApiPiiService;
  private final SgiApiCnfService sgiApiCnfService;
  private final SgiApiRelService sgiApiRelService;
  private final SgiApiSgePiiService sgiApiSgePiiService;

  @Autowired
  public BaremacionInvencionService(
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
      RangoRepository rangoRepository,
      SgiApiPiiService sgiApiPiiService,
      SgiApiCnfService sgiApiCnfService,
      SgiApiRelService sgiApiRelService,
      SgiApiSgePiiService sgiApiSgePiiService) {
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
    this.rangoRepository = rangoRepository;
    this.sgiApiPiiService = sgiApiPiiService;
    this.sgiApiCnfService = sgiApiCnfService;
    this.sgiApiRelService = sgiApiRelService;
    this.sgiApiSgePiiService = sgiApiSgePiiService;

    loadPredicates();
  }

  @Override
  protected TipoPuntuacion getTipoPuntuacion() {
    return TipoPuntuacion.INVENCIONES;
  }

  @Override
  protected void loadPredicates() {
    // INVENCION_PATENTE_NACIONAL
    getHmTipoBaremoPredicates().put(TipoBaremo.INVENCION_PATENTE_NACIONAL,
        getPredicateIsTipoProteccionPatente().and(getPredicateHasViaProteccionEspania()));

    // INVENCION_PATENTE_INTERNACIONAL
    getHmTipoBaremoPredicates().put(TipoBaremo.INVENCION_PATENTE_INTERNACIONAL,
        getPredicateIsTipoProteccionPatente().and(getPredicateHasViaProteccionEuropea()));

    // INVENCION_OTRO_NACIONAL
    LongPredicate predicateOtroNacional1 = getPredicateIsTipoProteccionPropiedadIndustrial()
        .and(getPredicateHasViaProteccionEspania());
    LongPredicate predicateOtroNacional2 = getPredicateIsTipoProteccionPropiedadIntelectual();
    getHmTipoBaremoPredicates().put(TipoBaremo.INVENCION_OTRO_NACIONAL,
        predicateOtroNacional1.or(predicateOtroNacional2));

    // INVENCION_OTRO_INTERNACIONAL
    getHmTipoBaremoPredicates().put(TipoBaremo.INVENCION_OTRO_INTERNACIONAL,
        getPredicateIsTipoProteccionPropiedadIndustrial().and(getPredicateHasViaProteccionEuropea()));
  }

  @Override
  protected BigDecimal evaluateBaremoPrincipal(BaremacionInput baremacionInput) {
    log.debug("evaluateBaremoPrincipal(baremacionInput) - start");

    BigDecimal puntos = BigDecimal.ZERO;

    TipoBaremo tipoBaremo = baremacionInput.getBaremo().getConfiguracionBaremo().getTipoBaremo();

    if (evaluateProduccionCientificaByTipoBaremo(baremacionInput, tipoBaremo)) {
      puntos = calculatePuntosByParticipacion(baremacionInput.getProduccionCientificaId(),
          baremacionInput.getBaremo().getPuntos());

      String optionalMessage = String.format("BAREMACION PRINCIPAL INVENCION [%s] %s", tipoBaremo.name(),
          puntos.toString());
      traceLog(baremacionInput, optionalMessage);
    }

    log.debug("evaluateBaremoPrincipal(baremacionInput) - end");
    return puntos;
  }

  private BigDecimal getParticipacion(Long produccionCientificaId) {
    BigDecimal participacion = BigDecimal.ZERO;
    Optional<ValorCampo> optValorCampo = findValoresByCampoProduccionCientificaId(
        CodigoCVN.PORCENTAJE_TITULARIDAD, produccionCientificaId).stream()
        .filter(valorCampo -> valorCampo.getOrden().compareTo(getAnio()) == 0).findFirst();

    if (optValorCampo.isPresent()) {
      participacion = new BigDecimal(optValorCampo.get().getValor());
    }
    return participacion;
  }

  @Override
  protected BigDecimal evaluateBaremoExtra(BaremacionInput baremacionInput) {
    log.debug("evaluateBaremoExtra(baremacionInput) - start");
    BigDecimal puntos = BigDecimal.ZERO;

    List<ValorCampo> valores = findValoresByCampoProduccionCientificaId(CodigoCVN.CUANTIA_LICENCIAS,
        baremacionInput.getProduccionCientificaId());

    if (!valores.isEmpty()) {
      BigDecimal cuantia = new BigDecimal(valores.get(0).getValor());

      Specification<Rango> specs = RangoSpecifications
          .byTipoRango(TipoRango.LICENCIA).and(RangoSpecifications.inRange(cuantia));

      List<Rango> rangos = rangoRepository.findAll(specs);
      if (!CollectionUtils.isEmpty(rangos)) {
        puntos = rangos.get(0).getPuntos();
      }

      // Multiplicamos por su participacion
      puntos = calculatePuntosByParticipacion(baremacionInput.getProduccionCientificaId(), puntos);

      String optionalMessage = String.format("BAREMACION EXTRA INVENCION %s", puntos.toString());
      traceLog(baremacionInput, optionalMessage);
    }

    log.debug("evaluateBaremoExtra(baremacionInput) - end");
    return puntos;
  }

  private BigDecimal calculatePuntosByParticipacion(Long produccionCientificaId, BigDecimal puntos) {
    BigDecimal participacion = getParticipacion(produccionCientificaId);

    if (participacion.compareTo(BigDecimal.ZERO) > 0) {
      puntos = puntos.multiply(participacion).divide(new BigDecimal("100.00"), 2, RoundingMode.HALF_UP);
    }
    return puntos;
  }

  /* -------------------- predicates -------------------- */
  private LongPredicate getPredicateIsTipoProteccionPatente() {
    return produccionCientificaId -> isValorEqualsStringValue(CODIGO_CVN_TIPO_PROTECCCION,
        TIPO_PROTECCION_PATENTE, produccionCientificaId);
  }

  private LongPredicate getPredicateIsTipoProteccionPropiedadIntelectual() {
    return produccionCientificaId -> isValorEqualsStringValue(CODIGO_CVN_TIPO_PROTECCCION,
        TIPO_PROTECCION_PROPIEDAD_INTELECTUAL, produccionCientificaId);
  }

  private LongPredicate getPredicateHasViaProteccionEspania() {
    return produccionCientificaId -> hasValorInAnio(CODIGO_CVN_AMBITO_ESPANIA,
        "true", produccionCientificaId);
  }

  private LongPredicate getPredicateHasViaProteccionEuropea() {
    return produccionCientificaId -> hasValorInAnio(CODIGO_CVN_AMBITO_EUROPA,
        "true", produccionCientificaId);
  }

  private boolean hasValorInAnio(CodigoCVN codigoCVN, String stringValue, Long produccionCientificaId) {
    return findValoresByCampoProduccionCientificaId(codigoCVN, produccionCientificaId).stream()
        .anyMatch(valorCampo -> valorCampo.getValor().equals(stringValue) &&
            valorCampo.getOrden().compareTo(getAnio()) == 0);
  }

  private LongPredicate getPredicateIsTipoProteccionPropiedadIndustrial() {
    return produccionCientificaId -> isValorEqualsStringValue(
        CODIGO_CVN_TIPO_PROTECCCION, TIPO_PROTECCION_MOD_UTILIDAD, produccionCientificaId) ||
        isValorEqualsStringValue(CODIGO_CVN_TIPO_PROTECCCION, TIPO_PROTECCION_DISE_SECR_IND, produccionCientificaId) ||
        isValorEqualsStringValue(CODIGO_CVN_TIPO_PROTECCCION, TIPO_PROTECCION_MARCA, produccionCientificaId);
  }

  @Override
  protected List<Pair<BigDecimal, Baremo>> calculatePuntosBaremosPrincipales(BaremacionInput baremacionInput,
      List<Baremo> baremosPrincipales) {
    // Aunque encuentre un baremo principal sigue buscando a ver si cumplen mas
    // condiciones de otros baremos principales
    return baremosPrincipales.stream().map(
        baremo -> {
          baremacionInput.setBaremo(getModelMapper().map(baremo, BaremoInput.class));
          return Pair.of(this.evaluateBaremoPrincipal(baremacionInput), baremo);
        })
        .filter(pairPuntosBaremo -> pairPuntosBaremo.getFirst().compareTo(BigDecimal.ZERO) > 0)
        .collect(Collectors.toList());
  }

  /* -------------------- Copy invenciones -------------------- */

  public void copyInvenciones(Integer anioInicio, Integer anioFin) {
    log.debug("copyInvenciones(anioInicio, anioFin) - start");

    // Delete all invenciones
    getProduccionCientificaRepository().findByEpigrafeCVNAndConvocatoriaBaremacionIdIsNull(EPIGRAFE_CVN_INVENCION)
        .forEach(getProduccionCientificaBuilderService()::deleteProduccionCientifica);

    String universidadId = sgiApiCnfService.findByName("id-entidad-sgemp");

    sgiApiPiiService.findInvencionesProduccionCientifica(anioInicio, anioFin,
        universidadId).stream()
        .forEach(invencion -> {

          Long invencionId = invencion.getId();
          String produccionCientificaRef = PREFIX_INVENCIONES + invencionId;

          ProduccionCientifica produccionCientifica = ProduccionCientifica.builder()
              .epigrafeCVN(EPIGRAFE_CVN_INVENCION)
              .produccionCientificaRef(produccionCientificaRef)
              .build();

          Long produccionCientificaId = getProduccionCientificaBuilderService().addProduccionCientificaAndEstado(
              produccionCientifica,
              TipoEstadoProduccion.VALIDADO);

          addCampoInvencionTitulo(invencion, produccionCientificaId);
          addCampoInvencionPorcentajeTitularidad(invencion, produccionCientificaId,
              anioInicio);
          addCampoInvencionTipoProteccion(invencion, produccionCientificaId);
          addCampoInvencionFechaConcesion(invencion, produccionCientificaId);
          addCampoInvencionAmbitoGeografico(invencion, produccionCientificaId,
              CODIGO_CVN_AMBITO_ESPANIA);
          addCampoInvencionAmbitoGeografico(invencion, produccionCientificaId,
              CODIGO_CVN_AMBITO_EUROPA);
          addCampoInvencionCuantiaLicencias(invencion, produccionCientificaId);

          invencion.getInventores().stream().forEach(
              personaRef -> getProduccionCientificaBuilderService().addAutorByPersonaRefAndIp(produccionCientificaId,
                  personaRef, Boolean.FALSE));

        });
    log.debug("copyInvenciones(anioInicio, anioFin) - end");
  }

  private void addCampoInvencionTipoProteccion(InvencionDto invencion, Long produccionCientificaId) {
    CodigoCVN codigoCVNTipoProteccion = CODIGO_CVN_TIPO_PROTECCCION;

    Optional<MapeoTipos> optMapeoTipos = mapeoTiposRepository.findByCodigoCVNAndIdTipoRef(
        codigoCVNTipoProteccion, invencion.getTipoProteccionId());
    if (optMapeoTipos.isPresent()) {
      getProduccionCientificaBuilderService().addCampoProduccionCientificaAndValor(produccionCientificaId,
          codigoCVNTipoProteccion, optMapeoTipos.get().getValor());
    }
  }

  private void addCampoInvencionAmbitoGeografico(InvencionDto invencion, Long produccionCientificaId,
      CodigoCVN codigoCVN) {
    if (!CollectionUtils.isEmpty(invencion.getSolicitudesProteccion())) {
      IntStream.range(0, invencion.getSolicitudesProteccion().size())
          .forEach(i -> {
            SolicitudProteccionDto solicitud = invencion.getSolicitudesProteccion().get(i);

            Optional<MapeoTipos> optMapeoTipos = mapeoTiposRepository.findByCodigoCVNAndIdTipoRef(
                codigoCVN, solicitud.getViaProteccionId());

            // Ponemos como orden el año de la fecha de concesión para poder referenciarlo
            // en el cálculo de la baremación
            Integer orden = LocalDateTime.ofInstant(solicitud.getFechaConcesion(), ZoneOffset.UTC).getYear();

            String valor = "false";
            if (optMapeoTipos.isPresent()) {
              valor = optMapeoTipos.get().getValor();
            }
            addCampoAndValor(produccionCientificaId, codigoCVN, valor, orden);
          });
    }
  }

  private void addCampoInvencionCuantiaLicencias(InvencionDto invencion, Long produccionCientificaId) {
    BigDecimal cuantiaLicencias = getCuantiaByInvencionId(invencion.getId());

    // Solo es necesario para facilitar los test
    if ((null == cuantiaLicencias
        || cuantiaLicencias.compareTo(BigDecimal.ZERO) == 0) && null != invencion.getCuantia()) {
      cuantiaLicencias = invencion.getCuantia();
    }

    if (null != cuantiaLicencias && cuantiaLicencias.compareTo(BigDecimal.ZERO) != 0) {
      getProduccionCientificaBuilderService()
          .addCampoProduccionCientificaAndValor(produccionCientificaId, CodigoCVN.CUANTIA_LICENCIAS,
              ProduccionCientificaFieldFormatUtil.formatNumber(cuantiaLicencias.toString()));
    }
  }

  private BigDecimal getCuantiaByInvencionId(Long invencionId) {
    try {
      return sgiApiRelService.findAllRelaciones(String.format("invencionRef==\"%d\"", invencionId)).stream()
          .map(this::getProyectoIdFromRelacion)
          .map(this::getProyectosSgeId)
          .map(this::getIngresosProyectosSge)
          .reduce(new BigDecimal("0.00"), BigDecimal::add);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return null;
  }

  private String getProyectoIdFromRelacion(RelacionOutput relacion) {
    return relacion.getTipoEntidadOrigen().equals(TipoEntidad.PROYECTO) ? relacion.getEntidadOrigenRef()
        : relacion.getEntidadDestinoRef();
  }

  private List<String> getProyectosSgeId(String proyectoId) {
    return this.getSgiApiCspService().findProyectosSgeByProyectoId(Long.valueOf(proyectoId)).stream()
        .map(ProyectoProyectoSgeDto::getProyectoSgeRef).collect(Collectors.toList());
  }

  private BigDecimal getIngresosProyectosSge(List<String> proyectoSgeIds) {
    return proyectoSgeIds.stream().map(proyectoSgeId -> {
      Optional<IngresoColumnaDefDto> ingresoColumnaDefDto = this.sgiApiSgePiiService
          .findIngresosInvencionColumnasDef(String.format("proyectoId==\"%s\"", proyectoSgeId)).stream()
          .filter(IngresoColumnaDefDto::isImporteReparto).findFirst();

      if (ingresoColumnaDefDto.isPresent()) {
        return this.sgiApiSgePiiService.findIngresosInvencion(String.format("proyectoId==\"%s\"", proyectoSgeId))
            .stream()
            .map(ingreso -> ingreso.getColumnas().get(ingresoColumnaDefDto.get().getId()).toString())
            .map(BigDecimal::new)
            .reduce(new BigDecimal("0.00"), BigDecimal::add);
      } else {
        return BigDecimal.ZERO;
      }
    }).reduce(new BigDecimal("0.00"), BigDecimal::add);
  }

  private void addCampoInvencionFechaConcesion(InvencionDto invencion, Long produccionCientificaId) {
    if (!CollectionUtils.isEmpty(invencion.getSolicitudesProteccion())) {
      CodigoCVN codigoCVN = CodigoCVN.E050_030_010_320;

      IntStream.range(0, invencion.getSolicitudesProteccion().size())
          .forEach(i -> {
            Instant fechaConcesion = invencion.getSolicitudesProteccion().get(i).getFechaConcesion();
            // Ponemos como orden el año de la fecha de concesión para poder referenciarlo
            // en el cálculo de la baremación
            Integer orden = LocalDateTime.ofInstant(fechaConcesion, ZoneOffset.UTC).getYear();
            addCampoAndValor(produccionCientificaId, codigoCVN, fechaConcesion.toString(), orden);
          });
    }
  }

  private void addCampoInvencionPorcentajeTitularidad(InvencionDto invencion, Long produccionCientificaId,
      Integer anioInicio) {
    if (!CollectionUtils.isEmpty(invencion.getParticipaciones())) {
      CodigoCVN codigoCVN = CodigoCVN.PORCENTAJE_TITULARIDAD;
      IntStream.range(0, invencion.getParticipaciones().size())
          .forEach(i -> {
            String valor = ProduccionCientificaFieldFormatUtil
                .formatNumber(invencion.getParticipaciones().get(i).toString());
            // Ponemos como orden el año de la fecha de baremación para poder referenciarlo
            addCampoAndValor(produccionCientificaId, codigoCVN, valor, anioInicio + i);
          });
    }
  }

  private void addCampoAndValor(Long produccionCientificaId, CodigoCVN codigoCVN, String valor, Integer orden) {
    Optional<CampoProduccionCientifica> campo = getCampoProduccionCientificaRepository()
        .findByCodigoCVNAndProduccionCientificaId(codigoCVN, produccionCientificaId);
    if (campo.isPresent()) {
      getProduccionCientificaBuilderService().addValorCampo(campo.get().getId(), valor, orden);
    } else {
      getProduccionCientificaBuilderService().addCampoProduccionCientificaAndValor(
          produccionCientificaId,
          codigoCVN,
          valor,
          orden);
    }
  }

  private void addCampoInvencionTitulo(InvencionDto invencion, Long produccionCientificaId) {
    getProduccionCientificaBuilderService()
        .addCampoProduccionCientificaAndValor(produccionCientificaId, CodigoCVN.E050_030_010_020,
            invencion.getTitulo());
  }

}
