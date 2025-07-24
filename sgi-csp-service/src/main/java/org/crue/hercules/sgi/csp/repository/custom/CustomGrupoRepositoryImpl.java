package org.crue.hercules.sgi.csp.repository.custom;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoNombre;
import org.crue.hercules.sgi.csp.model.GrupoNombre_;
import org.crue.hercules.sgi.csp.model.Grupo_;
import org.crue.hercules.sgi.csp.repository.specification.GrupoSpecifications;
import org.crue.hercules.sgi.csp.util.CriteriaQueryUtils;
import org.crue.hercules.sgi.framework.spring.context.i18n.SgiLocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA custom repository para {@link Grupo}.
 */
@Slf4j
@Component
public class CustomGrupoRepositoryImpl implements CustomGrupoRepository {

  private static final String SELECTION_NAME_SEPARATOR = ".";
  private static final String SELECTION_NAME_NOMBRE = Grupo_.NOMBRE + SELECTION_NAME_SEPARATOR + GrupoNombre_.VALUE;

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Devuelve si grupoRef pertenece a un grupo de investigación con el campo
   * "Grupo especial de investigación" a "No" el 31 de diciembre del
   * año que se esta baremando
   *
   * @param grupoRef        grupoRef
   * @param fechaBaremacion fecha de baremación
   * @return true/false
   */
  @Override
  public boolean isGrupoBaremable(Long grupoRef, Instant fechaBaremacion) {
    log.debug("isGrupoBaremable(grupoRef, fechaBaremacion) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Grupo> root = cq.from(Grupo.class);

    Predicate predicateIsGrupoRef = cb.equal(root.get(Grupo_.id), grupoRef);

    Predicate predicateFinal = cb.and(predicateIsGrupoRef,
        GrupoSpecifications.isBaremable(fechaBaremacion).toPredicate(root, cq, cb));

    cq.select(root.get(Grupo_.id)).where(predicateFinal);

    log.debug("isGrupoBaremable(isGrupoBaremable, fechaBaremacion) - end");

    return !entityManager.createQuery(cq).getResultList().isEmpty();
  }

  /**
   * Obtiene los ids de {@link Grupo} que cumplen con la specification recibida.
   * 
   * @param specification condiciones que deben cumplir.
   * @return lista de ids de {@link Grupo}.
   */
  @Override
  public List<Long> findIds(Specification<Grupo> specification) {
    log.debug("findIds(Specification<Grupo> specification) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Grupo> root = cq.from(Grupo.class);

    cq.select(root.get(Grupo_.id)).distinct(true).where(specification.toPredicate(root, cq, cb));

    log.debug("findIds(Specification<Grupo> specification) - end");

    return entityManager.createQuery(cq).getResultList();
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Grupo} sin duplicados y
   * ordenable por el nombre.
   * 
   * @param specs    condiciones que deben cumplir.
   * @param pageable la información de la paginación.
   * @return la lista de {@link Grupo} paginadas y/o filtradas.
   */
  @Override
  public Page<Grupo> findAllDistinct(Specification<Grupo> specs, Pageable pageable) {
    log.debug("findAllDistinct(String query, Pageable pageable) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Tuple> cq = cb.createTupleQuery();

    // Define FROM clause
    Root<Grupo> root = cq.from(Grupo.class);

    // Si se ordena por el nombre del grupo se hace una subquery para obtener el
    // nombre en el idioma actual para poder hacer la ordenacion, si no se ordena
    // por el nombre no es necesaria la subquery
    boolean sortingByNombreGrupo = pageable.getSort().get()
        .anyMatch(sort -> sort.getProperty().equals(SELECTION_NAME_NOMBRE));

    Expression<String> nombreGrupoExpression;
    if (sortingByNombreGrupo) {
      Subquery<String> subqueryNombre = cq.subquery(String.class);
      Root<Grupo> subRoot = subqueryNombre.correlate(root);
      Join<Grupo, GrupoNombre> joinGrupoNombre = subRoot.join(Grupo_.nombre);

      subqueryNombre.select(joinGrupoNombre.get(GrupoNombre_.value))
          .where(cb.equal(joinGrupoNombre.get(GrupoNombre_.lang), SgiLocaleContextHolder.getLanguage()));

      nombreGrupoExpression = subqueryNombre;
    } else {
      nombreGrupoExpression = cb.literal("");
    }

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Grupo> rootCount = countQuery.from(Grupo.class);
    countQuery.select(cb.countDistinct(rootCount));

    List<Predicate> listPredicates = new ArrayList<>();
    List<Predicate> listPredicatesCount = new ArrayList<>();

    // Where
    if (specs != null) {
      listPredicates.add(specs.toPredicate(root, cq, cb));
      listPredicatesCount.add(specs.toPredicate(rootCount, cq, cb));
    }

    cq.where(listPredicates.toArray(new Predicate[] {}));

    cq.distinct(true).multiselect(
        root,
        nombreGrupoExpression.alias(SELECTION_NAME_NOMBRE));

    String[] selectionNames = new String[] {
        SELECTION_NAME_NOMBRE
    };

    cq.orderBy(CriteriaQueryUtils.toOrders(pageable.getSort(), root, cb, cq, selectionNames));

    // Número de registros totales para la paginación
    countQuery.where(listPredicatesCount.toArray(new Predicate[] {}));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<Tuple> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<Grupo> result = typedQuery.getResultList().stream().map(a -> (Grupo) a.get(0)).toList();
    Page<Grupo> returnValue = new PageImpl<>(result, pageable, count);

    log.debug("findAllDistinct(String query, Pageable pageable) - end");

    return returnValue;
  }

}
