package org.crue.hercules.sgi.rep.util;

import java.util.Map;

import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;
import org.springframework.util.Assert;

import com.deepoove.poi.render.compute.ReadMapAccessor;

import lombok.var;

/*
 * Ignores non-existing map entries and just returns null.
 * This is necessary to allow empty entries to be handled with the null-safe operator to show a default value instead.
 */
public class NonStrictReadMapAccessor extends ReadMapAccessor {
  @Override
  public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
    return true;
  }

  @Override
  public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
    Assert.state(target instanceof Map, "Target must be of type Map");

    final var map = (Map<?, ?>) target;
    final var value = map.get(name);

    return new TypedValue(value);
  }
}