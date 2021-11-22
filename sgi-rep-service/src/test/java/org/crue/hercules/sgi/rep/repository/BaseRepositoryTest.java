package org.crue.hercules.sgi.rep.repository;

import org.crue.hercules.sgi.framework.test.context.support.SgiTestProfileResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(resolver = SgiTestProfileResolver.class)
abstract class BaseRepositoryTest {

  @Autowired
  protected TestEntityManager entityManager;

}