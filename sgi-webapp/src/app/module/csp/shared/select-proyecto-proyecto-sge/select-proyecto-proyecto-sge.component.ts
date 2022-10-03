import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { ProyectoProyectoSgeService } from '@core/services/csp/proyecto-proyecto-sge.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { from, Observable } from 'rxjs';
import { concatMap, map, switchMap, toArray } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-proyecto-proyecto-sge',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectProyectoProyectoSgeComponent
    }
  ]
})
export class SelectProyectoProyectoSgeComponent extends SelectServiceComponent<IProyectoProyectoSge> {

  @Input()
  get proyectoSgeRef(): string {
    return this._proyectoSgeRef;
  }
  set proyectoSgeRef(value: string) {
    const changes = this._proyectoSgeRef !== value;
    this._proyectoSgeRef = value;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _proyectoSgeRef: string;

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    private service: ProyectoProyectoSgeService,
    private proyectoService: ProyectoService
  ) {
    super(defaultErrorStateMatcher, ngControl);
    // Override default displayWith
    this.displayWith = (option) => option?.proyecto?.titulo ?? '';
  }

  protected loadServiceOptions(): Observable<IProyectoProyectoSge[]> {
    const findOptions: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('proyectoSgeRef', SgiRestFilterOperator.EQUALS, this.proyectoSgeRef),
      sort: new RSQLSgiRestSort('proyecto.titulo', SgiRestSortDirection.ASC)
    };
    return this.service.findAll(findOptions)
      .pipe(
        map(response => response.items),
        switchMap(proyectosProyectoSge =>
          from(proyectosProyectoSge)
            .pipe(
              concatMap(proyectoProyectoSge =>
                this.proyectoService.findById(proyectoProyectoSge?.proyecto?.id)
                  .pipe(
                    map(proyecto => {
                      proyectoProyectoSge.proyecto = proyecto;
                      return proyectoProyectoSge;
                    })
                  )
              ),
              toArray()
            )
        )
      );
  }
}
