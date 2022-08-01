import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { IComunidadAutonoma } from '@core/models/sgo/comunidad-autonoma';
import { ComunidadAutonomaService } from '@core/services/sgo/comunidad-autonoma/comunidad-autonoma.service';
import { Observable, of } from 'rxjs';
import { map, tap } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-comunidad-autonoma[paisRef]',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectComunidadAutonomaComponent
    }
  ]
})
export class SelectComunidadAutonomaComponent extends SelectServiceComponent<IComunidadAutonoma> {

  /** Restrict values to a paisRef */
  @Input()
  get paisRef(): string {
    return this._paisRef;
  }
  set paisRef(value: string) {
    const newValue = value;
    const changes = this._paisRef !== newValue;
    this._paisRef = newValue;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _paisRef: string;

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    private comunidadAutonomaService: ComunidadAutonomaService
  ) {
    super(defaultErrorStateMatcher, ngControl);

  }

  protected loadServiceOptions(): Observable<IComunidadAutonoma[]> {
    // If empty, null or zero, an empty array is returned
    if (!!!this.paisRef) {
      return of([]);
    }

    return this.comunidadAutonomaService.findByPaisId(this.paisRef).pipe(
      map(response => response.items.map(item => item)),
      tap(response => {
        if (!response.map(comunidad => comunidad.id).includes(this.value?.id)) {
          this.resetSelection();
        }
      })
    );
  }

}
