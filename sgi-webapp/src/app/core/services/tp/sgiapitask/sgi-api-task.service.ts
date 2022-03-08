import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISendEmailTask } from '@core/models/tp/send-email-task';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ISgiApiCronTaskResponse } from './sgi-api-cron-task-response';
import { ISgiApiInstantTaskResponse } from './sgi-api-instant-task-response';

// tslint:disable-next-line: variable-name
const _SgiApiTaskServiceMixinBase:
  FindByIdCtor<number, ISgiApiCronTaskResponse | ISgiApiInstantTaskResponse, ISgiApiCronTaskResponse | ISgiApiInstantTaskResponse> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    null,
  );

@Injectable({
  providedIn: 'root'
})
export class SgiApiTaskService extends _SgiApiTaskServiceMixinBase {
  private static readonly MAPPING = '/sgiapitasks';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.tp}${SgiApiTaskService.MAPPING}`,
      http
    );
  }

  findSendEmailTaskById(id: number): Observable<ISendEmailTask> {
    return this.findById(id).pipe(
      map((task: ISgiApiInstantTaskResponse) => {
        return {
          id: task.id,
          instant: LuxonUtils.fromBackend(task.instant)
        };
      })
    );
  }


}
