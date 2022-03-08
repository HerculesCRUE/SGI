package org.crue.hercules.sgi.com.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class ProcessedEmailTpl implements Serializable {

  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Processed Email Template subject */
  private String subject;
  /** Processed Email Template text content */
  private String contentText;
  /** Processed Email Template HTML content */
  private String contentHtml;
}
