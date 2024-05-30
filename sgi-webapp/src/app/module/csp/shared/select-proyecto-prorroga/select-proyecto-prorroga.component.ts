import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { IProyectoProrroga } from '@core/models/csp/proyecto-prorroga';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-proyecto-prorroga',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectProyectoProrrogaComponent
    }
  ]
})
export class SelectProyectoProrrogaComponent extends SelectServiceComponent<IProyectoProrroga> {

  @Input()
  get proyectoId(): number {
    return this._proyectoId;
  }
  set proyectoId(value: number) {
    const changes = this._proyectoId !== value;
    this._proyectoId = value;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _proyectoId: number;

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    private service: ProyectoService,
    private luxonDatePipe: LuxonDatePipe
  ) {
    super(defaultErrorStateMatcher, ngControl);
    // Override default displayWith
    this.displayWith = (option) => option ? `${option.numProrroga?.toString()}  - ${this.luxonDatePipe.transform(LuxonUtils.toBackend(option.fechaConcesion, true), 'shortDate')}` : '';
  }

  protected loadServiceOptions(): Observable<IProyectoProrroga[]> {
    if (!this.proyectoId) {
      return of([]);
    }

    return this.service.findAllProyectoProrrogaProyecto(this.proyectoId)
      .pipe(
        map(response => response.items),
      );
  }
}
