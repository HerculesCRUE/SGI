import { Component } from '@angular/core';
import { FieldType } from '@ngx-formly/material/form-field';

@Component({
  template: `
      <sgi-select-empresa
        [formControl]="formControl"
        [formlyAttributes]="field"
        [required]="to.required"
      >
      </sgi-select-empresa>
 `
})
export class SelectEmpresaTypeComponent extends FieldType { }
