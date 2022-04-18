package org.crue.hercules.sgi.eti.dto.com;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Status
 * <p>
 * Email status
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Status implements Serializable {

  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Error Type (if there is an error) */
  @JsonInclude(Include.NON_NULL)
  private ErrorType error;
  /** Message */
  private String message;

  /**
   * Email error types
   */
  public enum ErrorType {
    PARAM, SUBJECT, CONTENT, RECIPIENT, ATTACHMENT, SEND
  }
}
