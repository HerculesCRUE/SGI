package org.crue.hercules.sgi.framework.test.http.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.style.ToStringCreator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;

/**
 * {@link ClientHttpRequestInterceptor} that prints {@link HttpRequest} and
 * {@link ClientHttpResponse} details to a given output stream &mdash; for
 * example: {@code System.out}, {@code System.err}, a custom
 * {@code java.io.PrintWriter}, etc.
 *
 * <p>
 * An instance of this class is typically accessed via one of the
 * {@link SgiClientHttpRequestInterceptors#print print},
 * {@link SgiClientHttpRequestInterceptors#printOnError printOnError} or
 * {@link SgiClientHttpRequestInterceptors#log log} methods in
 * {@link SgiClientHttpRequestInterceptors}.
 */
public class PrintingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

  private final ResultValuePrinter printer;

  /**
   * Protected constructor.
   * 
   * @param printer a {@link ResultValuePrinter} to do the actual writing
   */
  protected PrintingClientHttpRequestInterceptor(ResultValuePrinter printer) {
    this.printer = printer;
  }

  /**
   * Intercept the given request, and return a response.
   * <p>
   * The implementation of this method follows the following pattern:
   * <ol>
   * <li>Prints the {@linkplain HttpRequest request} and body</li>
   * <li>Execute the request using
   * {@link ClientHttpRequestExecution#execute(org.springframework.http.HttpRequest, byte[])}</li>
   * <li>Prints the response</li>
   * </ol>
   * 
   * @param request   the request, containing method, URI, and headers
   * @param body      the body of the request
   * @param execution the request execution
   * @return the response
   * @throws IOException in case of I/O errors
   */
  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
      throws IOException {
    this.printer.printHeading("HttpRequest");
    printRequest(request, body);

    ClientHttpResponse response = execution.execute(request, body);

    this.printer.printHeading("ClientHttpResponse");
    printResponse(response);

    return response;
  }

  /**
   * Return the result value printer.
   * 
   * @return the printer
   */
  protected ResultValuePrinter getPrinter() {
    return this.printer;
  }

  /**
   * Print the request.
   * 
   * @param request the request, containing method, URI, and headers
   * @param body    the body of the request
   * @throws IOException in case of I/O errors
   */
  protected void printRequest(HttpRequest request, byte[] body) throws IOException {
    String bodyString = new String(body, StandardCharsets.UTF_8);

    this.printer.printValue("HTTP Method", request.getMethod());
    this.printer.printValue("Request URI", request.getURI());
    this.printer.printValue("Headers", request.getHeaders());
    this.printer.printValue("Body", bodyString);
  }

  /**
   * Print the response.
   * 
   * @param response the response containing the body to print
   * @throws IOException in case of I/O errors
   */
  protected void printResponse(ClientHttpResponse response) throws IOException {
    StringBuilder inputStringBuilder = new StringBuilder();
    BufferedReader bufferedReader = new BufferedReader(
        new InputStreamReader(response.getBody(), StandardCharsets.UTF_8));
    String line = bufferedReader.readLine();
    while (line != null) {
      inputStringBuilder.append(line);
      inputStringBuilder.append('\n');
      line = bufferedReader.readLine();
    }
    String bodyString = inputStringBuilder.toString();

    this.printer.printValue("Status", response.getStatusCode());
    this.printer.printValue("Headers", response.getHeaders());
    this.printer.printValue("Content type", response.getHeaders().getContentType());
    this.printer.printValue("Body", bodyString);
    printCookies(response.getHeaders());
  }

  /**
   * Print the cookies taken from the {@link HttpHeaders} in a human-readable
   * form, assuming the {@link HttpCookie} implementation does not provide its own
   * {@code toString()}.
   * 
   * @param headers the headers cotaining the cookies to print
   */
  protected void printCookies(HttpHeaders headers) {
    List<HttpCookie> cookies = new ArrayList<>();
    List<String> cookieStrings = headers.get(HttpHeaders.SET_COOKIE);
    if (cookieStrings != null) {
      for (String cookieString : cookieStrings) {
        cookies.addAll(HttpCookie.parse(cookieString));
      }
      for (String cookie2String : cookieStrings) {
        cookies.addAll(HttpCookie.parse(cookie2String));
      }
      printCookies(cookies);
    }
  }

  /**
   * Print the supplied cookies in a human-readable form, assuming the
   * {@link HttpCookie} implementation does not provide its own
   * {@code toString()}.
   * 
   * @param cookies the list of cookies to print
   */
  protected void printCookies(List<HttpCookie> cookies) {
    String[] cookieStrings = new String[cookies.size()];
    for (int i = 0; i < cookies.size(); i++) {
      HttpCookie cookie = cookies.get(i);
      cookieStrings[i] = new ToStringCreator(cookie).append("name", cookie.getName()).append("value", cookie.getValue())
          .append("comment", cookie.getComment()).append("domain", cookie.getDomain())
          .append("maxAge", cookie.getMaxAge()).append("path", cookie.getPath()).append("secure", cookie.getSecure())
          .append("version", cookie.getVersion()).append("httpOnly", cookie.isHttpOnly()).toString();
    }
    this.printer.printValue("Cookies", cookieStrings);
  }

  /**
   * A contract for how to actually write result information.
   */
  protected interface ResultValuePrinter {

    /**
     * Prints a heading value.
     * 
     * @param heading the heading
     */
    void printHeading(String heading);

    /**
     * Prints a content value.
     * 
     * @param label content label
     * @param value content value
     */
    void printValue(String label, @Nullable Object value);
  }

}
