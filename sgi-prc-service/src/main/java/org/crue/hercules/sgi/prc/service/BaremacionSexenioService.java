package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.LongPredicate;
import java.util.stream.IntStream;

import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.dto.BaremacionInput;
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

  @Override
  protected TipoPuntuacion getTipoPuntuacion() {
    return TipoPuntuacion.SEXENIO;
  }

  @Override
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

  public void copySexenios(Integer anioInicio, Integer anioFin) {
    log.debug("copySexenios(anioInicio, anioFin) - start");

    // Delete all sexenios
    getProduccionCientificaRepository().findByEpigrafeCVNAndConvocatoriaBaremacionIdIsNull(EPIGRAFE_CVN_SEXENIO)
        .forEach(getProduccionCientificaBuilderService()::deleteProduccionCientifica);

    try {
      IntStream.range(anioInicio, anioFin)
          .forEach(
              anio -> {
                Instant fechaFinBaremacion = ProduccionCientificaFieldFormatUtil.calculateFechaFinBaremacionByAnio(anio,
                    getSgiConfigProperties().getTimeZone());
                String strFechaFinBaremacion = ProduccionCientificaFieldFormatUtil
                    .formatInstantToStringWithTimeZoneAndPattern(
                        fechaFinBaremacion, getSgiConfigProperties().getTimeZone(), "yyyy-MM-dd'T'HH:mm:ss'Z'");
                List<SexenioDto> sexeniosAnio = getSgiApiSgpService().findSexeniosByFecha(strFechaFinBaremacion);

                List<String> personasEquipo = getAllPersonasInGruposBaremablesByAnio(anio);

                personasEquipo.forEach(personaRef -> {
                  Optional<SexenioDto> optSexenio = sexeniosAnio.stream()
                      .filter(sexenio -> sexenio.getPersonaRef().equals(personaRef))
                      .findFirst();
                  if (optSexenio.isPresent()) {
                    saveSexenio(optSexenio.get(), anio);
                  }
                });

              });
    } catch (Exception e) {
      log.error(e.getMessage());
    }

    log.debug("copySexenios(anioInicio, anioFin) - end");
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

        Long produccionCientificaId = getProduccionCientificaBuilderService().addProduccionCientificaAndEstado(
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
      log.error(e.getMessage());
    }
  }

}
