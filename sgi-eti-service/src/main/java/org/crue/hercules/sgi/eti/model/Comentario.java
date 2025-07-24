package org.crue.hercules.sgi.eti.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
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

  public enum TipoEstadoComentario {
    ABIERTO, CERRADO
  }

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
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "comentario_texto", joinColumns = @JoinColumn(name = "comentario_id"))
  @NotEmpty
  @Valid
  private Set<ComentarioTexto> texto = new HashSet<>();

  /** Estado */
  @Column(name = "estado", nullable = true)
  @Enumerated(EnumType.STRING)
  private TipoEstadoComentario estado;

  /** Fecha Estado */
  @Column(name = "fecha_estado", nullable = true)
  private Instant fechaEstado;

}