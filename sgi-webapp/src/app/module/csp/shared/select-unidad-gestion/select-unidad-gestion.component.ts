import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-unidad-gestion',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectUnidadGestionComponent
    }
  ]
})
export class SelectUnidadGestionComponent extends SelectServiceComponent<IUnidadGestion> {

  private filterByAuthorities = false;

  /** Restrict values to the current user. Default: true */
  @Input()
  get restricted(): boolean {
    return this._restricted;
  }
  set restricted(value: boolean) {
    const newValue = coerceBooleanProperty(value);
    const changes = this._restricted !== newValue;
    this._restricted = newValue;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _restricted = true;

  /** Restrict values to the current user. Default: true */
  @Input()
  get authorities(): string | string[] {
    return this._authorities;
  }
  set authorities(value: string | string[]) {
    this.filterByAuthorities = true;
    const changes = this._authorities !== value;
    this._authorities = value;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _authorities: string | string[];

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    private service: UnidadGestionService,
    private authService: SgiAuthService,
    @Self() @Optional() ngControl: NgControl) {
    super(defaultErrorStateMatcher, ngControl);
  }

  protected loadServiceOptions(): Observable<IUnidadGestion[]> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    const find$ = this.restricted ? this.service.findAllRestringidos(findOptions) : this.service.findAll(findOptions);
    return find$.pipe(
      map(response => {
        if (this.filterByAuthorities) {
          const authorities = this.authorities;
          if (Array.isArray(authorities)) {
            return response.items.filter(
              unidad => authorities.some((authority) => this.authService.hasAuthority(`${authority}_${unidad.id}`))
            );
          }
          else {
            return response.items.filter(unidad => this.authService.hasAuthority(`${authorities}_${unidad.id}`));
          }
        }
        return response.items;
      })
    );
  }

}
