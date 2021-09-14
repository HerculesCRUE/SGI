import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IDepartamento } from '@core/models/sgo/departamento';
import { environment } from '@env';
import { FindAllCtor, FindByIdCtor, mixinFindAll, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';

// tslint:disable-next-line: variable-name
const _DepartamentoServiceMixinBase:
  FindByIdCtor<string, IDepartamento, IDepartamento> &
  FindAllCtor<IDepartamento, IDepartamento> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(SgiRestBaseService)
  );

@Injectable({
  providedIn: 'root'
})
export class DepartamentoService extends _DepartamentoServiceMixinBase {
  private static readonly MAPPING = '/departamentos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgo}${DepartamentoService.MAPPING}`,
      http
    );
  }

}
