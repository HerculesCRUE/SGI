import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ESTADO_ACTA_CONVERTER } from '@core/converters/eti/estado-acta.converter';
import { IEstadoActaBackend } from '@core/models/eti/backend/estado-acta-backend';
import { IEstadoActa } from '@core/models/eti/estado-acta';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class EstadoActaService extends SgiMutableRestService<number, IEstadoActaBackend, IEstadoActa>{
  private static readonly MAPPING = '/estadoactas';

  constructor(protected http: HttpClient) {
    super(
      EstadoActaService.name,
      `${environment.serviceServers.eti}${EstadoActaService.MAPPING}`,
      http,
      ESTADO_ACTA_CONVERTER
    );
  }
}
