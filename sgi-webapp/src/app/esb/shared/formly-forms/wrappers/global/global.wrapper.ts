import { Component, OnInit } from '@angular/core';
import { FieldWrapper, FormlyTemplateOptions } from '@ngx-formly/core';

@Component({
  templateUrl: './global.wrapper.html',
  styleUrls: ['./global.wrapper.scss']
})
export class GlobalWrapperComponent extends FieldWrapper implements OnInit {
  readonly to: FormlyTemplateOptions;

  // Define estilos personalizados para el wrapper
  customStyles: any;

  constructor() {
    super();
  }

  ngOnInit(): void {
    if (this.to.styles) {
      this.customStyles = JSON.parse(this.to.styles);
    }
  }
}
