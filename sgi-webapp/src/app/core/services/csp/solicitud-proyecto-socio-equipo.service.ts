import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudProyectoSocioEquipo } from '@core/models/csp/solicitud-proyecto-socio-equipo';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ISolicitudProyectoSocioEquipoResponse } from './solicitud-proyecto-socio-equipo/solicitud-proyecto-socio-equipo-response';
import { SOLICITUD_PROYECTO_SOCIO_EQUIPO_RESPONSE_CONVERTER } from './solicitud-proyecto-socio-equipo/solicitud-proyecto-socio-equipo.converter';

// tslint:disable-next-line: variable-name
const _SolicitudProyectoSolicitudEquipoMixinBase:
  FindByIdCtor<number, ISolicitudProyectoSocioEquipo, ISolicitudProyectoSocioEquipoResponse> &
  typeof SgiRestBaseService =
  mixinFindById(
    SgiRestBaseService,
    SOLICITUD_PROYECTO_SOCIO_EQUIPO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoSocioEquipoService
  extends _SolicitudProyectoSolicitudEquipoMixinBase {
  private static readonly MAPPING = '/solicitudproyectosocioequipo';

  constructor(
    protected http: HttpClient
  ) {
    super(
      `${environment.serviceServers.csp}${SolicitudProyectoSocioEquipoService.MAPPING}`,
      http
    );
  }

  updateList(proyectoSolictudSocioId: number, entities: ISolicitudProyectoSocioEquipo[]): Observable<ISolicitudProyectoSocioEquipo[]> {
    return this.http.patch<ISolicitudProyectoSocioEquipoResponse[]>(
      `${this.endpointUrl}/${proyectoSolictudSocioId}`,
      SOLICITUD_PROYECTO_SOCIO_EQUIPO_RESPONSE_CONVERTER.fromTargetArray(entities)
    ).pipe(
      map(response => SOLICITUD_PROYECTO_SOCIO_EQUIPO_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }
}
