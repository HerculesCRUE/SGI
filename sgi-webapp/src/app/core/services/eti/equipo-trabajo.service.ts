import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EQUIPO_TRABAJO_CONVERTER } from '@core/converters/eti/equipo-trabajo.converter';
import { IEquipoTrabajoBackend } from '@core/models/eti/backend/equipo-trabajo-backend';
import { IEquipoTrabajo } from '@core/models/eti/equipo-trabajo';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class EquipoTrabajoService extends SgiMutableRestService<number, IEquipoTrabajoBackend, IEquipoTrabajo> {
  private static readonly MAPPING = '/equipotrabajos';

  constructor(protected http: HttpClient) {
    super(
      EquipoTrabajoService.name,
      `${environment.serviceServers.eti}${EquipoTrabajoService.MAPPING}`,
      http,
      EQUIPO_TRABAJO_CONVERTER
    );
  }
}
