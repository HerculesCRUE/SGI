package org.crue.hercules.sgi.eti.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.framework.i18n.Language;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Acta documento
 */

@Entity
@IdClass(ActaDocumentoId.class)
@Table(name = "acta_documento")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ActaDocumento implements Serializable {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  @Id
  @NotNull
  private Long actaId;

  /** Language. */
  @Id
  @NotNull
  private Language lang;

  /** Informe */
  @Column(name = "documento_ref", length = 250, nullable = false)
  private String documentoRef;

  /** Referencia a la transacción blockchain */
  @Column(name = "transaccion_ref", nullable = true)
  private String transaccionRef;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "acta_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_ACTADOCUMENTO_ACTA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @EqualsAndHashCode.Exclude
  private final Acta acta = null;
}