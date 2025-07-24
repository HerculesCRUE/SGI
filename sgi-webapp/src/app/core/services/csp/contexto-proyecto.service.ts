import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoContexto } from '@core/models/csp/proyecto-contexto';
import { IProyectoContextoResponse } from '@core/services/csp/proyecto-contexto/proyecto-contexto-response';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { PROYECTO_CONTEXTO_RESPONSE_CONVERTER } from './proyecto-contexto/proyecto-contexto-response.converter';

const _ContextoProyectoServiceMixinBase:
  CreateCtor<IProyectoContexto, IProyectoContexto, IProyectoContextoResponse, IProyectoContextoResponse> &
  UpdateCtor<number, IProyectoContexto, IProyectoContexto, IProyectoContextoResponse, IProyectoContextoResponse> &
  FindAllCtor<IProyectoContexto, IProyectoContextoResponse> &
  FindByIdCtor<number, IProyectoContexto, IProyectoContextoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          PROYECTO_CONTEXTO_RESPONSE_CONVERTER,
          PROYECTO_CONTEXTO_RESPONSE_CONVERTER
        ),
        PROYECTO_CONTEXTO_RESPONSE_CONVERTER,
        PROYECTO_CONTEXTO_RESPONSE_CONVERTER
      ),
      PROYECTO_CONTEXTO_RESPONSE_CONVERTER),
    PROYECTO_CONTEXTO_RESPONSE_CONVERTER
  );


@Injectable({
  providedIn: 'root'
})
export class ContextoProyectoService extends _ContextoProyectoServiceMixinBase {
  private static readonly MAPPING = '/proyecto-contextoproyectos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ContextoProyectoService.MAPPING}`,
      http
    );
  }

}
