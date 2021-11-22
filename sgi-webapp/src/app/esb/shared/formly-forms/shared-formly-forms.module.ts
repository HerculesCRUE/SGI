import { NgxMatDatetimePickerModule } from '@angular-material-components/datetime-picker';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { requiredRowTable } from '@formly-forms/validators/utils.validator';
import { MaterialDesignModule } from '@material/material-design.module';
import { FormlyModule } from '@ngx-formly/core';
import { FormlySelectModule } from '@ngx-formly/core/select';
import { FormlyMaterialModule } from '@ngx-formly/material';
import { FormlyMatDatepickerModule } from '@ngx-formly/material/datepicker';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { DateTimePickerTypeComponent } from './types/datetimepicker.type';
import { SelectAttributesTypeComponent } from './types/select-attributes.type';
import { SelectObjectTypeComponent } from './types/select-object.type';
import { TableCRUDModalComponent } from './types/table-crud/table-crud-modal/table-crud-modal.component';
import { TableCRUDTypeComponent } from './types/table-crud/table-crud.type';
import { GlobalWrapperComponent } from './wrappers/global/global.wrapper';
import { MatCardGroupWrapperComponent } from './wrappers/mat-card-group/mat-card-group.wrapper';

@NgModule({
  declarations: [
    SelectObjectTypeComponent,
    TableCRUDTypeComponent,
    TableCRUDModalComponent,
    SelectAttributesTypeComponent,
    DateTimePickerTypeComponent,
    MatCardGroupWrapperComponent,
    GlobalWrapperComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    NgxMatDatetimePickerModule,
    FormlyMatDatepickerModule,
    FormlySelectModule,
    FormlyMaterialModule,
    FormlyModule.forChild({
      types: [
        {
          name: 'select-object',
          component: SelectObjectTypeComponent,
          wrappers: ['form-field']
        },
        {
          name: 'table-crud',
          component: TableCRUDTypeComponent
        },
        {
          name: 'table-crud-one-element',
          component: TableCRUDTypeComponent
        },
        {
          name: 'select-attributes',
          component: SelectAttributesTypeComponent,
          wrappers: ['form-field'],
        },
        {
          name: 'dateTimePicker',
          component: DateTimePickerTypeComponent,
          wrappers: ['form-field'],
        }
      ],
      wrappers: [
        {
          name: 'mat-card-group',
          component: MatCardGroupWrapperComponent
        },
        {
          name: 'global',
          component: GlobalWrapperComponent
        }
      ],
      validators: [
        { name: 'oneRowRequired', validation: requiredRowTable },
      ],
      validationMessages: [
        { name: 'required', message: 'El campo es obligatorio' },
        { name: 'oneRowRequired', message: 'La tabla debe contener al menos una fila' },
      ]
    })
  ],
  exports: [
    SelectObjectTypeComponent,
    TableCRUDTypeComponent,
    TableCRUDModalComponent,
    SelectAttributesTypeComponent,
    DateTimePickerTypeComponent,
    MatCardGroupWrapperComponent,
    GlobalWrapperComponent
  ]
})
export class SharedFormlyFormsModule { }
