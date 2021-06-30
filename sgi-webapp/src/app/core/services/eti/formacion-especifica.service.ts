import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FormacionEspecifica } from '@core/models/eti/formacion-especifica';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class FormacionEspecificaService extends SgiRestService<number, FormacionEspecifica> {
  private static readonly MAPPING = '/formacionespecificas';

  constructor(protected http: HttpClient) {
    super(
      FormacionEspecificaService.name,
      `${environment.serviceServers.eti}${FormacionEspecificaService.MAPPING}`,
      http
    );
  }
}
