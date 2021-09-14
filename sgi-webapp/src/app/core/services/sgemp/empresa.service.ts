import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { environment } from '@env';
import { FormlyFieldConfig } from '@ngx-formly/core';
import { SgiRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EmpresaService extends SgiRestService<string, IEmpresa> {
  private static readonly MAPPING = '/empresas';

  constructor(protected http: HttpClient) {
    super(EmpresaService.name,
      `${environment.serviceServers.sgemp}${EmpresaService.MAPPING}`, http);
  }

  createEmpresa(model: any): Observable<void> {
    return this.http.post<void>(`${this.endpointUrl}/formly`, model);
  }

  updateEmpresa(id: string, model: any): Observable<void> {
    return this.http.put<void>(`${this.endpointUrl}/formly/${id}`, model);
  }

  getFormlyCreate(): Observable<FormlyFieldConfig[]> {
    return this.http.get<FormlyFieldConfig[]>(`${this.endpointUrl}/formly/create`);
  }

  getFormlyUpdate(): Observable<FormlyFieldConfig[]> {
    return this.http.get<FormlyFieldConfig[]>(`${this.endpointUrl}/formly/update`);
  }

  getFormlyModelById(id: string): Observable<any> {
    return this.http.get<any[]>(`${this.endpointUrl}/formly/${id}`);
  }

  getFormlyView(): Observable<FormlyFieldConfig[]> {
    return this.http.get<FormlyFieldConfig[]>(`${this.endpointUrl}/formly/view`);
  }
}