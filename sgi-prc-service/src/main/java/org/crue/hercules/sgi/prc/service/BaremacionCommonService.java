package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.LongPredicate;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.crue.hercules.sgi.prc.dto.BaremacionInput.BaremoInput;
import org.crue.hercules.sgi.prc.enums.TablaMaestraCVN;
import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.exceptions.ProduccionCientificaNotFoundException;
import org.crue.hercules.sgi.prc.model.Baremo;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoPuntos;
import org.crue.hercules.sgi.prc.model.IndiceImpacto;
import org.crue.hercules.sgi.prc.model.IndiceImpacto_;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.PuntuacionBaremoItem;
import org.crue.hercules.sgi.prc.model.TipoFuenteImpactoCuartil.Cuartil;
import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.repository.BaremoRepository;
import org.crue.hercules.sgi.prc.repository.CampoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.IndiceImpactoRepository;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionBaremoItemRepository;
import org.crue.hercules.sgi.prc.repository.TipoFuenteImpactoCuartilRepository;
import org.crue.hercules.sgi.prc.repository.ValorCampoRepository;
import org.crue.hercules.sgi.prc.repository.specification.BaremoSpecifications;
import org.crue.hercules.sgi.prc.repository.specification.IndiceImpactoSpecifications;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

  private static final String POSICION_REVISTA_50_00 = "50.00";
  private static final String POSICION_REVISTA_25_00 = "25.00";
  private static final String POSICION_REVISTA_75_00 = "75.00";

  private final ProduccionCientificaRepository produccionCientificaRepository;
  private final PuntuacionBaremoItemRepository puntuacionBaremoItemRepository;
  private final BaremoRepository baremoRepository;

  @Getter
  private final CampoProduccionCientificaRepository campoProduccionCientificaRepository;
  @Getter
  private final ValorCampoRepository valorCampoRepository;

  private final IndiceImpactoRepository indiceImpactoRepository;

  private final TipoFuenteImpactoCuartilRepository tipoFuenteImpactoCuartilRepository;

  private final ProduccionCientificaCloneService produccionCientificaCloneService;
  private final ModelMapper modelMapper;

  // Año de baremación necesario para algunos predicados
  @Getter
  @Setter
  private Integer anio;

  // Almacena por cada tipo de baremo sus predicados para su baremación de puntos
  @Getter
  private EnumMap<TipoBaremo, LongPredicate> hmTipoBaremoPredicates = new EnumMap<>(TipoBaremo.class);

  protected abstract BigDecimal evaluateBaremoModulador(BaremacionInput baremacionInput);

  protected abstract BigDecimal evaluateBaremoExtra(BaremacionInput baremacionInput);

  protected abstract void loadPredicates();

  public void evaluateProduccionCientificaByTypeAndAnio(BaremacionInput baremacionInput) {
    log.debug("evaluateProduccionCientificaByTypeAndAnio(baremacionInput) - start");

    setAnio(baremacionInput.getAnio());

    produccionCientificaRepository.findAllByBaremacion(baremacionInput).stream().forEach(produccionCientificaId -> {
      baremacionInput.setProduccionCientificaId(produccionCientificaId);
      BigDecimal puntos = evaluateItemProduccionCientifica(baremacionInput);
      if (puntos.compareTo(BigDecimal.ZERO) >= 0) {
        // TODO cuando esté grupos evaluar autor y grupo de investigación
      }
    });

    log.debug("evaluateProduccionCientificaByTypeAndAnio(baremacionInput) - end");
  }

  protected BigDecimal evaluateItemProduccionCientifica(BaremacionInput baremacionInput) {
    log.debug("evaluateItemProduccionCientifica(baremacionInput) - start");
    BigDecimal puntos = BigDecimal.ZERO;

    Specification<Baremo> specs = BaremoSpecifications
        .byConvocatoriaBaremacionId(baremacionInput.getConvocatoriaBaremacionId())
        .and(BaremoSpecifications.byConfiguracionBaremoEpigrafeCVN(baremacionInput.getEpigrafeCVN()))
        .and(BaremoSpecifications.byConfiguracionBaremoActivoIsTrue());

    List<Baremo> baremos = baremoRepository.findAll(specs);

    // PRINCIPAL
    Pair<BigDecimal, Long> pairEvaluateItemPrincipal = evaluateItemProduccionCientificaByTipoBaremoPrincipal(
        baremacionInput, baremos);
    puntos = pairEvaluateItemPrincipal.getFirst();
    Long newProduccionCientificaId = pairEvaluateItemPrincipal.getSecond();

    // MODULADOR
    // if (puntos.compareTo(BigDecimal.ZERO) > 0) {
    // puntos.add(baremos.stream()
    // .filter(baremo ->
    // baremo.getConfiguracionBaremo().getTipoPuntos().equals(TipoPuntos.MODULADOR))
    // .map(baremo -> {
    // baremacionInput.setBaremo(modelMapper.map(baremo, BaremoInput.class));
    // BigDecimal puntosModulador = this.evaluateBaremoModulador(baremacionInput);

    // savePuntuacionBaremoItem(baremo.getId(), newProduccionCientificaId,
    // puntosModulador);

    // return puntosModulador;
    // })
    // .reduce(puntos, BigDecimal::multiply));
    // }

    // // EXTRA
    // if (puntos.compareTo(BigDecimal.ZERO) > 0) {
    // puntos.add(baremos.stream()
    // .filter(baremo ->
    // baremo.getConfiguracionBaremo().getTipoPuntos().equals(TipoPuntos.EXTRA))
    // .map(baremo -> {
    // baremacionInput.setBaremo(modelMapper.map(baremo, BaremoInput.class));
    // BigDecimal puntosModulador = this.evaluateBaremoExtra(baremacionInput);

    // savePuntuacionBaremoItem(baremo.getId(), newProduccionCientificaId,
    // puntosModulador);

    // return puntosModulador;
    // })
    // .reduce(puntos, BigDecimal::add));
    // }

    log.debug("evaluateItemProduccionCientifica(baremacionInput) - end");
    return puntos;
  }

  protected PuntuacionBaremoItem savePuntuacionBaremoItem(Long baremoId, Long produccionCientificaId,
      BigDecimal puntosModulador) {
    PuntuacionBaremoItem puntuacionBaremoItem = PuntuacionBaremoItem.builder()
        .baremoId(baremoId)
        .produccionCientificaId(produccionCientificaId)
        .puntos(puntosModulador)
        .build();
    return puntuacionBaremoItemRepository.save(puntuacionBaremoItem);
  }

  protected Pair<BigDecimal, Long> evaluateItemProduccionCientificaByTipoBaremoPrincipal(
      BaremacionInput baremacionInput,
      List<Baremo> baremos) {
    log.debug("evaluateItemProduccionCientificaByTipoBaremoPrincipal(baremacionInput, baremos) - start");

    Pair<BigDecimal, Long> pairEvaluateItemPrincipal = Pair.of(BigDecimal.ZERO, -1L);
    List<Baremo> baremosPrincipales = baremos.stream()
        .filter(baremo -> baremo.getConfiguracionBaremo().getTipoPuntos().equals(TipoPuntos.PRINCIPAL))
        .collect(Collectors.toList());

    baremosPrincipales.sort(Comparator.comparing(baremo -> baremo.getConfiguracionBaremo().getPrioridad()));

    Optional<Pair<BigDecimal, Baremo>> puntosBaremoPrincipal = baremosPrincipales.stream().map(
        baremo -> {
          baremacionInput.setBaremo(modelMapper.map(baremo, BaremoInput.class));
          return Pair.of(
              this.evaluateBaremoPrincipal(baremacionInput),
              baremo);
        })
        .filter(pairPuntosBaremo -> pairPuntosBaremo.getFirst().compareTo(BigDecimal.ZERO) > 0)
        .findFirst();

    if (puntosBaremoPrincipal.isPresent()) {
      BigDecimal puntos = puntosBaremoPrincipal.get().getFirst();
      Baremo baremoPrincipal = puntosBaremoPrincipal.get().getSecond();

      ProduccionCientifica produccionCientificaClone = produccionCientificaRepository
          .findById(baremacionInput.getProduccionCientificaId())
          .map(produccionCientifica -> produccionCientificaCloneService.cloneProduccionCientificaAndRelations(
              baremacionInput.getConvocatoriaBaremacionId(),
              baremoPrincipal.getId(), produccionCientifica))
          .orElseThrow(
              () -> new ProduccionCientificaNotFoundException(baremacionInput.getProduccionCientificaId().toString()));

      savePuntuacionBaremoItem(baremoPrincipal.getId(), produccionCientificaClone.getId(),
          puntos).getId();

      pairEvaluateItemPrincipal = Pair.of(puntos, produccionCientificaClone.getId());
    }
    log.debug("evaluateItemProduccionCientificaByTipoBaremoPrincipal(baremacionInput, baremos) - end");
    return pairEvaluateItemPrincipal;
  }

  protected BigDecimal evaluateBaremoPrincipal(BaremacionInput baremacionInput) {
    log.debug("calcularBaremoPrincipalPublicacion(produccionCientificaId, baremo) - start");

    BigDecimal puntos = BigDecimal.ZERO;

    TipoBaremo tipoBaremo = baremacionInput.getBaremo().getConfiguracionBaremo().getTipoBaremo();

    if (LongStream.of(baremacionInput.getProduccionCientificaId())
        .anyMatch(getHmTipoBaremoPredicates().get(tipoBaremo))) {
      puntos = baremacionInput.getBaremo().getPuntos();
    }

    log.debug("calcularBaremoPrincipalPublicacion(produccionCientificaId, baremo) - end");
    return puntos;
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

  protected boolean hasFuenteImpactoIn(Long produccionCientificaId, List<TipoFuenteImpacto> fuentesImpacto) {
    Specification<IndiceImpacto> specs = IndiceImpactoSpecifications
        .byProduccionCientificaId(produccionCientificaId)
        .and(IndiceImpactoSpecifications.byAnio(getAnio()).or(IndiceImpactoSpecifications.byAnioIsNull()))
        .and(IndiceImpactoSpecifications.tipoFuenteImpactoIn(fuentesImpacto));
    PageRequest page = PageRequest.of(0, 1, sortIndiceImpactoByAnio());
    return indiceImpactoRepository.findAll(specs, page).hasContent();
  }

  protected boolean hasFuenteImpactoNotIn(Long produccionCientificaId, List<TipoFuenteImpacto> fuentesImpacto) {
    Specification<IndiceImpacto> specs = IndiceImpactoSpecifications
        .byProduccionCientificaId(produccionCientificaId)
        .and(IndiceImpactoSpecifications.byAnio(getAnio()).or(IndiceImpactoSpecifications.byAnioIsNull()))
        .and(IndiceImpactoSpecifications.tipoFuenteImpactoNotIn(fuentesImpacto));
    PageRequest page = PageRequest.of(0, 1, sortIndiceImpactoByAnio());
    return indiceImpactoRepository.findAll(specs, page).hasContent();
  }

  private Sort sortIndiceImpactoByAnio() {
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
    return findValoresByCampoProduccionCientificaId(codigoCVN, produccionCientificaId).stream()
        .anyMatch(valorCampo -> (codigoCVN.getInternValue() + "." + valorCampo.getValor())
            .equals(tablaMaestraCVN.getInternValue()));
  }

  protected boolean isValorEqualsStringValue(CodigoCVN codigoCVN, String stringValue, Long produccionCientificaId) {
    return findValoresByCampoProduccionCientificaId(codigoCVN, produccionCientificaId).stream()
        .anyMatch(valorCampo -> valorCampo.getValor().equals(stringValue));
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

  protected boolean isRevista25OrPosicionRevistaLessEqualThan25(IndiceImpacto indiceImpacto) {
    if (indiceImpacto.getRevista25()) {
      return true;
    } else if (!Objects.isNull(indiceImpacto.getPosicionPublicacion())
        && !Objects.isNull(indiceImpacto.getNumeroRevistas())) {
      return indiceImpacto.getPosicionPublicacion().divide(indiceImpacto.getNumeroRevistas(), 2, RoundingMode.HALF_UP)
          .compareTo(new BigDecimal(POSICION_REVISTA_25_00)) <= 0;
    }
    return false;
  }

  protected boolean isPosicionRevistaGreatherThan25(IndiceImpacto indiceImpacto) {
    if (!Objects.isNull(indiceImpacto.getPosicionPublicacion())
        && !Objects.isNull(indiceImpacto.getNumeroRevistas())) {
      return indiceImpacto.getPosicionPublicacion().divide(indiceImpacto.getNumeroRevistas(), 2, RoundingMode.HALF_UP)
          .compareTo(new BigDecimal(POSICION_REVISTA_25_00)) > 0;
    }
    return false;
  }

  protected boolean isPosicionRevistaGreatherThan25AndLessThanEqual50(IndiceImpacto indiceImpacto) {
    if (!Objects.isNull(indiceImpacto.getPosicionPublicacion())
        && !Objects.isNull(indiceImpacto.getNumeroRevistas())) {
      BigDecimal posicionRevista = indiceImpacto.getPosicionPublicacion().divide(indiceImpacto.getNumeroRevistas(), 2,
          RoundingMode.HALF_UP);
      return posicionRevista.compareTo(new BigDecimal(POSICION_REVISTA_25_00)) > 0 && posicionRevista
          .compareTo(new BigDecimal(POSICION_REVISTA_50_00)) <= 0;
    }
    return false;
  }

  protected boolean isPosicionRevistaGreatherThan50AndLessThanEqual75(IndiceImpacto indiceImpacto) {
    if (!Objects.isNull(indiceImpacto.getPosicionPublicacion())
        && !Objects.isNull(indiceImpacto.getNumeroRevistas())) {
      BigDecimal posicionRevista = indiceImpacto.getPosicionPublicacion().divide(indiceImpacto.getNumeroRevistas(), 2,
          RoundingMode.HALF_UP);
      return posicionRevista.compareTo(new BigDecimal(POSICION_REVISTA_50_00)) > 0 && posicionRevista
          .compareTo(new BigDecimal(POSICION_REVISTA_75_00)) <= 0;
    }
    return false;
  }

  protected boolean isPosicionRevistaGreatherThan75(IndiceImpacto indiceImpacto) {
    if (!Objects.isNull(indiceImpacto.getPosicionPublicacion())
        && !Objects.isNull(indiceImpacto.getNumeroRevistas())) {
      return indiceImpacto.getPosicionPublicacion().divide(indiceImpacto.getNumeroRevistas(), 2, RoundingMode.HALF_UP)
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
