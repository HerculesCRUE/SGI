package org.crue.hercules.sgi.pii.util;

import java.text.Normalizer;

public class SgiStringUtils {

  private SgiStringUtils() {
  }

  public static String normalize(String input) {
    // Normaliza el string y elimina los signos diacríticos (acentos, diéresis, etc)
    return Normalizer
        .normalize(input, Normalizer.Form.NFD)
        .replaceAll("\\p{M}", "")
        .toLowerCase();
  }
}