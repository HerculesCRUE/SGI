import { ChangeDetectionStrategy, Component } from '@angular/core';
import { MatSelectChange } from '@angular/material/select';
import { TIPO_VALOR_SOCIAL_MAP } from '@core/models/eti/peticion-evaluacion';
import { FieldType } from '@ngx-formly/material/form-field';

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
      (selectionChange)="change($event)"
      [errorStateMatcher]="errorStateMatcher"
      [aria-labelledby]="_getAriaLabelledby()"
      [disableOptionCentering]="to.disableOptionCentering"
    >
        <ng-container *ngFor="let item of selectOptions | keyvalue">
          <mat-option [value]="item.key" [disabled]="item.disabled">{{ item.value | translate }}</mat-option>
        </ng-container>
    </mat-select>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TipoValorSocialComponent extends FieldType {
  defaultOptions = {
    templateOptions: {
      options: [],
      compareWith(o1: any, o2: any) {
        return o1 === o2;
      },
    },
  };

  get selectOptions() {
    return TIPO_VALOR_SOCIAL_MAP;
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
