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
export class DepartamentoPublicService extends _DepartamentoServiceMixinBase {
  private static readonly MAPPING = '/departamentos';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgo}${DepartamentoPublicService.PUBLIC_PREFIX}${DepartamentoPublicService.MAPPING}`,
      http
    );
  }

}
