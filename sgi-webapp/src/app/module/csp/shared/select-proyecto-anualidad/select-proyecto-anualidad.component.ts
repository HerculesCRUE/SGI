import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { IProyectoAnualidad } from '@core/models/csp/proyecto-anualidad';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-proyecto-anualidad',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectProyectoAnualidadComponent
    }
  ]
})
export class SelectProyectoAnualidadComponent extends SelectServiceComponent<IProyectoAnualidad> {

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
    this.displayWith = (option) => option?.anio?.toString() ?? '';
  }

  protected loadServiceOptions(): Observable<IProyectoAnualidad[]> {
    if (!this.proyectoId) {
      return of([]);
    }

    return this.service.findAllProyectoAnualidadesByProyectoId(this.proyectoId)
      .pipe(
        map(response => response.items),
      );
  }
}
