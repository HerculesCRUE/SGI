import { Component, OnInit } from '@angular/core';
import { FormlyFieldConfig } from '@ngx-formly/core';
import { FieldType } from '@ngx-formly/material/form-field';

@Component({
  template: `
    <sgi-i18n-input
      [id]="id"
      [required]="to.required"
      [formControl]="formControl"
      [errorStateMatcher]="errorStateMatcher"
      [plainValue]="true"
      [formlyAttributes]="field"
      [placeholder]="to.placeholder"
      [tabindex]="to.tabindex"
      >
    </sgi-i18n-input>
  `
})
export class I18nInputTypeComponent extends FieldType implements OnInit {

  defaultOptions: FormlyFieldConfig = {
    defaultValue: []
  }

}
