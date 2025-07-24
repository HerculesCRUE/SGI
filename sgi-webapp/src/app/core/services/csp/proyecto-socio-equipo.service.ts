import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoSocioEquipo } from '@core/models/csp/proyecto-socio-equipo';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IProyectoSocioEquipoResponse } from './proyecto-socio-equipo/proyecto-socio-equipo-response';
import { PROYECTO_SOCIO_EQUIPO_RESPONSE_CONVERTER } from './proyecto-socio-equipo/proyecto-socio-equipo.converter';

// tslint:disable-next-line: variable-name
const _ProyectoSocioEquipoMixinBase:
  FindByIdCtor<number, IProyectoSocioEquipo, IProyectoSocioEquipoResponse> &
  typeof SgiRestBaseService =
  mixinFindById(
    SgiRestBaseService,
    PROYECTO_SOCIO_EQUIPO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ProyectoSocioEquipoService
  extends _ProyectoSocioEquipoMixinBase {
  private static readonly MAPPING = '/proyectosocioequipos';

  constructor(
    protected http: HttpClient
  ) {
    super(
      `${environment.serviceServers.csp}${ProyectoSocioEquipoService.MAPPING}`,
      http
    );
  }

  /**
   * Actualiza el listado de IProyectoSocioEquipo asociados a un IProyectoEquipo
   *
   * @param id Id del IProyectoEquipo
   * @param entities Listado de IProyectoSocioEquipo
   */
  updateList(id: number, entities: IProyectoSocioEquipo[]): Observable<IProyectoSocioEquipo[]> {
    return this.http.patch<IProyectoSocioEquipoResponse[]>(
      `${this.endpointUrl}/${id}`,
      PROYECTO_SOCIO_EQUIPO_RESPONSE_CONVERTER.fromTargetArray(entities)
    ).pipe(
      map(response => PROYECTO_SOCIO_EQUIPO_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }
}
