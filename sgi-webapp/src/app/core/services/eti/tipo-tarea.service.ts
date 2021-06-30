import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TipoTarea } from '@core/models/eti/tipo-tarea';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class TipoTareaService extends SgiRestService<number, TipoTarea> {
  private static readonly MAPPING = '/tipostarea';

  constructor(protected http: HttpClient) {
    super(
      TipoTareaService.name,
      `${environment.serviceServers.eti}${TipoTareaService.MAPPING}`,
      http
    );
  }
}
