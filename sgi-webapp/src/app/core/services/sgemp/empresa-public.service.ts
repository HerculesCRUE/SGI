import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';

// tslint:disable-next-line: variable-name
const _EmpresaMixinBase:
  FindByIdCtor<string, IEmpresa, IEmpresa> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService
  );

@Injectable({
  providedIn: 'root'
})
export class EmpresaPublicService extends _EmpresaMixinBase {
  private static readonly MAPPING = '/empresas';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgemp}${EmpresaPublicService.PUBLIC_PREFIX}${EmpresaPublicService.MAPPING}`,
      http
    );
  }

}
