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

@Entity
@Table(name = "comentario")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Comentario extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comentario_seq")
  @SequenceGenerator(name = "comentario_seq", sequenceName = "comentario_seq", allocationSize = 1)
  private Long id;

  /** Formulario Memoria */
  @ManyToOne
  @JoinColumn(name = "memoria_id", nullable = false, foreignKey = @ForeignKey(name = "FK_COMENTARIO_MEMORIA"))
  private Memoria memoria;

  /** Apartado Formulario */
  @ManyToOne
  @JoinColumn(name = "apartado_id", nullable = false, foreignKey = @ForeignKey(name = "FK_COMENTARIO_APARTADO"))
  @NotNull
  private Apartado apartado;

  /** Evaluacion */
  @ManyToOne
  @JoinColumn(name = "evaluacion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_COMENTARIO_EVALUACION"))
  @NotNull(groups = { Update.class })
  private Evaluacion evaluacion;

  /** Comentario */
  @ManyToOne
  @JoinColumn(name = "tipo_comentario_id", nullable = false, foreignKey = @ForeignKey(name = "FK_COMENTARIO_TIPOCOMENTARIO"))
  @NotNull(groups = { Update.class })
  private TipoComentario tipoComentario;

  /** Texto. */
  @Column(name = "texto", length = 2000, nullable = false)
  @NotNull
  private String texto;

}