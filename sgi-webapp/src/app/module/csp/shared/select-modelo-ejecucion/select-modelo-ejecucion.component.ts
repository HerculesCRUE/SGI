import { ChangeDetectionStrategy, Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { IModeloEjecucion } from '@core/models/csp/tipos-configuracion';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { ModeloUnidadService } from '@core/services/csp/modelo-unidad.service';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-modelo-ejecucion',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectModeloEjecucionComponent
    }
  ]
})
export class SelectModeloEjecucionComponent extends SelectServiceComponent<IModeloEjecucion> {

  private requestByUnidadGestion = false;

  /** Restrict values to an Id of UnidadGestion. Default: No restriction */
  @Input()
  get unidadGestionRef(): string {
    return this._unidadGestionRef;
  }
  set unidadGestionRef(value: string) {
    this.requestByUnidadGestion = true;
    const changes = this._unidadGestionRef !== value;
    this._unidadGestionRef = value;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _unidadGestionRef: string;

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    private service: ModeloEjecucionService,
    private unidadModeloService: ModeloUnidadService,
    @Self() @Optional() ngControl: NgControl) {
    super(defaultErrorStateMatcher, ngControl);
  }

  protected loadServiceOptions(): Observable<IModeloEjecucion[]> {
    if (this.requestByUnidadGestion) {
      // If empty, or null, an empty array is returned
      if (!!!this.unidadGestionRef) {
        return of([]);
      }
      const findOptions: SgiRestFindOptions = {
        filter: new RSQLSgiRestFilter('unidadGestionRef', SgiRestFilterOperator.EQUALS, this.unidadGestionRef?.toString()),
        sort: new RSQLSgiRestSort('modeloEjecucion.nombre', SgiRestSortDirection.ASC)
      };
      return this.unidadModeloService.findAll(findOptions).pipe(
        map(response => response.items.map(item => item.modeloEjecucion))
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
