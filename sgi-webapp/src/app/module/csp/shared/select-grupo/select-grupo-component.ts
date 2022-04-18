import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { IGrupo } from '@core/models/csp/grupo';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-grupo[personaRef]',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectGrupoComponent
    }
  ]
})
export class SelectGrupoComponent extends SelectServiceComponent<IGrupo> {

  /** Restrict values to a personaRef */
  @Input()
  get personaRef(): string {
    return this._personaRef;
  }
  set personaRef(value: string) {
    const newValue = value;
    const changes = this._personaRef !== newValue;
    this._personaRef = newValue;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _personaRef: string;

  @Input()
  get excluded(): IGrupo[] {
    return this._excluded;
  }
  set excluded(value: IGrupo[]) {
    if (Array.isArray(value)) {
      this._excluded = value;
    }
  }
  // tslint:disable-next-line: variable-name
  private _excluded: IGrupo[] = [];

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    private grupoService: GrupoService
  ) {
    super(defaultErrorStateMatcher, ngControl);

    this.disableWith = (option) => {
      if (this.excluded.length) {
        return this.excluded.some((excluded) => excluded.id === option.id);
      }
      return false;
    };

  }

  protected loadServiceOptions(): Observable<IGrupo[]> {
    // If empty, null or zero, an empty array is returned
    if (!!!this.personaRef) {
      return of([]);
    }

    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC),
      filter: new RSQLSgiRestFilter('responsable', SgiRestFilterOperator.EQUALS, this.personaRef)
        .or(new RSQLSgiRestFilter('personaAutorizada', SgiRestFilterOperator.EQUALS, this.personaRef))
    };

    return this.grupoService.findAll(findOptions).pipe(
      map(response => response.items.map(item => item))
    );
  }

}
