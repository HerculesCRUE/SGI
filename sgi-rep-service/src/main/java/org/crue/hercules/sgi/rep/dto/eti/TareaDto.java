package org.crue.hercules.sgi.rep.dto.eti;

import java.io.Serializable;

import org.crue.hercules.sgi.rep.dto.BaseRestDto;

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
public class TareaDto extends BaseRestDto {

  private EquipoTrabajoDto equipoTrabajo;
  private MemoriaDto memoria;
  private String tarea;
  private String formacion;
  private FormacionEspecificaDto formacionEspecifica;
  private String organismo;
  private Integer anio;
  private TipoTareaDto tipoTarea;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class EquipoTrabajoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String personaRef;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class FormacionEspecificaDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nombre;
    private Boolean activo;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoTareaDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nombre;
    private Boolean activo;
  }

}