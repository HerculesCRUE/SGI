package org.crue.hercules.sgi.eti.model;

import java.time.Instant;

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
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "retrospectiva")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Retrospectiva extends BaseEntity {

  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "retrospectiva_seq")
  @SequenceGenerator(name = "retrospectiva_seq", sequenceName = "retrospectiva_seq", allocationSize = 1)
  private Long id;

  /** Estado Retrospectiva. */
  @ManyToOne
  @JoinColumn(name = "estado_retrospectiva_id", nullable = false, foreignKey = @ForeignKey(name = "FK_RETROSPECTIVA_ESTADORETROSPECTIVA"))
  @NotNull
  private EstadoRetrospectiva estadoRetrospectiva;

  /** Fecha Retrospectiva. */
  @Column(name = "fecha_retrospectiva", nullable = false)
  @NotNull
  private Instant fechaRetrospectiva;

}
