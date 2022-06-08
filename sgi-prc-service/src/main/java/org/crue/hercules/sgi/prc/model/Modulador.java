package org.crue.hercules.sgi.prc.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.crue.hercules.sgi.prc.model.BaseEntity.Create;
import org.crue.hercules.sgi.prc.validation.UniqueFieldsValues;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = Modulador.TABLE_NAME, uniqueConstraints = {
    @UniqueConstraint(columnNames = { "tipo", "area_ref",
        "convocatoria_baremacion_id" }, name = "UK_MODULADOR_TIPO_AREAREF_CONVOCATORIABAREMACIONID") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UniqueFieldsValues(groups = { Create.class }, entityClass = Modulador.class, fieldsNames = {
    Modulador_.TIPO, Modulador_.CONVOCATORIA_BAREMACION_ID, Modulador_.AREA_REF })
public class Modulador extends BaseEntity {

  protected static final String TABLE_NAME = "modulador";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  private static final long serialVersionUID = 1L;

  public enum TipoModulador {
    NUMERO_AUTORES,
    AREAS;
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** areaRef */
  @Column(name = "area_ref", length = AREA_REF_LENGTH, nullable = false)
  private String areaRef;

  @Column(name = "tipo", length = TIPO_MODULADOR_LENGTH, nullable = false)
  @Enumerated(EnumType.STRING)
  private TipoModulador tipo;

  @Column(name = "valor1", nullable = false)
  private BigDecimal valor1;

  @Column(name = "valor2", nullable = true)
  private BigDecimal valor2;

  @Column(name = "valor3", nullable = true)
  private BigDecimal valor3;

  @Column(name = "valor4", nullable = true)
  private BigDecimal valor4;

  @Column(name = "valor5", nullable = true)
  private BigDecimal valor5;

  @Column(name = "convocatoria_baremacion_id", nullable = false)
  private Long convocatoriaBaremacionId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "convocatoria_baremacion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_MODULADOR_CONVOCATORIABAREMACION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ConvocatoriaBaremacion convocatoriaBaremacion = null;

}
