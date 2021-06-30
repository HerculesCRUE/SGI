import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TipoEstadoActa } from '@core/models/eti/tipo-estado-acta';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class TipoEstadoActaService extends SgiRestService<number, TipoEstadoActa> {
  private static readonly MAPPING = '/tipoestadoactas';

  constructor(protected http: HttpClient) {
    super(
      TipoEstadoActaService.name,
      `${environment.serviceServers.eti}${TipoEstadoActaService.MAPPING}`,
      http
    );
  }
}
