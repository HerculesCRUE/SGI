package org.crue.hercules.sgi.prc.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

abstract class BaseRepositoryTest {

  @Autowired
  protected TestEntityManager entityManager;

}