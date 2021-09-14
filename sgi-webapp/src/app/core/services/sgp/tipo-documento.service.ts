import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoDocumento } from '@core/models/sgp/tipo-documento';
import { environment } from '@env';
import { FindAllCtor, FindByIdCtor, mixinFindAll, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';

// tslint:disable-next-line: variable-name
const _TipoDocumentoServiceMixinBase:
  FindByIdCtor<string, ITipoDocumento, ITipoDocumento> &
  FindAllCtor<ITipoDocumento, ITipoDocumento> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(SgiRestBaseService)
  );

@Injectable({
  providedIn: 'root'
})
export class TipoDocumentoService extends _TipoDocumentoServiceMixinBase {
  private static readonly MAPPING = '/tipos-documento';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgp}${TipoDocumentoService.MAPPING}`,
      http
    );
  }

}
