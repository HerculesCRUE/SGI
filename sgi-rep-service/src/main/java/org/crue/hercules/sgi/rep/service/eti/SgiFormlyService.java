package org.crue.hercules.sgi.rep.service.eti;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.ApartadoOutput;
import org.crue.hercules.sgi.rep.dto.eti.ElementOutput;
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
  private static final String COMMENT_TYPE = "comment";
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

  public void parseApartadoAndRespuestaAndComentarios(ApartadoOutput apartadoOutput) {
    try {
      String apartadoJson = apartadoOutput.getEsquema();
      String respuestaJson = null != apartadoOutput.getRespuesta() ? apartadoOutput.getRespuesta().getValor() : "";

      StringBuilder apartadoTitlePdf = new StringBuilder();

      String apartadoTitle = JsonPath.parse(apartadoJson)
          .read("$[0]." + TEMPLATE_OPTIONS_PROPERTY + "." + LABEL_PROPERTY).toString();
      apartadoTitlePdf.append(apartadoTitle);

      JSONArray fieldGroup = (JSONArray) JsonPath.parse(apartadoJson).read("$[0]." + FIELD_GROUP_PROPERTY);

      apartadoOutput.setElementos(new ArrayList<>());

      parseComentarios(apartadoOutput);

      if (null != apartadoOutput.getMostrarContenidoApartado() && apartadoOutput.getMostrarContenidoApartado()) {
        evaluateFieldGroup(apartadoOutput.getElementos(), respuestaJson, fieldGroup);
      } else {
        log.debug(String.format("%d [%s] no mostrarContenidoApartado ", apartadoOutput.getId(), apartadoTitle));
      }
      apartadoOutput.setTitulo(apartadoTitle);
    } catch (Exception e) {
      log.error(String.format("Error en apartado %d. %s", apartadoOutput.getId(), e.getMessage()));
      throw e;
    }
  }

  private void parseComentarios(ApartadoOutput apartadoOutput) {
    if (null != apartadoOutput.getComentarios() && !apartadoOutput.getComentarios().isEmpty()) {
      apartadoOutput.getComentarios().stream().forEach(c -> apartadoOutput.getElementos()
          .add(ElementOutput.builder().content(c.getTexto()).tipo(COMMENT_TYPE).nombre("").build()));
    }
  }

  @SuppressWarnings("unchecked")
  private void evaluateFieldGroup(List<ElementOutput> elementos, String respuestaJson, JSONArray fieldGroup) {

    for (int i = 0; i < fieldGroup.size(); i++) {
      LinkedHashMap<String, Object> jsonFieldItem = (LinkedHashMap<String, Object>) fieldGroup.get(i);

      if (null != jsonFieldItem.get(TEMPLATE_PROPERTY)) {
        evaluateFieldGroupTemplateProperty(elementos, jsonFieldItem);
      } else if (null != jsonFieldItem.get(KEY_PROPERTY)) {
        evaluateFieldGroupKeyProperty(elementos, respuestaJson, jsonFieldItem);
      } else if (null != jsonFieldItem.get(FIELD_GROUP_PROPERTY)) {
        evaluateFieldGroup(elementos, respuestaJson, (JSONArray) jsonFieldItem.get(FIELD_GROUP_PROPERTY));
      }
    }
  }

  private void evaluateFieldGroupTemplateProperty(List<ElementOutput> elementos,
      LinkedHashMap<String, Object> jsonFieldItem) {
    ElementOutput elementOutput = evaluateTemplateField(jsonFieldItem);
    if (null != elementOutput) {
      elementos.add(elementOutput);
    }
  }

  private void evaluateFieldGroupKeyProperty(List<ElementOutput> elementos, String respuestaJson,
      LinkedHashMap<String, Object> jsonFieldItem) {
    if (StringUtils.hasText(respuestaJson) && !"{}".equals(respuestaJson)) {
      ElementOutput elementOutput = evaluateKeyField(respuestaJson, jsonFieldItem);
      if (null != elementOutput) {
        elementos.add(elementOutput);
      }
    } else {
      log.debug("Elemento sin respuesta");
    }
  }

  private ElementOutput evaluateTemplateField(LinkedHashMap<String, Object> jsonFieldItem) {
    String templateText = jsonFieldItem.get(TEMPLATE_PROPERTY).toString() + BR_HTML + "\n";
    return ElementOutput.builder().content(templateText).tipo(TEMPLATE_PROPERTY).nombre("").build();
  }

  private ElementOutput evaluateKeyField(String respuestaJson, LinkedHashMap<String, Object> jsonFieldItem) {
    ElementOutput elementOutput = null;

    String fieldKey = jsonFieldItem.get(KEY_PROPERTY).toString();
    String fieldType = jsonFieldItem.get(TYPE_PROPERTY).toString();

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

    if (!fieldType.equals(TABLE_CRUD_TYPE)) {
      elementOutput = evaluateNotTableCrudKeyField(respuestaEvaluateDto);
    } else {
      elementOutput = evaluateTableCrudKeyField(respuestaEvaluateDto);
    }

    return elementOutput;
  }

  private ElementOutput evaluateTableCrudKeyField(RespuestaEvaluateDto respuestaEvaluateDto) {
    ElementOutput elementOutput = null;
    String fieldKey = respuestaEvaluateDto.getFieldKey();
    String fieldType = respuestaEvaluateDto.getFieldTypeTableCrud();
    String contentTableCrud = parseTableCrudField(respuestaEvaluateDto);
    if (StringUtils.hasText(contentTableCrud)) {
      elementOutput = ElementOutput.builder().content(contentTableCrud).tipo(fieldType).nombre(fieldKey).build();
    }
    return elementOutput;
  }

  @SuppressWarnings("unchecked")
  private ElementOutput evaluateNotTableCrudKeyField(RespuestaEvaluateDto respuestaEvaluateDto) {
    ElementOutput elementOutput = null;

    String fieldKey = respuestaEvaluateDto.getFieldKey();
    String fieldType = respuestaEvaluateDto.getFieldTypeTableCrud();
    LinkedHashMap<String, Object> jsonFieldItem = respuestaEvaluateDto.getJsonFieldItem();
    if (null != jsonFieldItem.get(TEMPLATE_OPTIONS_PROPERTY)) {
      LinkedHashMap<String, Object> templateOptions = (LinkedHashMap<String, Object>) jsonFieldItem
          .get(TEMPLATE_OPTIONS_PROPERTY);
      String apartadoTextPdf = evaluateNotTableCrudKeyFieldLabelProperty(respuestaEvaluateDto, templateOptions);

      elementOutput = ElementOutput.builder().content(apartadoTextPdf).tipo(fieldType).nombre(fieldKey).build();
    }
    return elementOutput;
  }

  private String evaluateNotTableCrudKeyFieldLabelProperty(RespuestaEvaluateDto respuestaEvaluateDto,
      LinkedHashMap<String, Object> templateOptions) {
    StringBuilder apartadoTextPdf = new StringBuilder();

    if (null != templateOptions.get(LABEL_PROPERTY)) {
      apartadoTextPdf.append(P_HTML);
      String fieldLabel = templateOptions.get(LABEL_PROPERTY).toString().trim();
      if (fieldLabel.length() > 0) {
        fieldLabel += fieldLabel.charAt(fieldLabel.length() - 1) == ':' ? " " : ": ";
      }
      apartadoTextPdf.append(fieldLabel);

      String respuestaFieldConfig = getRespuestaFromFormlyFieldType(respuestaEvaluateDto);

      if (null != respuestaFieldConfig) {
        apartadoTextPdf.append(I_HTML + respuestaFieldConfig + I_CLOSE_HTML);
      }
      apartadoTextPdf.append(P_CLOSE_HTML + BR_HTML + "\n");
    }

    return apartadoTextPdf.toString();
  }

  @SuppressWarnings("unchecked")
  private String parseTableCrudField(RespuestaEvaluateDto respuestaEvaluateDto) {
    String contentTableCrud = "";
    if (null != respuestaEvaluateDto.getJsonFieldItem().get(FIELD_ARRAY_PROPERTY)) {

      List<List<ElementOutput>> elementsTableCrud = new ArrayList<>();

      LinkedHashMap<String, Object> fieldArrayTableCrud = (LinkedHashMap<String, Object>) respuestaEvaluateDto
          .getJsonFieldItem().get(FIELD_ARRAY_PROPERTY);

      JSONArray fieldGroupTableCrud = (JSONArray) fieldArrayTableCrud.get(FIELD_GROUP_PROPERTY);
      String respuestaJson = respuestaEvaluateDto.getRespuestaJson();
      String fieldKey = respuestaEvaluateDto.getFieldKey();
      if (StringUtils.hasText(respuestaJson) && !"{}".equals(respuestaJson)) {
        JSONArray respuestasTableCrud = (JSONArray) JsonPath.parse(respuestaJson).read("$." + fieldKey);

        for (int respuestaIndex = 0; respuestaIndex < respuestasTableCrud.size(); respuestaIndex++) {
          List<ElementOutput> rowElementsTableCrud = parseRowTableCrudField(respuestaEvaluateDto, fieldGroupTableCrud,
              respuestaIndex);

          elementsTableCrud.add(rowElementsTableCrud);
        }
      }

      contentTableCrud = serializeElementsTableCrud(elementsTableCrud);
    }
    return contentTableCrud;
  }

  @SuppressWarnings("unchecked")
  private List<ElementOutput> parseRowTableCrudField(RespuestaEvaluateDto respuestaEvaluateDto,
      JSONArray fieldGroupTableCrud, int respuestaIndex) {

    List<ElementOutput> rowElementsTableCrud = new ArrayList<>();
    String respuestaJson = respuestaEvaluateDto.getRespuestaJson();
    String fieldKey = respuestaEvaluateDto.getFieldKey();

    for (int i = 0; i < fieldGroupTableCrud.size(); i++) {

      LinkedHashMap<String, Object> jsonFieldItemTableCrud = (LinkedHashMap<String, Object>) fieldGroupTableCrud.get(i);

      String keyFieldTableCrud = jsonFieldItemTableCrud.get(KEY_PROPERTY).toString();
      String fieldTypeTableCrud = jsonFieldItemTableCrud.get(TYPE_PROPERTY).toString();

      LinkedHashMap<String, Object> templateOptionsTableCrud = (LinkedHashMap<String, Object>) jsonFieldItemTableCrud
          .get(TEMPLATE_OPTIONS_PROPERTY);

      String labelFieldTableCrud = templateOptionsTableCrud.get(LABEL_PROPERTY).toString();

      // @formatter:off
      RespuestaEvaluateDto respuestaEvaluateTableCrudDto = RespuestaEvaluateDto.builder()
        .jsonFieldItem(jsonFieldItemTableCrud)
        .fieldTypeTableCrud(fieldTypeTableCrud)
        .respuestaKeyField(keyFieldTableCrud)
        .fieldKey(fieldKey)
        .respuestaJson(respuestaJson)
        .respuestaIndex(respuestaIndex)
        .build();
      // @formatter:on

      String respuestaFieldConfig = getRespuestaFromFormlyFieldType(respuestaEvaluateTableCrudDto);

      // @formatter:off
      ElementOutput elementOutputTableCrud = ElementOutput.builder()
        .nombre(labelFieldTableCrud)
        .content(null != respuestaFieldConfig ? respuestaFieldConfig : "")
        .build();
      // @formatter:on
      rowElementsTableCrud.add(elementOutputTableCrud);
    }
    return rowElementsTableCrud;
  }

  private String serializeElementsTableCrud(List<List<ElementOutput>> elementsTableCrud) {
    String contentTableCrud = "";
    if (!elementsTableCrud.isEmpty()) {
      try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, elementsTableCrud);
        contentTableCrud = new String(out.toByteArray());
      } catch (Exception e) {
        log.error(e.getMessage());
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
        respuestaFieldConfig = formatDatePickerCustom(dtFormatDatePickerOut, respuestaFieldConfig,
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
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
        break;
      default:
        log.debug("respuestaFieldConfig generic");
    }

    return respuestaFieldConfig;
  }

  private String formatDatePickerCustom(String dtFormatOut, String datePicker, String dtFormatIn) {
    String result = "";
    if (StringUtils.hasText(datePicker)) {
      try {
        dtFormatIn = StringUtils.hasText(dtFormatIn) ? dtFormatIn : "yyyy-MM-dd'T'HH:mm:ss.SSS";
        DateTimeFormatter dfDateTimeIn = DateTimeFormatter.ofPattern(dtFormatIn);
        dfDateTimeIn.withZone(sgiConfigProperties.getTimeZone().toZoneId());

        DateTimeFormatter dfDateTimeOut = DateTimeFormatter.ofPattern(dtFormatOut);
        dfDateTimeOut.withZone(sgiConfigProperties.getTimeZone().toZoneId());

        result = dfDateTimeOut.format(dfDateTimeIn.parse(datePicker));
      } catch (Exception e) {
        log.error(e.getMessage());
      }
    }
    return result;
  }

  private String formatDatePicker(String dtFormatOut, String datePicker) {
    return formatDatePickerCustom(dtFormatOut, datePicker, null);
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

        parseOPtionsListField(respuestaOptionList, respuestaFieldConfig, options);
      }
      if (respuestaOptionList.length() > 2) {
        respuestaOptionList = respuestaOptionList.delete(respuestaOptionList.length() - 2,
            respuestaOptionList.length());
      }
    }
    return respuestaOptionList.toString();
  }

  @SuppressWarnings("unchecked")
  private void parseOPtionsListField(StringBuilder respuestaOptionList, String respuestaFieldConfig,
      JSONArray options) {
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

  private String getRespuestaStringByKey(String fieldKey, String respuestaJson, Integer respuestaIndex) {
    String respuestaValue = null;
    try {
      respuestaIndex = null != respuestaIndex && respuestaIndex.compareTo(0) >= 0 ? respuestaIndex : 0;
      JSONArray respuestaJsonArray = (JSONArray) JsonPath.parse(respuestaJson).read("$.." + fieldKey);
      respuestaValue = respuestaJsonArray.get(respuestaIndex).toString();

    } catch (Exception e) {
      log.debug(e.getMessage());
    }

    return respuestaValue;
  }

  private List<String> getRespuestaListByKey(String fieldKey, String respuestaJson, Integer respuestaIndex) {
    List<String> respuestasFieldConfig = new ArrayList<>();
    try {
      respuestaIndex = null != respuestaIndex && respuestaIndex.compareTo(0) >= 0 ? respuestaIndex : 0;
      JSONArray respuestaJsonArray = (JSONArray) JsonPath.parse(respuestaJson).read("$.." + fieldKey);
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
  public static class RespuestaEvaluateDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient LinkedHashMap<String, Object> jsonFieldItem;
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
          message = ApplicationContextSupport.getMessage(tipoValorSocialBusq.i18nMessage);
        }
      }
      return message;
    }
  }

}
