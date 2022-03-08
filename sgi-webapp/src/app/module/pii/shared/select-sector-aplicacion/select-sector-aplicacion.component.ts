import { PlatformLocation } from '@angular/common';
import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { ISectorAplicacion } from '@core/models/pii/sector-aplicacion';
import { SectorAplicacionService } from '@core/services/pii/sector-aplicacion/sector-aplicacion.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SectorAplicacionModalComponent } from '../../sector-aplicacion/sector-aplicacion-modal/sector-aplicacion-modal.component';

@Component({
  selector: 'sgi-select-sector-aplicacion',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectSectorAplicacionComponent
    }
  ]
})
export class SelectSectorAplicacionComponent extends SelectServiceExtendedComponent<ISectorAplicacion> {

  @Input()
  get excluded(): ISectorAplicacion[] {
    return this._excluded;
  }
  set excluded(value: ISectorAplicacion[]) {
    if (Array.isArray(value)) {
      this._excluded = value;
    }
  }
  // tslint:disable-next-line: variable-name
  private _excluded: ISectorAplicacion[] = [];

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    platformLocation: PlatformLocation,
    dialog: MatDialog,
    private sectorAplicacionService: SectorAplicacionService,
    private authService: SgiAuthService,
  ) {
    super(defaultErrorStateMatcher, ngControl, platformLocation, dialog);
    this.disableWith = (option) => {
      if (this.excluded.length) {
        return this.excluded.some((excluded) => excluded.id === option.id);
      }
      return false;
    };
    this.addTarget = SectorAplicacionModalComponent;
  }

  protected loadServiceOptions(): Observable<ISectorAplicacion[]> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    return this.sectorAplicacionService.findAll(findOptions).pipe(map(({ items }) => items));
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthorityForAnyUO('PII-SEA-C');
  }
}
