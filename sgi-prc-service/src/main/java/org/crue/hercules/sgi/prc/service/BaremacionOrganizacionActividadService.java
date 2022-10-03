package org.crue.hercules.sgi.prc.service;

import java.util.function.LongPredicate;

import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.enums.TablaMaestraCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * Servicio para la baremación de organización de actividades
 */
@Service
@Transactional(readOnly = true)
@Validated
public class BaremacionOrganizacionActividadService extends BaremacionCommonService {

  @Autowired
  public BaremacionOrganizacionActividadService(
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
    return TipoPuntuacion.ORGANIZACION_ACTIVIDADES;
  }

  @Override
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
    return produccionCientificaId -> isValorEqualsTablaMaestraCVN(
        CodigoCVN.E060_020_030_030, TablaMaestraCVN.PAIS_724, produccionCientificaId);
  }

}
