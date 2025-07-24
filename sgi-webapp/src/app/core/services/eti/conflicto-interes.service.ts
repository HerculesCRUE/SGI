import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConflictoInteres } from '@core/models/eti/conflicto-interes';
import { IConflictoInteresResponse } from '@core/services/eti/conflicto-intereses/conflicto-intereses-response';
import { CONFLICTO_INTERESES_RESPONSE_CONVERTER } from '@core/services/eti/conflicto-intereses/conflicto-intereses-response.converter';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';

const _ConflictoInteresesServiceMixinBase:
  CreateCtor<IConflictoInteres, IConflictoInteres, IConflictoInteresResponse, IConflictoInteresResponse> &
  UpdateCtor<number, IConflictoInteres, IConflictoInteres, IConflictoInteresResponse, IConflictoInteresResponse> &
  FindByIdCtor<number, IConflictoInteres, IConflictoInteresResponse> &
  FindAllCtor<IConflictoInteres, IConflictoInteresResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          CONFLICTO_INTERESES_RESPONSE_CONVERTER,
          CONFLICTO_INTERESES_RESPONSE_CONVERTER
        ),
        CONFLICTO_INTERESES_RESPONSE_CONVERTER,
        CONFLICTO_INTERESES_RESPONSE_CONVERTER
      ),
      CONFLICTO_INTERESES_RESPONSE_CONVERTER
    ),
    CONFLICTO_INTERESES_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ConflictoInteresService extends _ConflictoInteresesServiceMixinBase {
  private static readonly MAPPING = '/conflictosinteres';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eti}${ConflictoInteresService.MAPPING}`,
      http
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }
}
