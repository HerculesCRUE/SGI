import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITarea } from '@core/models/eti/tarea';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { ITareaResponse } from './tarea/tarea-response';
import { TAREA_RESPONSE_CONVERTER } from './tarea/tarea-response.converter';

// tslint:disable-next-line: variable-name
const _TareaServiceMixinBase:
  CreateCtor<ITarea, ITarea, ITareaResponse, ITareaResponse> &
  UpdateCtor<number, ITarea, ITarea, ITareaResponse, ITareaResponse> &
  FindByIdCtor<number, ITarea, ITareaResponse> &
  FindAllCtor<ITarea, ITareaResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          TAREA_RESPONSE_CONVERTER,
          TAREA_RESPONSE_CONVERTER
        ),
        TAREA_RESPONSE_CONVERTER,
        TAREA_RESPONSE_CONVERTER
      ),
      TAREA_RESPONSE_CONVERTER
    ),
    TAREA_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class TareaService extends _TareaServiceMixinBase {
  private static readonly MAPPING = '/tareas';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eti}${TareaService.MAPPING}`,
      http
    );
  }
}
