import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ICentro } from '@core/models/sgo/centro';
import { environment } from '@env';
import { FindAllCtor, FindByIdCtor, mixinFindAll, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';

// tslint:disable-next-line: variable-name
const CentroServiceMixinBase:
  FindByIdCtor<string, ICentro, ICentro> &
  FindAllCtor<ICentro, ICentro> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(SgiRestBaseService)
  );

@Injectable({
  providedIn: 'root'
})
export class CentroService extends CentroServiceMixinBase {
  private static readonly MAPPING = '/centros';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgo}${CentroService.MAPPING}`,
      http
    );
  }

}
