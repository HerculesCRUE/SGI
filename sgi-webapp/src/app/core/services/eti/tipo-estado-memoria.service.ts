import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TipoEstadoMemoria } from '@core/models/eti/tipo-estado-memoria';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class TipoEstadoMemoriaService extends SgiRestService<number, TipoEstadoMemoria> {
  private static readonly MAPPING = '/tipoestadomemorias';

  constructor(protected http: HttpClient) {
    super(
      TipoEstadoMemoriaService.name,
      `${environment.serviceServers.eti}${TipoEstadoMemoriaService.MAPPING}`,
      http
    );
  }
}
