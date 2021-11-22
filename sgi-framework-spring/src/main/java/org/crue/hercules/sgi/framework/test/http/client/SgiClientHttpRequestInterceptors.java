package org.crue.hercules.sgi.framework.test.http.client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Static factory methods for {@link ClientHttpRequestInterceptor}.
 */
@Slf4j
public class SgiClientHttpRequestInterceptors {

  private SgiClientHttpRequestInterceptors() {
  }

  /**
   * Logs (at DEBUG level) {@link HttpRequest} and {@link ClientHttpResponse}
   * details via SL4J.
   * 
   * @return a {@link ClientHttpRequestInterceptor} that logs {@link HttpRequest}
   *         and {@link ClientHttpResponse} details at {@code DEBUG} level via
   *         SL4J..
   * @see #print(OutputStream)
   * @see #print(Writer)
   */
  public static ClientHttpRequestInterceptor log() {
    return new LoggingClientHttpRequestInterceptor();
  }

  /**
   * Logs (at ERROR level) {@link HttpRequest} and {@link ClientHttpResponse}
   * details via SL4J only if the response status code is greater or equal to 400.
   * 
   * @return a {@link ClientHttpRequestInterceptor} that logs only if the response
   *         status code is greater or equal to 400.
   * @see #printOnError(Writer)
   */
  public static ClientHttpRequestInterceptor logOnError() {
    return new LoggingErrorClientHttpRequestInterceptor();
  }

  /**
   * Print {@link HttpRequest} and {@link ClientHttpResponse} details to the
   * supplied {@link OutputStream}.
   * 
   * @param stream the OutputStream to write to
   * @return a {@link PrintingClientHttpRequestInterceptor} that writes to an
   *         {@link OutputStream}.
   * @see #print(Writer)
   * @see #log()
   */
  public static ClientHttpRequestInterceptor print(OutputStream stream) {
    return new PrintWriterHttpRequestInterceptor(new PrintWriter(stream, true));
  }

  /**
   * Print {@link HttpRequest} and {@link ClientHttpResponse} details to the
   * supplied {@link Writer}.
   * 
   * @param writer the Writer to write to
   * @return a {@link PrintingClientHttpRequestInterceptor} that writes to a
   *         {@link Writer}.
   * @see #print(OutputStream)
   * @see #log()
   */
  public static ClientHttpRequestInterceptor print(Writer writer) {
    return new PrintWriterHttpRequestInterceptor(new PrintWriter(writer, true));
  }

  /**
   * Print {@link HttpRequest} and {@link ClientHttpResponse} details to the
   * supplied {@link OutputStream} only if the response status code is greater or
   * equal to 400.
   * 
   * @param stream the OutputStream to write to
   * @return a {@link ClientHttpRequestInterceptor} that writes to an
   *         {@link OutputStream} only if the response status code is greater or
   *         equal to 400.
   * @see #logOnError()
   * @see #printOnError(Writer)
   */
  public static ClientHttpRequestInterceptor printOnError(OutputStream stream) {
    return printOnError(new OutputStreamWriter(stream));
  }

  /**
   * Print {@link HttpRequest} and {@link ClientHttpResponse} details to the
   * supplied {@link Writer} only if the response status code is greater or equal
   * to 400.
   * 
   * @param writer the Writer to write to
   * @return a {@link PrintingClientHttpRequestInterceptor} that writes to a
   *         {@link Writer}.
   * @see #logOnError()
   * @see #printOnError(OutputStream)
   */
  public static ClientHttpRequestInterceptor printOnError(Writer writer) {
    return new ErrorClientHttpRequestInterceptor(writer);
  }

  /**
   * A {@link PrintingClientHttpRequestInterceptor} that writes to a
   * {@link PrintWriter}.
   */
  private static class PrintWriterHttpRequestInterceptor extends PrintingClientHttpRequestInterceptor {

    public PrintWriterHttpRequestInterceptor(PrintWriter writer) {
      super(new ResultValuePrinter() {
        @Override
        public void printHeading(String heading) {
          writer.println();
          writer.println(String.format("%s:", heading));
        }

        @Override
        public void printValue(String label, @Nullable Object value) {
          if (value != null && value.getClass().isArray()) {
            value = CollectionUtils.arrayToList(value);
          }
          writer.println(String.format("%17s = %s", label, value));
        }
      });
    }
  }

  /**
   * A {@link PrintingClientHttpRequestInterceptor} that writes to a
   * {@link Writer} only if the response status code is greater or equal to 400.
   *
   * <p>
   * Delegates to a {@link PrintWriterHttpRequestInterceptor} for writing.
   */
  private static class ErrorClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    Writer writer;

    public ErrorClientHttpRequestInterceptor(Writer writer) {
      this.writer = writer;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
      ClientHttpResponse response = execution.execute(request, body);
      if (response.getStatusCode().value() >= 400) {
        return SgiClientHttpRequestInterceptors.print(writer).intercept(request, body,
            new ClientHttpRequestExecution() {

              @Override
              public ClientHttpResponse execute(HttpRequest request, byte[] body) throws IOException {
                return response;
              }

            });
      } else {
        return response;
      }
    }
  }

  /**
   * A {@link PrintingClientHttpRequestInterceptor} that writes to a
   * {@link Writer} only if the response status code is greater or equal to 400.
   *
   * <p>
   * Delegates to a {@link PrintWriterHttpRequestInterceptor} for writing.
   */
  private static class LoggingErrorClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
      if (log.isErrorEnabled()) {
        StringWriter writer = new StringWriter();
        ClientHttpResponse response = SgiClientHttpRequestInterceptors.print(writer).intercept(request, body,
            execution);
        log.error("HttpRequest and ClientHttpResponse details:\n" + writer);
        return response;
      } else {
        return execution.execute(request, body);
      }
    }
  }

  /**
   * A {@link ClientHttpRequestInterceptor} that logs {@link HttpRequest} and
   * {@link ClientHttpResponse} details at {@code DEBUG} level via SL4J.
   *
   * <p>
   * Delegates to a {@link PrintWriterHttpRequestInterceptor} for building the log
   * message.
   */
  private static class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
      if (log.isDebugEnabled()) {
        StringWriter writer = new StringWriter();
        ClientHttpResponse response = SgiClientHttpRequestInterceptors.print(writer).intercept(request, body,
            execution);
        log.debug("HttpRequest and ClientHttpResponse details:\n" + writer);
        return response;
      } else {
        return execution.execute(request, body);
      }
    }
  }

}
