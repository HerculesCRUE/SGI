package org.crue.hercules.sgi.rep.report.data.objects;

import java.util.ArrayList;
import java.util.List;

import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.rep.dto.eti.ApartadoOutput;
import org.crue.hercules.sgi.rep.dto.eti.BloqueOutput;
import org.crue.hercules.sgi.rep.dto.eti.ComentarioDto;
import org.crue.hercules.sgi.rep.util.SgiReportContextHolder;

import lombok.Getter;

@Getter
public class BloqueApartadoObject {
  private String nombre;
  private List<Apartado> apartados = new ArrayList<>();

  public BloqueApartadoObject(BloqueOutput dto) {
    this.nombre = dto.getNombre();
    if (dto.getApartados() != null) {
      this.apartados = dto.getApartados().stream().map(Apartado::new).toList();
    }
  }

  @Getter
  private class Apartado {
    private String titulo;
    private List<Comentario> comentarios = new ArrayList<>();
    private List<Apartado> apartadosHijos = new ArrayList<>();

    private Apartado(ApartadoOutput dto) {
      this.titulo = dto.getTitulo();
      if (dto.getComentarios() != null) {
        this.comentarios = dto.getComentarios().stream().map(Comentario::new).toList();
      }
      if (dto.getApartadosHijos() != null) {
        this.apartadosHijos = dto.getApartadosHijos().stream().map(Apartado::new).toList();
      }
    }

    @Getter
    private class Comentario {
      private String texto;
      private String sexoPersonaCreated;
      private String personaCreated;

      private Comentario(ComentarioDto dto) {
        this.texto = I18nHelper.getFieldValue(dto.getTexto(), SgiReportContextHolder.getLanguage());
        this.sexoPersonaCreated = dto.getSexoPersonaCreated();
        this.personaCreated = dto.getPersonaCreated();
      }
    }
  }
}
