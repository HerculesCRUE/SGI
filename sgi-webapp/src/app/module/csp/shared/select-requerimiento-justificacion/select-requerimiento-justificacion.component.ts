import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { formatRequerimientoJustificacionNombre } from '../../ejecucion-economica/pipes/requerimiento-justificacion-nombre.pipe';

@Component({
  selector: 'sgi-select-requerimiento-justificacion',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectRequerimientoJustificacionComponent
    }
  ]
})
export class SelectRequerimientoJustificacionComponent extends SelectServiceComponent<IRequerimientoJustificacion> {

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

  @Input()
  get numRequerimiento(): number {
    return this.numRequerimiento;
  }
  set numRequerimiento(value: number) {
    const changes = this._numRequerimiento !== value;
    this._numRequerimiento = value;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _numRequerimiento: number;

  @Input()
  get excluded(): IRequerimientoJustificacion[] {
    return this._excluded;
  }
  set excluded(value: IRequerimientoJustificacion[]) {
    if (Array.isArray(value)) {
      this._excluded = value;
    }
  }
  // tslint:disable-next-line: variable-name
  private _excluded: IRequerimientoJustificacion[] = [];

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    private service: ProyectoService
  ) {
    super(defaultErrorStateMatcher, ngControl);
    // Override default displayWith
    this.displayWith = (option) => formatRequerimientoJustificacionNombre(option) ?? '';
    this.disableWith = (option) => {
      if (this.excluded.length) {
        return this.excluded.some((excluded) => excluded.id === option.id);
      }
      return false;
    };
  }

  protected loadServiceOptions(): Observable<IRequerimientoJustificacion[]> {
    if (!this.proyectoId) {
      return of([]);
    }
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('numRequerimiento', SgiRestSortDirection.ASC)
    };
    return this.service.findRequerimientosJustificacion(this.proyectoId, findOptions)
      .pipe(
        map(response => response.items),
      );
  }
}
