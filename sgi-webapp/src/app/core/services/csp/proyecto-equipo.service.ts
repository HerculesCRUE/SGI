import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoEquipo } from '@core/models/csp/proyecto-equipo';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IProyectoEquipoResponse } from './proyecto-equipo/proyecto-equipo-response';
import { PROYECTO_EQUIPO_RESPONSE_CONVERTER } from './proyecto-equipo/proyecto-equipo-response.converter';

// tslint:disable-next-line: variable-name
const _ProyectoEquipoMixinBase:
  FindByIdCtor<number, IProyectoEquipo, IProyectoEquipoResponse> &
  typeof SgiRestBaseService =
  mixinFindById(
    SgiRestBaseService,
    PROYECTO_EQUIPO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ProyectoEquipoService extends _ProyectoEquipoMixinBase {
  private static readonly MAPPING = '/proyectoequipos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProyectoEquipoService.MAPPING}`,
      http
    );
  }

  /**
   * Actualiza el listado de IDProyectoEquipo asociados a un IProyectoEquipo
   *
   * @param id Id del IProyectoEquipo
   * @param entities Listado de IProyectoEquipo
   */
  updateList(id: number, entities: IProyectoEquipo[]): Observable<IProyectoEquipo[]> {
    return this.http.patch<IProyectoEquipoResponse[]>(
      `${this.endpointUrl}/${id}`,
      PROYECTO_EQUIPO_RESPONSE_CONVERTER.fromTargetArray(entities)
    ).pipe(
      map(response => PROYECTO_EQUIPO_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }

}
