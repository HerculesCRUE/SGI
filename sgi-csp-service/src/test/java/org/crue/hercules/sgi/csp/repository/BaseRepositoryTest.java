package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.framework.test.context.support.SgiTestProfileResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(resolver = SgiTestProfileResolver.class)
public class BaseRepositoryTest {

  @Autowired
  protected TestEntityManager entityManager;

}