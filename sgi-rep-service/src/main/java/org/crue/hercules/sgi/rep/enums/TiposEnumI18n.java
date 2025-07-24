package org.crue.hercules.sgi.rep.enums;

import java.util.Locale;
import java.util.stream.Stream;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class TiposEnumI18n {

  public enum TipoConvocatoriaReunionI18n {
    /** Ordinaria <code>1L</code> */
    ORDINARIA(1L, "enum.tipo-convocatoria-reunion.ORDINARIA"),
    /** Extraordinaria <code>2L</code> */
    EXTRAORDINARIA(2L, "enum.tipo-convocatoria-reunion.EXTRAORDINARIA"),
    /** Seguimiento <code>3L</code> */
    SEGUIMIENTO(3L, "enum.tipo-convocatoria-reunion.SEGUIMIENTO");

    private final Long id;
    private final String i18nMessage;

    private TipoConvocatoriaReunionI18n(Long id, String i18nMessage) {
      this.id = id;
      this.i18nMessage = i18nMessage;
    }

    public Long getId() {
      return this.id;
    }

    public String getI18nMessage() {
      return this.i18nMessage;
    }

    public static String getI18nMessageFromEnum(final Long id) {
      String message = "";
      if (!StringUtils.isEmpty(id)) {
        TipoConvocatoriaReunionI18n tipoBusq = Stream.of(TipoConvocatoriaReunionI18n.values())
            .filter(tit -> id.equals(tit.getId())).findFirst().orElse(null);
        if (!ObjectUtils.isEmpty(tipoBusq)) {
          message = ApplicationContextSupport.getMessage(tipoBusq.i18nMessage);
        }
      }
      return message;
    }

    public static String getI18nMessageFromEnumAndLocale(final Long id, Locale locale) {
      String message = "";
      if (!StringUtils.isEmpty(id)) {
        TipoConvocatoriaReunionI18n tipoBusq = Stream.of(TipoConvocatoriaReunionI18n.values())
            .filter(ta -> id.equals(ta.getId())).findFirst().orElse(null);
        if (!ObjectUtils.isEmpty(tipoBusq)) {
          message = ApplicationContextSupport.getApplicationContext().getMessage(tipoBusq.i18nMessage, null, locale);
        }
      }
      return message;
    }
  }

}
