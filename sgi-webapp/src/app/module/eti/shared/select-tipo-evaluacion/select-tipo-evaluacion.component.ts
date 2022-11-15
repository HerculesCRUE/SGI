import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { TipoEvaluacion, TIPO_EVALUACION } from '@core/models/eti/tipo-evaluacion';
import { TipoEvaluacionService } from '@core/services/eti/tipo-evaluacion.service';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-tipo-evaluacion',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectTipoEvaluacionComponent
    }
  ]
})
export class SelectTipoEvaluacionComponent extends SelectServiceComponent<TipoEvaluacion> {

  @Input()
  get isTipoEvaluacion(): Boolean {
    return this._isTipoEvaluacion;
  }
  set isTipoEvaluacion(value: Boolean) {
    const changes = this._isTipoEvaluacion !== value;
    this._isTipoEvaluacion = value;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }

  // tslint:disable-next-line: variable-name
  private _isTipoEvaluacion: Boolean

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    private service: TipoEvaluacionService,
    @Self() @Optional() ngControl: NgControl) {
    super(defaultErrorStateMatcher, ngControl);
  }

  protected loadServiceOptions(): Observable<TipoEvaluacion[]> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };

    if (this.notIsNullAndNotUndefined(this.isTipoEvaluacion)) {
      if (this.isTipoEvaluacion) {
        findOptions.filter = (new RSQLSgiRestFilter('id', SgiRestFilterOperator.EQUALS, TIPO_EVALUACION.MEMORIA.toString()))
          .or(new RSQLSgiRestFilter('id', SgiRestFilterOperator.EQUALS, TIPO_EVALUACION.RETROSPECTIVA.toString()));
      } else {
        findOptions.filter = (new RSQLSgiRestFilter('id', SgiRestFilterOperator.EQUALS, TIPO_EVALUACION.SEGUIMIENTO_ANUAL.toString()))
          .or(new RSQLSgiRestFilter('id', SgiRestFilterOperator.EQUALS, TIPO_EVALUACION.SEGUIMIENTO_FINAL.toString()));
      }
    }
    return this.service.findAll(findOptions).pipe(map(response => response.items));
  }

  protected notIsNullAndNotUndefined(value: any): boolean {
    return value !== null && value !== undefined;
  }

}
