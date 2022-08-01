package org.crue.hercules.sgi.eer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = TipoDocumento.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ActivableIsActivo(entityClass = TipoDocumento.class, groups = { BaseEntity.Update.class })
public class TipoDocumento extends BaseActivableEntity {
  protected static final String TABLE_NAME = "tipo_documento";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TipoDocumento.SEQUENCE_NAME)
  @SequenceGenerator(name = TipoDocumento.SEQUENCE_NAME, sequenceName = TipoDocumento.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Nombre */
  @Column(name = "nombre", length = SHORT_TEXT_LENGTH, nullable = false)
  private String nombre;

  /** Descripción */
  @Column(name = "descripcion", length = SHORT_TEXT_LENGTH, nullable = true)
  private String descripcion;

  @ManyToOne
  @JoinColumn(name = "tipo_documento_padre_id", nullable = true, foreignKey = @ForeignKey(name = "FK_TIPODOCUMENTO_PADRE"))
  private TipoDocumento padre;
}
