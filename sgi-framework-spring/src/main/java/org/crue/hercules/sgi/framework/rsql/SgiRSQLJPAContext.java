package org.crue.hercules.sgi.framework.rsql;

import javax.persistence.criteria.Path;
import javax.persistence.metamodel.Attribute;

import lombok.Value;

/**
 * RSQL JPA Context used by RSQL JPA Predicate Converter
 */
@Value(staticConstructor = "of")
public class SgiRSQLJPAContext {

  private Path<?> path;
  private Attribute<?, ?> attribute;
  private SgiI18nJoinHolder<?, ?> i18nHolder;
}
