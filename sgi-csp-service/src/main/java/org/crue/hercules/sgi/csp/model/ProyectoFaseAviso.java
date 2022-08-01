package org.crue.hercules.sgi.csp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = ProyectoFaseAviso.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoFaseAviso extends BaseEntity {

  public static final String TABLE_NAME = "proyecto_fase_aviso";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Referencia externa del aviso a enviar, procedente del servicio COM */
  @Column(name = "comunicado_ref", nullable = false)
  @NotNull
  private String comunicadoRef;

  /**
   * Referencia externa de la tarea programada de envío del comunicado, procedente
   * de TP
   */
  @Column(name = "tarea_programada_ref", nullable = false)
  @NotNull
  private String tareaProgramadaRef;

  /**
   * Indica si en el aviso se han de incluir como destinatarios los IPs de los
   * Proyectos relacionadas con la Convocatoria
   */
  @Column(name = "incluir_ips_proyecto", columnDefinition = "boolean default false", nullable = false)
  @NotNull
  private Boolean incluirIpsProyecto;

  // Relation mappings for JPA metamodel generation only
  @OneToOne(mappedBy = "proyectoFaseAviso1")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProyectoFase proyectoFase = null;

}
