package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.LongPredicate;

import org.apache.commons.collections4.CollectionUtils;
import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.crue.hercules.sgi.prc.dto.BaremacionInput.BaremoInput;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.Baremo;
import org.crue.hercules.sgi.prc.model.Baremo.TipoCuantia;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionCampo;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador.TipoPuntuacion;
import org.crue.hercules.sgi.prc.model.Rango;
import org.crue.hercules.sgi.prc.model.Rango.TipoRango;
import org.crue.hercules.sgi.prc.repository.AliasEnumeradoRepository;
import org.crue.hercules.sgi.prc.repository.AutorGrupoRepository;
import org.crue.hercules.sgi.prc.repository.AutorRepository;
import org.crue.hercules.sgi.prc.repository.BaremoRepository;
import org.crue.hercules.sgi.prc.repository.CampoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.ConfiguracionCampoRepository;
import org.crue.hercules.sgi.prc.repository.IndiceExperimentalidadRepository;
import org.crue.hercules.sgi.prc.repository.IndiceImpactoRepository;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionBaremoItemRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionItemInvestigadorRepository;
import org.crue.hercules.sgi.prc.repository.RangoRepository;
import org.crue.hercules.sgi.prc.repository.ValorCampoRepository;
import org.crue.hercules.sgi.prc.repository.specification.RangoSpecifications;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiCspService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiSgpService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la baremación de costes indirectos
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public abstract class BaremacionCosteIndirectoService extends BaremacionCommonService {
  private final ConfiguracionCampoRepository configuracionCampoRepository;
  private final RangoRepository rangoRepository;

  @Autowired
  protected BaremacionCosteIndirectoService(
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
      ConfiguracionCampoRepository configuracionCampoRepository,
      RangoRepository rangoRepository) {
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

    this.configuracionCampoRepository = configuracionCampoRepository;
    this.rangoRepository = rangoRepository;

    loadPredicates();
  }

  protected abstract CodigoCVN getCodigoCVNCuantiaCostesIndirectos();

  protected abstract EpigrafeCVN getEpigrafeCVNCostesIndirectos();

  @Override
  protected TipoPuntuacion getTipoPuntuacion() {
    return TipoPuntuacion.COSTE_INDIRECTO;
  }

  protected void loadPredicates() {

    // COSTE_INDIRECTO
    getHmTipoBaremoPredicates().put(TipoBaremo.COSTE_INDIRECTO, getPredicateIsCuantiaNotEmpty());
  }

  @Override
  protected List<Baremo> findBaremosByBaremacionInput(BaremacionInput baremacionInput) {
    return findBaremosByTipoBaremoActivo(baremacionInput, TipoBaremo.COSTE_INDIRECTO);
  }

  @Override
  protected BigDecimal evaluateBaremoPrincipal(BaremacionInput baremacionInput) {
    log.debug("evaluateBaremoPrincipal(baremacionInput) - start");

    BigDecimal puntos = BigDecimal.ZERO;

    TipoBaremo tipoBaremo = baremacionInput.getBaremo().getConfiguracionBaremo().getTipoBaremo();

    if (evaluateProduccionCientificaByTipoBaremo(baremacionInput, tipoBaremo)) {

      puntos = evaluateCuantia(baremacionInput.getProduccionCientificaId(), baremacionInput.getBaremo());

      String optionalMessage = String.format("BAREMACION CUANTIA COSTE INDIRECTO [%s] %s", tipoBaremo.name(),
          null != puntos ? puntos.toString() : "");
      traceLog(baremacionInput, optionalMessage);
    }

    log.debug("evaluateBaremoPrincipal(baremacionInput) - end");
    return puntos;
  }

  private BigDecimal evaluateCuantia(Long produccionCientificaId, BaremoInput baremo) {
    BigDecimal cuantia = new BigDecimal(
        findValoresByCampoProduccionCientificaId(getCodigoCVNCuantiaCostesIndirectos(), produccionCientificaId)
            .get(0).getValor());

    if (baremo.getTipoCuantia().equals(TipoCuantia.RANGO)) {
      Specification<Rango> specs = RangoSpecifications
          .byTipoRango(TipoRango.CUANTIA_COSTES_INDIRECTOS).and(RangoSpecifications.inRange(cuantia));

      List<Rango> rangos = rangoRepository.findAll(specs);
      if (CollectionUtils.isEmpty(rangos)) {
        return BigDecimal.ZERO;
      } else {
        return rangos.get(0).getPuntos();
      }
    } else {
      return cuantia.divide(baremo.getCuantia(), 2, RoundingMode.HALF_UP);
    }
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
  protected List<Long> getProduccionCientificaIdsByEpigrafeCVNAndAnio(BaremacionInput baremacionInput) {
    List<Long> result = new ArrayList<>();
    Optional<ConfiguracionCampo> optFechaInicio = configuracionCampoRepository
        .findByEpigrafeCVNAndFechaReferenciaInicioIsTrue(getEpigrafeCVNCostesIndirectos());
    Optional<ConfiguracionCampo> optFechaFin = configuracionCampoRepository
        .findByEpigrafeCVNAndFechaReferenciaFinIsTrue(getEpigrafeCVNCostesIndirectos());

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

    List<Autor> autoresBaremables = findAutoresBaremablesByAnio(newProduccionCientificaId, baremacionInput.getAnio());

    evaluateAutoresBaremables(baremacionInput, puntos, newProduccionCientificaId, autoresBaremables);
    log.debug("evaluateAutores(baremacionInput, puntos, newProduccionCientificaId) - end");
  }

  /* -------------------- predicates -------------------- */

  private LongPredicate getPredicateIsCuantiaNotEmpty() {
    return produccionCientificaId -> isValorCampoNotEmpty(produccionCientificaId,
        getCodigoCVNCuantiaCostesIndirectos());
  }

}
