import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_SGE_CONVERTER } from '@core/converters/sge/proyecto-sge.converter';
import { IProyectoSgeBackend } from '@core/models/sge/backend/proyecto-sge-backend';
import { IProyectoAnualidadPartida } from '@core/models/sge/proyecto-anualidad-partida';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { environment } from '@env';
import { FormlyFieldConfig } from '@ngx-formly/core';
import { SgiMutableRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { PROYECTO_ANUALIDAD_PARTIDA_REQUEST_CONVERTER } from './proyecto-anualidad-partida/proyecto-anualidad-partida-request.converter';

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

  createProyecto(model: any): Observable<string | void> {
    return this.http.post<string>(`${this.endpointUrl}/formly`, model);
  }

  updateProyecto(id: string, model: any): Observable<void> {
    return this.http.put<void>(`${this.endpointUrl}/formly/${id}`, model);
  }

  getFormlyCreate(): Observable<FormlyFieldConfig[]> {
    return this.http.get<FormlyFieldConfig[]>(`${this.endpointUrl}/formly/create`);
  }

  getFormlyUpdate(): Observable<FormlyFieldConfig[]> {
    return this.http.get<FormlyFieldConfig[]>(`${this.endpointUrl}/formly/update`);
  }

  getFormlyView(): Observable<FormlyFieldConfig[]> {
    return this.http.get<FormlyFieldConfig[]>(`${this.endpointUrl}/formly/view`);
  }

  getFormlyModelById(id: string): Observable<any> {
    return this.http.get<any[]>(`${this.endpointUrl}/formly/${id}`);
  }

  createProyectoAnualidadesPartidas(proyectoAnualidadesPartidas: IProyectoAnualidadPartida[]): Observable<void> {
    return this.http.post<void>(`${this.endpointUrl}/anualidades`,
      proyectoAnualidadesPartidas.map(p => PROYECTO_ANUALIDAD_PARTIDA_REQUEST_CONVERTER.fromTarget(p)));
  }

}
