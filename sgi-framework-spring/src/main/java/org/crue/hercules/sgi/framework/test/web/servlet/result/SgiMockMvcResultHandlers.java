package org.crue.hercules.sgi.framework.test.web.servlet.result;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

/**
 * Static factory methods for {@link ResultHandler}-based result actions.
 */
public interface SgiMockMvcResultHandlers {

  /**
   * Print {@link MvcResult} details to the "standard" output stream only if the
   * response status code is greater or equal to 400.
   * 
   * @return a {@link ResultHandler} that writes to {@link System#out} only if the
   *         response status code is greater or equal to 400.
   * @see System#out
   * @see #printOnError(OutputStream)
   * @see #printOnError(Writer)
   */
  @SuppressWarnings("java:S106")
  public static ResultHandler printOnError() {
    return printOnError(System.out);
  }

  /**
   * Print {@link MvcResult} details to the supplied {@link OutputStream} only if
   * the response status code is greater or equal to 400.
   * 
   * @param stream the OutputStream to write to
   * @return a {@link ResultHandler} that writes to an {@link OutputStream} only
   *         if the response status code is greater or equal to 400.
   * @see #printOnError()
   * @see #printOnError(Writer)
   */
  public static ResultHandler printOnError(OutputStream stream) {
    return printOnError(new OutputStreamWriter(stream));
  }

  /**
   * Print {@link MvcResult} details to the supplied {@link Writer} only if the
   * response status code is greater or equal to 400.
   * 
   * @param writer the Writer to write to
   * @return a {@link ResultHandler} that writes to a {@link Writer} only if the
   *         response status code is greater or equal to 400.
   * @see #printOnError()
   * @see #printOnError(OutputStream)
   */
  public static ResultHandler printOnError(Writer writer) {
    return new PrintWriterPrintingErrorResultHandler(writer);
  }

  /**
   * A {@link ResultHandler} that writes to a {@link Writer} only if the response
   * status code is greater or equal to 400.
   */
  public static class PrintWriterPrintingErrorResultHandler implements ResultHandler {
    Writer writer;

    /**
     * Creates the {@link PrintWriterPrintingErrorResultHandler}
     * 
     * @param writer the {@link Writer} to write to
     */
    public PrintWriterPrintingErrorResultHandler(Writer writer) {
      this.writer = writer;
    }

    @Override
    public void handle(MvcResult result) throws Exception {
      if (result.getResponse().getStatus() >= 400) {
        MockMvcResultHandlers.print(writer).handle(result);
      }
    }
  }

}
