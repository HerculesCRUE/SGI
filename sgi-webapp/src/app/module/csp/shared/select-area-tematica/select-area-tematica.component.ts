import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { AreaTematicaService } from '@core/services/csp/area-tematica.service';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-area-tematica',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectAreaTematicaComponent
    }
  ]
})
export class SelectAreaTematicaComponent extends SelectServiceComponent<IAreaTematica> {

  @Input()
  get todos(): boolean {
    return this._todos;
  }
  set todos(value: boolean) {
    this._todos = value;
  }
  // tslint:disable-next-line: variable-name
  private _todos: boolean;

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    private service: AreaTematicaService,
    @Self() @Optional() ngControl: NgControl) {
    super(defaultErrorStateMatcher, ngControl);
  }

  protected loadServiceOptions(): Observable<IAreaTematica[]> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    if (this._todos) {
      return this.service.findTodos(findOptions).pipe(map(response => response.items));
    } else {
      return this.service.findAllGrupo(findOptions).pipe(map(response => response.items));
    }
  }

}
