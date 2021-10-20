package org.crue.hercules.sgi.eti.service;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import org.crue.hercules.sgi.eti.config.SgiConfigProperties;
import org.crue.hercules.sgi.eti.dto.ApartadoOutput;
import org.crue.hercules.sgi.eti.dto.ElementOutput;
import org.crue.hercules.sgi.eti.util.I18nUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

/**
 * Servicio de utilidades para formularios Formly
 */
@Service
@Slf4j
public class SgiFormlyService {
  private final SgiConfigProperties sgiConfigProperties;

  public static final String P_HTML = "<p>";
  public static final String I_HTML = "<i>";
  public static final String P_CLOSE_HTML = "</p>";
  public static final String I_CLOSE_HTML = "</i>";
  public static final String BR_HTML = "<br/>";

  // FORMLY PROPERTIES
  public static final String TEMPLATE_PROPERTY = "template";
  public static final String TABLE_CRUD_TYPE = "table-crud";
  private static final String FIELD_GROUP_PROPERTY = "fieldGroup";
  private static final String FIELD_ARRAY_PROPERTY = "fieldArray";
  private static final String TEMPLATE_OPTIONS_PROPERTY = "templateOptions";
  private static final String OPTIONS_PROPERTY = "options";
  private static final String LABEL_PROPERTY = "label";
  private static final String VALUE_PROPERTY = "value";
  private static final String KEY_PROPERTY = "key";
  private static final String TYPE_PROPERTY = "type";
  private static final String DATEPICKER_TYPE = "datepicker";
  private static final String DATETIMEPICKER_TYPE = "dateTimePicker";
  private static final String VALOR_SOCIAL_TYPE = "tipo-valor-social";
  private static final String SELECT_TYPE = "select";
  private static final String RADIO_TYPE = "radio";
  private static final String CHECKBOX_TYPE = "checkbox";
  private static final String DOCUMENTO_TYPE = "documento";
  private static final String MULTICHECKBOX_TYPE = "multicheckbox";
  private static final String ANSWER_YES = "Sí";
  private static final String ANSWER_NO = "No";

  /**
   * Instancia un SgiFormlyService
   * 
   * @param sgiConfigProperties {link SgiConfigProperties}
   */
  public SgiFormlyService(SgiConfigProperties sgiConfigProperties) {
    this.sgiConfigProperties = sgiConfigProperties;
  }

  public void parseApartadoAndRespuesta(ApartadoOutput apartadoOutput) {
    try {
      String apartadoJson = apartadoOutput.getEsquema();
      String respuestaJson = null != apartadoOutput.getRespuesta() ? apartadoOutput.getRespuesta().getValor() : "";

      StringBuilder apartadoTitlePdf = new StringBuilder();

      String apartadoTitle = JsonPath.parse(apartadoJson)
          .read("$[0]." + TEMPLATE_OPTIONS_PROPERTY + "." + LABEL_PROPERTY).toString();
      apartadoTitlePdf.append(apartadoTitle);

      JSONArray fieldGroup = (JSONArray) JsonPath.parse(apartadoJson).read("$[0]." + FIELD_GROUP_PROPERTY);

      // Recorremos fieldGroup para obtener el texto de template y el valor de las
      // keys
      apartadoOutput.setElementos(new ArrayList<>());
      if (StringUtils.hasText(respuestaJson) && !"{}".equals(respuestaJson)) {
        evaluateFieldGroup(apartadoOutput.getElementos(), respuestaJson, fieldGroup);
      } else {
        log.debug(String.format("%d [%s] sin respuesta ", apartadoOutput.getId(), apartadoTitle));
      }

      apartadoOutput.setTitulo(apartadoTitle.toString());
    } catch (Exception e) {
      log.debug(apartadoOutput.getId().toString());
      log.error(e.getMessage());
      throw e;
    }
  }

  @SuppressWarnings("unchecked")
  private void evaluateFieldGroup(List<ElementOutput> elementos, String respuestaJson, JSONArray fieldGroup) {

    for (int i = 0; i < fieldGroup.size(); i++) {

      LinkedHashMap<String, Object> jsonFieldItem = (LinkedHashMap<String, Object>) fieldGroup.get(i);

      if (null != jsonFieldItem.get(TEMPLATE_PROPERTY)) {
        ElementOutput elementOutput = evaluateTemplateField(jsonFieldItem);
        if (null != elementOutput) {
          elementos.add(elementOutput);
        }

      } else if (null != jsonFieldItem.get(KEY_PROPERTY)) {
        if (StringUtils.hasText(respuestaJson) && !"{}".equals(respuestaJson)) {
          ElementOutput elementOutput = evaluateKeyField(respuestaJson, jsonFieldItem);
          if (null != elementOutput) {
            elementos.add(elementOutput);
          }
        } else {
          log.debug("Elemento sin respuesta");
        }
      } else if (null != jsonFieldItem.get(FIELD_GROUP_PROPERTY)) {
        evaluateFieldGroup(elementos, respuestaJson, (JSONArray) jsonFieldItem.get(FIELD_GROUP_PROPERTY));
      }
    }
  }

  private ElementOutput evaluateTemplateField(LinkedHashMap<String, Object> jsonFieldItem) {
    String templateText = jsonFieldItem.get(TEMPLATE_PROPERTY).toString() + BR_HTML + "\n";
    return ElementOutput.builder().content(templateText).tipo(TEMPLATE_PROPERTY).nombre("").build();
  }

  @SuppressWarnings("unchecked")
  private ElementOutput evaluateKeyField(String respuestaJson, LinkedHashMap<String, Object> jsonFieldItem) {
    ElementOutput elementOutput = null;

    StringBuilder apartadoTextPdf = new StringBuilder();
    String fieldKey = jsonFieldItem.get(KEY_PROPERTY).toString();
    String fieldType = jsonFieldItem.get(TYPE_PROPERTY).toString();
    if (!fieldType.equals(TABLE_CRUD_TYPE)) {
      if (null != jsonFieldItem.get(TEMPLATE_OPTIONS_PROPERTY)) {
        LinkedHashMap<String, Object> templateOptions = (LinkedHashMap<String, Object>) jsonFieldItem
            .get(TEMPLATE_OPTIONS_PROPERTY);
        if (null != templateOptions.get(LABEL_PROPERTY)) {
          apartadoTextPdf.append(P_HTML);
          String fieldLabel = templateOptions.get(LABEL_PROPERTY).toString().trim();
          if (fieldLabel.length() > 0) {
            fieldLabel += fieldLabel.charAt(fieldLabel.length() - 1) == ':' ? " " : ": ";
          }
          apartadoTextPdf.append(fieldLabel);

          // @formatter:off
          RespuestaEvaluateDto respuestaEvaluateDto = RespuestaEvaluateDto.builder()
            .jsonFieldItem(jsonFieldItem)
            .fieldTypeTableCrud(fieldType)
            .respuestaKeyField(fieldKey)
            .fieldKey(fieldKey)
            .respuestaJson(respuestaJson)
            .respuestaIndex(0)
            .build();
          // @formatter:on

          String respuestaFieldConfig = getRespuestaFromFormlyFieldType(respuestaEvaluateDto);

          if (null != respuestaFieldConfig) {
            apartadoTextPdf.append(I_HTML + respuestaFieldConfig.toString() + I_CLOSE_HTML);
          }
          apartadoTextPdf.append(P_CLOSE_HTML + BR_HTML + "\n");
        }

        elementOutput = ElementOutput.builder().content(apartadoTextPdf.toString()).tipo(fieldType).nombre(fieldKey)
            .build();
      }
    } else {
      String contentTableCrud = parseTableCrudField(respuestaJson, jsonFieldItem, fieldKey);
      if (StringUtils.hasLength(contentTableCrud)) {
        elementOutput = ElementOutput.builder().content(contentTableCrud).tipo(fieldType).nombre(fieldKey).build();
      }
    }

    return elementOutput;
  }

  @SuppressWarnings("unchecked")
  private String parseTableCrudField(String respuestaJson, LinkedHashMap<String, Object> jsonFieldItem,
      String fieldKey) {
    String contentTableCrud = "";
    if (null != jsonFieldItem.get(FIELD_ARRAY_PROPERTY)) {

      List<List<ElementOutput>> elementsTableCrud = new ArrayList<>();

      LinkedHashMap<String, Object> fieldArrayTableCrud = (LinkedHashMap<String, Object>) jsonFieldItem
          .get(FIELD_ARRAY_PROPERTY);

      JSONArray fieldGroupTableCrud = (JSONArray) fieldArrayTableCrud.get(FIELD_GROUP_PROPERTY);

      if (StringUtils.hasText(respuestaJson) && !"{}".equals(respuestaJson)) {
        JSONArray respuestasTableCrud = (JSONArray) JsonPath.parse(respuestaJson).read("$." + fieldKey);

        for (int respuestaIndex = 0; respuestaIndex < respuestasTableCrud.size(); respuestaIndex++) {
          List<ElementOutput> rowElementsTableCrud = new ArrayList<>();

          for (int i = 0; i < fieldGroupTableCrud.size(); i++) {

            LinkedHashMap<String, Object> jsonFieldItemTableCrud = (LinkedHashMap<String, Object>) fieldGroupTableCrud
                .get(i);

            String keyFieldTableCrud = jsonFieldItemTableCrud.get(KEY_PROPERTY).toString();
            String fieldTypeTableCrud = jsonFieldItemTableCrud.get(TYPE_PROPERTY).toString();

            LinkedHashMap<String, Object> templateOptionsTableCrud = (LinkedHashMap<String, Object>) jsonFieldItemTableCrud
                .get(TEMPLATE_OPTIONS_PROPERTY);

            String labelFieldTableCrud = templateOptionsTableCrud.get(LABEL_PROPERTY).toString();

          // @formatter:off
          RespuestaEvaluateDto respuestaEvaluateDto = RespuestaEvaluateDto.builder()
            .jsonFieldItem(jsonFieldItemTableCrud)
            .fieldTypeTableCrud(fieldTypeTableCrud)
            .respuestaKeyField(keyFieldTableCrud)
            .fieldKey(fieldKey)
            .respuestaJson(respuestaJson)
            .respuestaIndex(respuestaIndex)
            .build();
          // @formatter:on

            String respuestaFieldConfig = getRespuestaFromFormlyFieldType(respuestaEvaluateDto);

          // @formatter:off
          ElementOutput elementOutputTableCrud = ElementOutput.builder()
            .nombre(labelFieldTableCrud)
            .content(null != respuestaFieldConfig ? respuestaFieldConfig.toString() : "")
            .build();
          // @formatter:on
            rowElementsTableCrud.add(elementOutputTableCrud);
          }

          elementsTableCrud.add(rowElementsTableCrud);
        }
      }

      if (!elementsTableCrud.isEmpty()) {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
          final ObjectMapper mapper = new ObjectMapper();
          mapper.writeValue(out, elementsTableCrud);
          contentTableCrud = new String(out.toByteArray());
        } catch (Exception e) {
          log.error(e.getMessage());
        }
      }
    }
    return contentTableCrud;
  }

  private String getRespuestaFromFormlyFieldType(RespuestaEvaluateDto respuestaEvaluateDto) {

    String respuestaFieldConfig = getRespuestaStringByKey(respuestaEvaluateDto.getRespuestaKeyField(),
        respuestaEvaluateDto.getRespuestaJson(), respuestaEvaluateDto.getRespuestaIndex());
    switch (respuestaEvaluateDto.getFieldTypeTableCrud()) {
      case VALOR_SOCIAL_TYPE:
        if (StringUtils.hasText(respuestaFieldConfig)) {
          respuestaFieldConfig = TipoValorSocialI18n.getI18nMessageFromValorSocialEnum(respuestaFieldConfig);
        }
        break;
      case DATEPICKER_TYPE:
        String dtFormatDatePickerOut = "dd/MM/yyyy";
        respuestaFieldConfig = formatDatePicker(dtFormatDatePickerOut, respuestaFieldConfig);
        break;
      case DATETIMEPICKER_TYPE:
        String dtFormatDateTimePickerOut = "dd/MM/yyyy HH:mm:ss";
        respuestaFieldConfig = formatDatePicker(dtFormatDateTimePickerOut, respuestaFieldConfig);
        break;
      case MULTICHECKBOX_TYPE:
        List<String> respuestasFieldConfig = getRespuestaListByKey(respuestaEvaluateDto.getFieldKey(),
            respuestaEvaluateDto.getRespuestaJson(), respuestaEvaluateDto.getRespuestaIndex());
        respuestaFieldConfig = evaluateOptionListField(respuestaEvaluateDto.getJsonFieldItem(), respuestasFieldConfig);
        break;
      case SELECT_TYPE:
      case RADIO_TYPE:
      case DOCUMENTO_TYPE:
        if (null != respuestaFieldConfig) {
          respuestaFieldConfig = evaluateOptionListField(respuestaEvaluateDto.getJsonFieldItem(),
              Arrays.asList(respuestaFieldConfig));
        }
        break;
      case CHECKBOX_TYPE:
        if (null != respuestaFieldConfig) {
          respuestaFieldConfig = respuestaFieldConfig.equals("true") ? ANSWER_YES : ANSWER_NO;
        }
      default:
    }

    return respuestaFieldConfig;
  }

  private String formatDatePicker(String dtFormatOut, String datePicker) {
    String result = "";
    if (StringUtils.hasText(datePicker)) {
      try {
        DateFormat dfDateTimeIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        dfDateTimeIn.setTimeZone(sgiConfigProperties.getTimeZone());

        DateFormat dfDateTimeOut = new SimpleDateFormat(dtFormatOut);
        dfDateTimeOut.setTimeZone(sgiConfigProperties.getTimeZone());

        result = dfDateTimeOut.format(dfDateTimeIn.parse(datePicker));
      } catch (Exception e) {
        log.error(e.getMessage());
      }
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  private String evaluateOptionListField(LinkedHashMap<String, Object> jsonFieldItem,
      List<String> respuestasFieldConfig) {
    StringBuilder respuestaOptionList = new StringBuilder();
    if (!respuestasFieldConfig.isEmpty()) {
      for (String respuestaFieldConfig : respuestasFieldConfig) {
        LinkedHashMap<String, Object> templateOptions = (LinkedHashMap<String, Object>) jsonFieldItem
            .get(TEMPLATE_OPTIONS_PROPERTY);

        JSONArray options = (JSONArray) templateOptions.get(OPTIONS_PROPERTY);

        if (null != options && !options.isEmpty()) {
          for (int i = 0; i < options.size(); i++) {
            LinkedHashMap<String, Object> objectOptionList = (LinkedHashMap<String, Object>) options.get(i);
            if (objectOptionList.get(VALUE_PROPERTY).toString().equals(respuestaFieldConfig)) {
              respuestaOptionList.append(objectOptionList.get(LABEL_PROPERTY).toString());
              respuestaOptionList.append(", ");
            }
          }
        }
      }
      if (respuestaOptionList.length() > 2) {
        respuestaOptionList = respuestaOptionList.delete(respuestaOptionList.length() - 2,
            respuestaOptionList.length());
      }
    }
    return respuestaOptionList.toString();
  }

  private String getRespuestaStringByKey(String fieldKey, String respuestaJson, Integer respuestaIndex) {
    String respuestaValue = null;
    try {
      respuestaIndex = null != respuestaIndex && respuestaIndex.compareTo(0) >= 0 ? respuestaIndex : 0;
      JSONArray respuestaJsonArray = (JSONArray) JsonPath.parse(respuestaJson.toString()).read("$.." + fieldKey);
      respuestaValue = respuestaJsonArray.get(respuestaIndex).toString();

    } catch (Exception e) {
      log.debug(e.getMessage());
    }

    return respuestaValue;
  }

  private List<String> getRespuestaListByKey(String fieldKey, String respuestaJson, Integer respuestaIndex) {
    List<String> respuestasFieldConfig = new ArrayList<String>();
    try {
      respuestaIndex = null != respuestaIndex && respuestaIndex.compareTo(0) >= 0 ? respuestaIndex : 0;
      JSONArray respuestaJsonArray = (JSONArray) JsonPath.parse(respuestaJson.toString()).read("$.." + fieldKey);
      JSONArray respuestasJsonArray = (JSONArray) respuestaJsonArray.get(respuestaIndex);
      for (int i = 0; i < respuestasJsonArray.size(); i++) {
        respuestasFieldConfig.add((String) respuestasJsonArray.get(i));
      }

    } catch (Exception e) {
      log.debug(e.getMessage());
    }

    return respuestasFieldConfig;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class RespuestaEvaluateDto {
    private LinkedHashMap<String, Object> jsonFieldItem;
    private String fieldTypeTableCrud;
    private String respuestaKeyField;
    private String fieldKey;
    private String respuestaJson;
    private Integer respuestaIndex;
  }

  /** Tipo valor social */
  public enum TipoValorSocialI18n {
    /** INVESTIGACION_FUNDAMENTAL */
    INVESTIGACION_FUNDAMENTAL("enum.tipo-valor-social.INVESTIGACION_FUNDAMENTAL"),
    /** INVESTIGACION_PREVENCION */
    INVESTIGACION_PREVENCION("enum.tipo-valor-social.INVESTIGACION_PREVENCION"),
    /** INVESTIGACION_EVALUACIÓN */
    INVESTIGACION_EVALUACION("enum.tipo-valor-social.INVESTIGACION_EVALUACION"),
    /** INVESTIGACION_DESARROLLO */
    INVESTIGACION_DESARROLLO("enum.tipo-valor-social.INVESTIGACION_DESARROLLO"),
    /** INVESTIGACION_PROTECCION */
    INVESTIGACION_PROTECCION("enum.tipo-valor-social.INVESTIGACION_PROTECCION"),
    /** INVESTIGACION_BIENESTAR */
    INVESTIGACION_BIENESTAR("enum.tipo-valor-social.INVESTIGACION_BIENESTAR"),
    /** INVESTIGACION_CONSERVACION */
    INVESTIGACION_CONSERVACION("enum.tipo-valor-social.INVESTIGACION_CONSERVACION"),
    /** ENSEÑANZA_SUPERIOR */
    ENSENIANZA_SUPERIOR("enum.tipo-valor-social.ENSENIANZA_SUPERIOR"),
    /** INVESTIGACION_JURIDICA */
    INVESTIGACION_JURIDICA("enum.tipo-valor-social.INVESTIGACION_JURIDICA"),
    /** OTRA FINALIDAD */
    OTRA_FINALIDAD("enum.tipo-valor-social.OTRA_FINALIDAD");

    private final String i18nMessage;

    private TipoValorSocialI18n(String i18nMessage) {
      this.i18nMessage = i18nMessage;
    }

    public String getI18nMessage() {
      return this.i18nMessage;
    }

    public static String getI18nMessageFromValorSocialEnum(final String tipoValorSocial) {
      String message = "";
      if (StringUtils.hasText(tipoValorSocial)) {
        TipoValorSocialI18n tipoValorSocialBusq = Stream.of(TipoValorSocialI18n.values())
            .filter(tvs -> tipoValorSocial.equals(tvs.name())).findFirst().orElse(null);
        if (null != tipoValorSocialBusq) {
          message = I18nUtil.toLocale(tipoValorSocialBusq.i18nMessage);
        }
      }
      return message;
    }
  }

}