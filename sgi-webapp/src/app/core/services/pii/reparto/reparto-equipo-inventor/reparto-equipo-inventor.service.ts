import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IRepartoEquipoInventor } from '@core/models/pii/reparto-equipo-inventor';
import { environment } from '@env';
import {
  CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll,
  mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor
} from '@sgi/framework/http';
import { IRepartoEquipoInventorRequest } from './reparto-equipo-inventor-request';
import { REPARTO_EQUIPO_INVENTOR_REQUEST_CONVERTER } from './reparto-equipo-inventor-request.converter';
import { IRepartoEquipoInventorResponse } from './reparto-equipo-inventor-response';
import { REPARTO_EQUIPO_INVENTOR_RESPONSE_CONVERTER } from './reparto-equipo-inventor-response.converter';

// tslint:disable-next-line: variable-name
const _RepartoEquipoInventorServiceMixinBase:
  FindAllCtor<IRepartoEquipoInventor, IRepartoEquipoInventorResponse> &
  FindByIdCtor<number, IRepartoEquipoInventor, IRepartoEquipoInventorResponse> &
  CreateCtor<IRepartoEquipoInventor, IRepartoEquipoInventor, IRepartoEquipoInventorRequest, IRepartoEquipoInventorResponse> &
  UpdateCtor<number, IRepartoEquipoInventor, IRepartoEquipoInventor, IRepartoEquipoInventorRequest, IRepartoEquipoInventorResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinCreate(
        mixinUpdate(
          SgiRestBaseService,
          REPARTO_EQUIPO_INVENTOR_REQUEST_CONVERTER,
          REPARTO_EQUIPO_INVENTOR_RESPONSE_CONVERTER
        ),
        REPARTO_EQUIPO_INVENTOR_REQUEST_CONVERTER,
        REPARTO_EQUIPO_INVENTOR_RESPONSE_CONVERTER
      ),
      REPARTO_EQUIPO_INVENTOR_RESPONSE_CONVERTER
    ),
    REPARTO_EQUIPO_INVENTOR_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class RepartoEquipoInventorService extends _RepartoEquipoInventorServiceMixinBase {

  private static readonly MAPPING = '/repartoequiposinventor';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.pii}${RepartoEquipoInventorService.MAPPING}`,
      http,
    );
  }
}
