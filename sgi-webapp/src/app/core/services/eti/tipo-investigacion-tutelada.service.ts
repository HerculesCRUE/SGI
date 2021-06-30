import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoInvestigacionTutelada } from '@core/models/eti/tipo-investigacion-tutelada';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class TipoInvestigacionTuteladaService extends SgiRestService<number, ITipoInvestigacionTutelada> {
  private static readonly MAPPING = '/tipoinvestigaciontuteladas';

  constructor(protected http: HttpClient) {
    super(
      TipoInvestigacionTuteladaService.name,
      `${environment.serviceServers.eti}${TipoInvestigacionTuteladaService.MAPPING}`,
      http
    );
  }
}
