package org.crue.hercules.sgi.prc.repository.custom;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaResumen;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica_;
import org.crue.hercules.sgi.prc.model.ConfiguracionCampo;
import org.crue.hercules.sgi.prc.model.ConfiguracionCampo_;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica_;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica_;
import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.model.ValorCampo_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link ProduccionCientifica}.
 */
@Slf4j
@Component
public class CustomProduccionCientificaRepositoryImpl implements CustomProduccionCientificaRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Elimina el estado de {@link ProduccionCientifica} cuyo id coincide con el
   * indicado.
   * 
   * @param produccionCientificaId el identificador de la
   *                               {@link ProduccionCientifica}
   * @return el número de registros eliminados
   */
  @Override
  public int updateEstadoNull(long produccionCientificaId) {
    log.debug("updateEstadoNull(produccionCientificaId) : {} - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaUpdate<ProduccionCientifica> update = cb.createCriteriaUpdate(ProduccionCientifica.class);

    // Define FROM IndiceImpacto clause
    Root<ProduccionCientifica> root = update.from(ProduccionCientifica.class);

    update.set(ProduccionCientifica_.ESTADO, null);

    // Set WHERE restrictions
    update.where(cb.equal(root.get(ProduccionCientifica_.id), produccionCientificaId));

    // Execute query
    int result = entityManager.createQuery(update).executeUpdate();

    log.debug("updateEstadoNull(produccionCientificaId) : {} - start");

    return result;
  }

  /**
   * Devuelve el identificador CVN y el estado (Validado O Rechazado) de aquellos
   * items almacenados en producción científica que han cambiado al estado
   * Validado o Rechazado en una fecha igual o superior a la fecha de estado
   * pasada por parámetro
   * 
   * @param specification filtro fechaEstado
   * @return lista de {@link ProduccionCientificaResumen}
   */
  @Override
  public List<ProduccionCientificaResumen> findByEstadoValidadoOrRechazadoByFechaModificacion(
      Specification<ProduccionCientifica> specification) {
    log.debug("findByEstadoValidadoOrRechazadoByFechaModificacion(specification) : {} - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<ProduccionCientificaResumen> cq = cb.createQuery(ProduccionCientificaResumen.class);

    Root<ProduccionCientifica> root = cq.from(ProduccionCientifica.class);

    cq.multiselect(root.get(ProduccionCientifica_.id).alias("id"),
        root.get(ProduccionCientifica_.produccionCientificaRef).alias(
            ProduccionCientifica_.PRODUCCION_CIENTIFICA_REF),
        root.get(ProduccionCientifica_.estado).get(EstadoProduccionCientifica_.estado)
            .alias(ProduccionCientifica_.ESTADO),
        root.get(ProduccionCientifica_.epigrafeCVN).alias(ProduccionCientifica_.EPIGRAFE_CV_N))
        .distinct(true)
        .where(specification.toPredicate(root, cq, cb));

    log.debug("findByEstadoValidadoOrRechazadoByFechaModificacion(specification) : {} - end");
    return entityManager.createQuery(cq).getResultList();
  }

  /**
   * Devuelve una lista de ids de {@link ProduccionCientifica} de un
   * {@link EpigrafeCVN} que cumplan las condiciones de baremación.
   * 
   * @param baremacionInput fechaInicio Fecha inicio de baremación en formato UTC,
   *                        fechaFin Fecha fin de baremación en formato UTC,
   *                        epigrafeCVN {@link EpigrafeCVN} a filtrar,
   *                        codigoCVN {@link CodigoCVN} a filtrar
   * @return lista de ids de {@link ProduccionCientifica}
   */
  @Override
  public List<Long> findAllBaremacionByFechaInicio(BaremacionInput baremacionInput) {
    log.debug("findAllBaremacionByFechaInicio(BaremacionInput baremacionInput) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<Long> cq = cb.createQuery(Long.class);

    Root<ProduccionCientifica> root = cq.from(ProduccionCientifica.class);

    Join<ProduccionCientifica, EstadoProduccionCientifica> joinEstado = root.join(
        ProduccionCientifica_.estado);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCampos = root.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    Join<CampoProduccionCientifica, ValorCampo> joinValores = joinCampos.join(
        CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    cq.select(root.get(ProduccionCientifica_.id)).distinct(true);

    Predicate predicateEpigrafe = cb.equal(root.get(ProduccionCientifica_.epigrafeCVN),
        baremacionInput.getEpigrafeCVN());

    Predicate predicateConvocatoriaBaremacionIsNull = cb
        .isNull(root.get(ProduccionCientifica_.convocatoriaBaremacionId));

    Predicate predicateEstado = cb.or(
        cb.equal(joinEstado.get(EstadoProduccionCientifica_.estado), TipoEstadoProduccion.VALIDADO),
        cb.equal(joinEstado.get(EstadoProduccionCientifica_.estado),
            TipoEstadoProduccion.VALIDADO_PARCIALMENTE));

    Subquery<CodigoCVN> queryConfiguracionCampo = cq.subquery(CodigoCVN.class);
    Root<ConfiguracionCampo> rootConfiguracionCampo = queryConfiguracionCampo.from(ConfiguracionCampo.class);
    Predicate existsConfiguracionCampoFecha = cb.equal(queryConfiguracionCampo
        .select(rootConfiguracionCampo.get(ConfiguracionCampo_.codigoCVN))
        .where(cb.equal(rootConfiguracionCampo.get(ConfiguracionCampo_.epigrafeCVN), baremacionInput.getEpigrafeCVN()),
            cb.isTrue(rootConfiguracionCampo.get(ConfiguracionCampo_.fechaReferenciaInicio))),
        joinCampos.get(CampoProduccionCientifica_.codigoCVN));

    Predicate predicateValorFecha = cb.between(joinValores.get(ValorCampo_.valor), baremacionInput.getFechaInicio(),
        baremacionInput.getFechaFin());

    Predicate predicateFinal = cb.and(predicateConvocatoriaBaremacionIsNull, predicateEpigrafe, predicateEstado,
        existsConfiguracionCampoFecha, predicateValorFecha);

    cq.where(predicateFinal);

    List<Long> result = entityManager.createQuery(cq).getResultList();

    log.debug("findAllBaremacionByFechaInicio(BaremacionInput baremacionInput) - end");

    return result;
  }

  /**
   * Devuelve una lista de ids de {@link ProduccionCientifica} de un
   * {@link EpigrafeCVN} que cumplan las condiciones de baremación.
   * 
   * @param baremacionInput      fechaInicio Fecha inicio de baremación en formato
   *                             UTC,
   *                             fechaFin Fecha fin de baremación en formato UTC,
   *                             {@link EpigrafeCVN} a filtrar,
   *                             {@link CodigoCVN} a filtrar
   * @param codigoCVNFechaInicio {@link CodigoCVN} de fechaInicio
   * @param codigoCVNFechaFin    {@link CodigoCVN} de fechaFin
   * @return lista de ids de {@link ProduccionCientifica}
   */
  @Override
  public List<Long> findAllBaremacionByFechaInicioAndFechaFin(BaremacionInput baremacionInput,
      CodigoCVN codigoCVNFechaInicio, CodigoCVN codigoCVNFechaFin) {
    log.debug(
        "findAllBaremacionByFechaInicioAndFechaFin(baremacionInput, codigoCVNFechaInicio, codigoCVNFechaFin) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<Long> cq = cb.createQuery(Long.class);

    Root<ProduccionCientifica> root = cq.from(ProduccionCientifica.class);

    Join<ProduccionCientifica, EstadoProduccionCientifica> joinEstado = root.join(
        ProduccionCientifica_.estado);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposFechaInicio = root.join(
        ProduccionCientifica_.campos);
    Join<CampoProduccionCientifica, ValorCampo> joinValoresFechaInicio = joinCamposFechaInicio.join(
        CampoProduccionCientifica_.valoresCampos);
    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposFechaFin = root.join(
        ProduccionCientifica_.campos);
    Join<CampoProduccionCientifica, ValorCampo> joinValoresFechaFin = joinCamposFechaFin.join(
        CampoProduccionCientifica_.valoresCampos);

    cq.select(root.get(ProduccionCientifica_.id)).distinct(true);

    Predicate predicateEpigrafe = cb.equal(root.get(ProduccionCientifica_.epigrafeCVN),
        baremacionInput.getEpigrafeCVN());

    Predicate predicateConvocatoriaBaremacionIsNull = cb
        .isNull(root.get(ProduccionCientifica_.convocatoriaBaremacionId));

    Predicate predicateEstado = cb.or(
        cb.equal(joinEstado.get(EstadoProduccionCientifica_.estado), TipoEstadoProduccion.VALIDADO),
        cb.equal(joinEstado.get(EstadoProduccionCientifica_.estado),
            TipoEstadoProduccion.VALIDADO_PARCIALMENTE));

    Predicate predicateFechaInicio = cb.equal(joinCamposFechaInicio.get(CampoProduccionCientifica_.codigoCVN),
        codigoCVNFechaInicio);
    Predicate predicateFechaFin = cb.equal(joinCamposFechaFin.get(CampoProduccionCientifica_.codigoCVN),
        codigoCVNFechaFin);

    Predicate predicateValorFechaInicio = cb.lessThanOrEqualTo(joinValoresFechaInicio.get(ValorCampo_.valor),
        baremacionInput.getFechaFin());
    Predicate predicateValorFechaFin = cb.greaterThanOrEqualTo(joinValoresFechaFin.get(ValorCampo_.valor),
        baremacionInput.getFechaInicio());

    Predicate predicateFinal = cb.and(predicateConvocatoriaBaremacionIsNull, predicateEpigrafe, predicateEstado,
        predicateFechaInicio, predicateFechaFin, predicateValorFechaInicio, predicateValorFechaFin);

    cq.where(predicateFinal);

    List<Long> result = entityManager.createQuery(cq).getResultList();

    log.debug(
        "findAllBaremacionByFechaInicioAndFechaFin(baremacionInput, codigoCVNFechaInicio, codigoCVNFechaFin) - end");

    return result;
  }
}