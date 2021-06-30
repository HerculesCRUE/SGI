package org.crue.hercules.sgi.csp.repository.custom;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaDocumento;
import org.crue.hercules.sgi.csp.model.ConvocatoriaDocumento_;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace_;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase_;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHito;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHito_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link Convocatoria}.
 */
@Slf4j
@Component
public class CustomConvocatoriaRepositoryImpl implements CustomConvocatoriaRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Comprueba si existen datos vinculados a la {@link Convocatoria} de
   * {@link TipoFase}, {@link TipoHito}, {@link TipoEnlace} y
   * {@link TipoDocumento} con el fin de permitir la edición de los campos
   * unidadGestionRef y modeloEjecucion.
   *
   * @param id Id del {@link Convocatoria}.
   * @return true existen datos vinculados/false no existen datos vinculados.
   */
  @Override
  public Boolean tieneVinculaciones(Long id) {
    log.debug("tieneVinculaciones(Long id) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Convocatoria> root = cq.from(Convocatoria.class);

    Subquery<Long> queryConvocatoriaEnlace = cq.subquery(Long.class);
    Root<ConvocatoriaEnlace> convocatoriaEnlaceRoot = queryConvocatoriaEnlace.from(ConvocatoriaEnlace.class);
    Predicate existsQueryConvocatoriaEnlace = cb.exists(queryConvocatoriaEnlace
        .select(convocatoriaEnlaceRoot.get(ConvocatoriaEnlace_.convocatoria).get(Convocatoria_.id))
        .where(cb.equal(convocatoriaEnlaceRoot.get(ConvocatoriaEnlace_.convocatoria).get(Convocatoria_.id),
            root.get(Convocatoria_.id))));

    Subquery<Long> queryConvocatoriaFase = cq.subquery(Long.class);
    Root<ConvocatoriaFase> convocatoriaFaseRoot = queryConvocatoriaFase.from(ConvocatoriaFase.class);
    Predicate existsQueryConvocatoriaFase = cb.exists(
        queryConvocatoriaFase.select(convocatoriaFaseRoot.get(ConvocatoriaFase_.convocatoria).get(Convocatoria_.id))
            .where(cb.equal(convocatoriaFaseRoot.get(ConvocatoriaFase_.convocatoria).get(Convocatoria_.id),
                root.get(Convocatoria_.id))));

    Subquery<Long> queryConvocatoriaHito = cq.subquery(Long.class);
    Root<ConvocatoriaHito> convocatoriaHitoRoot = queryConvocatoriaHito.from(ConvocatoriaHito.class);
    Predicate existsQueryConvocatoriaHito = cb.exists(
        queryConvocatoriaHito.select(convocatoriaHitoRoot.get(ConvocatoriaHito_.convocatoria).get(Convocatoria_.id))
            .where(cb.equal(convocatoriaHitoRoot.get(ConvocatoriaHito_.convocatoria).get(Convocatoria_.id),
                root.get(Convocatoria_.id))));

    Subquery<Long> queryConvocatoriaDocumento = cq.subquery(Long.class);
    Root<ConvocatoriaDocumento> convocatoriaDocumentoRoot = queryConvocatoriaDocumento
        .from(ConvocatoriaDocumento.class);
    Predicate existsQueryConvocatoriaDocumento = cb.exists(queryConvocatoriaDocumento
        .select(convocatoriaDocumentoRoot.get(ConvocatoriaDocumento_.convocatoria).get(Convocatoria_.id))
        .where(cb.equal(convocatoriaDocumentoRoot.get(ConvocatoriaDocumento_.convocatoria).get(Convocatoria_.id),
            root.get(Convocatoria_.id))));

    Predicate convocatoria = cb.equal(root.get(Convocatoria_.id), id);
    Predicate vinculaciones = cb.or(existsQueryConvocatoriaEnlace,
        cb.or(existsQueryConvocatoriaFase, cb.or(existsQueryConvocatoriaHito, existsQueryConvocatoriaDocumento)));

    Predicate finalPredicate = cb.and(convocatoria, vinculaciones);
    cq.select(root.get(Convocatoria_.id)).where(finalPredicate);

    Boolean returnValue = entityManager.createQuery(cq).getResultList().size() > 0;

    log.debug("tieneVinculaciones(Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba si la {@link Convocatoria} está en estado 'Registrada' y existen
   * {@link Solicitud} o proyectos vinculados a la {@link Convocatoria} con el fin
   * de permitir la creación, modificación o eliminación de ciertas entidades
   * relacionadas con la propia {@link Convocatoria}.
   *
   * @param id Id del {@link Convocatoria}.
   * @return true registrada y con datos vinculados/false no registrada o sin
   *         datos vinculados.
   */
  @Override
  public Boolean esRegistradaConSolicitudesOProyectos(Long id) {
    log.debug("esRegistradaConSolicitudesOProyectos(Long id) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Convocatoria> root = cq.from(Convocatoria.class);

    Subquery<Long> querySolicitud = cq.subquery(Long.class);
    Root<Solicitud> solicitudRoot = querySolicitud.from(Solicitud.class);
    Predicate existsQuerySolicitud = cb
        .exists(querySolicitud.select(solicitudRoot.get(Solicitud_.convocatoria).get(Convocatoria_.id))
            .where(cb.and(
                cb.equal(solicitudRoot.get(Solicitud_.convocatoria).get(Convocatoria_.id), root.get(Convocatoria_.id)),
                cb.equal(solicitudRoot.get(Solicitud_.activo), Boolean.TRUE))));

    Subquery<Long> queryProyecto = cq.subquery(Long.class);
    Root<Proyecto> proyectoRoot = queryProyecto.from(Proyecto.class);
    Predicate existsQueryProyecto = cb
        .exists(queryProyecto.select(proyectoRoot.get(Proyecto_.convocatoria).get(Convocatoria_.id))
            .where(cb.and(
                cb.equal(proyectoRoot.get(Proyecto_.convocatoria).get(Convocatoria_.id), root.get(Convocatoria_.id)),
                cb.equal(proyectoRoot.get(Proyecto_.activo), Boolean.TRUE))));

    Predicate convocatoria = cb.equal(root.get(Convocatoria_.id), id);
    Predicate registrada = cb.equal(root.get(Convocatoria_.estado), Convocatoria.Estado.REGISTRADA);
    Predicate convocatoriaRegistrada = cb.and(convocatoria, registrada);

    Predicate vinculaciones = cb.or(existsQuerySolicitud, existsQueryProyecto);
    Predicate finalPredicate = cb.and(convocatoriaRegistrada, vinculaciones);
    cq.select(root.get(Convocatoria_.id)).where(finalPredicate);

    Boolean returnValue = entityManager.createQuery(cq).getResultList().size() > 0;

    log.debug("esRegistradaConSolicitudesOProyectos(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene la Unidad de Gestión asignada a la {@link Convocatoria}.
   * 
   * @param id Id del {@link Convocatoria}.
   * @return unidadGestionRef asignada
   */
  @Override
  public Optional<String> getUnidadGestionRef(Long id) {
    log.debug("getUnidadGestionRef(Long id) - start");

    Optional<String> returnValue = Optional.empty();

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<String> cq = cb.createQuery(String.class);
    Root<Convocatoria> root = cq.from(Convocatoria.class);

    Predicate finalPredicate = cb.equal(root.get(Convocatoria_.id), id);
    cq.select(root.get(Convocatoria_.unidadGestionRef)).where(finalPredicate);

    returnValue = entityManager.createQuery(cq).getResultList().stream().findFirst();

    log.debug("getUnidadGestionRef(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene el {@link ModeloEjecucion} asignada a la {@link Convocatoria}.
   * 
   * @param id Id de la {@link Convocatoria}.
   * @return {@link ModeloEjecucion} asignado
   */
  public Optional<ModeloEjecucion> getModeloEjecucion(Long id) {
    log.debug("getModeloEjecucion(Long id) - start");

    Optional<ModeloEjecucion> returnValue = Optional.empty();

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<ModeloEjecucion> cq = cb.createQuery(ModeloEjecucion.class);
    Root<Convocatoria> root = cq.from(Convocatoria.class);

    Predicate finalPredicate = cb.equal(root.get(Convocatoria_.id), id);
    cq.select(root.get(Convocatoria_.modeloEjecucion)).where(finalPredicate);

    returnValue = entityManager.createQuery(cq).getResultList().stream().findFirst();

    log.debug("getModeloEjecucion(Long id) - end");
    return returnValue;
  }
}
