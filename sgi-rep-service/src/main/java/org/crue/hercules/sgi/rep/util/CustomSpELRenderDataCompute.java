package org.crue.hercules.sgi.rep.util;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.crue.hercules.sgi.framework.i18n.I18nConfig;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.rep.report.SgiReportHelper;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ReflectionUtils;

import com.deepoove.poi.render.compute.EnvModel;
import com.deepoove.poi.render.compute.RenderDataCompute;
import com.google.gson.internal.LinkedTreeMap;

/**
 * Adds support for a #root object for the SpEL expressions. Necessary to access
 * the root object from in side an iterable block {{?block}} {{/block}}
 */
public class CustomSpELRenderDataCompute implements RenderDataCompute {

  private static final String I18N_FIELD_LANG = "lang";
  private static final String I18N_FIELD_VALUE = "value";

  private final ExpressionParser parser;
  private final EvaluationContext context;
  private EvaluationContext envContext;
  private boolean isStrict;

  private final StandardEvaluationContext rootContext;
  private final Language requestedLang;
  private final List<Language> languagePriorities;

  public CustomSpELRenderDataCompute(
      Map<String, Object> rootModel, EnvModel model, boolean isStrict) {
    this.isStrict = isStrict;
    this.parser = new SpelExpressionParser();
    if (null != model.getEnv() && !model.getEnv().isEmpty()) {
      this.envContext = new StandardEvaluationContext(model.getEnv());
      ((StandardEvaluationContext) envContext).addPropertyAccessor(new NonStrictReadMapAccessor());
    }
    this.context = new StandardEvaluationContext(model.getRoot());
    ((StandardEvaluationContext) context).addPropertyAccessor(new NonStrictReadMapAccessor());

    this.rootContext = new StandardEvaluationContext(rootModel);
    rootContext.addPropertyAccessor(new NonStrictReadMapAccessor());

    registerHelperFunctions();
    this.requestedLang = SgiReportContextHolder.getLanguage();
    this.languagePriorities = I18nConfig.get().getLanguagePriorities();
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Object compute(String el) {
    Object resolved = innerCompute(el);
    if (resolved instanceof ArrayList) {
      boolean isLinkedTreeMap = ((ArrayList) resolved).stream().allMatch(LinkedTreeMap.class::isInstance);
      if (isLinkedTreeMap) {
        boolean isI18n = ((ArrayList) resolved).stream().allMatch(nested -> isI18nField((LinkedTreeMap) nested));
        if (isI18n) {
          Map<Language, String> collectedValues = collectValues((ArrayList<LinkedTreeMap>) resolved);
          if (collectedValues.containsKey(requestedLang)) {
            return collectedValues.get(requestedLang);
          } else {
            if (collectedValues.isEmpty()) {
              // Si no hay ningún valor, retornamos null
              return null;
            } else {
              String other = null;
              Iterator<Language> itPriority = this.languagePriorities.iterator();
              do {
                Language lang = itPriority.next();
                other = collectedValues.get(lang);
              } while (other == null && itPriority.hasNext());
              return other;
            }
          }
        }
      }
    }
    return resolved;
  }

  private Map<Language, String> collectValues(ArrayList<LinkedTreeMap> i18nField) {
    Map<Language, String> values = new HashMap<>(i18nField.size());
    try {
      for (LinkedTreeMap i18nValue : i18nField) {
        Language lang = null;
        String value = "";
        if (i18nValue.get(I18N_FIELD_LANG) instanceof String) {
          lang = Language.fromCode((String) i18nValue.get(I18N_FIELD_LANG));
        }
        if (i18nValue.get(I18N_FIELD_VALUE) instanceof String) {
          value = (String) i18nValue.get(I18N_FIELD_VALUE);
        }
        if (lang != null) {
          values.put(lang, value);
        }
      }
    } catch (ClassCastException e) {
      throw new RuntimeException("Error procesando expresion I18n", e);
    }
    return values;
  }

  private boolean isI18nField(LinkedTreeMap field) {
    if (field.containsKey(I18N_FIELD_LANG) && field.containsKey(I18N_FIELD_VALUE)) {
      return true;
    }
    return false;
  }

  private Object innerCompute(String el) {
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

  private Object parseExpressionIgnoreException(String el, EvaluationContext context) {
    Object val = null;
    try {
      val = parser.parseExpression(el).getValue(context);
    } catch (Exception e) {
      // ignore
    }

    return val;
  }

  private void registerHelperFunctions() {
    ReflectionUtils.doWithMethods(SgiReportHelper.class, m -> {
      ReflectionUtils.makeAccessible(m);
      ((StandardEvaluationContext) this.context).registerFunction(m.getName(), m);
      this.rootContext.registerFunction(m.getName(), m);

    },
        m -> Modifier.isPublic(m.getModifiers()) && Modifier.isStatic(m.getModifiers()));
  }

}