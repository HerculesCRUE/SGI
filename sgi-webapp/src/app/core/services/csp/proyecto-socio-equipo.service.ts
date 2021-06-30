import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_SOCIO_EQUIPO_CONVERTER } from '@core/converters/csp/proyecto-socio-equipo.converter';
import { IProyectoSocioEquipoBackend } from '@core/models/csp/backend/proyecto-socio-equipo-backend';
import { IProyectoSocioEquipo } from '@core/models/csp/proyecto-socio-equipo';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProyectoSocioEquipoService
  extends SgiMutableRestService<number, IProyectoSocioEquipoBackend, IProyectoSocioEquipo> {
  private static readonly MAPPING = '/proyectosocioequipos';

  constructor(
    protected http: HttpClient
  ) {
    super(
      ProyectoSocioEquipoService.name,
      `${environment.serviceServers.csp}${ProyectoSocioEquipoService.MAPPING}`,
      http,
      PROYECTO_SOCIO_EQUIPO_CONVERTER
    );
  }

  /**
   * Actualiza el listado de IProyectoSocioEquipo asociados a un IProyectoEquipo
   *
   * @param id Id del IProyectoEquipo
   * @param entities Listado de IProyectoSocioEquipo
   */
  updateList(id: number, entities: IProyectoSocioEquipo[]): Observable<IProyectoSocioEquipo[]> {
    return this.http.patch<IProyectoSocioEquipoBackend[]>(
      `${this.endpointUrl}/${id}`,
      this.converter.fromTargetArray(entities)
    ).pipe(
      map(response => this.converter.toTargetArray(response))
    );
  }
}
