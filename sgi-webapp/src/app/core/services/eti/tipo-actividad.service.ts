import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoActividad } from '@core/models/eti/tipo-actividad';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class TipoActividadService extends SgiRestService<number, ITipoActividad> {
  private static readonly MAPPING = '/tipoactividades';

  constructor(protected http: HttpClient) {
    super(
      TipoActividadService.name,
      `${environment.serviceServers.eti}${TipoActividadService.MAPPING}`,
      http
    );
  }
}
