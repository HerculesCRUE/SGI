import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ASISTENTE_CONVERTER } from '@core/converters/eti/asistente.converter';
import { IAsistente } from '@core/models/eti/asistente';
import { IAsistenteBackend } from '@core/models/eti/backend/asistente-backend';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class AsistenteService extends SgiMutableRestService<number, IAsistenteBackend, IAsistente>{
  private static readonly MAPPING = '/asistentes';

  constructor(protected http: HttpClient) {
    super(
      AsistenteService.name,
      `${environment.serviceServers.eti}${AsistenteService.MAPPING}`,
      http,
      ASISTENTE_CONVERTER
    );
  }

}
