import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoTarea } from '@core/models/eti/tipo-tarea';
import { environment } from '@env';
import { FindAllCtor, FindByIdCtor, mixinFindAll, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { ITipoTareaResponse } from './tipo-tarea/tipo-tarea-response';
import { TIPO_TAREA_RESPONSE_CONVERTER } from './tipo-tarea/tipo-tarea-response.converter';


// tslint:disable-next-line: variable-name
const _TipoTareaServiceMixinBase:
  FindByIdCtor<number, ITipoTarea, ITipoTareaResponse> &
  FindAllCtor<ITipoTarea, ITipoTareaResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      SgiRestBaseService,
      TIPO_TAREA_RESPONSE_CONVERTER
    ),
    TIPO_TAREA_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class TipoTareaService extends _TipoTareaServiceMixinBase {
  private static readonly MAPPING = '/tipostarea';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eti}${TipoTareaService.MAPPING}`,
      http
    );
  }
}
