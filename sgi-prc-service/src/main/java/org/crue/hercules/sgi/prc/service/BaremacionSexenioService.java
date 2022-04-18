package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.LongPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.crue.hercules.sgi.prc.dto.csp.GrupoDto;
import org.crue.hercules.sgi.prc.dto.csp.GrupoEquipoDto;
import org.crue.hercules.sgi.prc.dto.sgp.SexenioDto;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador;
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
import org.crue.hercules.sgi.prc.util.ProduccionCientificaFieldFormatUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la baremación de sexenios
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class BaremacionSexenioService extends BaremacionCommonService {
  public static final String PREFIX_SEXENIOS = "SEX_";

  public static final EpigrafeCVN EPIGRAFE_CVN_SEXENIO = EpigrafeCVN.E060_030_070_000;

  @Autowired
  public BaremacionSexenioService(
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
    return TipoPuntuacion.SEXENIO;
  }

  protected void loadPredicates() {

    // SEXENIO
    getHmTipoBaremoPredicates().put(TipoBaremo.SEXENIO, getPredicateIsSexenioNotEmpty());

  }

  /* -------------------- predicates -------------------- */

  private LongPredicate getPredicateIsSexenioNotEmpty() {
    return produccionCientificaId -> isValorCampoNotEmpty(produccionCientificaId, CodigoCVN.E060_030_070_010);
  }

  @Override
  protected void evaluatePuntuacionItemInvestigador(BaremacionInput baremacionInput, BigDecimal puntosInvestigador,
      Autor autor) {
    // Los puntos de cada investigador se deben de multiplicar por un el índice de
    // experimentalidad en todos los items excepto en "Contratos", "Costes
    // indirectos" y "Sexenios"
    log.debug("evaluatePuntuacioItemInvestigador(baremacionInput, puntosInvestigador, autor) - start");

    PuntuacionItemInvestigador puntuacionItemInvestigador = PuntuacionItemInvestigador.builder()
        .anio(baremacionInput.getAnio())
        .personaRef(autor.getPersonaRef())
        .tipoPuntuacion(getTipoPuntuacion())
        .produccionCientificaId(baremacionInput.getProduccionCientificaId())
        .puntos(puntosInvestigador)
        .build();

    String optionalMessage = String.format("PuntuacioItemInvestigador CONTRATO Autor[%s] [%s] %s",
        autor.getPersonaRef(), puntosInvestigador.toString(),
        puntuacionItemInvestigador.toString());
    traceLog(baremacionInput, optionalMessage);

    getPuntuacionItemInvestigadorRepository().save(puntuacionItemInvestigador);
    log.debug("evaluatePuntuacioItemInvestigador(produccionCientificaId, puntosInvestigador, autor) - end");
  }

  @Override
  protected BigDecimal evaluateBaremoPrincipal(BaremacionInput baremacionInput) {
    log.debug("calcularBaremoPrincipalPublicacion(produccionCientificaId, baremo) - start");

    BigDecimal puntos = BigDecimal.ZERO;

    TipoBaremo tipoBaremo = baremacionInput.getBaremo().getConfiguracionBaremo().getTipoBaremo();

    if (evaluateProduccionCientificaByTipoBaremo(baremacionInput, tipoBaremo)) {
      puntos = baremacionInput.getBaremo().getPuntos();
      if (tipoBaremo.equals(TipoBaremo.SEXENIO)) {
        BigDecimal numeroSexenios = findValoresByCampoProduccionCientificaId(CodigoCVN.E060_030_070_010,
            baremacionInput.getProduccionCientificaId()).stream()
            .map(valorCampo -> new BigDecimal(valorCampo.getValor()))
            .reduce(new BigDecimal(0), BigDecimal::add);

        puntos = puntos.multiply(numeroSexenios);
      }

      String optionalMessage = String.format("BAREMACION PRINCIPAL [%s] %s", tipoBaremo.name(), puntos.toString());
      traceLog(baremacionInput, optionalMessage);
    }

    log.debug("calcularBaremoPrincipalPublicacion(produccionCientificaId, baremo) - end");
    return puntos;
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

  public void copySexenios(Integer anioInicio, Integer anioFin) {
    log.debug("copySexenios(anioInicio, anioFin) - start");

    try {
      IntStream.range(anioInicio, anioFin)
          .forEach(anio -> getSgiApiCspService().findAllGruposByAnio(anio).stream()
              .forEach(grupo -> getSexeniosByPersonasEquipo(grupo, anio)));
    } catch (Exception e) {
      log.error(e.getMessage());
    }

    log.debug("copySexenios(anioInicio, anioFin) - end");
  }

  private void getSexeniosByPersonasEquipo(GrupoDto grupo, Integer anio) {

    List<SexenioDto> sexeniosAnio = getSgiApiSgpService().findSexeniosByAnio(anio);

    List<String> personasEquipo = getSgiApiCspService().findAllGruposEquipoByGrupoIdAndAnio(grupo.getId(), anio)
        .stream()
        .map(GrupoEquipoDto::getPersonaRef)
        .distinct()
        .collect(Collectors.toList());

    personasEquipo.forEach(personaRef -> {
      Optional<SexenioDto> optSexenio = sexeniosAnio.stream()
          .filter(sexenio -> sexenio.getPersonaRef().equals(personaRef))
          .findFirst();
      if (optSexenio.isPresent()) {
        saveSexenio(optSexenio.get(), anio);
      }
    });
  }

  private void saveSexenio(SexenioDto sexenioDto, Integer anio) {

    final CodigoCVN codigoCVNNumSexenios = CodigoCVN.E060_030_070_010;
    final CodigoCVN codigoCVNAnioSexenios = CodigoCVN.ANIO_SEXENIOS;

    try {

      Integer numeroSexenios = Integer.valueOf(sexenioDto.getNumero());
      String personaRef = sexenioDto.getPersonaRef();

      if (numeroSexenios.compareTo(0) > 0) {

        String produccionCientificaRef = PREFIX_SEXENIOS + personaRef + "_" + anio;

        ProduccionCientifica produccionCientifica = ProduccionCientifica.builder()
            .epigrafeCVN(EPIGRAFE_CVN_SEXENIO)
            .produccionCientificaRef(produccionCientificaRef)
            .build();

        Long produccionCientificaId = getProduccionCientificaBuilderService().addProduccionCientifaAndEstado(
            produccionCientifica, TipoEstadoProduccion.VALIDADO);

        // NUMERO_SEXENIOS
        String numSexeniosFormated = ProduccionCientificaFieldFormatUtil.formatNumber(numeroSexenios.toString());
        getProduccionCientificaBuilderService().addCampoProduccionCientificaAndValor(produccionCientificaId,
            codigoCVNNumSexenios, numSexeniosFormated);

        // ANIO_SEXENIOS
        Instant fechaFin = ProduccionCientificaFieldFormatUtil.calculateFechaFinBaremacionByAnio(anio,
            getSgiConfigProperties().getTimeZone());
        getProduccionCientificaBuilderService().addCampoProduccionCientificaAndValor(produccionCientificaId,
            codigoCVNAnioSexenios, fechaFin.toString());

        getProduccionCientificaBuilderService().addAutorByPersonaRefAndIp(produccionCientificaId,
            personaRef, Boolean.FALSE);
      }
    } catch (Exception e) {
      log.debug(e.getMessage());
    }
  }

}
