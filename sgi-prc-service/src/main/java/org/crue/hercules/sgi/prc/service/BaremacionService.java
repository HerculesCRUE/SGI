package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.exceptions.ConvocatoriaBaremacionNotFoundException;
import org.crue.hercules.sgi.prc.model.Baremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupo;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupoInvestigador;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador;
import org.crue.hercules.sgi.prc.repository.BaremoRepository;
import org.crue.hercules.sgi.prc.repository.ConvocatoriaBaremacionRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionGrupoInvestigadorRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionGrupoRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionItemInvestigadorRepository;
import org.crue.hercules.sgi.prc.repository.specification.BaremoSpecifications;
import org.crue.hercules.sgi.prc.repository.specification.PuntuacionItemInvestigadorSpecifications;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiCspService;
import org.crue.hercules.sgi.prc.util.ProduccionCientificaFieldFormatUtil;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la baremación
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class BaremacionService {
  private static final String PROBLEM_MESSAGE_PARAMETER_FIELD = "field";
  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String PROBLEM_MESSAGE_ISNULL = "isNull";

  private final BaremoRepository baremoRepository;
  private final PuntuacionItemInvestigadorRepository puntuacionItemInvestigadorRepository;
  private final PuntuacionGrupoRepository puntuacionGrupoRepository;
  private final PuntuacionGrupoInvestigadorRepository puntuacionGrupoInvestigadorRepository;

  private final ConvocatoriaBaremacionRepository convocatoriaBaremacionRepository;
  private final BaremacionSexenioService baremacionSexenioService;
  private final BaremacionProyectoService baremacionProyectoService;
  private final BaremacionContratoService baremacionContratoService;
  private final BaremacionCosteIndirectoContratoService baremacionCosteIndirectoContratoService;
  private final BaremacionCosteIndirectoProyectoService baremacionCosteIndirectoProyectoService;
  private final BaremacionInvencionService baremacionInvencionService;
  private final BaremacionPublicacionLibroService baremacionPublicacionLibrosService;
  private final BaremacionPublicacionArticuloService baremacionPublicacionArticulosService;
  private final BaremacionComiteEditorialService baremacionComitesEditorialesService;
  private final BaremacionCongresoService baremacionCongresosService;
  private final BaremacionDireccionTesisService baremacionDireccionTesisService;
  private final BaremacionObraArtisticaService baremacionObraArtisticaService;
  private final BaremacionOrganizacionActividadService baremacionOrganizacionActividadService;
  private final ConvocatoriaBaremacionService convocatoriaBaremacionService;
  private final ConvocatoriaBaremacionLogService convocatoriaBaremacionLogService;

  private final SgiApiCspService sgiApiCspService;

  private final SgiConfigProperties sgiConfigProperties;

  @Transactional
  public synchronized void baremacion(Long convocatoriaBaremacionId) {
    log.debug("baremacion(convocatoriaBaremacionId) - start");

    checkInitBaremacion(convocatoriaBaremacionId);

    // Open a new transaction to evict a new call before finishing
    ConvocatoriaBaremacion convocatoriaBaremacionUpdate = convocatoriaBaremacionService.updateFechaInicioEjecucion(
        convocatoriaBaremacionId,
        Instant.now());

    this.baremacionIntern(convocatoriaBaremacionUpdate);

  }

  private void checkInitBaremacion(Long convocatoriaBaremacionId) {
    log.debug("checkInitBaremacion(convocatoriaBaremacionId) - start");

    ConvocatoriaBaremacion convocatoriaBaremacion = convocatoriaBaremacionRepository.findById(convocatoriaBaremacionId)
        .orElseThrow(() -> new ConvocatoriaBaremacionNotFoundException(convocatoriaBaremacionId));

    Assert.isNull(convocatoriaBaremacion.getFechaInicioEjecucion(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_ISNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("fechaInicioEjecucion"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(ConvocatoriaBaremacion.class))
            .build());
    log.debug("checkInitBaremacion(convocatoriaBaremacionId) - end");
  }

  private void baremacionIntern(ConvocatoriaBaremacion convocatoriaBaremacion) {
    log.debug("baremacion(convocatoriaBaremacion) - start");
    Long convocatoriaBaremacionId = convocatoriaBaremacion.getId();
    try {

      convocatoriaBaremacionService.deleteItemsConvocatoriaBaremacion(convocatoriaBaremacion);

      convocatoriaBaremacionLogService.save(convocatoriaBaremacionId, "Inicio");

      Integer anioInicio = convocatoriaBaremacion.getUltimoAnio() - convocatoriaBaremacion.getAniosBaremables() + 1;
      Integer anioFin = convocatoriaBaremacion.getUltimoAnio() + 1;

      baremacionInvencionService.copyInvenciones(anioInicio, anioFin);
      baremacionProyectoService.copyProyectos(anioInicio, anioFin);
      baremacionSexenioService.copySexenios(anioInicio, anioFin);

      IntStream.range(anioInicio, anioFin).forEach(anio -> {
        BaremacionInput baremacionInput = ProduccionCientificaFieldFormatUtil.createBaremacionInput(anio,
            convocatoriaBaremacionId,
            sgiConfigProperties.getTimeZone());

        convocatoriaBaremacionLogService.save(convocatoriaBaremacionId,
            String.format("[%d] [%d]", convocatoriaBaremacionId, anio));

        // Proyectos
        baremacionInput.setEpigrafeCVN(BaremacionProyectoService.EPIGRAFE_CVN_PROYECTO);
        baremacionProyectoService.evaluateProduccionCientificaByTypeAndAnio(baremacionInput);
        baremacionCosteIndirectoProyectoService.evaluateProduccionCientificaByTypeAndAnio(baremacionInput);

        // Contratos
        baremacionInput.setEpigrafeCVN(BaremacionContratoService.EPIGRAFE_CVN_CONTRATO);
        baremacionContratoService.evaluateProduccionCientificaByTypeAndAnio(baremacionInput);
        baremacionCosteIndirectoContratoService.evaluateProduccionCientificaByTypeAndAnio(baremacionInput);

        // Invenciones
        baremacionInput.setEpigrafeCVN(EpigrafeCVN.E050_030_010_000);
        baremacionInvencionService.evaluateProduccionCientificaByTypeAndAnio(baremacionInput);

        // Sexenios
        baremacionInput.setEpigrafeCVN(EpigrafeCVN.E060_030_070_000);
        baremacionSexenioService.evaluateProduccionCientificaByTypeAndAnio(baremacionInput);

        // Publicaciones libros
        baremacionInput.setEpigrafeCVN(EpigrafeCVN.E060_010_010_000);
        baremacionPublicacionLibrosService.evaluateProduccionCientificaByTypeAndAnio(baremacionInput);

        // Publicaciones articulos
        baremacionInput.setEpigrafeCVN(EpigrafeCVN.E060_010_010_000);
        baremacionPublicacionArticulosService.evaluateProduccionCientificaByTypeAndAnio(baremacionInput);

        // Cómites editoriales
        baremacionInput.setEpigrafeCVN(EpigrafeCVN.E060_030_030_000);
        baremacionComitesEditorialesService.evaluateProduccionCientificaByTypeAndAnio(baremacionInput);

        // Congresos
        baremacionInput.setEpigrafeCVN(EpigrafeCVN.E060_010_020_000);
        baremacionCongresosService.evaluateProduccionCientificaByTypeAndAnio(baremacionInput);

        // Direccion tesis
        baremacionInput.setEpigrafeCVN(EpigrafeCVN.E030_040_000_000);
        baremacionDireccionTesisService.evaluateProduccionCientificaByTypeAndAnio(baremacionInput);

        // Obra artística
        baremacionInput.setEpigrafeCVN(EpigrafeCVN.E050_020_030_000);
        baremacionObraArtisticaService.evaluateProduccionCientificaByTypeAndAnio(baremacionInput);

        // Organización de actividades
        baremacionInput.setEpigrafeCVN(EpigrafeCVN.E060_020_030_000);
        baremacionOrganizacionActividadService.evaluateProduccionCientificaByTypeAndAnio(baremacionInput);

        evaluatePuntuacionGrupoInvestigador(convocatoriaBaremacionId, anio);
      });

      evaluatePuntosConvocatoriaBaremacion(convocatoriaBaremacionId);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw e;
    } finally {
      convocatoriaBaremacionLogService.save(convocatoriaBaremacionId, "Fin");
      convocatoriaBaremacionService.updateFechaInicioEjecucion(convocatoriaBaremacionId, null);
    }
    log.debug("baremacion(convocatoriaBaremacion) - end");
  }

  private void evaluatePuntosConvocatoriaBaremacion(Long convocatoriaBaremacionId) {
    log.debug("evaluatePuntosConvocatoriaBaremacion(convocatoriaBaremacionId) - start");

    ConvocatoriaBaremacion convocatoriaBaremacionUpdate = convocatoriaBaremacionRepository
        .findById(convocatoriaBaremacionId).map(convocatoriaBaremacion -> {

          ConvocatoriaBaremacion convocatoriaBaremacionPuntos = convocatoriaBaremacionRepository
              .findSumPuntosById(convocatoriaBaremacionId);

          convocatoriaBaremacionPuntos.setId(convocatoriaBaremacionId);
          convocatoriaBaremacionPuntos.setImporteTotal(convocatoriaBaremacion.getImporteTotal());

          BigDecimal importeConvocatoriaBaremacion = convocatoriaBaremacion.getImporteTotal();

          convocatoriaBaremacion
              .setPuntoSexenio(calculatePuntosByBaremo(convocatoriaBaremacionId, importeConvocatoriaBaremacion,
                  convocatoriaBaremacionPuntos.getPuntoSexenio(), TipoBaremo.SEXENIO));

          convocatoriaBaremacion
              .setPuntoCostesIndirectos(calculatePuntosByBaremo(convocatoriaBaremacionId, importeConvocatoriaBaremacion,
                  convocatoriaBaremacionPuntos.getPuntoCostesIndirectos(), TipoBaremo.COSTE_INDIRECTO));

          convocatoriaBaremacion
              .setPuntoProduccion(calculatePuntosByBaremo(convocatoriaBaremacionId, importeConvocatoriaBaremacion,
                  convocatoriaBaremacionPuntos.getPuntoProduccion(), TipoBaremo.PRODUCCION_CIENTIFICA));

          convocatoriaBaremacion.setFechaFinEjecucion(Instant.now());
          return convocatoriaBaremacion;

        }).orElse(null);

    if (null != convocatoriaBaremacionUpdate) {
      convocatoriaBaremacionRepository.save(convocatoriaBaremacionUpdate);
    }

    log.debug("evaluatePuntosConvocatoriaBaremacion(convocatoriaBaremacionId) - end");
  }

  private BigDecimal calculatePuntosByBaremo(Long convocatoriaBaremacionId, BigDecimal importeTotal,
      BigDecimal sumaPuntos, TipoBaremo tipoBaremo) {
    BigDecimal puntos = BigDecimal.ZERO;
    if (sumaPuntos.compareTo(BigDecimal.ZERO) > 0) {
      Specification<Baremo> specs = BaremoSpecifications
          .byConvocatoriaBaremacionId(convocatoriaBaremacionId)
          .and(BaremoSpecifications.byConfiguracionBaremoTipoBaremo(tipoBaremo))
          .and(BaremoSpecifications.byConfiguracionBaremoActivoIsTrue());

      List<Baremo> baremos = baremoRepository.findAll(specs);
      if (!baremos.isEmpty()) {
        BigDecimal pesoPorcentaje = new BigDecimal(baremos.get(0).getPeso()).divide(new BigDecimal("100.00"), 2,
            RoundingMode.HALF_UP);
        BigDecimal peso = importeTotal.multiply(pesoPorcentaje);
        puntos = peso.divide(sumaPuntos, 2, RoundingMode.HALF_UP);

        String messageTracing = String.format(
            "Importe total[%s] Suma puntos[%s] TipoBaremo[%s] PesoPorcentaje[%s] Puntos[%s]",
            importeTotal.toString(), sumaPuntos.toString(),
            tipoBaremo.name(), pesoPorcentaje.toString(), puntos.toString());
        convocatoriaBaremacionLogService.save(convocatoriaBaremacionId, messageTracing);

      }
    }

    return puntos;
  }

  private void evaluatePuntuacionGrupoInvestigador(Long convocatoriaBaremacionId, Integer anio) {
    log.debug("evaluatePuntuacionGrupoInvestigador(convocatoriaBaremacionId, anio) - start");

    sgiApiCspService.findAllGruposByAnio(anio).stream().forEach(grupo -> {

      Long grupoRef = grupo.getId();
      PuntuacionGrupo puntuacionGrupoSearch = getOrInitPuntuacionGrupo(grupoRef, convocatoriaBaremacionId);

      Stream.of(puntuacionGrupoSearch).forEach(puntuacionGrupo -> {

        sgiApiCspService.findAllGruposEquipoByGrupoIdAndAnio(grupoRef, anio).stream().forEach(grupoEquipo -> {

          Specification<PuntuacionItemInvestigador> specs = PuntuacionItemInvestigadorSpecifications
              .byPersonaRef(grupoEquipo.getPersonaRef())
              .and(PuntuacionItemInvestigadorSpecifications.byConvocatoriaBaremacionAnio(anio))
              .and(PuntuacionItemInvestigadorSpecifications.byConvocatoriaBaremacionId(convocatoriaBaremacionId));

          puntuacionItemInvestigadorRepository.findAll(specs).stream().forEach(puntuacionItemInvestigador -> {
            PuntuacionGrupoInvestigador puntuacionGrupoInvestigador = initPuntuacionGrupoInvestigador(
                puntuacionItemInvestigador, grupoEquipo.getParticipacion(), puntuacionGrupoSearch.getId());

            String messageTracing = String.format("Autor[%s] Participacion[%s] PuntuacionGrupoInvestigador[%s] ",
                grupoEquipo.getPersonaRef(), grupoEquipo.getParticipacion().toString(),
                puntuacionGrupoInvestigador.toString());
            convocatoriaBaremacionLogService.save(convocatoriaBaremacionId, messageTracing);

            puntuacionGrupoInvestigadorRepository.save(puntuacionGrupoInvestigador);

            switch (puntuacionItemInvestigador.getTipoPuntuacion()) {
              case SEXENIO:
                puntuacionGrupo.setPuntosSexenios(
                    puntuacionGrupo.getPuntosSexenios().add(puntuacionGrupoInvestigador.getPuntos()));
                break;
              case COSTE_INDIRECTO:
                puntuacionGrupo.setPuntosCostesIndirectos(
                    puntuacionGrupo.getPuntosCostesIndirectos().add(puntuacionGrupoInvestigador.getPuntos()));
                break;
              default:
                puntuacionGrupo.setPuntosProduccion(
                    puntuacionGrupo.getPuntosProduccion().add(puntuacionGrupoInvestigador.getPuntos()));
            }
          });
        });

        String messageTracing = String.format("PuntuacionGrupo[%s] ", puntuacionGrupo.toString());
        convocatoriaBaremacionLogService.save(convocatoriaBaremacionId, messageTracing);

        puntuacionGrupoRepository.save(puntuacionGrupo);
      });
    });
    log.debug("evaluatePuntuacionGrupoInvestigador(convocatoriaBaremacionId, anio) - end");
  }

  private PuntuacionGrupoInvestigador initPuntuacionGrupoInvestigador(
      PuntuacionItemInvestigador puntuacionItemInvestigador, BigDecimal participacion, Long puntuacionGrupoId) {
    BigDecimal puntosItemInvestigadorPorcentaje = puntuacionItemInvestigador.getPuntos()
        .multiply(participacion.divide(new BigDecimal("100.00"), 2, RoundingMode.HALF_UP));
    return PuntuacionGrupoInvestigador.builder()
        .puntuacionItemInvestigadorId(puntuacionItemInvestigador.getId())
        .puntuacionGrupoId(puntuacionGrupoId)
        .puntos(puntosItemInvestigadorPorcentaje)
        .build();
  }

  private PuntuacionGrupo getOrInitPuntuacionGrupo(Long grupoRef, Long convocatoriaBaremacionId) {
    PuntuacionGrupo puntuacionGrupo = puntuacionGrupoRepository
        .findByConvocatoriaBaremacionIdAndGrupoRef(convocatoriaBaremacionId, grupoRef)
        .orElse(null);
    if (null == puntuacionGrupo) {
      puntuacionGrupo = PuntuacionGrupo.builder()
          .convocatoriaBaremacionId(convocatoriaBaremacionId)
          .puntosCostesIndirectos(BigDecimal.ZERO)
          .puntosSexenios(BigDecimal.ZERO)
          .puntosProduccion(BigDecimal.ZERO)
          .grupoRef(grupoRef)
          .build();
      puntuacionGrupo = puntuacionGrupoRepository.save(puntuacionGrupo);
    }

    return puntuacionGrupo;
  }

}
