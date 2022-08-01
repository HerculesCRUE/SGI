import { PlatformLocation } from '@angular/common';
import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { ITipoDocumento } from '@core/models/eer/tipo-documento';
import { TipoDocumentoService } from '@core/services/eer/tipo-documento/tipo-documento.service';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-eer-select-subtipo-documento',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectSubtipoDocumentoComponent
    }
  ]
})
export class SelectSubtipoDocumentoComponent extends SelectServiceExtendedComponent<ITipoDocumento> {

  @Input()
  get tipoDocumentoId(): number {
    return this._tipoDocumentoId;
  }
  set tipoDocumentoId(value: number) {
    const changes = this._tipoDocumentoId !== value;
    this._tipoDocumentoId = value;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _tipoDocumentoId: number;

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    platformLocation: PlatformLocation,
    private tipoDocumentoService: TipoDocumentoService,
  ) {
    super(defaultErrorStateMatcher, ngControl, platformLocation);
  }

  protected loadServiceOptions(): Observable<ITipoDocumento[]> {
    // If empty, or null, an empty array is returned
    if (!!!this.tipoDocumentoId) {
      return of([]);
    }
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    return this.tipoDocumentoService.findSubtipos(this.tipoDocumentoId, findOptions).pipe(
      map(({ items }) => items));
  }


  protected isAddAuthorized(): boolean {
    // Creation feature not implemented
    return false;
  }
}
