import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatSelectChange } from '@angular/material/select';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IProvincia } from '@core/models/sgo/provincia';
import { ProvinciaService } from '@core/services/sgo/provincia/provincia.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FieldType } from '@ngx-formly/material/form-field';
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
      <ng-container *ngIf="provincias$ | formlySelectOptions: field | async as selectOptions">
        <ng-container *ngFor="let item of selectOptions">
          <mat-option [value]="item.value" [disabled]="item.disabled">{{ item.label }}</mat-option>
        </ng-container>
      </ng-container>
    </mat-select>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectProvinciasTypeComponent extends FieldType implements OnInit {
  defaultOptions = {
    templateOptions: {
      options: [],
      compareWith(o1: any, o2: any) {
        return o1 === o2;
      },
    },
  };

  provincias$: Observable<IProvincia[]> = of([]);

  change($event: MatSelectChange) {
    this.to.change?.(this.field, $event);
  }

  _getAriaLabelledby() {
    if (this.to.attributes?.['aria-labelledby']) {
      return this.to.attributes['aria-labelledby'];
    }

    return this.formField?._labelId;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly provinciaService: ProvinciaService
  ) {
    super();
  }

  ngOnInit() {
    const valuePropertyBound = this.getComunidadFormControl()?.value;
    if (valuePropertyBound) {
      this.provincias$ = this.findByComunidadAutonomaId(valuePropertyBound);
    }

    this.onChangesPropertyBound();
  }

  private onChangesPropertyBound(): void {
    this.getComunidadFormControl().valueChanges.subscribe(comunidadId => {
      this.field.formControl.setValue(null);
      if (comunidadId) {
        this.provincias$ = this.findByComunidadAutonomaId(comunidadId);
      } else {
        this.provincias$ = of([]);
      }
    });
  }

  private getComunidadFormControl(): FormControl {
    return this.form.get(this.to.propertyBound) as FormControl;
  }

  private findByComunidadAutonomaId(comunidadAutonomaId: string): Observable<IProvincia[]> {
    return this.provinciaService.findByComunidadAutonomaId(comunidadAutonomaId).pipe(
      map(response => response.items),
      catchError((error) => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR_INIT);
        return of([]);
      })
    );
  }
}
