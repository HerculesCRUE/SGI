import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { IProyectoPeriodoJustificacion } from '@core/models/csp/proyecto-periodo-justificacion';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-proyecto-periodo-justificacion',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectProyectoPeriodoJustificacionComponent
    }
  ]
})
export class SelectProyectoPeriodoJustificacionComponent extends SelectServiceComponent<IProyectoPeriodoJustificacion> {

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
    private service: ProyectoService
  ) {
    super(defaultErrorStateMatcher, ngControl);
    // Override default displayWith
    this.displayWith = (option) => option?.identificadorJustificacion ?? '';
  }

  protected loadServiceOptions(): Observable<IProyectoPeriodoJustificacion[]> {
    if (!this.proyectoId) {
      return of([]);
    }
    const findOptions: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('identificadorJustificacion', SgiRestFilterOperator.IS_NOT_NULL, null),
      sort: new RSQLSgiRestSort('identificadorJustificacion', SgiRestSortDirection.ASC)
    };
    return this.service.findAllPeriodoJustificacion(this.proyectoId, findOptions)
      .pipe(
        map(response => response.items),
      );
  }
}
