import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IChecklist } from '@core/models/eti/checklist';
import { environment } from '@env';
import { CreateCtor, FindByIdCtor, mixinCreate, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IChecklistRequest } from './checklist-request';
import { CHECKLIST_REQUEST_CONVERTER } from './checklist-request.converter';
import { IChecklistResponse } from './checklist-response';
import { CHECKLIST_RESPONSE_CONVERTER } from './checklist-response.converter';


// tslint:disable-next-line: variable-name
const _ChecklistServiceMixinBase:
  FindByIdCtor<number, IChecklist, IChecklistResponse> &
  CreateCtor<IChecklist, IChecklist, IChecklistRequest, IChecklistResponse> &
  typeof SgiRestBaseService =
  mixinCreate(
    mixinFindById(
      SgiRestBaseService,
      CHECKLIST_RESPONSE_CONVERTER
    ),
    CHECKLIST_REQUEST_CONVERTER,
    CHECKLIST_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ChecklistService extends _ChecklistServiceMixinBase {
  private static readonly MAPPING = '/checklists';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eti}${ChecklistService.MAPPING}`,
      http,
    );
  }

  updateRespuesta(id: number, respuesta: { [name: string]: any; }): Observable<IChecklist> {
    return this.http.patch<IChecklistResponse>(`${this.endpointUrl}/${id}/respuesta`, respuesta).pipe(
      map(checklist => CHECKLIST_RESPONSE_CONVERTER.toTarget(checklist))
    );
  }

  findByPersonaActual(): Observable<IChecklist> {
    return this.get<IChecklistResponse>(`${this.endpointUrl}/persona-actual`).pipe(
      map(checklist => CHECKLIST_RESPONSE_CONVERTER.toTarget(checklist))
    );
  }

}
