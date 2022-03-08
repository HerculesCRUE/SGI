import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { ITipoIdentificador } from '@core/models/sgemp/tipo-identificador';
import { environment } from '@env';
import { FormlyFieldConfig } from '@ngx-formly/core';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EmpresaService extends SgiRestService<string, IEmpresa> {
  private static readonly MAPPING = '/empresas';

  constructor(protected http: HttpClient) {
    super(EmpresaService.name,
      `${environment.serviceServers.sgemp}${EmpresaService.MAPPING}`, http);
  }

  createEmpresa(model: any): Observable<string> {
    return this.http.post<string>(`${this.endpointUrl}/formly`, model);
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

  /**
   * Busca todas las empresas que tengan alguno de los ids de la lista
   *
   * @param ids lista de identificadores de empresa
   * @returns la lista de empresas
   */
  findAllByIdIn(ids: string[]): Observable<SgiRestListResult<IEmpresa>> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('id', SgiRestFilterOperator.IN, ids)
    };

    return this.findAll(options);
  }
}
