package org.crue.hercules.sgi.eti.model;

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
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DocumentacionMemoria
 */
@Entity
@Table(name = "documentacion_memoria")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class DocumentacionMemoria extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "documentacion_memoria_seq")
  @SequenceGenerator(name = "documentacion_memoria_seq", sequenceName = "documentacion_memoria_seq", allocationSize = 1)
  private Long id;

  /** Memoria */
  @ManyToOne
  @JoinColumn(name = "memoria_id", nullable = true, foreignKey = @ForeignKey(name = "FK_DOCUMENTACIONMEMORIA_MEMORIA"))
  private Memoria memoria;

  /** Tipo Documento */
  @ManyToOne
  @JoinColumn(name = "tipo_documento_id", nullable = true, foreignKey = @ForeignKey(name = "FK_DOCUMENTACIONMEMORIA_TIPODOCUMENTO"))
  private TipoDocumento tipoDocumento;

  /** Referencia documento */
  @Column(name = "documento_ref", length = 250, nullable = false)
  @NotNull
  private String documentoRef;

  /** Nombre */
  @Column(name = "nombre", nullable = false, length = 250)
  private String nombre;

}