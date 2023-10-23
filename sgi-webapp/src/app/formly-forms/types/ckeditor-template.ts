import { Component } from '@angular/core';
import { FieldType } from '@ngx-formly/material/form-field';
import { SgiCkEditorConfig } from '@shared/sgi-ckeditor-config';
import Editor from 'ckeditor5-custom-build/build/ckeditor';

@Component({
  template: `
      <div fxLayout="column" fxFlex="100" fxLayoutGap="10px" >
              <span class="ckeditor-label">{{to.name}}<span *ngIf="to.required"
          [class]="(formControl.touched && formControl.errors?.required) ? 'warn' : ''">
          *</span></span>
      <ckeditor [class]="formControl.touched && formControl.errors ? 'ck-editor-border-warn' : ''" [editor]="CkEditor" [config]="configCkEditor" [formControl]="formControl">
      </ckeditor>
    </div>
  `})
export class CKEditorTemplate extends FieldType {
  public CkEditor = Editor;
  public readonly configCkEditor = SgiCkEditorConfig.defaultConfig;

}
