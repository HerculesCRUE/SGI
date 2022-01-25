package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.framework.data.jpa.repository.support.SgiJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {
    "org.crue.hercules.sgi.eti.repository" }, repositoryBaseClass = SgiJpaRepository.class)
public abstract class BaseRepositoryTest {

  @Autowired
  protected TestEntityManager entityManager;

}