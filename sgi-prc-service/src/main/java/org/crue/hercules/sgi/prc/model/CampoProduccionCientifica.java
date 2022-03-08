package org.crue.hercules.sgi.prc.model;

import java.util.List;
import java.util.stream.Stream;

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

import org.crue.hercules.sgi.prc.exceptions.CampoCVNNotFoundException;
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
@Table(name = CampoProduccionCientifica.TABLE_NAME, uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "codigo_cvn",
        "produccion_cientifica_id" }, name = "UK_CAMPOPRODUCCIONCIENTIFICA_CODIGOCVN_PRODUCCIONCIENTIFICAID") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UniqueFieldsValues(groups = {
    Create.class }, entityClass = CampoProduccionCientifica.class, fieldsNames = {
        CampoProduccionCientifica_.CODIGO_CV_N, CampoProduccionCientifica_.PRODUCCION_CIENTIFICA_ID })
public class CampoProduccionCientifica extends BaseEntity {

  protected static final String TABLE_NAME = "campo_produccion_cientifica";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** CodigoCVN */
  public enum CodigoCVN {
    /** Título de la publicación */
    E060_010_010_030("060.010.010.030"),
    /** Fecha de la publicación */
    E060_010_010_140("060.010.010.140"),
    /** Tipo de producción */
    E060_010_010_010("060.010.010.010"),
    /** Tipo de soporte */
    E060_010_010_070("060.010.010.070"),
    /** Nombre de la publicación */
    E060_010_010_210("060.010.010.210"),
    /** ISBN - ISSN publicación */
    E060_010_010_160("060.010.010.160"),
    /** Editorial */
    E060_010_010_100("060.010.010.100"),
    /** Volumen - Número */
    E060_010_010_080("060.010.010.080"),
    /** Página inicial - final */
    E060_010_010_090("060.010.010.090"),
    /** Autor/a de correspondencia */
    E060_010_010_390("060.010.010.390"),
    /** Identificadores digitales */
    E060_010_010_400("060.010.010.400"),
    /** Tipo identificadores digitales */
    E060_010_010_410("060.010.010.410"),
    /** Publicación relevante */
    E060_010_010_300("060.010.010.300"),
    /** Indice normalizado */
    INDICE_NORMALIZADO("INDICE_NORMALIZADO"),
    /** Publicación muy relevante */
    PUBLICACION_MUY_RELEVANTE("PUBLICACION_MUY_RELEVANTE"),
    /** Tipo Open Access */
    TIPO_OPEN_ACCESS("TIPO_OPEN_ACCESS"),
    /** Internacional */
    INTERNACIONAL("INTERNACIONAL"),
    /** Interdisciplinar */
    INTERDISCIPLINAR("INTERDISCIPLINAR"),
    /** Título del trabajo */
    E060_010_020_030("060.010.020.030"),
    /** Fecha de celebración */
    E060_010_020_190("060.010.020.190"),
    /** Fecha de finalización */
    E060_010_020_380("060.010.020.380"),
    /** Tipo de evento */
    E060_010_020_010("060.010.020.010"),
    /** Ámbito geográfico */
    E060_010_020_080("060.010.020.080"),
    /** Nombre del congreso */
    E060_010_020_100("060.010.020.100"),
    /** Nombre de la publicación (congreso) */
    E060_010_020_370("060.010.020.370"),
    /** ISBN - ISSN congreso */
    E060_010_020_320("060.010.020.320"),
    /** Tipo de participación */
    E060_010_020_050("060.010.020.050"),
    /** Autor/a de de correspondencia */
    E060_010_020_390("060.010.020.390"),
    /** Resumen o abstract en una revista */
    RESUMEN_REVISTA("RESUMEN_REVISTA"),
    /** Descripción */
    E050_020_030_010("050.020.030.010"),
    /* Nombre de la exposición */
    E050_020_030_020("050.020.030.020"),
    /* Fecha de inicio */
    E050_020_030_120("050.020.030.120"),
    /* País celebración */
    E050_020_030_040("050.020.030.040"),
    /* Comunidad Autónoma */
    E050_020_030_050("050.020.030.050"),
    /* Monográfica */
    E050_020_030_090("050.020.030.090"),
    /* Catálogo */
    E050_020_030_100("050.020.030.100"),
    /* Comisario de exposición */
    E050_020_030_110("050.020.030.110"),
    /* Colectiva */
    COLECTIVA("COLECTIVA"),
    /* Tipo */
    TIPO_OBRA("TIPO_OBRA"),
    /* Nombre del Consejo editorial */
    E060_030_030_010("060.030.030.010"),
    /* País de radicación */
    E060_030_030_020("060.030.030.020"),
    /* Fecha de inicio */
    E060_030_030_140("060.030.030.140"),
    /* ISSN */
    ISSN("ISSN"),
    /* Título del trabajo */
    E030_040_000_030("030.040.000.030"),
    /* Fecha de defensa */
    E030_040_000_140("030.040.000.140"),
    /* Alumno/a */
    E030_040_000_120("030.040.000.120"),
    /* Mención de calidad del programa */
    E030_040_000_170("030.040.000.170"),
    /* Fecha Mención de calidad */
    E030_040_000_200("030.040.000.200"),
    /* Doctorado Europeo */
    E030_040_000_190("030.040.000.190"),
    /* Si es Doctorado Europeo, fecha de mención */
    E030_040_000_160("030.040.000.160"),
    /* Tipo de proyecto */
    E030_040_000_010("030.040.000.010"),
    /* Mención Industrial */
    MENCION_INDUSTRIAL("MENCION_INDUSTRIAL"),
    /* Mención Internacional */
    MENCION_INTERNACIONAL("MENCION_INTERNACIONAL"),
    /* Título de la actividad */
    E060_020_030_010("060.020.030.010"),
    /* Fecha de inicio */
    E060_020_030_160("060.020.030.160"),
    /* Tipo de actividad */
    E060_020_030_020("060.020.030.020"),
    /* País de la actividad */
    E060_020_030_030("060.020.030.030"),
    /* Modo de participación */
    E060_020_030_110("060.020.030.110"),
    /* Nombre del proyecto */
    E050_020_010_010("050.020.010.010"),
    /* Fecha de inicio del proyecto */
    E050_020_010_270("050.020.010.270"),
    /* Fecha de fin del proyecto */
    E050_020_010_410("050.020.010.410"),
    /* Ámbito del proyecto */
    E050_020_010_040("050.020.010.040"),
    /* Cuantía total */
    E050_020_010_290("050.020.010.290"),
    /* Convocatoria de excelencia */
    CONVOCATORIA_EXCELENCIA("CONVOCATORIA_EXCELENCIA"),
    /* Nombre del proyecto */
    E050_020_020_010("050.020.020.010"),
    /* Fecha de inicio del proyecto */
    E050_020_020_180("050.020.020.180"),
    /* Fecha de fin del proyecto */
    FECHA_FIN_CONTRATO("FECHA_FIN_CONTRATO"),
    /* Cuantía total */
    E050_020_020_200("050.020.020.200"),
    /* Título/nombre/denominación */
    E050_030_010_020("050.030.010.020"),
    /* Porcentaje de titularidad de la Universidad */
    PORCENTAJE_TITULARIDAD("PORCENTAJE_TITULARIDAD"),
    /* Tipo de la propiedad industrial */
    E050_030_010_030("050.030.010.030"),
    /* Fecha de concesión */
    E050_030_010_320("050.030.010.320"),
    /* Ámbito geográfico, España */
    E050_030_010_160("050.030.010.160"),
    /* Ámbito geográfico, patente europea */
    E050_030_010_170("050.030.010.170"),
    /*
     * Cuantía de las suma de las licencias de explotación, suma de los ingresos de
     * los contratos relacionados con la invención
     */
    CUANTIA_LICENCIAS("CUANTIA_LICENCIAS"),
    /* Número de tramos de investigación reconocidos */
    E060_030_070_010("060.030.070.010");

    private String internValue;

    private CodigoCVN(String internValue) {
      this.internValue = internValue;
    }

    public String getInternValue() {
      return internValue;
    }

    public static CodigoCVN getByInternValue(String internValue) {
      try {
        return Stream.of(CodigoCVN.values())
            .filter(campoValue -> campoValue.getInternValue().equalsIgnoreCase(internValue))
            .findFirst()
            .orElseThrow(() -> new CampoCVNNotFoundException(internValue));

      } catch (Exception e) {
        throw new CampoCVNNotFoundException(internValue);
      }
    }
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** CampoCVN */
  @Column(name = "codigo_cvn", length = CAMPO_CVN_LENGTH, nullable = false)
  private CodigoCVN codigoCVN;

  /** ProduccionCientifica Id */
  @Column(name = "produccion_cientifica_id", nullable = false)
  private Long produccionCientificaId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "produccion_cientifica_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_CAMPOPRODUCCIONCIENTIFICA_PRODUCCIONCIENTIFICA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProduccionCientifica produccionCientifica = null;

  @OneToMany(mappedBy = "campoProduccionCientifica")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ValorCampo> valoresCampos = null;

}
