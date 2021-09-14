import { NgxMatDatetimeInput } from '@angular-material-components/datetime-picker';
import { AfterViewInit, Component, TemplateRef, ViewChild } from '@angular/core';
import { MatInput } from '@angular/material/input';
import { ɵdefineHiddenProp as defineHiddenProp } from '@ngx-formly/core';
import { FieldType } from '@ngx-formly/material/form-field';

@Component({
  template: `
    <input matInput
      [id]="id"
      [errorStateMatcher]="errorStateMatcher"
      [formControl]="formControl"
      [ngxMatDatetimePicker]="picker"
      [formlyAttributes]="field"
      [placeholder]="to.placeholder"
      [tabindex]="to.tabindex"
      [readonly]="to.readonly"
      [required]="to.required"
      (dateInput)="to.datepickerOptions.dateInput(field, $event)"
      (dateChange)="to.datepickerOptions.dateChange(field, $event)">
    <ng-template #datepickerToggle>
      <mat-datepicker-toggle [disabled]="to.disabled" [for]="picker"></mat-datepicker-toggle>
    </ng-template>

    <ngx-mat-datetime-picker #picker
      [showSeconds]="to.showSeconds"
      [defaultTime]="to.defaultTime"
     >
    </ngx-mat-datetime-picker>
  `,
})
export class DateTimePickerTypeComponent extends FieldType implements AfterViewInit {
  @ViewChild(MatInput, { static: true }) formFieldControl!: MatInput;
  @ViewChild(NgxMatDatetimeInput) datepickerInput!: NgxMatDatetimeInput<any>;
  @ViewChild('datepickerToggle') datepickerToggle!: TemplateRef<any>;

  defaultOptions = {
    templateOptions: {
      datepickerOptions: {
        datepickerTogglePosition: 'suffix',
        dateInput: () => { },
        dateChange: () => { },
      },
      showSeconds: true,
      defaultTime: [0, 0, 0]
    },
  };

  ngAfterViewInit() {
    super.ngAfterViewInit();
    // temporary fix for https://github.com/angular/material2/issues/6728
    // TODO tener cuidado si cambiamos de versión de formly porque este hack cambiará
    (this.datepickerInput as any)._formField = this.formField;
    setTimeout(() => {
      defineHiddenProp(this.field, '_mat' + this.to.datepickerOptions.datepickerTogglePosition, this.datepickerToggle);
      (this.options as any)._markForCheck(this.field);
    });
  }
}
