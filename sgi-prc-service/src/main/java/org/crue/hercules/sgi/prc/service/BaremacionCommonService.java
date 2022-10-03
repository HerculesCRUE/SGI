package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.function.LongPredicate;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.crue.hercules.sgi.prc.dto.BaremacionInput.BaremoInput;
import org.crue.hercules.sgi.prc.dto.csp.GrupoDto;
import org.crue.hercules.sgi.prc.dto.csp.GrupoEquipoDto;
import org.crue.hercules.sgi.prc.dto.sgp.PersonaDto.DepartamentoDto;
import org.crue.hercules.sgi.prc.dto.sgp.PersonaDto.VinculacionDto;
import org.crue.hercules.sgi.prc.enums.TablaMaestraCVN;
import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.exceptions.ProduccionCientificaNotFoundException;
import org.crue.hercules.sgi.prc.model.AliasEnumerado;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.Autor_;
import org.crue.hercules.sgi.prc.model.Baremo;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoPuntos;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.IndiceExperimentalidad;
import org.crue.hercules.sgi.prc.model.IndiceImpacto;
import org.crue.hercules.sgi.prc.model.IndiceImpacto_;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.PuntuacionBaremoItem;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador.TipoPuntuacion;
import org.crue.hercules.sgi.prc.model.ValorCampo;
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
import org.crue.hercules.sgi.prc.repository.specification.AutorSpecifications;
import org.crue.hercules.sgi.prc.repository.specification.BaremoSpecifications;
import org.crue.hercules.sgi.prc.repository.specification.IndiceImpactoSpecifications;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiCspService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiSgpService;
import org.crue.hercules.sgi.prc.util.ProduccionCientificaFieldFormatUtil;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de operaciones comunes para la baremación de producción científica
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public abstract class BaremacionCommonService implements BaremacionItemService {

  private final AliasEnumeradoRepository aliasEnumeradoRepository;
  @Getter
  private final ProduccionCientificaRepository produccionCientificaRepository;
  private final PuntuacionBaremoItemRepository puntuacionBaremoItemRepository;
  @Getter
  private final PuntuacionItemInvestigadorRepository puntuacionItemInvestigadorRepository;

  private final IndiceExperimentalidadRepository indiceExperimentalidadRepository;
  private final BaremoRepository baremoRepository;

  @Getter
  private final AutorRepository autorRepository;
  private final AutorGrupoRepository autorGrupoRepository;

  @Getter
  private final CampoProduccionCientificaRepository campoProduccionCientificaRepository;
  @Getter
  private final ValorCampoRepository valorCampoRepository;
  @Getter
  private final IndiceImpactoRepository indiceImpactoRepository;
  @Getter
  private final ProduccionCientificaBuilderService produccionCientificaBuilderService;

  @Getter
  private final SgiApiSgpService sgiApiSgpService;
  @Getter
  private final SgiApiCspService sgiApiCspService;
  @Getter
  private final ConvocatoriaBaremacionLogService convocatoriaBaremacionLogService;

  @Getter
  private final ModelMapper modelMapper;
  @Getter
  private final SgiConfigProperties sgiConfigProperties;

  // Año de baremación necesario para algunos predicados
  @Getter
  @Setter
  private Integer anio;

  // Almacena por cada tipo de baremo sus predicados para su baremación de puntos
  @Getter
  private EnumMap<TipoBaremo, LongPredicate> hmTipoBaremoPredicates = new EnumMap<>(TipoBaremo.class);

  protected abstract void loadPredicates();

  protected abstract TipoPuntuacion getTipoPuntuacion();

  public void evaluateProduccionCientificaByTypeAndAnio(BaremacionInput baremacionInput) {
    log.debug("evaluateProduccionCientificaByTypeAndAnio(baremacionInput) - start");

    setAnio(baremacionInput.getAnio());

    getProduccionCientificaIdsByEpigrafeCVNAndAnio(baremacionInput).stream().forEach(produccionCientificaId -> {
      baremacionInput.setProduccionCientificaId(produccionCientificaId);
      Pair<BigDecimal, Long> pairEvaluateItem = evaluateItemProduccionCientifica(baremacionInput);
      BigDecimal puntos = pairEvaluateItem.getFirst();
      Long newProduccionCientificaId = pairEvaluateItem.getSecond();
      if (puntos.compareTo(BigDecimal.ZERO) > 0) {

        String optionalMessage = String.format("BAREMACION PRINCIPAL %s", puntos.toString());
        traceLog(baremacionInput, optionalMessage);

        evaluateAutores(baremacionInput, puntos, newProduccionCientificaId);
      }
    });

    log.debug("evaluateProduccionCientificaByTypeAndAnio(baremacionInput) - end");
  }

  protected BigDecimal evaluateBaremoExtra(BaremacionInput baremacionInput) {
    log.debug("evaluateBaremoExtra({}) - Tipo de produccion cientifica sin baremo extra, Puntos extra: 0",
        baremacionInput);
    return BigDecimal.ZERO;
  }

  protected BigDecimal evaluateBaremoModulador(BaremacionInput baremacionInput) {
    log.debug("evaluateBaremoModulador({}) - Tipo de produccion cientifica sin baremo modulador, Puntos modulador: 1",
        baremacionInput);
    return new BigDecimal("1.00");
  }

  protected void evaluateAutores(BaremacionInput baremacionInput, BigDecimal puntos, Long newProduccionCientificaId) {
    List<Autor> autoresBaremables = findAutoresBaremables(newProduccionCientificaId);

    evaluateAutoresBaremables(baremacionInput, puntos, newProduccionCientificaId, autoresBaremables);
  }

  protected void evaluateAutoresBaremables(BaremacionInput baremacionInput, BigDecimal puntos,
      Long newProduccionCientificaId,
      List<Autor> autoresBaremables) {
    if (!autoresBaremables.isEmpty()) {
      int numAutores = autoresBaremables.size();
      BigDecimal puntosInvestigador = puntos.divide(new BigDecimal(numAutores), 2, RoundingMode.HALF_UP);

      String optionalMessage = String.format("AUTORES BAREMABLES %d PUNTOS TOTALES=%s --> %s",
          numAutores, puntos.toString(), puntosInvestigador.toString());
      traceLog(baremacionInput, optionalMessage);

      autoresBaremables.stream()
          .filter(autor -> isAutorGrupoValidado(autor.getId(), newProduccionCientificaId))
          .forEach(
              autor -> {
                BaremacionInput baremacionInputNew = modelMapper.map(baremacionInput, BaremacionInput.class);
                baremacionInputNew.setProduccionCientificaId(newProduccionCientificaId);

                evaluatePuntuacionItemInvestigador(baremacionInputNew, puntosInvestigador, autor);
              });
    }
  }

  protected List<Long> getProduccionCientificaIdsByEpigrafeCVNAndAnio(BaremacionInput baremacionInput) {
    return produccionCientificaRepository.findAllBaremacionByFechaInicio(baremacionInput);
  }

  protected void traceLog(BaremacionInput baremacionInput, String optionalMessage) {
    String messageTracing = String.format("PRC[%d]-[%d] CVN[%s] %s", baremacionInput.getProduccionCientificaId(),
        getAnio(), baremacionInput.getEpigrafeCVN().getCode(), optionalMessage);
    log.debug(messageTracing);
    convocatoriaBaremacionLogService.save(baremacionInput.getConvocatoriaBaremacionId(), messageTracing);
  }

  protected void evaluatePuntuacionItemInvestigador(BaremacionInput baremacionInput, BigDecimal puntosInvestigador,
      Autor autor) {
    log.debug("evaluatePuntuacioItemInvestigador(baremacionInput, puntosInvestigador, autor) - start");

    DepartamentoDto departamento = getSgiApiSgpService().findVinculacionByPersonaId(autor.getPersonaRef())
        .map(VinculacionDto::getDepartamento)
        .orElse(null);

    if (null != departamento) {
      IndiceExperimentalidad indiceExperimentalidadSearch = getIndiceExperimentalidadByDepartamentoAndAnio(departamento,
          anio);

      if (null != indiceExperimentalidadSearch) {
        PuntuacionItemInvestigador puntuacionItemInvestigador = PuntuacionItemInvestigador.builder()
            .anio(anio)
            .personaRef(autor.getPersonaRef())
            .tipoPuntuacion(getTipoPuntuacion())
            .produccionCientificaId(baremacionInput.getProduccionCientificaId())
            .puntos(puntosInvestigador.multiply(indiceExperimentalidadSearch.getValor()))
            .build();

        String optionalMessage = String.format("Autor[%s] Departamento[%s] [%s] %s",
            autor.getPersonaRef(), departamento.getId(), puntosInvestigador.toString(),
            puntuacionItemInvestigador.toString());
        traceLog(baremacionInput, optionalMessage);

        puntuacionItemInvestigadorRepository.save(puntuacionItemInvestigador);
      }

    }
    log.debug("evaluatePuntuacioItemInvestigador(produccionCientificaId, puntosInvestigador, autor) - end");
  }

  private IndiceExperimentalidad getIndiceExperimentalidadByDepartamentoAndAnio(DepartamentoDto departamento,
      Integer anio) {
    log.debug("getIndiceExperimentalidadByDepartamentoAndAnio(departamento, anio) - start");

    Pair<Instant, Instant> pairFechasBaremacion = ProduccionCientificaFieldFormatUtil
        .calculateFechasInicioFinBaremacionByAnio(anio, getSgiConfigProperties().getTimeZone());

    log.debug("getIndiceExperimentalidadByDepartamentoAndAnio(departamento, anio) - end");
    String departamentoRef = departamento.getId();

    return indiceExperimentalidadRepository
        .findByDepartamentoRefAndAnioBaremacion(departamentoRef, pairFechasBaremacion.getFirst(),
            pairFechasBaremacion.getSecond())
        .stream().filter(
            indiceExperimentalidad -> indiceExperimentalidad.getDepartamentoRef()
                .equalsIgnoreCase(departamentoRef))
        .findFirst()
        .orElse(null);
  }

  protected Pair<BigDecimal, Long> evaluateItemProduccionCientifica(BaremacionInput baremacionInput) {
    log.debug("evaluateItemProduccionCientifica(baremacionInput) - start");
    BigDecimal puntos = null;

    List<Baremo> baremos = findBaremosByBaremacionInput(baremacionInput);

    // PRINCIPAL
    Pair<BigDecimal, Long> pairEvaluateItemPrincipal = evaluateItemProduccionCientificaByTipoBaremoPrincipal(
        baremacionInput, baremos);
    puntos = pairEvaluateItemPrincipal.getFirst();
    Long newProduccionCientificaId = pairEvaluateItemPrincipal.getSecond();

    // MODULADOR
    if (puntos.compareTo(BigDecimal.ZERO) > 0) {
      puntos = baremos.stream()
          .filter(baremo -> baremo.getConfiguracionBaremo().getTipoPuntos().equals(TipoPuntos.MODULADOR))
          .sorted(getBaremoPrioridadComparator())
          .map(baremo -> {
            baremacionInput.setBaremo(modelMapper.map(baremo, BaremoInput.class));
            BigDecimal puntosModulador = this.evaluateBaremoModulador(baremacionInput);

            if (puntosModulador.compareTo(BigDecimal.ZERO) > 0) {

              String optionalMessage = String.format("PuntuacionBaremoItem MODULADOR Baremo[%d] PRC[%d] Puntos[%s]",
                  baremo.getId(), newProduccionCientificaId, puntosModulador.toString());
              traceLog(baremacionInput, optionalMessage);

              savePuntuacionBaremoItem(baremo.getId(), newProduccionCientificaId,
                  puntosModulador, baremacionInput.getAnio());
            }

            return puntosModulador;
          })
          .filter(puntosModulador -> puntosModulador.compareTo(BigDecimal.ZERO) > 0)
          .reduce(puntos, BigDecimal::multiply);
    }

    // EXTRA
    if (puntos.compareTo(BigDecimal.ZERO) > 0) {
      puntos = baremos.stream()
          .filter(baremo -> baremo.getConfiguracionBaremo().getTipoPuntos().equals(TipoPuntos.EXTRA))
          .sorted(getBaremoPrioridadComparator())
          .map(baremo -> {
            baremacionInput.setBaremo(modelMapper.map(baremo, BaremoInput.class));
            BigDecimal puntosExtra = this.evaluateBaremoExtra(baremacionInput);

            if (puntosExtra.compareTo(BigDecimal.ZERO) > 0) {

              String optionalMessage = String.format("PuntuacionBaremoItem EXTRA Baremo[%d] PRC[%d] Puntos[%s]",
                  baremo.getId(), newProduccionCientificaId, puntosExtra.toString());
              traceLog(baremacionInput, optionalMessage);

              savePuntuacionBaremoItem(baremo.getId(), newProduccionCientificaId,
                  puntosExtra, baremacionInput.getAnio());
            }

            return puntosExtra;
          })
          .reduce(puntos, BigDecimal::add);
    }

    log.debug("evaluateItemProduccionCientifica(baremacionInput) - end");
    return Pair.of(puntos, newProduccionCientificaId);
  }

  protected List<Baremo> findBaremosByBaremacionInput(BaremacionInput baremacionInput) {
    Specification<Baremo> specs = BaremoSpecifications
        .byConvocatoriaBaremacionId(baremacionInput.getConvocatoriaBaremacionId())
        .and(BaremoSpecifications.byConfiguracionBaremoEpigrafeCVN(baremacionInput.getEpigrafeCVN()))
        .and(BaremoSpecifications.byConfiguracionBaremoActivoIsTrue());

    return baremoRepository.findAll(specs);
  }

  protected List<Baremo> findBaremosByTipoBaremoActivo(BaremacionInput baremacionInput, TipoBaremo tipoBaremo) {
    Specification<Baremo> specs = BaremoSpecifications
        .byConvocatoriaBaremacionId(baremacionInput.getConvocatoriaBaremacionId())
        .and(BaremoSpecifications.byConfiguracionBaremoTipoBaremo(tipoBaremo))
        .and(BaremoSpecifications.byConfiguracionBaremoActivoIsTrue());
    return baremoRepository.findAll(specs);
  }

  protected Baremo findBaremoByTipoBaremoActivo(BaremacionInput baremacionInput, TipoBaremo tipoBaremo) {
    List<Baremo> baremos = findBaremosByTipoBaremoActivo(baremacionInput, tipoBaremo);
    if (!CollectionUtils.isEmpty(baremos)) {
      return baremos.get(0);
    } else {
      return null;
    }
  }

  protected PuntuacionBaremoItem savePuntuacionBaremoItem(Long baremoId, Long produccionCientificaId,
      BigDecimal puntos, Integer anio) {
    PuntuacionBaremoItem puntuacionBaremoItem = PuntuacionBaremoItem.builder()
        .baremoId(baremoId)
        .anio(anio)
        .produccionCientificaId(produccionCientificaId)
        .puntos(puntos)
        .build();
    return puntuacionBaremoItemRepository.save(puntuacionBaremoItem);
  }

  protected Pair<BigDecimal, Long> evaluateItemProduccionCientificaByTipoBaremoPrincipal(
      BaremacionInput baremacionInput, List<Baremo> baremos) {
    log.debug("evaluateItemProduccionCientificaByTipoBaremoPrincipal(baremacionInput, baremos) - start");

    Pair<BigDecimal, Long> pairEvaluateItemPrincipal = Pair.of(BigDecimal.ZERO, -1L);
    List<Baremo> baremosPrincipales = baremos.stream()
        .filter(baremo -> baremo.getConfiguracionBaremo().getTipoPuntos().equals(TipoPuntos.PRINCIPAL))
        .sorted(getBaremoPrioridadComparator())
        .collect(Collectors.toList());

    List<Pair<BigDecimal, Baremo>> puntosBaremoPrincipal = calculatePuntosBaremosPrincipales(baremacionInput,
        baremosPrincipales);

    if (!CollectionUtils.isEmpty(puntosBaremoPrincipal)) {
      Long newProduccionCientificaId = cloneProduccionCientifica(baremacionInput).getId();

      BigDecimal puntosFinales = puntosBaremoPrincipal.stream().map(pairPuntosBaremo -> {
        Long baremoId = pairPuntosBaremo.getSecond().getId();
        BigDecimal puntos = pairPuntosBaremo.getFirst();

        String optionalMessage = String.format("PuntuacionBaremoItem PRINCIPAL Baremo[%d] PRC[%d] Puntos[%s]",
            baremoId, newProduccionCientificaId, puntos.toString());
        traceLog(baremacionInput, optionalMessage);

        savePuntuacionBaremoItem(baremoId, newProduccionCientificaId, puntos, baremacionInput.getAnio());

        return puntos;
      }).reduce(new BigDecimal("0"), BigDecimal::add);

      pairEvaluateItemPrincipal = Pair.of(puntosFinales, newProduccionCientificaId);
    }
    log.debug("evaluateItemProduccionCientificaByTipoBaremoPrincipal(baremacionInput, baremos) - end");
    return pairEvaluateItemPrincipal;
  }

  protected List<Pair<BigDecimal, Baremo>> calculatePuntosBaremosPrincipales(BaremacionInput baremacionInput,
      List<Baremo> baremosPrincipales) {
    return baremosPrincipales.stream().map(
        baremo -> {
          baremacionInput.setBaremo(modelMapper.map(baremo, BaremoInput.class));
          return Pair.of(this.evaluateBaremoPrincipal(baremacionInput), baremo);
        })
        .filter(pairPuntosBaremo -> pairPuntosBaremo.getFirst().compareTo(BigDecimal.ZERO) > 0)
        .findFirst().map(Arrays::asList)
        .orElse(new ArrayList<>());
  }

  protected ProduccionCientifica cloneProduccionCientifica(BaremacionInput baremacionInput) {
    return produccionCientificaRepository.findById(baremacionInput.getProduccionCientificaId())
        .map(produccionCientifica -> {
          Optional<ProduccionCientifica> prcAlreadyCalled = produccionCientificaRepository
              .findByProduccionCientificaRefAndConvocatoriaBaremacionId(
                  produccionCientifica.getProduccionCientificaRef(), baremacionInput.getConvocatoriaBaremacionId());
          if (prcAlreadyCalled.isPresent()) {
            return prcAlreadyCalled.get();
          } else {
            return produccionCientificaBuilderService.cloneProduccionCientificaAndRelations(
                baremacionInput.getConvocatoriaBaremacionId(), produccionCientifica);
          }
        })
        .orElseThrow(
            () -> new ProduccionCientificaNotFoundException(baremacionInput.getProduccionCientificaId().toString()));
  }

  protected Comparator<Baremo> getBaremoPrioridadComparator() {
    return Comparator.comparing(baremo -> baremo.getConfiguracionBaremo().getPrioridad());
  }

  protected BigDecimal evaluateBaremoPrincipal(BaremacionInput baremacionInput) {
    log.debug("evaluateBaremoPrincipal(baremacionInput) - start");

    BigDecimal puntos = BigDecimal.ZERO;

    TipoBaremo tipoBaremo = baremacionInput.getBaremo().getConfiguracionBaremo().getTipoBaremo();

    if (evaluateProduccionCientificaByTipoBaremo(baremacionInput, tipoBaremo)) {
      puntos = baremacionInput.getBaremo().getPuntos();

      String optionalMessage = String.format("BAREMACION PRINCIPAL [%s] %s", tipoBaremo.name(), puntos.toString());
      traceLog(baremacionInput, optionalMessage);
    }

    log.debug("evaluateBaremoPrincipal(baremacionInput) - end");
    return puntos;
  }

  protected boolean evaluateProduccionCientificaByTipoBaremo(BaremacionInput baremacionInput, TipoBaremo tipoBaremo) {
    return getHmTipoBaremoPredicates().containsKey(tipoBaremo)
        && LongStream.of(baremacionInput.getProduccionCientificaId())
            .allMatch(getHmTipoBaremoPredicates().get(tipoBaremo));
  }

  /* -------------------- Common filters -------------------- */
  protected boolean isValorCampoISBNNational(Long produccionCientificaId, CodigoCVN codigoCVN) {
    return findValoresByCampoProduccionCientificaId(codigoCVN, produccionCientificaId).stream()
        .anyMatch(valorCampo -> isISBNNational(valorCampo.getValor()));
  }

  protected boolean isISBNNational(String valor) {
    boolean isValid = false;
    if (StringUtils.hasText(valor)) {
      int lengthISBN = valor.trim().replace("-", "").length();
      String[] arrISBN = valor.trim().split("-");
      if (lengthISBN == 10) {
        isValid = arrISBN[0].equals("84");
      } else if (lengthISBN == 13) {
        isValid = arrISBN[1].equals("84");
      }
    }
    return isValid;
  }

  protected boolean isValorCampoNotEmpty(Long produccionCientificaId, CodigoCVN codigoCVN) {
    return findValoresByCampoProduccionCientificaId(
        codigoCVN, produccionCientificaId).stream().anyMatch(valorCampo -> StringUtils.hasText(valorCampo.getValor()));
  }

  protected List<IndiceImpacto> findFuentesImpactoByTipoFuenteIn(Long produccionCientificaId,
      List<TipoFuenteImpacto> fuentesImpacto) {
    Specification<IndiceImpacto> specs = IndiceImpactoSpecifications
        .byProduccionCientificaId(produccionCientificaId)
        .and(IndiceImpactoSpecifications.byAnio(getAnio()).or(IndiceImpactoSpecifications.byAnioIsNull()))
        .and(IndiceImpactoSpecifications.tipoFuenteImpactoIn(fuentesImpacto));
    return indiceImpactoRepository.findAll(specs, sortIndiceImpactoByAnio());
  }

  protected Sort sortIndiceImpactoByAnio() {
    return Sort.by(Sort.Direction.ASC, IndiceImpacto_.ANIO);
  }

  protected List<ValorCampo> findValoresByCampoProduccionCientificaId(CodigoCVN codigoCVN,
      Long produccionCientificaId) {
    return campoProduccionCientificaRepository
        .findByCodigoCVNAndProduccionCientificaId(codigoCVN, produccionCientificaId)
        .map(campo -> valorCampoRepository.findAllByCampoProduccionCientificaId(campo.getId()))
        .orElse(new ArrayList<>());
  }

  protected boolean isValorEqualsTablaMaestraCVN(CodigoCVN codigoCVN, TablaMaestraCVN tablaMaestraCVN,
      Long produccionCientificaId) {
    String codigoCVNCode = aliasEnumeradoRepository.findByCodigoCVN(codigoCVN).map(AliasEnumerado::getPrefijoEnumerado)
        .orElse(codigoCVN.getCode());
    return findValoresByCampoProduccionCientificaId(codigoCVN, produccionCientificaId).stream()
        .anyMatch(valorCampo -> (codigoCVNCode + "." + valorCampo.getValor())
            .equalsIgnoreCase(tablaMaestraCVN.getCode()));
  }

  protected boolean isValorEqualsStringValue(CodigoCVN codigoCVN, String stringValue, Long produccionCientificaId) {
    return findValoresByCampoProduccionCientificaId(codigoCVN, produccionCientificaId).stream()
        .anyMatch(valorCampo -> valorCampo.getValor().equals(stringValue));
  }

  protected List<Autor> findAutoresBaremables(Long produccionCientificaId) {
    return autorRepository.findAllByProduccionCientificaIdAndPersonaRefIsNotNull(produccionCientificaId).stream()
        .filter(autor -> isPersonaRefAndBaremable(autor.getPersonaRef()))
        .collect(Collectors.toList());
  }

  protected List<Autor> findAutoresBaremablesByAnio(Long produccionCientificaId, Integer anio) {
    Instant fechaBaremacion = ProduccionCientificaFieldFormatUtil.calculateFechaFinBaremacionByAnio(anio,
        getSgiConfigProperties().getTimeZone());
    PageRequest page = PageRequest.of(0, 10000, Sort.by(Sort.Direction.ASC, Autor_.PERSONA_REF));

    Specification<Autor> specs = AutorSpecifications.byProduccionCientificaId(
        produccionCientificaId).and(AutorSpecifications.byPersonaRefIsNotNull())
        .and(AutorSpecifications.byRangoFechaInFechaBaremacion(fechaBaremacion));

    return getAutorRepository().findAll(specs, page).getContent().stream()
        .filter(autor -> isPersonaRefAndBaremable(autor.getPersonaRef()))
        .collect(Collectors.toList());
  }

  protected Boolean isPersonaRefAndBaremable(String personaRef) {
    return StringUtils.hasText(personaRef) && isPersonaRefBaremable(personaRef);
  }

  protected Boolean isPersonaRefBaremable(String personaRef) {
    return sgiApiCspService.isPersonaBaremable(personaRef, getAnio());
  }

  protected Boolean isGrupoRefBaremable(Long grupoRef) {
    return sgiApiCspService.isGrupoBaremable(grupoRef, getAnio());
  }

  protected Boolean isAutorGrupoValidado(Long autorId, Long produccionCientificaId) {
    Optional<ProduccionCientifica> produccionCientifica = produccionCientificaRepository
        .findById(produccionCientificaId);

    if (produccionCientifica.isPresent()
        && ((produccionCientifica.get().getEstado().getEstado().equals(TipoEstadoProduccion.VALIDADO))
            || (produccionCientifica.get().getEstado().getEstado().equals(TipoEstadoProduccion.VALIDADO_PARCIALMENTE)
                && autorGrupoRepository.findAllByAutorId(autorId).stream()
                    .anyMatch(autorGrupo -> autorGrupo.getEstado().equals(TipoEstadoProduccion.VALIDADO))))) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  private List<String> getPersonasByGrupoBaremableAndAnio(GrupoDto grupo, Integer anio) {
    return getSgiApiCspService().findAllGruposEquipoByGrupoIdAndAnio(grupo.getId(), anio)
        .stream()
        .map(GrupoEquipoDto::getPersonaRef)
        .distinct()
        .collect(Collectors.toList());
  }

  protected List<String> getAllPersonasInGruposBaremablesByAnio(Integer anio) {
    List<String> personasEquipo = new ArrayList<>();

    getSgiApiCspService().findAllGruposByAnio(anio).stream()
        .map(grupo -> getPersonasByGrupoBaremableAndAnio(grupo, anio))
        .forEach(personasEquipo::addAll);

    return personasEquipo.stream().distinct().collect(Collectors.toList());
  }

}
