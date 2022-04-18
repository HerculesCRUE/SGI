package org.crue.hercules.sgi.csp.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.enums.TipoPartida;
import org.crue.hercules.sgi.csp.model.AnualidadGasto;
import org.crue.hercules.sgi.csp.model.AnualidadGasto_;
import org.crue.hercules.sgi.csp.model.AnualidadIngreso;
import org.crue.hercules.sgi.csp.model.AnualidadIngreso_;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPartida;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPartida_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.crue.hercules.sgi.csp.model.ProyectoPartida;
import org.crue.hercules.sgi.csp.model.ProyectoPartida_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link Convocatoria}.
 */
@Slf4j
@Component
public class CustomConvocatoriaPartidaRepositoryImpl implements CustomConvocatoriaPartidaRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Comprueba si la {@link ConvocatoriaPartida} está en estado 'Registrada' y
   * existen {@link AnualidadGasto} o {@link AnualidadIngreso} vinculados a la
   * {@link ProyectoPartida}
   *
   * @param id Id del {@link ConvocatoriaPartida}.
   * @return true registrada y con datos vinculados/false no registrada o sin
   *         datos vinculados.
   */
  @Override
  public boolean isPosibleEditar(Long id) {
    log.debug("isPosibleEditar(Long id) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<ConvocatoriaPartida> root = cq.from(ConvocatoriaPartida.class);

    Subquery<Long> queryAnualidadGasto = cq.subquery(Long.class);
    Root<AnualidadGasto> anualidadGastoRoot = queryAnualidadGasto.from(AnualidadGasto.class);
    Predicate existsQueryAnualidadGasto = cb.exists(queryAnualidadGasto
        .select(anualidadGastoRoot.get(AnualidadGasto_.id))
        .where(cb.and(cb.equal(
            anualidadGastoRoot.get(AnualidadGasto_.proyectoPartida).get(ProyectoPartida_.convocatoriaPartidaId), id),
            cb.equal(anualidadGastoRoot.get(AnualidadGasto_.proyectoPartida).get(ProyectoPartida_.tipoPartida),
                TipoPartida.GASTO),
            cb.equal(anualidadGastoRoot.get(AnualidadGasto_.proyectoPartida).get(ProyectoPartida_.proyecto)
                .get(Proyecto_.activo), Boolean.TRUE))));

    Subquery<Long> queryAnualidadIngreso = cq.subquery(Long.class);
    Root<AnualidadIngreso> anualidadIngresoRoot = queryAnualidadIngreso.from(AnualidadIngreso.class);
    Predicate existsQueryAnualidadIngreso = cb
        .exists(queryAnualidadIngreso.select(anualidadIngresoRoot.get(AnualidadIngreso_.id))
            .where(cb.and(
                cb.equal(anualidadIngresoRoot.get(AnualidadIngreso_.proyectoPartida)
                    .get(ProyectoPartida_.convocatoriaPartidaId), id),
                cb.equal(anualidadIngresoRoot.get(AnualidadIngreso_.proyectoPartida).get(ProyectoPartida_.tipoPartida),
                    TipoPartida.INGRESO),
                cb.equal(anualidadIngresoRoot.get(AnualidadIngreso_.proyectoPartida).get(ProyectoPartida_.proyecto)
                    .get(Proyecto_.activo), Boolean.TRUE))));

    Predicate convocatoriaPartida = cb.equal(root.get(ConvocatoriaPartida_.id), id);
    Predicate convocatoriaRegistrada = cb.equal(root.get(ConvocatoriaPartida_.convocatoria).get(Convocatoria_.estado),
        Convocatoria.Estado.REGISTRADA);

    Predicate convocatoriaPartidaGasto = cb.equal(root.get(ConvocatoriaPartida_.tipoPartida), TipoPartida.GASTO);
    Predicate convocatoriaPartidaIngreso = cb.equal(root.get(ConvocatoriaPartida_.tipoPartida), TipoPartida.INGRESO);

    Predicate convocatoriaPartidaGastoRegistrada = cb.and(convocatoriaPartida, convocatoriaPartidaGasto,
        convocatoriaRegistrada, existsQueryAnualidadGasto);
    Predicate convocatoriaPartidaIngresoRegistrada = cb.and(convocatoriaPartida, convocatoriaPartidaIngreso,
        convocatoriaRegistrada, existsQueryAnualidadIngreso);

    Predicate predicateFinal = cb.and(cb.or(convocatoriaPartidaGastoRegistrada, convocatoriaPartidaIngresoRegistrada));
    cq.select(root.get(ConvocatoriaPartida_.id)).where(predicateFinal);

    boolean returnValue = entityManager.createQuery(cq).getResultList().isEmpty();

    log.debug("isPosibleEditar(Long id) - end");
    return returnValue;
  }
}
