import { Component, OnInit } from '@angular/core';
import { FormlyFieldConfig } from '@ngx-formly/core';
import { FieldType } from '@ngx-formly/material/form-field';

@Component({
  template: `
    <span class="ckeditor-label">{{to.name}}<span *ngIf="to.required"
      [class]="(formControl.touched && formControl.errors?.required) ? 'warn' : ''">
      *</span></span>
    <sgi-i18n-ckeditor
      [id]="id"
      [required]="to.required"
      [formControl]="formControl"
      [errorStateMatcher]="errorStateMatcher"
      [plainValue]="true"
      [formlyAttributes]="field"
      [placeholder]="to.placeholder"
      [tabindex]="to.tabindex"
      >
    </sgi-i18n-ckeditor>
  `
})
export class I18nCkeditorTypeComponent extends FieldType implements OnInit {

  defaultOptions: FormlyFieldConfig = {
    defaultValue: []
  };

}
