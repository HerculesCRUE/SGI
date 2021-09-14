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
import { SelectComunidadesTypeComponent } from './types/select-comunidades.type';
import { SelectDepartamentosTypeComponent } from './types/select-departamentos.type';
import { SelectPaisesTypeComponent } from './types/select-paises.type';
import { SelectProvinciasTypeComponent } from './types/select-provincias.type';
import { TableCRUDClasificacionesTypeComponent } from './types/table-crud-clasificaciones/table-crud-clasificaciones.type';

@NgModule({
  declarations: [
    SelectPaisesTypeComponent,
    SelectComunidadesTypeComponent,
    SelectProvinciasTypeComponent,
    SelectDepartamentosTypeComponent,
    TableCRUDClasificacionesTypeComponent
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
          component: SelectPaisesTypeComponent,
          wrappers: ['form-field']
        },
        {
          name: 'select-comunidades',
          component: SelectComunidadesTypeComponent,
          wrappers: ['form-field']
        },
        {
          name: 'select-provincias',
          component: SelectProvinciasTypeComponent,
          wrappers: ['form-field']
        },
        {
          name: 'select-departamentos',
          component: SelectDepartamentosTypeComponent,
          wrappers: ['form-field']
        },
        {
          name: 'table-crud-clasificaciones',
          component: TableCRUDClasificacionesTypeComponent
        }
      ]
    })
  ],
  exports: [
    SelectPaisesTypeComponent,
    SelectComunidadesTypeComponent,
    SelectProvinciasTypeComponent,
    SelectDepartamentosTypeComponent,
    TableCRUDClasificacionesTypeComponent
  ]
})
export class SgoFormlyFormsModule { }
