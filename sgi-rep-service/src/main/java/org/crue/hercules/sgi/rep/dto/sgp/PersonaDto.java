package org.crue.hercules.sgi.rep.dto.sgp;

import java.io.Serializable;
import java.util.List;

import org.crue.hercules.sgi.rep.dto.BaseRestDto;
import org.crue.hercules.sgi.rep.dto.sgemp.EmpresaDto;

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
public class PersonaDto extends BaseRestDto {
  private String nombre;
  private String numeroDocumento;
  private String apellidos;
  private TipoDocumentoDto tipoDocumento;
  private SexoDto sexo;
  private transient DatosContactoDto datosContacto;
  private VinculacionDto vinculacion;
  private DatosAcademicosDto datosAcademicos;
  private EmpresaDto entidad;
  private List<EmailDto> emails;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoDocumentoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class SexoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class VinculacionDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private CategoriaProfesionalDto categoriaProfesional;
    private String fechaObtencionCategoria;
    private AreaConocimientoDto areaConocimiento;
    private DepartamentoDto departamento;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class DepartamentoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class CategoriaProfesionalDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class DatosAcademicosDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private NivelAcademicoDto nivelAcademico;
    private String fechaObtencion;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class NivelAcademicoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoIdentificadorDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class AreaConocimientoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
    private String padreId;
  }

}