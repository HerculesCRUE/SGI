package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.prc.model.Acreditacion_;
import org.crue.hercules.sgi.prc.model.Autor_;
import org.crue.hercules.sgi.prc.model.BaseEntity;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica_;
import org.crue.hercules.sgi.prc.model.IndiceImpacto.TipoRanking;
import org.crue.hercules.sgi.prc.validation.CodigoCVNValueValid;
import org.crue.hercules.sgi.prc.validation.FirmaOrPersonaRefOrNombreAndApellidosAutor;
import org.crue.hercules.sgi.prc.validation.TipoFuenteImpactoValueValid;
import org.crue.hercules.sgi.prc.validation.UniqueElementsByFields;
import org.crue.hercules.sgi.prc.validation.UrlOrDocumentoRefAcreditacion;
import org.crue.hercules.sgi.prc.validation.ValorFormat;
import org.hibernate.validator.constraints.UniqueElements;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProduccionCientificaApiInput implements Serializable {

  @Valid
  @NotEmpty
  @UniqueElementsByFields(fieldsNames = CampoProduccionCientifica_.CODIGO_CV_N)
  private List<CampoProduccionCientificaInput> campos;

  @Valid
  @NotEmpty
  @UniqueElementsByFields(fieldsNames = Autor_.FIRMA)
  @UniqueElementsByFields(fieldsNames = Autor_.PERSONA_REF)
  @UniqueElementsByFields(fieldsNames = { Autor_.NOMBRE, Autor_.APELLIDOS })
  private List<AutorInput> autores;

  @Valid
  @UniqueElements
  private List<IndiceImpactoInput> indicesImpacto;

  @Valid
  @UniqueElementsByFields(fieldsNames = Acreditacion_.DOCUMENTO_REF)
  @UniqueElementsByFields(fieldsNames = Acreditacion_.URL)
  private List<AcreditacionInput> acreditaciones;

  @UniqueElements
  private List<@NotNull Long> proyectos;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @ValorFormat
  public static class CampoProduccionCientificaInput implements Serializable {
    private static final long serialVersionUID = 1L;

    @CodigoCVNValueValid
    @NotEmpty
    @Size(max = BaseEntity.CAMPO_CVN_LENGTH)
    private String codigoCVN;

    @NotEmpty
    @UniqueElements
    private List<@NotEmpty String> valores;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @FirmaOrPersonaRefOrNombreAndApellidosAutor
  public static class AutorInput implements Serializable {
    private static final long serialVersionUID = 1L;

    @Size(max = BaseEntity.FIRMA_LENGTH)
    private String firma;

    @Size(max = BaseEntity.PERSONA_REF_LENGTH)
    private String personaRef;

    @Size(max = BaseEntity.NOMBRE_LENGTH)
    private String nombre;

    @Size(max = BaseEntity.APELLIDOS_LENGTH)
    private String apellidos;

    @NotNull
    private Integer orden;

    @Size(max = BaseEntity.ORCID_ID_LENGTH)
    private String orcidId;

    private Boolean ip;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class IndiceImpactoInput implements Serializable {
    private static final long serialVersionUID = 1L;

    @TipoFuenteImpactoValueValid
    @NotEmpty
    @Size(max = BaseEntity.TIPO_FUENTE_IMPACTO_LENGTH)
    private String fuenteImpacto;

    private BigDecimal indice;

    private TipoRanking ranking;

    private Integer anio;

    @Size(max = BaseEntity.OTRA_FUENTE_IMPACTO_LENGTH)
    private String otraFuenteImpacto;

    private BigDecimal posicionPublicacion;

    private BigDecimal numeroRevistas;

    private Boolean revista25;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @UrlOrDocumentoRefAcreditacion
  public static class AcreditacionInput implements Serializable {
    private static final long serialVersionUID = 1L;

    @Size(max = BaseEntity.DOCUMENTO_REF_LENGTH)
    private String documentoRef;

    @Size(max = BaseEntity.URL_LENGTH)
    private String url;
  }
}
