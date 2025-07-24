import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IFormacionEspecifica } from '@core/models/eti/formacion-especifica';
import { environment } from '@env';
import { FindAllCtor, FindByIdCtor, mixinFindAll, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { IFormacionEspecificaResponse } from './formacion-especifica/formacion-especifica-response';
import { FORMACION_ESPECIFICA_RESPONSE_CONVERTER } from './formacion-especifica/formacion-especifica-response.converter';


// tslint:disable-next-line: variable-name
const _FormacionEspecificaServiceMixinBase:
  FindByIdCtor<number, IFormacionEspecifica, IFormacionEspecificaResponse> &
  FindAllCtor<IFormacionEspecifica, IFormacionEspecificaResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      SgiRestBaseService,
      FORMACION_ESPECIFICA_RESPONSE_CONVERTER
    ),
    FORMACION_ESPECIFICA_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class FormacionEspecificaService extends _FormacionEspecificaServiceMixinBase {
  private static readonly MAPPING = '/formacionespecificas';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eti}${FormacionEspecificaService.MAPPING}`,
      http
    );
  }
}

