import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class EmpresaService extends SgiRestService<string, IEmpresa> {
  private static readonly MAPPING = '/empresas';

  constructor(protected http: HttpClient) {
    super(EmpresaService.name,
      `${environment.serviceServers.sgemp}${EmpresaService.MAPPING}`, http);
  }
}
