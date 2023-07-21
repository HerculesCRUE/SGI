import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { FormlyModule } from '@ngx-formly/core';
import { FormlySelectModule } from '@ngx-formly/core/select';
import { FormlyMaterialModule } from '@ngx-formly/material';
import { FormlyMatDatepickerModule } from '@ngx-formly/material/datepicker';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { SelectComunidadesPublicTypeComponent } from './types/select-comunidades-public.type';
import { SelectDepartamentosPublicTypeComponent } from './types/select-departamentos-public.type';
import { SelectPaisesPublicTypeComponent } from './types/select-paises-public.type';
import { SelectProvinciasPublicTypeComponent } from './types/select-provincias-public.type';

@NgModule({
  declarations: [
    SelectPaisesPublicTypeComponent,
    SelectComunidadesPublicTypeComponent,
    SelectProvinciasPublicTypeComponent,
    SelectDepartamentosPublicTypeComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    FormlyMatDatepickerModule,
    FormlyMaterialModule,
    FormlySelectModule,
    FormlyModule.forChild({
      types: [
        {
          name: 'select-paises',
          component: SelectPaisesPublicTypeComponent,
          wrappers: ['form-field']
        },
        {
          name: 'select-comunidades',
          component: SelectComunidadesPublicTypeComponent,
          wrappers: ['form-field']
        },
        {
          name: 'select-provincias',
          component: SelectProvinciasPublicTypeComponent,
          wrappers: ['form-field']
        },
        {
          name: 'select-departamentos',
          component: SelectDepartamentosPublicTypeComponent,
          wrappers: ['form-field']
        }
      ]
    })
  ],
  exports: [
    SelectPaisesPublicTypeComponent,
    SelectComunidadesPublicTypeComponent,
    SelectProvinciasPublicTypeComponent,
    SelectDepartamentosPublicTypeComponent
  ]
})
export class SgoPublicFormlyFormsModule { }
