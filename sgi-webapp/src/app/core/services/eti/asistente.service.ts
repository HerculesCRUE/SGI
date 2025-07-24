import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IAsistente } from '@core/models/eti/asistente';
import { IAsistenteResponse } from '@core/services/eti/asistente/asistente-response';
import { ASISTENTE_RESPONSE_CONVERTER } from '@core/services/eti/asistente/asistente-response.converter';
import { environment } from '@env';
import { CreateCtor, FindByIdCtor, mixinCreate, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';

// tslint:disable-next-line: variable-name
const _AsistenteServiceMixinBase:
  CreateCtor<IAsistente, IAsistente, IAsistenteResponse, IAsistenteResponse> &
  UpdateCtor<number, IAsistente, IAsistente, IAsistenteResponse, IAsistenteResponse> &
  FindByIdCtor<number, IAsistente, IAsistenteResponse> &
  typeof SgiRestBaseService =
  mixinFindById(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        ASISTENTE_RESPONSE_CONVERTER,
        ASISTENTE_RESPONSE_CONVERTER
      ),
      ASISTENTE_RESPONSE_CONVERTER,
      ASISTENTE_RESPONSE_CONVERTER
    ),
    ASISTENTE_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class AsistenteService extends _AsistenteServiceMixinBase {
  private static readonly MAPPING = '/asistentes';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eti}${AsistenteService.MAPPING}`,
      http
    );
  }

}
