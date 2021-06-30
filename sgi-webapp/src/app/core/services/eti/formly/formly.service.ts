import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { IFormly } from "@core/models/eti/formly";
import { environment } from "@env";
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from "@sgi/framework/http";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { IFormlyResponse } from "./formly-response";
import { FORMLY_RESPONSE_CONVERTER } from "./formly-response.converter";


// tslint:disable-next-line: variable-name
const _FormlyServiceMixinBase:
  FindByIdCtor<number, IFormly, IFormlyResponse> &
  typeof SgiRestBaseService =
  mixinFindById(
    SgiRestBaseService,
    FORMLY_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class FormlyService extends _FormlyServiceMixinBase {
  private static readonly MAPPING = '/formlies';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eti}${FormlyService.MAPPING}`,
      http,
    );
  }

  findByNombre(nombre: string): Observable<IFormly> {
    return this.get<IFormlyResponse>(`${this.endpointUrl}/${nombre}`).pipe(
      map(response => FORMLY_RESPONSE_CONVERTER.toTarget(response))
    );
  }

}
