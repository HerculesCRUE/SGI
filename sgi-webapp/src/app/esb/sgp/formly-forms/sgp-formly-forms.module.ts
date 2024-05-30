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
import { SgempSharedModule } from '../../sgemp/shared/sgemp-shared.module';
import { SharedFormlyFormsModule } from '../../shared/formly-forms/shared-formly-forms.module';
import { PersonaFormlyModalComponent } from './persona-formly-modal/persona-formly-modal.component';
import { InputPersonaNombreCompletoTypeComponent } from './types/input-persona-nombre-completo.type';
import { SelectCategoriasProfesionalesTypeComponent } from './types/select-categorias-profesionales.type';
import { SelectColectivosTypeComponent } from './types/select-colectivos.type';
import { SelectEmpresaTypeComponent } from './types/select-empresa.type';
import { SelectNivelesAcademicosTypeComponent } from './types/select-niveles-academicos.type';
import { SelectSexosTypeComponent } from './types/select-sexo.type';
import { SelectTiposDocumentosTypeComponent } from './types/select-tipos-documentos.type';

@NgModule({
  declarations: [
    InputPersonaNombreCompletoTypeComponent,
    PersonaFormlyModalComponent,
    SelectColectivosTypeComponent,
    SelectEmpresaTypeComponent,
    SelectCategoriasProfesionalesTypeComponent,
    SelectNivelesAcademicosTypeComponent,
    SelectSexosTypeComponent,
    SelectTiposDocumentosTypeComponent
  ],
  imports: [
    SharedModule,
    SharedFormlyFormsModule,
    SgempSharedModule,
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
          name: 'input-persona-nombre-completo',
          component: InputPersonaNombreCompletoTypeComponent,
          wrappers: ['form-field']
        },
        {
          name: 'select-categorias-profesionales',
          component: SelectCategoriasProfesionalesTypeComponent,
          wrappers: ['form-field']
        },
        {
          name: 'select-colectivos',
          component: SelectColectivosTypeComponent,
          wrappers: ['form-field']
        },
        {
          name: 'select-empresas',
          component: SelectEmpresaTypeComponent,
          wrappers: ['form-field']
        },
        {
          name: 'select-niveles-academicos',
          component: SelectNivelesAcademicosTypeComponent,
          wrappers: ['form-field']
        },
        {
          name: 'select-sexos',
          component: SelectSexosTypeComponent,
          wrappers: ['form-field']
        },
        {
          name: 'select-tipos-documentos',
          component: SelectTiposDocumentosTypeComponent,
          wrappers: ['form-field']
        }
      ]
    })
  ],
  exports: [
    InputPersonaNombreCompletoTypeComponent,
    PersonaFormlyModalComponent,
    SelectCategoriasProfesionalesTypeComponent,
    SelectColectivosTypeComponent,
    SelectNivelesAcademicosTypeComponent,
    SelectSexosTypeComponent,
    SelectTiposDocumentosTypeComponent
  ]
})
export class SgpFormlyFormsModule { }
