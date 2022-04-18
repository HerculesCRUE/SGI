package org.crue.hercules.sgi.eti.dto.com;

import java.io.Serializable;
import java.util.List;

import org.crue.hercules.sgi.eti.enums.ServiceType;
import org.springframework.http.HttpMethod;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Email
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class EmailInput implements Serializable {

  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Template Name */
  private String template;
  /** Recipients */
  private List<Recipient> recipients;
  /** List of External Document Reference's for the Email Attachments */
  private List<String> attachments;
  /** Email Template Params */
  private List<EmailParam> params;

  private Deferrable deferrableRecipients;
  private Deferrable deferrableAttachments;
  private Deferrable deferrableParams;

  @EqualsAndHashCode(callSuper = false)
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @SuperBuilder
  @ToString
  public static class Deferrable implements Serializable {
    /** Serial version */
    private static final long serialVersionUID = 1L;

    /** SGI Service type */
    private ServiceType type;

    /** Relative URL to the service endpoint URL for retrieving information */
    private String url;

    /** HTTP Method used to call the service endpoint */
    private HttpMethod method;
  }
}
