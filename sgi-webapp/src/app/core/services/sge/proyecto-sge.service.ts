import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_SGE_CONVERTER } from '@core/converters/sge/proyecto-sge.converter';
import { IProyectoSgeBackend } from '@core/models/sge/backend/proyecto-sge-backend';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { environment } from '@env';
import { FormlyFieldConfig } from '@ngx-formly/core';
import { SgiMutableRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProyectoSgeService extends SgiMutableRestService<string, IProyectoSgeBackend, IProyectoSge>{
  private static readonly MAPPING = '/proyectos';

  constructor(protected http: HttpClient) {
    super(
      ProyectoSgeService.name,
      `${environment.serviceServers.sge}${ProyectoSgeService.MAPPING}`,
      http,
      PROYECTO_SGE_CONVERTER
    );
  }

  createProyecto(model: any): Observable<void> {
    return this.http.post<void>(`${this.endpointUrl}/formly`, model);
  }

  getFormlyCreate(): Observable<FormlyFieldConfig[]> {
    return this.http.get<FormlyFieldConfig[]>(`${this.endpointUrl}/formly/create`);
  }
}