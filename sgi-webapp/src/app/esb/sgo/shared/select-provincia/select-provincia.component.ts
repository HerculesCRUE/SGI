import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { IProvincia } from '@core/models/sgo/provincia';
import { ProvinciaService } from '@core/services/sgo/provincia/provincia.service';
import { Observable, of } from 'rxjs';
import { map, tap } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-provincia[comunidadAutonomaRef]',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelecProvinciaComponent
    }
  ]
})
export class SelecProvinciaComponent extends SelectServiceComponent<IProvincia> {

  /** Restrict values to a comunidadAutonomaRef */
  @Input()
  get comunidadAutonomaRef(): string {
    return this._comunidadAutonomaRef;
  }
  set comunidadAutonomaRef(value: string) {
    const newValue = value;
    const changes = this._comunidadAutonomaRef !== newValue;
    this._comunidadAutonomaRef = newValue;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _comunidadAutonomaRef: string;

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    private provinciaService: ProvinciaService
  ) {
    super(defaultErrorStateMatcher, ngControl);

  }

  protected loadServiceOptions(): Observable<IProvincia[]> {
    // If empty, null or zero, an empty array is returned
    if (!!!this.comunidadAutonomaRef) {
      return of([]);
    }

    return this.provinciaService.findByComunidadAutonomaId(this.comunidadAutonomaRef).pipe(
      map(response => response.items.map(item => item)),
      tap(response => {
        if (!response.map(provincia => provincia.id).includes(this.value?.id)) {
          this.resetSelection();
        }
      })
    );
  }

}
