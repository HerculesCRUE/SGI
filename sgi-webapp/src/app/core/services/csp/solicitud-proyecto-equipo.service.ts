import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SOLICITUD_PROYECTO_EQUIPO_CONVERTER } from '@core/converters/csp/solicitud-proyecto-equipo.converter';
import { ISolicitudProyectoEquipoBackend } from '@core/models/csp/backend/solicitud-proyecto-equipo-backend';
import { ISolicitudProyectoEquipo } from '@core/models/csp/solicitud-proyecto-equipo';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoEquipoService
  extends SgiMutableRestService<number, ISolicitudProyectoEquipoBackend, ISolicitudProyectoEquipo> {
  private static readonly MAPPING = '/solicitudproyectoequipo';

  constructor(protected http: HttpClient) {
    super(
      SolicitudProyectoEquipoService.name,
      `${environment.serviceServers.csp}${SolicitudProyectoEquipoService.MAPPING}`,
      http,
      SOLICITUD_PROYECTO_EQUIPO_CONVERTER
    );
  }

  updateSolicitudProyectoEquipo(solicitudId: number, solicitudProyectoEquipos: ISolicitudProyectoEquipo[]):
    Observable<ISolicitudProyectoEquipo[]> {
    return this.http.patch<ISolicitudProyectoEquipoBackend[]>(`${this.endpointUrl}/${solicitudId}`,
      this.converter.fromTargetArray(solicitudProyectoEquipos)
    ).pipe(
      map(response => this.converter.toTargetArray(response))
    );
  }
}
