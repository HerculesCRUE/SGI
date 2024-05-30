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
import { SharedFormlyFormsModule } from '../../shared/formly-forms/shared-formly-forms.module';
import { PersonaFormlyPublicModalComponent } from './persona-formly-public-modal/persona-formly-public-modal.component';
import { SelectCategoriasProfesionalesPublicTypeComponent } from './types/select-categorias-profesionales-public.type';
import { SelectColectivosPublicTypeComponent } from './types/select-colectivos-public.type';
import { SelectEmpresaPublicTypeComponent } from './types/select-empresa-public.type';
import { SelectNivelesAcademicosPublicTypeComponent } from './types/select-niveles-academicos-public.type';
import { SelectSexosPublicTypeComponent } from './types/select-sexo-public.type';
import { SelectTiposDocumentosPublicTypeComponent } from './types/select-tipos-documentos-public.type';

@NgModule({
  declarations: [
    PersonaFormlyPublicModalComponent,
    SelectColectivosPublicTypeComponent,
    SelectCategoriasProfesionalesPublicTypeComponent,
    SelectEmpresaPublicTypeComponent,
    SelectNivelesAcademicosPublicTypeComponent,
    SelectSexosPublicTypeComponent,
    SelectTiposDocumentosPublicTypeComponent
  ],
  imports: [
    SharedModule,
    SharedFormlyFormsModule,
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
          name: 'select-categorias-profesionales',
          component: SelectCategoriasProfesionalesPublicTypeComponent,
          wrappers: ['form-field']
        },
        {
          name: 'select-colectivos',
          component: SelectColectivosPublicTypeComponent,
          wrappers: ['form-field']
        },
        {
          name: 'select-empresas',
          component: SelectEmpresaPublicTypeComponent,
          wrappers: ['form-field']
        },
        {
          name: 'select-niveles-academicos',
          component: SelectNivelesAcademicosPublicTypeComponent,
          wrappers: ['form-field']
        },
        {
          name: 'select-sexos',
          component: SelectSexosPublicTypeComponent,
          wrappers: ['form-field']
        },
        {
          name: 'select-tipos-documentos',
          component: SelectTiposDocumentosPublicTypeComponent,
          wrappers: ['form-field']
        }
      ]
    })
  ],
  exports: [
    PersonaFormlyPublicModalComponent,
    SelectCategoriasProfesionalesPublicTypeComponent,
    SelectColectivosPublicTypeComponent,
    SelectEmpresaPublicTypeComponent,
    SelectNivelesAcademicosPublicTypeComponent,
    SelectSexosPublicTypeComponent,
    SelectTiposDocumentosPublicTypeComponent
  ]
})
export class SgpPublicFormlyFormsModule { }
