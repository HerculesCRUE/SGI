import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IEquipoTrabajo } from '@core/models/eti/equipo-trabajo';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { IEquipoTrabajoResponse } from './equipo-trabajo/equipo-trabajo-response';
import { EQUIPO_TRABAJO_RESPONSE_CONVERTER } from './equipo-trabajo/equipo-trabajo-response.converter';

const _EquipoTrabajoServiceMixinBase:
  CreateCtor<IEquipoTrabajo, IEquipoTrabajo, IEquipoTrabajoResponse, IEquipoTrabajoResponse> &
  UpdateCtor<number, IEquipoTrabajo, IEquipoTrabajo, IEquipoTrabajoResponse, IEquipoTrabajoResponse> &
  FindByIdCtor<number, IEquipoTrabajo, IEquipoTrabajoResponse> &
  FindAllCtor<IEquipoTrabajo, IEquipoTrabajoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          EQUIPO_TRABAJO_RESPONSE_CONVERTER,
          EQUIPO_TRABAJO_RESPONSE_CONVERTER
        ),
        EQUIPO_TRABAJO_RESPONSE_CONVERTER,
        EQUIPO_TRABAJO_RESPONSE_CONVERTER
      ),
      EQUIPO_TRABAJO_RESPONSE_CONVERTER
    ),
    EQUIPO_TRABAJO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class EquipoTrabajoService extends _EquipoTrabajoServiceMixinBase {
  private static readonly MAPPING = '/equipotrabajos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eti}${EquipoTrabajoService.MAPPING}`,
      http
    );
  }
}
