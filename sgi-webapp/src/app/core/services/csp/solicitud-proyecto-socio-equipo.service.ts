import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SOLICITUD_PROYECTO_SOCIO_EQUIPO_CONVERTER } from '@core/converters/csp/solicitud-proyecto-socio-equipo.converter';
import { ISolicitudProyectoSocioEquipoBackend } from '@core/models/csp/backend/solicitud-proyecto-socio-equipo-backend';
import { ISolicitudProyectoSocioEquipo } from '@core/models/csp/solicitud-proyecto-socio-equipo';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoSocioEquipoService
  extends SgiMutableRestService<number, ISolicitudProyectoSocioEquipoBackend, ISolicitudProyectoSocioEquipo> {
  private static readonly MAPPING = '/solicitudproyectosocioequipo';

  constructor(
    protected http: HttpClient
  ) {
    super(
      SolicitudProyectoSocioEquipoService.name,
      `${environment.serviceServers.csp}${SolicitudProyectoSocioEquipoService.MAPPING}`,
      http,
      SOLICITUD_PROYECTO_SOCIO_EQUIPO_CONVERTER
    );
  }

  updateList(proyectoSolictudSocioId: number, entities: ISolicitudProyectoSocioEquipo[]): Observable<ISolicitudProyectoSocioEquipo[]> {
    return this.http.patch<ISolicitudProyectoSocioEquipoBackend[]>(
      `${this.endpointUrl}/${proyectoSolictudSocioId}`,
      this.converter.fromTargetArray(entities)
    ).pipe(
      map(response => this.converter.toTargetArray(response))
    );
  }
}
