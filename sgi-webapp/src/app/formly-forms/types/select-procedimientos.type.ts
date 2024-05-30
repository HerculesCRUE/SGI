import { ChangeDetectionStrategy, Component } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatSelectChange } from '@angular/material/select';
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
      [multiple]="to.multiple"
      (selectionChange)="change($event)"
      [errorStateMatcher]="errorStateMatcher"
      [aria-labelledby]="_getAriaLabelledby()"
      [disableOptionCentering]="to.disableOptionCentering"
    >
        <ng-container *ngFor="let item of selectOptions">
          <mat-option [value]="item.numeroProcedimiento" [disabled]="item.disabled">{{ item.numeroProcedimiento }}</mat-option>
        </ng-container>
    </mat-select>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectProcedimientosTypeComponent extends FieldType {
  defaultOptions = {
    templateOptions: {
      options: [],
      compareWith(o1: any, o2: any) {
        return o1 === o2;
      },
    },
  };

  get selectOptions(): any[] {
    return this.field?.templateOptions?.options as any[];
  }

  change($event: MatSelectChange) {
    this.getSeveridadFormControl().setValue(this.selectOptions.find(option => option.numeroProcedimiento == $event.value).categoriaSeveridad);
    this.to.change?.(this.field, $event);
  }

  _getAriaLabelledby() {
    if (this.to.attributes?.['aria-labelledby']) {
      return this.to.attributes['aria-labelledby'];
    }

    return this.formField?._labelId;
  }

  private getSeveridadFormControl(): FormControl {
    return this.form.get('severidad') as FormControl;
  }
}
