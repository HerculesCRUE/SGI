import { PlatformLocation } from '@angular/common';
import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { TipoPropiedad } from '@core/enums/tipo-propiedad';
import { IViaProteccion } from '@core/models/pii/via-proteccion';
import { LanguageService } from '@core/services/language.service';
import { ViaProteccionService } from '@core/services/pii/via-proteccion/via-proteccion.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { ViaProteccionModalComponent } from '../../via-proteccion/via-proteccion-modal/via-proteccion-modal.component';

@Component({
  selector: 'sgi-select-via-proteccion',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectViaProteccionComponent
    }
  ]
})
export class SelectViaProteccionComponent extends SelectServiceExtendedComponent<IViaProteccion> {

  @Input()
  get tipoPropiedad(): TipoPropiedad {
    return this._tipoPropiedad;
  }
  set tipoPropiedad(value: TipoPropiedad) {
    const changes = this._tipoPropiedad !== value;
    this._tipoPropiedad = value;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _tipoPropiedad: TipoPropiedad;

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    languageService: LanguageService,
    platformLocation: PlatformLocation,
    dialog: MatDialog,
    private authService: SgiAuthService,
    private viaProteccionService: ViaProteccionService
  ) {
    super(defaultErrorStateMatcher, ngControl, languageService, platformLocation, dialog);
    this.addTarget = ViaProteccionModalComponent;
  }

  protected loadServiceOptions(): Observable<IViaProteccion[]> {
    // If empty, or null, an empty array is returned
    if (!!!this.tipoPropiedad) {
      return of([]);
    }

    const findOptions: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('activo', SgiRestFilterOperator.EQUALS, 'true')
        .and('tipoPropiedad', SgiRestFilterOperator.EQUALS, this.tipoPropiedad),
      sort: new RSQLSgiRestSort('nombre.value', SgiRestSortDirection.ASC)
    };
    return this.viaProteccionService.findTodos(findOptions).pipe(map(({ items }) => items));
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthorityForAnyUO('PII-VPR-C');
  }
}
