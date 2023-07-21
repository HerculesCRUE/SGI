import { ChangeDetectionStrategy, Component } from '@angular/core';
import { MatSelectChange } from '@angular/material/select';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ISexo } from '@core/models/sgp/sexo';
import { SexoPublicService } from '@core/services/sgp/sexo-public.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FieldType } from '@ngx-formly/material/form-field';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

const MSG_ERROR_INIT = marker('error.load');

@Component({
  template: `
    <mat-select
      [id]="id"
      [formControl]="formControl"
      [formlyAttributes]="field"
      [placeholder]="to.placeholder"
      [tabIndex]="to.tabindex"
      [required]="to.required"
      [compareWith]="to.compareWith"
      [multiple]="to.multiple"
      (selectionChange)="change($event)"
      [errorStateMatcher]="errorStateMatcher"
      [aria-labelledby]="_getAriaLabelledby()"
      [disableOptionCentering]="to.disableOptionCentering"
    >
      <ng-container *ngIf="sexos$ | formlySelectOptions: field | async as selectOptions">
        <ng-container *ngFor="let item of selectOptions">
          <mat-option [value]="item.value" [disabled]="item.disabled">{{ item.label }}</mat-option>
        </ng-container>
      </ng-container>
    </mat-select>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectSexosPublicTypeComponent extends FieldType {

  defaultOptions = {
    templateOptions: {
      options: [],
      compareWith(o1: any, o2: any) {
        return o1 === o2;
      },
    },
  };

  sexos$: Observable<ISexo[]> = of([]);

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly sexoService: SexoPublicService
  ) {
    super();

    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    this.sexos$ = this.sexoService.findAll(findOptions).pipe(
      map(response => response.items),
      catchError((error) => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR_INIT);
        return of([]);
      })
    );
  }

  change($event: MatSelectChange) {
    this.to.change?.(this.field, $event);
  }

  _getAriaLabelledby() {
    if (this.to.attributes?.['aria-labelledby']) {
      return this.to.attributes['aria-labelledby'];
    }
    return this.formField?._labelId;
  }

}
