import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_EQUIPO_CONVERTER } from '@core/converters/csp/proyecto-equipo.converter';
import { IProyectoEquipoBackend } from '@core/models/csp/backend/proyecto-equipo-backend';
import { IProyectoEquipo } from '@core/models/csp/proyecto-equipo';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProyectoEquipoService extends SgiMutableRestService<number, IProyectoEquipoBackend, IProyectoEquipo> {
  private static readonly MAPPING = '/proyectoequipos';

  constructor(protected http: HttpClient) {
    super(
      ProyectoEquipoService.name,
      `${environment.serviceServers.csp}${ProyectoEquipoService.MAPPING}`,
      http,
      PROYECTO_EQUIPO_CONVERTER
    );
  }

  /**
   * Actualiza el listado de IDProyectoEquipo asociados a un IProyectoEquipo
   *
   * @param id Id del IProyectoEquipo
   * @param entities Listado de IProyectoEquipo
   */
  updateList(id: number, entities: IProyectoEquipo[]): Observable<IProyectoEquipo[]> {
    return this.http.patch<IProyectoEquipoBackend[]>(
      `${this.endpointUrl}/${id}`,
      this.converter.fromTargetArray(entities)
    ).pipe(
      map(response => this.converter.toTargetArray(response))
    );
  }

}
