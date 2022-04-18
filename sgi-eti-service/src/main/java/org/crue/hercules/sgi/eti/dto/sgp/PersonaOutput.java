package org.crue.hercules.sgi.eti.dto.sgp;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class PersonaOutput implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Id */
  private String id;
  private String nombre;
  private String apellidos;
  private Sexo sexo;
  private TipoDocumento tipoDocumento;
  private String numeroDocumento;
  private Boolean personalPropio;
  private String entidadPropiaRef;
  private List<Email> emails;

  @EqualsAndHashCode(callSuper = false)
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @SuperBuilder
  @ToString
  public static class Sexo implements Serializable {
    /** Serial version */
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
  }

  @EqualsAndHashCode(callSuper = false)
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @SuperBuilder
  @ToString
  public static class TipoDocumento implements Serializable {
    /** Serial version */
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
  }

  @EqualsAndHashCode(callSuper = false)
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @SuperBuilder
  @ToString
  public static class Email implements Serializable {
    /** Serial version */
    private static final long serialVersionUID = 1L;

    private String email;
    private Boolean principal;
  }
}
