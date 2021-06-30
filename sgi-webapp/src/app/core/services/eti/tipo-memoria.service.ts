import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoMemoria } from '@core/models/eti/tipo-memoria';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class TipoMemoriaService extends SgiRestService<number, ITipoMemoria> {
  private static readonly MAPPING = '/tipomemorias';

  constructor(protected http: HttpClient) {
    super(
      TipoMemoriaService.name,
      `${environment.serviceServers.eti}${TipoMemoriaService.MAPPING}`,
      http
    );
  }
}
