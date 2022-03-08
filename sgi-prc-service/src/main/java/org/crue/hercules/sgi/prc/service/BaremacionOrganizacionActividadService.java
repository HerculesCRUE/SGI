package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.util.function.LongPredicate;

import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.crue.hercules.sgi.prc.enums.TablaMaestraCVN;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.repository.BaremoRepository;
import org.crue.hercules.sgi.prc.repository.CampoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.IndiceImpactoRepository;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionBaremoItemRepository;
import org.crue.hercules.sgi.prc.repository.TipoFuenteImpactoCuartilRepository;
import org.crue.hercules.sgi.prc.repository.ValorCampoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la baremación de organización de actividades
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class BaremacionOrganizacionActividadService extends BaremacionCommonService {

  public BaremacionOrganizacionActividadService(
      ProduccionCientificaRepository produccionCientificaRepository,
      PuntuacionBaremoItemRepository puntuacionBaremoItemRepository,
      BaremoRepository baremoRepository,
      CampoProduccionCientificaRepository campoProduccionCientificaRepository,
      ValorCampoRepository valorCampoRepository,
      IndiceImpactoRepository indiceImpactoRepository,
      TipoFuenteImpactoCuartilRepository tipoFuenteImpactoCuartilRepository,
      ProduccionCientificaCloneService produccionCientificaCloneService,
      ModelMapper modelMapper) {
    super(produccionCientificaRepository, puntuacionBaremoItemRepository, baremoRepository,
        campoProduccionCientificaRepository, valorCampoRepository, indiceImpactoRepository,
        tipoFuenteImpactoCuartilRepository, produccionCientificaCloneService,
        modelMapper);
    loadPredicates();
  }

  protected void loadPredicates() {

    LongPredicate isParticipacionOrganizativoComite = getPredicateIsParticipacionOrganizativoComite();
    LongPredicate isParticipacionOrganizativoPresidenteComite = getPredicateIsParticipacionOrganizativoPresidenteComite();
    LongPredicate isEspania = getPredicateIsPaisEspania();
    LongPredicate isNotEspania = getPredicateIsPaisEspania().negate();

    // ORG_ACT_COMITE_CIENTIFICO_ORGANIZ_NACIONAL
    getHmTipoBaremoPredicates().put(TipoBaremo.ORG_ACT_COMITE_CIENTIFICO_ORGANIZ_NACIONAL,
        isParticipacionOrganizativoComite
            .and(isEspania));

    // ORG_ACT_COMITE_CIENTIFICO_ORGANIZ_INTERNACIONAL
    getHmTipoBaremoPredicates().put(TipoBaremo.ORG_ACT_COMITE_CIENTIFICO_ORGANIZ_INTERNACIONAL,
        isParticipacionOrganizativoComite
            .and(isNotEspania));

    // ORG_ACT_COMITE_CIENTIFICO_ORGANIZ_NACIONAL_PRESIDENTE
    getHmTipoBaremoPredicates().put(TipoBaremo.ORG_ACT_COMITE_CIENTIFICO_ORGANIZ_NACIONAL_PRESIDENTE,
        isParticipacionOrganizativoPresidenteComite
            .and(isEspania));

    // ORG_ACT_COMITE_CIENTIFICO_ORGANIZ_INTERNACIONAL_PRESIDENTE
    getHmTipoBaremoPredicates().put(TipoBaremo.ORG_ACT_COMITE_CIENTIFICO_ORGANIZ_INTERNACIONAL_PRESIDENTE,
        isParticipacionOrganizativoPresidenteComite
            .and(isNotEspania));
  }

  /* -------------------- predicates -------------------- */

  private LongPredicate getPredicateIsParticipacionOrganizativoPresidenteComite() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E060_020_030_110, TablaMaestraCVN.E060_020_030_110_ORGANIZATIVO_PRESIDENTE_COMITE,
        produccionCientificaId);
  }

  private LongPredicate getPredicateIsParticipacionOrganizativoComite() {
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E060_020_030_110, TablaMaestraCVN.E060_020_030_110_ORGANIZATIVO_COMITE, produccionCientificaId);
  }

  private LongPredicate getPredicateIsPaisEspania() {
    return produccionCientificaId -> isValorEqualsStringValue(
        CodigoCVN.E060_020_030_030, TablaMaestraCVN.PAIS_724.getInternValue(), produccionCientificaId);
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

}
