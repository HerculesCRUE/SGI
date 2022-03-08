package org.crue.hercules.sgi.com.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.crue.hercules.sgi.com.dto.sgdoc.Document;
import org.springframework.core.io.Resource;

public class DocumentDataSource implements DataSource {
  private Document document;
  private Resource resource;

  public DocumentDataSource(Document document, Resource resource) {
    this.document = document;
    this.resource = resource;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return resource.getInputStream();
  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getContentType() {
    return document.getTipo();
  }

  @Override
  public String getName() {
    return document.getNombre();
  }

}
