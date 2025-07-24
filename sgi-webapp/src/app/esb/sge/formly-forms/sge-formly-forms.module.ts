import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FormlyFormsModule } from '@formly-forms/formly-forms.module';
import { MaterialDesignModule } from '@material/material-design.module';
import { FormlyModule } from '@ngx-formly/core';
import { FormlySelectModule } from '@ngx-formly/core/select';
import { FormlyMaterialModule } from '@ngx-formly/material';
import { FormlyMatDatepickerModule } from '@ngx-formly/material/datepicker';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { SharedFormlyFormsModule } from '../../shared/formly-forms/shared-formly-forms.module';
import { ProyectoEconomicoFormlyModalComponent } from './proyecto-economico-formly-modal/proyecto-economico-formly-modal.component';
import { InputReadOnlyObjectFormlyDataSgeTypeComponent } from './types/input-read-only-object-formly-data-sge.type';
import { SelectFormlyDataSgeTypeComponent } from './types/select-formly-data-sge.type';

@NgModule({
  declarations: [
    InputReadOnlyObjectFormlyDataSgeTypeComponent,
    ProyectoEconomicoFormlyModalComponent,
    SelectFormlyDataSgeTypeComponent,
  ],
  imports: [
    CommonModule,
    FormlyFormsModule,
    FormlyMatDatepickerModule,
    FormlyMaterialModule,
    FormlyModule.forChild({
      types: [
        {
          name: 'input-read-only-object-formly-data-sge',
          component: InputReadOnlyObjectFormlyDataSgeTypeComponent,
          wrappers: ['form-field'],
        },
        {
          name: 'select-formly-data-sge',
          component: SelectFormlyDataSgeTypeComponent,
          wrappers: ['form-field'],
        },
      ]
    }),
    FormlySelectModule,
    FormsModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    SharedFormlyFormsModule,
    SharedModule,
    TranslateModule,
  ],
  exports: [
    ProyectoEconomicoFormlyModalComponent,
    SelectFormlyDataSgeTypeComponent,
  ]
})
export class SgeFormlyFormsModule { }
