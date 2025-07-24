import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudProyectoEquipo } from '@core/models/csp/solicitud-proyecto-equipo';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ISolicitudProyectoEquipoResponse } from './solicitud-proyecto-equipo/solicitud-proyecto-equipo-response';
import { SOLICITUD_PROYECTO_EQUIPO_RESPONSE_CONVERTER } from './solicitud-proyecto-equipo/solicitud-proyecto-equipo.converter';

// tslint:disable-next-line: variable-name
const _SolicitudProyectoEquipoMixinBase:
  FindByIdCtor<number, ISolicitudProyectoEquipo, ISolicitudProyectoEquipoResponse> &
  typeof SgiRestBaseService =
  mixinFindById(
    SgiRestBaseService,
    SOLICITUD_PROYECTO_EQUIPO_RESPONSE_CONVERTER
  );


@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoEquipoService
  extends _SolicitudProyectoEquipoMixinBase {
  private static readonly MAPPING = '/solicitudproyectoequipo';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${SolicitudProyectoEquipoService.MAPPING}`,
      http
    );
  }

  updateSolicitudProyectoEquipo(solicitudId: number, solicitudProyectoEquipos: ISolicitudProyectoEquipo[]):
    Observable<ISolicitudProyectoEquipo[]> {
    return this.http.patch<ISolicitudProyectoEquipoResponse[]>(`${this.endpointUrl}/${solicitudId}`,
      SOLICITUD_PROYECTO_EQUIPO_RESPONSE_CONVERTER.fromTargetArray(solicitudProyectoEquipos)
    ).pipe(
      map(response => SOLICITUD_PROYECTO_EQUIPO_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }
}
