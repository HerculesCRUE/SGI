package org.crue.hercules.sgi.framework.data.jpa.repository.support;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.data.jpa.domain.Activable;
import org.crue.hercules.sgi.framework.data.jpa.repository.support.SgiJpaRepositoryTest.TestConfig;
import org.crue.hercules.sgi.framework.data.jpa.repository.support.SgiJpaRepositoryTest.TestConfig.TestEntity;
import org.crue.hercules.sgi.framework.data.jpa.repository.support.SgiJpaRepositoryTest.TestConfig.TestEntityRepository;
import org.crue.hercules.sgi.framework.test.db.DbTestUtil;
import org.crue.hercules.sgi.framework.web.method.annotation.RequestPageableArgumentResolver.UnpagedPageable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ContextConfiguration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@DataJpaTest
@ContextConfiguration(classes = { TestConfig.class })
class SgiJpaRepositoryTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  protected TestEntityManager entityManager;

  @Autowired
  protected TestEntityRepository repository;

  @BeforeEach
  void setUp() throws SQLException {
    // We need to reset squence before each test (so we can assert with id field)
    DbTestUtil.resetSequence(applicationContext, "test_entity_seq");
  }

  @Test
  void create_sgiJpaRepository_fromDomainClass_andEntityManager() throws Exception {
    // given: the domainClass, the entityManager and a TestEntity
    Class<TestEntity> domainClass = TestEntity.class;
    EntityManager em = entityManager.getEntityManager();
    TestEntity testEntity = TestEntity.builder().name("Name 1").build();
    entityManager.persistAndFlush(testEntity);

    // when: create new SgiJpaRepository for the domainClass
    SgiJpaRepository<TestEntity, Long> sgiJpaRepository = new SgiJpaRepository<>(domainClass, em);

    // then: the repository can be used to fetch the TestEntity
    Assertions.assertThat(sgiJpaRepository).isNotNull();
    TestEntity entity = sgiJpaRepository.getOne(1l);
    Assertions.assertThat(entity).isNotNull().isEqualTo(TestEntity.builder().id(1l).name("Name 1").build());
  }

  @Test
  void refresh() throws Exception {
    // given: a TestEntity
    TestEntity testEntity = TestEntity.builder().name("Name 1").build();
    entityManager.persistAndFlush(testEntity);

    // when: get the entity using the repository, modify and same using the
    // enitityManger, then refresed using the repository
    SgiJpaRepository<TestEntity, Long> sgiJpaRepository = new SgiJpaRepository<>(TestEntity.class,
        entityManager.getEntityManager());
    TestEntity entity = sgiJpaRepository.getOne(1l);
    testEntity.setName("Modded name");
    entityManager.persistAndFlush(testEntity);
    sgiJpaRepository.refresh(entity);

    // then: the refresed entity contains the modded data
    Assertions.assertThat(entity).isNotNull().isEqualTo(TestEntity.builder().id(1l).name("Modded name").build());
  }

  @Test
  void findAll_unPagedWithSorting_returnsSorted() throws Exception {
    // given: fifteen TestEntities
    for (int i = 1; i <= 15; i++) {
      TestEntity testEntity = TestEntity.builder().name(String.format("Name %2d", i)).build();
      entityManager.persistAndFlush(testEntity);
    }

    // when: get unpaged
    Sort sort = Sort.by("name").descending();
    Pageable pageRequest = new UnpagedPageable(sort);
    Page<TestEntity> page = repository.findAll(pageRequest);

    // then: return all
    Assertions.assertThat(page).isNotNull();
    List<TestEntity> content = page.getContent();
    Assertions.assertThat(content).isNotNull();
    Assertions.assertThat(content.size()).as("size()").isEqualTo(15);
    for (int i = 0; i < 15; i++) {
      Long id = 15l - i;
      Assertions.assertThat(content.get(i))
          .isEqualTo(TestEntity.builder().id(id).name(String.format("Name %2d", id)).build());
    }
  }

  @Test
  void findAll_withPageable_returnsPage() throws Exception {
    // given: thirty TestEntities
    for (int i = 1; i <= 30; i++) {
      TestEntity testEntity = TestEntity.builder().name(String.format("Name %2d", i)).build();
      entityManager.persistAndFlush(testEntity);
    }

    // when: get second page of ten elements
    Pageable pageRequest = PageRequest.of(1, 10);
    Page<TestEntity> page = repository.findAll(pageRequest);

    // then: return the requested page
    Assertions.assertThat(page).isNotNull();
    List<TestEntity> content = page.getContent();
    Assertions.assertThat(content).isNotNull();
    Assertions.assertThat(content.size()).as("size()").isEqualTo(10);
    for (long i = 11; i <= 20; i++) {
      Assertions.assertThat(content).as("content().contains()")
          .contains(TestEntity.builder().id(i).name(String.format("Name %2d", i)).build());
    }
  }

  @Test
  void findAll_withoutPageable_returnsAll() throws Exception {
    // given: fifteen TestEntities
    for (int i = 1; i <= 15; i++) {
      TestEntity testEntity = TestEntity.builder().name(String.format("Name %2d", i)).build();
      entityManager.persistAndFlush(testEntity);
    }

    // when: get unpaged
    Pageable pageRequest = Pageable.unpaged();
    Page<TestEntity> page = repository.findAll(pageRequest);

    // then: return all
    Assertions.assertThat(page).isNotNull();
    List<TestEntity> content = page.getContent();
    Assertions.assertThat(content).isNotNull();
    Assertions.assertThat(content.size()).as("size()").isEqualTo(15);
    for (long i = 1; i <= 15; i++) {
      Assertions.assertThat(content).as("content().contains()")
          .contains(TestEntity.builder().id(i).name(String.format("Name %2d", i)).build());
    }
  }

  @Test
  void findAll_withSpec_returnsMatchingEntities() throws Exception {
    // given: thirty TestEntities
    for (int i = 1; i <= 30; i++) {
      TestEntity testEntity = TestEntity.builder().name(String.format("Name %2d", i)).build();
      entityManager.persistAndFlush(testEntity);
    }

    // when: get enities with name ending in "2"
    Specification<TestEntity> spec = (root, query, cb) -> {
      return cb.like(root.get("name"), "%2");
    };
    Pageable pageRequest = Pageable.unpaged();
    Page<TestEntity> page = repository.findAll(spec, pageRequest);

    // then: return the entities with name "Name 2", "Name 12" and "Name 22"
    Assertions.assertThat(page).isNotNull();
    List<TestEntity> content = page.getContent();
    Assertions.assertThat(content).isNotNull();
    Assertions.assertThat(content.size()).as("size()").isEqualTo(3);
    for (long i = 2; i <= 30; i += 10) {
      Assertions.assertThat(content).as("content().contains()")
          .contains(TestEntity.builder().id(i).name(String.format("Name %2d", i)).build());
    }
  }

  @Test
  void findAll_withExample_returnsMatchingEntities() throws Exception {
    // given: thirty TestEntities
    for (int i = 1; i <= 30; i++) {
      TestEntity testEntity = TestEntity.builder().name(String.format("Name %2d", i))
          .activo(i % 2 == 0 ? Boolean.TRUE : Boolean.FALSE).build();
      entityManager.persistAndFlush(testEntity);
    }

    // when: get enities with activo=FALSE
    Example<TestEntity> example = Example.of(TestEntity.builder().activo(Boolean.FALSE).build());
    Pageable pageRequest = Pageable.unpaged();
    Page<TestEntity> page = repository.findAll(example, pageRequest);

    // then: return the entities with activo=FALSE (half of total entities)
    Assertions.assertThat(page).isNotNull();
    List<TestEntity> content = page.getContent();
    Assertions.assertThat(content).isNotNull();
    Assertions.assertThat(content.size()).as("size()").isEqualTo(15);
    for (TestEntity entity : content) {
      Assertions.assertThat(entity.getActivo()).isFalse();
    }
  }

  @Test
  void findAll_withExample_andPage_returnsMatchingPage() throws Exception {
    // given: thirty TestEntities
    for (int i = 1; i <= 30; i++) {
      TestEntity testEntity = TestEntity.builder().name(String.format("Name %2d", i)).activo(Boolean.FALSE).build();
      entityManager.persistAndFlush(testEntity);
    }

    // when: get enities with activo=FALSE
    Example<TestEntity> example = Example.of(TestEntity.builder().activo(Boolean.FALSE).build());
    Pageable pageRequest = PageRequest.of(1, 10);
    Page<TestEntity> page = repository.findAll(example, pageRequest);

    // then: return the entities with activo=FALSE (half of total entities)
    Assertions.assertThat(page).isNotNull();
    List<TestEntity> content = page.getContent();
    Assertions.assertThat(content).isNotNull();
    Assertions.assertThat(content.size()).as("size()").isEqualTo(10);
    for (long i = 11; i <= 20; i++) {
      Assertions.assertThat(content).as("content().contains()")
          .contains(TestEntity.builder().id(i).name(String.format("Name %2d", i)).activo(Boolean.FALSE).build());
    }
  }

  /**
   * A nested @Configuration class wild be used instead of the application’s
   * primary configuration.
   * <p>
   * Unlike a nested @Configuration class, which would be used instead of your
   * application’s primary configuration, a nested @TestConfiguration class is
   * used in addition to your application’s primary configuration.
   */
  // Using full qualified class names (otherwise compilation fail with maven).
  // See: https://bugs.openjdk.java.net/browse/JDK-8056066
  @org.springframework.context.annotation.Configuration
  @org.springframework.data.jpa.repository.config.EnableJpaRepositories(considerNestedRepositories = true, basePackageClasses = TestEntityRepository.class, repositoryBaseClass = SgiJpaRepository.class)
  @org.springframework.boot.autoconfigure.domain.EntityScan(basePackageClasses = TestEntity.class)
  public static class TestConfig {

    @Repository
    public static interface TestEntityRepository
        extends JpaRepository<TestEntity, Long>, JpaSpecificationExecutor<TestEntity> {
    }

    @Entity
    @Table(name = "test_entity")
    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    public static class TestEntity extends Activable {
      /** Id */
      @Id
      @Column(name = "id", nullable = false)
      @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "test_entity_seq")
      @SequenceGenerator(name = "test_entity_seq", sequenceName = "test_entity_seq", allocationSize = 1)
      private Long id;

      @Column(name = "name", nullable = false)
      private String name;
    }
  }

}
