package org.crue.hercules.sgi.eti.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.persistence.LanguageConverter;
import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Formulario Report
 */

@Entity
@Table(name = "formulario_report")
@IdClass(FormularioReportPK.class)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class FormularioReport implements Serializable {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "formularioId", nullable = false)
  @NotNull
  private Long formularioId;

  /** Language */
  @Column(name = "lang", nullable = false, length = 2)
  @Convert(converter = LanguageConverter.class)
  @NotNull
  private Language lang;

  /** Report */
  @Lob
  @Basic(fetch = FetchType.LAZY)
  @Type(type = "org.hibernate.type.BinaryType")
  @Column(name = "value_", nullable = true, columnDefinition = "blob")
  private byte[] value;

}