package org.crue.hercules.sgi.com.model;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import org.crue.hercules.sgi.com.enums.ServiceType;
import org.springframework.http.HttpMethod;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DeferrableEntity
 * <p>
 * Allows deferring data retrieval untill it's needed.
 */
@MappedSuperclass
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Deferrable extends BaseEntity {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** SGI Service type */
  @Column(name = "type", length = TYPE_LENGTH, nullable = true)
  @Enumerated(EnumType.STRING)
  private ServiceType type;

  /** Relative URL to the service endpoint URL for retrieving information */
  @Column(name = "url", length = RELATIVE_URL_LENGTH, nullable = false)
  private String url;

  /** HTTP Method used to call the service endpoint */
  @Column(name = "method", length = TYPE_LENGTH, nullable = true)
  @Enumerated(EnumType.STRING)
  private HttpMethod method;
}
