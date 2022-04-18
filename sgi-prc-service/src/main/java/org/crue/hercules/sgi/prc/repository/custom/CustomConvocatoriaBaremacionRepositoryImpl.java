package org.crue.hercules.sgi.prc.repository.custom;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion_;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupo;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupo_;
import org.crue.hercules.sgi.prc.repository.ConvocatoriaBaremacionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link ConvocatoriaBaremacionRepository}.
 */
@Slf4j
@Component
public class CustomConvocatoriaBaremacionRepositoryImpl implements CustomConvocatoriaBaremacionRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Obtiene la suma de puntos de cada tipo de una {@link ConvocatoriaBaremacion}
   * cuyo id
   * coincide con el indicado.
   * 
   * @param id el identificador de la {@link ConvocatoriaBaremacion}
   * @return suma de puntos de cada tipo
   */
  @Override
  public ConvocatoriaBaremacion findSumPuntosById(Long id) {
    log.debug("findSumPuntosById(long id) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<ConvocatoriaBaremacion> cq = cb.createQuery(ConvocatoriaBaremacion.class);

    Root<ConvocatoriaBaremacion> root = cq.from(ConvocatoriaBaremacion.class);

    Subquery<BigDecimal> sqlTotalPuntosProduccion = getSubqueryPuntosByTipoBaremo(cb, cq, id,
        TipoBaremo.PRODUCCION_CIENTIFICA);
    Subquery<BigDecimal> sqlTotalPuntosSexenios = getSubqueryPuntosByTipoBaremo(cb, cq, id, TipoBaremo.SEXENIO);
    Subquery<BigDecimal> sqlTotalPuntosCostesIndirectos = getSubqueryPuntosByTipoBaremo(cb, cq, id,
        TipoBaremo.COSTE_INDIRECTO);

    cq.where(cb.equal(root.get(ConvocatoriaBaremacion_.id), id));

    cq.multiselect(
        cb.coalesce(sqlTotalPuntosProduccion.getSelection(), new BigDecimal(0)),
        cb.coalesce(sqlTotalPuntosSexenios.getSelection(), new BigDecimal(0)),
        cb.coalesce(sqlTotalPuntosCostesIndirectos.getSelection(), new BigDecimal(0)));

    final TypedQuery<ConvocatoriaBaremacion> q = entityManager.createQuery(cq);

    final ConvocatoriaBaremacion result = q.getSingleResult();

    log.debug("ProyectoPresupuestoTotales getTotales(Long proyectoId) - end");
    return result;
  }

  private Subquery<BigDecimal> getSubqueryPuntosByTipoBaremo(CriteriaBuilder cb,
      CriteriaQuery<ConvocatoriaBaremacion> cq, Long id, TipoBaremo tipoBaremo) {
    Subquery<BigDecimal> sqlTotalPuntos = cq.subquery(BigDecimal.class);
    Root<PuntuacionGrupo> rootTotalPuntos = sqlTotalPuntos
        .from(PuntuacionGrupo.class);
    switch (tipoBaremo) {
      case SEXENIO:
        sqlTotalPuntos
            .select(cb.sum(rootTotalPuntos.get(PuntuacionGrupo_.puntosSexenios)));
        break;
      case COSTE_INDIRECTO:
        sqlTotalPuntos
            .select(cb.sum(rootTotalPuntos.get(PuntuacionGrupo_.puntosCostesIndirectos)));
        break;
      case PRODUCCION_CIENTIFICA:
      default:
        sqlTotalPuntos
            .select(cb.sum(rootTotalPuntos.get(PuntuacionGrupo_.puntosProduccion)));
        break;
    }

    sqlTotalPuntos.where(cb.and(
        cb.equal(rootTotalPuntos.get(PuntuacionGrupo_.convocatoriaBaremacionId), id)));
    return sqlTotalPuntos;
  }

  /**
   * Retorna el Id de {@link ConvocatoriaBaremacion} del último año
   * 
   * @return Id de {@link ConvocatoriaBaremacion}
   */
  @Override
  public Long findIdByMaxAnio() {
    // TODO incluir filtro activo
    log.debug("findIdByMaxAnio - start");
    Long convocatoriaBaremacionId = null;

    // Create query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<Long> cq = cb.createQuery(Long.class);

    // Define FROM clause
    Root<ConvocatoriaBaremacion> root = cq.from(ConvocatoriaBaremacion.class);

    cq.select(root.get(ConvocatoriaBaremacion_.id));

    // Where

    // Order
    List<Order> orders = QueryUtils.toOrders(Sort.by(Sort.Direction.DESC, ConvocatoriaBaremacion_.ANIO), root, cb);
    cq.orderBy(orders);

    TypedQuery<Long> typedQuery = entityManager.createQuery(cq);
    typedQuery.setMaxResults(1);

    convocatoriaBaremacionId = typedQuery.getSingleResult();

    log.debug("findIdByMaxAnio - end");

    return convocatoriaBaremacionId;
  }

}
