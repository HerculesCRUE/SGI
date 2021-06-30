package org.crue.hercules.sgi.framework.test.http.client;

import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

/**
 * Wrapper for a {@link HttpComponentsClientHttpRequestFactory} that buffers all
 * outgoing and incoming streams in memory.
 *
 * <p>
 * Using this wrapper allows for multiple reads of the
 * {@linkplain ClientHttpResponse#getBody() response body}.
 */
public class BufferingHttpComponentsClientHttpRequestFactory extends BufferingClientHttpRequestFactory {

  /**
   * Create a buffering wrapper for a new
   * {@link HttpComponentsClientHttpRequestFactory}.
   */
  public BufferingHttpComponentsClientHttpRequestFactory() {
    super(new HttpComponentsClientHttpRequestFactory());
  }

}
