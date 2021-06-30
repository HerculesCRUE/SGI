import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CargoComite } from '@core/models/eti/cargo-comite';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root',
})
export class CargoComiteService extends SgiRestService<number, CargoComite> {
  private static readonly MAPPING = '/cargocomites';

  constructor(protected http: HttpClient) {
    super(
      CargoComiteService.name,
      `${environment.serviceServers.eti}${CargoComiteService.MAPPING}`,
      http
    );
  }
}
