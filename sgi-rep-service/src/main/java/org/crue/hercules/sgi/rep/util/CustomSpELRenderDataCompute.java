package org.crue.hercules.sgi.rep.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.deepoove.poi.render.compute.EnvModel;
import com.deepoove.poi.render.compute.RenderDataCompute;
import com.google.gson.internal.LinkedTreeMap;

import lombok.var;

/**
 * Adds support for a #root object for the SpEL expressions. Necessary to access
 * the root object from in side an iterable block {{?block}} {{/block}}
 */
public class CustomSpELRenderDataCompute implements RenderDataCompute {

  private static final String F_LINKED_TREE_MAP_EQUALS = "fLinkedTreeMapEquals";
  private static final String F_LINKED_TREE_MAP_IN = "fLinkedTreeMapIn";
  private static final String F_OBJECT_EQUALS = "fObjectEquals";

  private final ExpressionParser parser;
  private final EvaluationContext context;
  private EvaluationContext envContext;
  private boolean isStrict;

  // --
  private final StandardEvaluationContext rootContext;

  public CustomSpELRenderDataCompute(EnvModel model) throws NoSuchMethodException, SecurityException {
    this(model, true);
  }

  public CustomSpELRenderDataCompute(EnvModel model, boolean isStrict) throws NoSuchMethodException, SecurityException {
    this(model, model, isStrict, Collections.emptyMap());
  }

  public CustomSpELRenderDataCompute(Object rootModel, EnvModel model, boolean isStrict,
      Map<String, Method> spELFunction) throws NoSuchMethodException, SecurityException {
    this.isStrict = isStrict;
    this.parser = new SpelExpressionParser();
    if (null != model.getEnv() && !model.getEnv().isEmpty()) {
      this.envContext = new StandardEvaluationContext(model.getEnv());
      ((StandardEvaluationContext) envContext).addPropertyAccessor(new NonStrictReadMapAccessor());
    }
    this.context = new StandardEvaluationContext(model.getRoot());
    ((StandardEvaluationContext) context).addPropertyAccessor(new NonStrictReadMapAccessor());
    spELFunction.forEach(((StandardEvaluationContext) context)::registerFunction);

    registerFunction((StandardEvaluationContext) context, F_LINKED_TREE_MAP_EQUALS, String.class, String.class);
    registerFunction((StandardEvaluationContext) context, F_LINKED_TREE_MAP_IN, String.class, String.class);
    registerFunction((StandardEvaluationContext) context, F_OBJECT_EQUALS, String.class, String.class);

    final var propertyAccessor = new NonStrictReadMapAccessor();

    this.rootContext = new StandardEvaluationContext(rootModel);
    rootContext.addPropertyAccessor(propertyAccessor);
    spELFunction.forEach(rootContext::registerFunction);
    context.getPropertyAccessors().remove(0);
    ((StandardEvaluationContext) context).addPropertyAccessor(propertyAccessor);

    registerFunction(rootContext, F_LINKED_TREE_MAP_EQUALS, String.class, String.class);
    registerFunction(rootContext, F_LINKED_TREE_MAP_IN, String.class, String.class);
    registerFunction(rootContext, F_OBJECT_EQUALS, String.class, String.class);

  }

  @Override
  public Object compute(String el) {
    try {
      while (el.contains("#currentContext.get(")) {
        Object value = parser
            .parseExpression(el.replaceFirst(".*(#currentContext.get\\()([\\w]*)(\\)).*", "$2")).getValue(context);
        String valueString = value != null ? value.toString() : "";
        el = el.replaceFirst("(#currentContext.get\\()([\\w]*)(\\))", valueString);
      }

      while (el.contains("#rootContext.get(")) {
        String value = (String) parser
            .parseExpression(el.replaceFirst(".*(#rootContext.get\\()([\\w]*)(\\)).*", "$2")).getValue(rootContext);
        el = el.replaceFirst("(#rootContext.get\\()([\\w]*)(\\))", value);
      }

      if (el.contains("#root")) {
        return parser.parseExpression(el.replace("#root.", "")).getValue(rootContext);
      }

      if (el.contains("!#root")) {
        return parser.parseExpression(el.replace("#root.", "!")).getValue(rootContext);
      }

      if (null != envContext && !el.contains("#this")) {
        Object val = parseExpressionIgnoreException(el, envContext);
        if (null != val) {
          return val;
        }
      }
      return parser.parseExpression(el).getValue(context);
    } catch (Exception e) {
      if (isStrict)
        throw e;
      return null;
    }
  }

  public static Predicate<LinkedTreeMap<Object, Object>> fLinkedTreeMapEquals(String property, String value) {
    return s -> {
      if (s == null) {
        return false;
      }
      return s.get(property) != null && s.get(property).toString().equals(value);
    };
  }

  public static Predicate<Object> fObjectEquals(String methodName, String value) {
    return s -> {
      if (s == null) {
        return false;
      }

      Object objectValue = null;

      try {
        objectValue = s.getClass().getMethod(methodName).invoke(s);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
          | SecurityException e) {
        return false;
      }

      return objectValue != null && objectValue.toString().equals(value);
    };
  }

  public static Predicate<LinkedTreeMap<Object, Object>> fLinkedTreeMapIn(String property,
      String values) {
    return s -> {
      if (s == null || StringUtils.isEmpty(values)) {
        return false;
      }

      List<String> valuesList = Arrays.asList(values.replace("[", "").replace("]", "").split(","));
      return valuesList.stream().map(String::trim)
          .anyMatch(value -> s.get(property) != null && s.get(property).toString().equals(value));
    };
  }

  private void registerFunction(StandardEvaluationContext context, String methodName, Class<?>... parameterTypes)
      throws NoSuchMethodException, SecurityException {
    context.registerFunction(methodName, CustomSpELRenderDataCompute.class.getMethod(methodName, parameterTypes));
  }

  private Object parseExpressionIgnoreException(String el, EvaluationContext context) {
    Object val = null;
    try {
      val = parser.parseExpression(el).getValue(context);
    } catch (Exception e) {
      // ignore
    }

    return val;
  }

}