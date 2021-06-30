import { coerceNumberProperty } from '@angular/cdk/coercion';
import { ChangeDetectionStrategy, Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { ITipoFinalidad } from '@core/models/csp/tipos-configuracion';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { TipoFinalidadService } from '@core/services/csp/tipo-finalidad.service';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-tipo-finalidad',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectTipoFinalidadComponent
    }
  ]
})
export class SelectTipoFinalidadComponent extends SelectServiceComponent<ITipoFinalidad> {

  private requestByModeloEjecucion = false;

  /** Restrict values to an Id of ModeloEjecucion. Default: No restriction */
  @Input()
  get modeloEjecucionId(): number {
    return this._modeloEjecucionId;
  }
  set modeloEjecucionId(value: number) {
    this.requestByModeloEjecucion = true;
    const newValue = coerceNumberProperty(value);
    const changes = this._modeloEjecucionId !== newValue;
    this._modeloEjecucionId = newValue;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _modeloEjecucionId: number;

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    private service: TipoFinalidadService,
    private modeloEjecucionService: ModeloEjecucionService,
    @Self() @Optional() ngControl: NgControl) {
    super(defaultErrorStateMatcher, ngControl);
  }

  protected loadServiceOptions(): Observable<ITipoFinalidad[]> {
    if (this.requestByModeloEjecucion) {
      // If empty, null or zero, an empty array is returned
      if (!!!this.modeloEjecucionId) {
        return of([]);
      }
      const findOptions: SgiRestFindOptions = {
        filter: new RSQLSgiRestFilter('tipoFinalidad.activo', SgiRestFilterOperator.EQUALS, 'true'),
        sort: new RSQLSgiRestSort('tipoFinalidad.nombre', SgiRestSortDirection.ASC)
      };
      return this.modeloEjecucionService.findModeloTipoFinalidad(this.modeloEjecucionId, findOptions).pipe(
        map(response => response.items.map(item => item.tipoFinalidad))
      );
    }
    else {
      const findOptions: SgiRestFindOptions = {
        sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
      };
      return this.service.findAll(findOptions).pipe(map(response => response.items));
    }
  }
}
