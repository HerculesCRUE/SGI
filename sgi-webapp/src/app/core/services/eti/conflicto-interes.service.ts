import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONFLICTO_INTERESES_CONVERTER } from '@core/converters/eti/conflicto-intereses.converter';
import { IConflictoInteresBackend } from '@core/models/eti/backend/conflicto-intereses-backend';
import { IConflictoInteres } from '@core/models/eti/conflicto-interes';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ConflictoInteresService extends SgiMutableRestService<number, IConflictoInteresBackend, IConflictoInteres> {
  private static readonly MAPPING = '/conflictosinteres';

  constructor(protected http: HttpClient) {
    super(
      ConflictoInteresService.name,
      `${environment.serviceServers.eti}${ConflictoInteresService.MAPPING}`,
      http,
      CONFLICTO_INTERESES_CONVERTER
    );
  }
}
