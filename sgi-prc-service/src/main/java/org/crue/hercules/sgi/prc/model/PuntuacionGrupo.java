package org.crue.hercules.sgi.prc.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = PuntuacionGrupo.TABLE_NAME, uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "grupo_ref",
        "convocatoria_baremacion_id" }, name = "UK_PUNTUACIONGRUPO_GRUPOREF_CONVOCATORIABAREMACIONID") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PuntuacionGrupo extends BaseEntity {

  protected static final String TABLE_NAME = "puntuacion_grupo";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** grupoRef */
  @Column(name = "grupo_ref", length = GRUPO_REF_LENGTH, nullable = false)
  private String grupoRef;

  @Column(name = "puntos", nullable = false)
  private BigDecimal puntos;

  @Column(name = "convocatoria_baremacion_id", nullable = true)
  private Long convocatoriaBaremacionId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "convocatoria_baremacion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PUNTUACIONGRUPO_CONVOCATORIABAREMACION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ConvocatoriaBaremacion convocatoriaBaremacion = null;

  @OneToMany(mappedBy = "puntuacionGrupo")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<PuntuacionGrupoInvestigador> puntuacionesGrupoInvestigador = null;

}
