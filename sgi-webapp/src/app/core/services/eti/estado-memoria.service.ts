import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ESTADO_MEMORIA_CONVERTER } from '@core/converters/eti/estado-memoria.converter';
import { IEstadoMemoriaBackend } from '@core/models/eti/backend/estado-memoria-backend';
import { IEstadoMemoria } from '@core/models/eti/estado-memoria';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class EstadoMemoriaService extends SgiMutableRestService<number, IEstadoMemoriaBackend, IEstadoMemoria>{
  private static readonly MAPPING = '/estadomemorias';

  constructor(protected http: HttpClient) {
    super(
      EstadoMemoriaService.name,
      `${environment.serviceServers.eti}${EstadoMemoriaService.MAPPING}`,
      http,
      ESTADO_MEMORIA_CONVERTER
    );
  }
}
