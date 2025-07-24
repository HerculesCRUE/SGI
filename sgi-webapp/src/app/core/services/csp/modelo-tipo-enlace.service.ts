import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IModeloTipoEnlace } from '@core/models/csp/modelo-tipo-enlace';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IModeloTipoEnlaceResponse } from './modelo-tipo-enlace/modelo-tipo-enlace-response';
import { MODELO_TIPO_ENLACE_RESPONSE_CONVERTER } from './modelo-tipo-enlace/modelo-tipo-enlace-response.converter';

const _ModeloTipoEnlaceServiceMixinBase:
  CreateCtor<IModeloTipoEnlace, IModeloTipoEnlace, IModeloTipoEnlaceResponse, IModeloTipoEnlaceResponse> &
  UpdateCtor<number, IModeloTipoEnlace, IModeloTipoEnlace, IModeloTipoEnlaceResponse, IModeloTipoEnlaceResponse> &
  FindByIdCtor<number, IModeloTipoEnlace, IModeloTipoEnlaceResponse> &
  FindAllCtor<IModeloTipoEnlace, IModeloTipoEnlaceResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          MODELO_TIPO_ENLACE_RESPONSE_CONVERTER,
          MODELO_TIPO_ENLACE_RESPONSE_CONVERTER
        ),
        MODELO_TIPO_ENLACE_RESPONSE_CONVERTER,
        MODELO_TIPO_ENLACE_RESPONSE_CONVERTER
      ),
      MODELO_TIPO_ENLACE_RESPONSE_CONVERTER
    ),
    MODELO_TIPO_ENLACE_RESPONSE_CONVERTER
  );


@Injectable({
  providedIn: 'root'
})
export class ModeloTipoEnlaceService extends _ModeloTipoEnlaceServiceMixinBase {
  private static readonly MAPPING = '/modelotipoenlaces';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ModeloTipoEnlaceService.MAPPING}`,
      http
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
