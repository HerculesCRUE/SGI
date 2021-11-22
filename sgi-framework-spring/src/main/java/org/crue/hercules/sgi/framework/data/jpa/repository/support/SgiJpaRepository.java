package org.crue.hercules.sgi.framework.data.jpa.repository.support;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation of the
 * {@link org.springframework.data.repository.CrudRepository} interface. This
 * will offer you a more sophisticated interface than the plain
 * {@link EntityManager} .
 */
@Slf4j
public class SgiJpaRepository<T, I> extends SimpleJpaRepository<T, I> implements SgiRepository<T, I> {

  private EscapeCharacter escapeCharacter = EscapeCharacter.DEFAULT;
  private final EntityManager em;

  /**
   * Creates a new {@link SgiJpaRepository} to manage objects of the given
   * {@link JpaEntityInformation}.
   *
   * @param entityInformation must not be {@literal null}.
   * @param entityManager     must not be {@literal null}.
   */
  public SgiJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
    super(entityInformation, entityManager);
    log.debug("SgiJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) - start");
    this.em = entityManager;
    log.debug("SgiJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) - end");
  }

  /**
   * Creates a new {@link SgiJpaRepository} to manage objects of the given domain
   * type.
   *
   * @param domainClass must not be {@literal null}.
   * @param em          must not be {@literal null}.
   */
  public SgiJpaRepository(Class<T> domainClass, EntityManager em) {
    super(domainClass, em);
    log.debug("SgiJpaRepository(Class<T> domainClass, EntityManager em) - start");
    this.em = em;
    log.debug("SgiJpaRepository(Class<T> domainClass, EntityManager em) - end");
  }

  /**
   * Configures the {@link EscapeCharacter} to be used with the repository.
   *
   * @param escapeCharacter Must not be {@literal null}.
   */
  @Override
  public void setEscapeCharacter(EscapeCharacter escapeCharacter) {
    log.debug("setEscapeCharacter(EscapeCharacter escapeCharacter) - start");
    this.escapeCharacter = escapeCharacter;
    super.setEscapeCharacter(escapeCharacter);
    log.debug("setEscapeCharacter(EscapeCharacter escapeCharacter) - end");
  }

  /**
   * Returns a {@link Page} of entities meeting the paging restriction provided in
   * the {@code Pageable} object.
   *
   * @param pageable the Pageable
   * @return a page of entities
   */
  @Override
  public Page<T> findAll(Pageable pageable) {
    log.debug("findAll(Pageable pageable) - start");
    if (isUnpagedAndUnsorted(pageable)) {
      log.info("Not paged or sorted");
      Page<T> returnValue = new PageImpl<>(findAll());
      log.debug("findAll(Pageable pageable) - end");
      return returnValue;
    }

    Page<T> returnValue = findAll((Specification<T>) null, pageable);
    log.debug("findAll(Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Returns a {@link Page} of entities matching the given {@link Specification}.
   *
   * @param spec     can be {@literal null}.
   * @param pageable must not be {@literal null}.
   * @return never {@literal null}.
   */
  @Override
  public Page<T> findAll(@Nullable Specification<T> spec, Pageable pageable) {
    log.debug("findAll(@Nullable Specification<T> spec, Pageable pageable) - start");
    TypedQuery<T> query = getQuery(spec, pageable);
    Page<T> returnValue = isUnpagedAndUnsorted(pageable) ? new PageImpl<>(query.getResultList())
        : readPage(query, getDomainClass(), pageable, spec);
    log.debug("findAll(@Nullable Specification<T> spec, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Returns a {@link Page} of entities matching the given {@link Example}. In
   * case no match could be found, an empty {@link Page} is returned.
   *
   * @param example  must not be {@literal null}.
   * @param pageable can be {@literal null}.
   * @return a {@link Page} of entities matching the given {@link Example}.
   */
  @Override
  public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
    log.debug("findAll(Example<S> example, Pageable pageable) - start");
    ExampleSpecification<S> spec = new ExampleSpecification<>(example, escapeCharacter);
    Class<S> probeType = example.getProbeType();
    TypedQuery<S> query = getQuery(new ExampleSpecification<>(example, escapeCharacter), probeType, pageable);

    Page<S> returnValue = isUnpagedAndUnsorted(pageable) ? new PageImpl<>(query.getResultList())
        : readPage(query, probeType, pageable, spec);
    log.debug("findAll(Example<S> example, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Bring EntityManager.refresh to all of Spring Data repositories.
   * <p>
   * The refresh method refreshes the state of an instance from the database, and
   * overwrites the copy held by the EntityManager. This ensures the EntityManager
   * has the most up to date version of the data.
   * 
   * @param t the instance to be refreshed
   */
  @Override
  public void refresh(T t) {
    em.refresh(t);
  }

  /**
   * Creates a new {@link TypedQuery} from the given {@link Specification}.
   *
   * @param spec     can be {@literal null}.
   * @param pageable must not be {@literal null}.
   * @return TypedQuery
   */
  @Override
  protected TypedQuery<T> getQuery(@Nullable Specification<T> spec, Pageable pageable) {
    log.debug("getQuery(@Nullable Specification<T> spec, Pageable pageable) - start");
    Sort sort = pageable.getSort();
    TypedQuery<T> returnValue = getQuery(spec, getDomainClass(), sort);
    log.debug("getQuery(@Nullable Specification<T> spec, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Creates a new {@link TypedQuery} from the given {@link Specification}.
   *
   * @param spec        can be {@literal null}.
   * @param domainClass must not be {@literal null}.
   * @param pageable    must not be {@literal null}.
   * @return TypedQuery
   */
  @Override
  protected <S extends T> TypedQuery<S> getQuery(@Nullable Specification<S> spec, Class<S> domainClass,
      Pageable pageable) {
    log.debug("getQuery(@Nullable Specification<S> spec, Class<S> domainClass, Pageable pageable) - start");
    Sort sort = pageable.getSort();
    TypedQuery<S> returnValue = getQuery(spec, domainClass, sort);
    log.debug("getQuery(@Nullable Specification<S> spec, Class<S> domainClass, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * @param pageable
   * @return boolean
   */
  private static boolean isUnpagedAndUnsorted(Pageable pageable) {
    log.debug("isUnpagedAndUnsorted(Pageable pageable) - start");
    boolean returnValue = pageable.isUnpaged() && !pageable.getSort().isSorted();
    log.debug("isUnpagedAndUnsorted(Pageable pageable) - end");
    return returnValue;
  }

  /**
   * {@link Specification} that gives access to the {@link Predicate} instance
   * representing the values contained in the {@link Example}.
   *
   * @author Christoph Strobl
   * @since 1.10
   * @param <T> the Type
   */
  private static class ExampleSpecification<T> implements Specification<T> {

    private static final long serialVersionUID = 1L;

    private final transient Example<T> example;
    private final transient EscapeCharacter escapeCharacter;

    /**
     * Creates new {@link ExampleSpecification}.
     *
     * @param example
     * @param escapeCharacter
     */
    ExampleSpecification(Example<T> example, EscapeCharacter escapeCharacter) {
      log.debug("ExampleSpecification(Example<T> example, EscapeCharacter escapeCharacter) - start");
      Assert.notNull(example, "Example must not be null!");
      Assert.notNull(escapeCharacter, "EscapeCharacter must not be null!");

      this.example = example;
      this.escapeCharacter = escapeCharacter;
      log.debug("ExampleSpecification(Example<T> example, EscapeCharacter escapeCharacter) - end");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.jpa.domain.Specification#toPredicate(javax.
     * persistence.criteria.Root, javax.persistence.criteria.CriteriaQuery,
     * javax.persistence.criteria.CriteriaBuilder)
     */
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
      log.debug("toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) - start");
      Predicate returnValue = QueryByExamplePredicateBuilder.getPredicate(root, cb, example, escapeCharacter);
      log.debug("toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) - end");
      return returnValue;
    }
  }
}