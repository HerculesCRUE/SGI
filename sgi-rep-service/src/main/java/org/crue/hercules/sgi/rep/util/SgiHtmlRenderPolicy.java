package org.crue.hercules.sgi.rep.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ddr.poi.html.HtmlRenderPolicy;

import com.deepoove.poi.render.RenderContext;

/**
 * Política de renderizado HTML personalizada con conversión de colores HSL a
 * RGB y estilos para la visualizacion correcta del html generado por ckeditor.
 */
public class SgiHtmlRenderPolicy extends HtmlRenderPolicy {
  private static final String STYLE_PADDING_TD = "padding: 0 10px 10px";
  private static final String STYLE_WHITE_SPACE_PRE_WRAP = "white-space: pre-wrap";
  private static final String TAG_P = "p";
  private static final String TAG_TABLE = "table";
  private static final String TAG_TD = "td";

  @Override
  public void doRender(RenderContext<String> context) throws Exception {
    String convertedHtml = convertHSLColors(convertRGBWithPercentageColors(context.getData()));
    String styledHtml = addStyles(convertedHtml);
    super.doRender(new RenderContext<>(context.getEleTemplate(), styledHtml, context.getTemplate()));
  }

  /**
   * Agrega estilos al contenido HTML.
   *
   * @param html El contenido HTML al que se le agregarán estilos.
   * @return El contenido HTML con estilos aplicados.
   */
  private String addStyles(String html) {
    html = addStyleToHTML(html, TAG_TD, STYLE_PADDING_TD);
    html = addStyleToHTML(html, TAG_P, STYLE_WHITE_SPACE_PRE_WRAP);
    html = addStyleToHTML(html, TAG_TABLE, STYLE_WHITE_SPACE_PRE_WRAP);
    return html;
  }

  /**
   * Agrega estilos a las etiquetas HTML específicas en el contenido.
   *
   * @param html    El contenido HTML.
   * @param tagName El nombre de la etiqueta HTML.
   * @param style   El estilo a aplicar.
   * @return El contenido HTML con los estilos aplicados.
   */
  private String addStyleToHTML(String html, String tagName, String style) {
    return html.replace("<" + tagName + ">", "<" + tagName + " style='" + style + "'>");
  }

  /**
   * Convierte los colores RGB con porcentajes en el contenido HTML a RGB.
   *
   * @param htmlString El contenido HTML.
   * @return El contenido HTML con los colores RGB con porcentajes convertidos a
   *         RGB.
   */
  private String convertRGBWithPercentageColors(String htmlString) {
    Pattern pattern = Pattern.compile("rgb\\((\\d+\\.*\\d+)%,(\\d+\\.*\\d+)%,(\\d+\\.*\\d+)%\\)");
    Matcher matcher = pattern.matcher(htmlString);
    StringBuffer result = new StringBuffer();
    while (matcher.find()) {
      int red = (int) (Double.parseDouble(matcher.group(1)) * 2.55);
      int green = (int) (Double.parseDouble(matcher.group(2)) * 2.55);
      int blue = (int) (Double.parseDouble(matcher.group(3)) * 2.55);
      matcher.appendReplacement(result, "rgb(" + red + "," + green + "," + blue + ")");
    }
    matcher.appendTail(result);
    return result.toString();
  }

  /**
   * Convierte los colores HSL en el contenido HTML a RGB.
   *
   * @param htmlString El contenido HTML.
   * @return El contenido HTML con los colores HSL convertidos a RGB.
   */
  private String convertHSLColors(String htmlString) {
    Pattern pattern = Pattern.compile("hsl\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*%,\\s*(\\d+)\\s*%\\s*\\)");
    Matcher matcher = pattern.matcher(htmlString);
    StringBuffer result = new StringBuffer();
    while (matcher.find()) {
      int h = Integer.parseInt(matcher.group(1));
      int s = Integer.parseInt(matcher.group(2));
      int l = Integer.parseInt(matcher.group(3));
      String rgbValue = convertHSLtoRGB(h, s, l);
      matcher.appendReplacement(result, "rgb(" + rgbValue + ")");
    }
    matcher.appendTail(result);
    return result.toString();
  }

  /**
   * Convierte los valores de color HSL a RGB.
   *
   * @param h El valor de matiz.
   * @param s El valor de saturación.
   * @param l El valor de luminosidad.
   * @return El color RGB como un string.
   */
  private String convertHSLtoRGB(int h, int s, int l) {
    double c = (1 - Math.abs(2 * l / 100.0 - 1)) * (s / 100.0);
    double x = c * (1 - Math.abs(((h / 60.0) % 2) - 1));
    double m = (l / 100.0) - c / 2.0;

    double r;
    double g;
    double b;
    if (h >= 0 && h < 60) {
      r = (c + m) * 255;
      g = (x + m) * 255;
      b = (m) * 255;
    } else if (h >= 60 && h < 120) {
      r = (x + m) * 255;
      g = (c + m) * 255;
      b = (m) * 255;
    } else if (h >= 120 && h < 180) {
      r = (m) * 255;
      g = (c + m) * 255;
      b = (x + m) * 255;
    } else if (h >= 180 && h < 240) {
      r = (m) * 255;
      g = (x + m) * 255;
      b = (c + m) * 255;
    } else if (h >= 240 && h < 300) {
      r = (x + m) * 255;
      g = (m) * 255;
      b = (c + m) * 255;
    } else {
      r = (c + m) * 255;
      g = (m) * 255;
      b = (x + m) * 255;
    }

    return String.format("%d, %d, %d", (int) r, (int) g, (int) b);
  }

}
