import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TAREA_CONVERTER } from '@core/converters/eti/tarea.converter';
import { ITareaBackend } from '@core/models/eti/backend/tarea-backend';
import { ITarea } from '@core/models/eti/tarea';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class TareaService extends SgiMutableRestService<number, ITareaBackend, ITarea> {
  private static readonly MAPPING = '/tareas';

  constructor(protected http: HttpClient) {
    super(
      TareaService.name,
      `${environment.serviceServers.eti}${TareaService.MAPPING}`,
      http,
      TAREA_CONVERTER
    );
  }
}
