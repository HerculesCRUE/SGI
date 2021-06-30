import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { FormlyModule } from '@ngx-formly/core';
import { FormlyMaterialModule } from '@ngx-formly/material';
import { FormlyMatDatepickerModule } from '@ngx-formly/material/datepicker';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { TableCRUDModalComponent } from './types/table-crud/table-crud-modal/table-crud-modal.component';
import { TableCRUDTypeComponent } from './types/table-crud/table-crud.type';
import { TableTypeRepetible } from './types/table-type-repetible.component';
import { TableType } from './types/table-type.component';
import { TipoValorSocialComponent } from './types/tipo-valor-social.component';
import { requiredChecked, requiredRowTable } from './validators/utils.validator';
import { InfoDivWrapperComponent } from './wrappers/info-div/info-div.wrapper';
import { PanelWrapperComponent } from './wrappers/panel/panel.wrapper';
import { TitleDivWrapperComponent } from './wrappers/title-div/title-div.wrapper';

@NgModule({
  declarations: [
    TableType,
    TableTypeRepetible,
    TableCRUDTypeComponent,
    TableCRUDModalComponent,
    PanelWrapperComponent,
    TitleDivWrapperComponent,
    InfoDivWrapperComponent,
    TipoValorSocialComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    MaterialDesignModule,
    TranslateModule,
    FormsModule,
    ReactiveFormsModule,
    FormlyMatDatepickerModule,
    FormlyModule.forRoot({
      types: [
        {
          name: 'table',
          component: TableType
        },
        {
          name: 'table-repetible',
          component: TableTypeRepetible
        },
        {
          name: 'tipo-valor-social',
          component: TipoValorSocialComponent,
          wrappers: ['form-field'],
        },
        {
          name: 'table-crud',
          component: TableCRUDTypeComponent
        },
        { name: 'documento', extends: 'radio' }
      ],
      wrappers: [
        {
          name: 'expansion-panel',
          component: PanelWrapperComponent
        },
        {
          name: 'title-div',
          component: TitleDivWrapperComponent
        },
        {
          name: 'info-div',
          component: InfoDivWrapperComponent
        }
      ],
      validators: [
        { name: 'requiredChecked', validation: requiredChecked },
        /** TODO: Remove when any declared template didn't use it */
        { name: 'nif', validation: Validators.required },
        { name: 'oneRowRequired', validation: requiredRowTable },
      ],
      validationMessages: [
        { name: 'required', message: 'El campo es obligatorio' },
        { name: 'oneRowRequired', message: 'La tabla debe contener al menos una fila' },
      ],
    }),
    FormlyMaterialModule
  ],
  exports: [
    TableType,
    TableTypeRepetible,
    TableCRUDTypeComponent,
    FormlyMatDatepickerModule,
    FormlyModule,
    FormlyMaterialModule
  ]
})
export class FormlyFormsModule { }
