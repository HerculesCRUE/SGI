package org.crue.hercules.sgi.rep.service;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.util.SgiHtmlRenderPolicy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.PictureType;
import com.deepoove.poi.data.Pictures;

import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informes
 */
@Service
@Slf4j
@Validated
public class SgiReportDocxService {

  private final SgiConfigProperties sgiConfigProperties;

  private final SgiApiConfService sgiApiConfService;

  protected static final String DATE_PATTERN_DEFAULT = "dd/MM/yyyy";

  public SgiReportDocxService(SgiConfigProperties sgiConfigProperties, SgiApiConfService sgiApiConfService) {
    this.sgiConfigProperties = sgiConfigProperties;
    this.sgiApiConfService = sgiApiConfService;

  }

  /**
   * Obtiene el report
   * 
   * @param reportPath ruta del report
   * @return MasterReport
   */
  protected InputStream getReportDefinitionStream(String reportPath) {
    try {
      byte[] reportDefinition = sgiApiConfService.getResource(reportPath);
      return new ByteArrayInputStream(reportDefinition);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
  }

  /**
   * Obtiene la imágen de la cabecera del informe
   * 
   * @return PictureRenderData
   */
  protected PictureRenderData getImageHeaderLogo() {
    try {
      byte[] imgByte = sgiApiConfService.getResource("rep-common-header-logo");

      return Pictures.ofBytes(imgByte, PictureType.JPEG).fitSize().create();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
  }

  public byte[] scale(byte[] fileData, int width, int height) throws NotFoundException {
    ByteArrayInputStream in = new ByteArrayInputStream(fileData);
    try {
      BufferedImage img = ImageIO.read(in);
      if (height == 0) {
        height = (width * img.getHeight()) / img.getWidth();
      }
      if (width == 0) {
        width = (height * img.getWidth()) / img.getHeight();
      }
      Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
      BufferedImage imageBuff = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      imageBuff.getGraphics().drawImage(scaledImage, 0, 0, new Color(0, 0, 0), null);

      ByteArrayOutputStream buffer = new ByteArrayOutputStream();

      ImageIO.write(imageBuff, "jpg", buffer);

      return buffer.toByteArray();
    } catch (IOException e) {
      throw new NotFoundException("scale not fou", e);
    }
  }

  /**
   * Generic message to show into report
   * 
   * @param e Exception
   * @return message encoded in html
   */
  protected String getErrorMessage(Exception e) {
    log.error(e.getMessage());
    String msgError = " - Error - ";
    if (ObjectUtils.isNotEmpty(e.getMessage())) {
      msgError = e.getMessage();
    }
    return msgError;
  }

  protected String formatInstantToString(Instant instantDate, String pattern) {
    String result = "";

    if (null != instantDate) {
      pattern = StringUtils.hasText(pattern) ? pattern : DATE_PATTERN_DEFAULT;
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern)
          .withZone(sgiConfigProperties.getTimeZone().toZoneId()).withLocale(LocaleContextHolder.getLocale());

      result = formatter.format(instantDate);
    }

    return result;
  }

  protected String formatInstantToString(Instant instantDate) {
    return formatInstantToString(instantDate, DATE_PATTERN_DEFAULT);
  }

  protected XWPFDocument compileReportData(InputStream is, HashMap<String, Object> dataReport) {

    Configure config = Configure.builder()
        .useSpringEL()
        .addPlugin('<', new SgiHtmlRenderPolicy())
        .build();

    return compileReportData(is, config, dataReport);
  }

  protected XWPFDocument compileReportData(InputStream is, Configure config, HashMap<String, Object> dataReport) {
    return XWPFTemplate.compile(is, config).render(dataReport).getXWPFDocument();
  }

}
