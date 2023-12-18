package org.crue.hercules.sgi.sgdoc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.time.format.DateTimeFormatter;

import org.crue.hercules.sgi.sgdoc.model.Documento;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class StoreUtils {

  public static final String SAMPLE_DATA_PREFIX = "sample-";
  private static final String PATTERN = "YYYY" + File.separator + "MM" + File.separator + "dd";
  private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

  private StoreUtils() {
    // To prevent instances
  }

  private static String getRelativePath(Documento documento) {
    return FORMATTER.format(documento.getFechaCreacion()) + File.separator + documento.getDocumentoRef();
  }

  private static String getAbsolutePath(String storePath, Documento documento) {
    return storePath + File.separator + getRelativePath(documento);
  }

  public static Resource getResource(String storePath, Documento documento) {
    if (documento.getDocumentoRef().startsWith(SAMPLE_DATA_PREFIX)) {
      return new ClassPathResource("db/changelog/changes/sample-blob/" + documento.getDocumentoRef().replace('-', '.'));
    } else {
      return new FileSystemResource(getAbsolutePath(storePath, documento));
    }
  }

  public static String getFileChecksum(MessageDigest digest, File file) throws IOException {
    // Get file input stream for reading the file content
    FileInputStream fis = new FileInputStream(file);

    // Create byte array to read data in chunks
    byte[] byteArray = new byte[1024];
    int bytesCount = 0;

    // Read file data and update in message digest
    while ((bytesCount = fis.read(byteArray)) != -1) {
      digest.update(byteArray, 0, bytesCount);
    }

    // close the stream; We don't need it now.
    fis.close();

    // Get the hash's bytes
    byte[] bytes = digest.digest();

    // This bytes[] has bytes in decimal format;
    // Convert it to hexadecimal format
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < bytes.length; i++) {
      sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
    }

    // return complete hash
    return sb.toString();
  }
}
