package org.crue.hercules.sgi.rep.report;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.util.SgiReportContextHolder;

import com.google.gson.internal.LinkedTreeMap;

/**
 * Helper to use inside templates
 */
public class SgiReportHelper {

  private SgiReportHelper() {

  }

  public static String formatDateNow(String pattern) {
    return formatDate(Instant.now(), pattern);
  }

  public static String formatDate(Instant instant, String pattern) {
    String result = "";

    if (null != instant) {
      DateTimeFormatter formatter;
      switch (pattern) {
        case "FULL":
          formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
          break;
        case "LONG":
          formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
          break;
        case "MEDIUM":
          formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
          break;
        case "SHORT":
          formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
          break;
        default:
          formatter = DateTimeFormatter.ofPattern(pattern);
          break;
      }
      formatter = formatter
          .withZone(SgiReportContextHolder.getTimeZone().toZoneId())
          .localizedBy(SgiReportContextHolder.getLanguage().getLocale());
      result = formatter.format(instant);
    }

    return result;
  }

  public static String formatTime(LocalTime time, String pattern) {
    String result = "";

    if (null != time) {
      DateTimeFormatter formatter;
      switch (pattern) {
        case "FULL":
          formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.FULL);
          break;
        case "SHORT":
          formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
          break;
        default:
          formatter = DateTimeFormatter.ofPattern(pattern);
          break;
      }
      formatter = formatter
          .withZone(SgiReportContextHolder.getTimeZone().toZoneId())
          .localizedBy(SgiReportContextHolder.getLanguage().getLocale());
      result = formatter.format(time);
    }

    return result;
  }

  public static String formatDuration(Duration duration, String pattern) {
    return DurationFormatUtils.formatDuration(duration.toMillis(), pattern);
  }

  public static String getMessage(String code) {
    return ApplicationContextSupport.getApplicationContext().getMessage(code, null,
        SgiReportContextHolder.getLanguage().getLocale());
  }

  public static String formatJsonDate(String value, String pattern) {
    Instant date = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(value, Instant::from);
    return formatDate(date, pattern);
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
}
