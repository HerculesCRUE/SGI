import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { SgpPublicFormlyFormsModule } from '../formly-forms/sgp-public-formly-forms.module';
import { PersonaEmailPublicPipe } from './pipes/persona-email-public.pipe';
import { PersonaEntidadPublicPipe } from './pipes/persona-entidad-public.pipe';
import { SearchPersonaPublicModalComponent } from './select-persona-public/dialog/search-persona-public.component';
import { SelectPersonaPublicComponent } from './select-persona-public/select-persona-public.component';
import { SelectSexoPublicComponent } from './select-sexo-public/select-sexo-public.component';
import { SelectTipoDocumentoPublicComponent } from './select-tipo-documento-public/select-tipo-documento-public.component';

@NgModule({
  declarations: [
    PersonaEmailPublicPipe,
    PersonaEntidadPublicPipe,
    SearchPersonaPublicModalComponent,
    SelectPersonaPublicComponent,
    SelectSexoPublicComponent,
    SelectTipoDocumentoPublicComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SgpPublicFormlyFormsModule
  ],
  exports: [
    PersonaEmailPublicPipe,
    PersonaEntidadPublicPipe,
    SearchPersonaPublicModalComponent,
    SelectPersonaPublicComponent,
    SelectSexoPublicComponent,
    SelectTipoDocumentoPublicComponent
  ]
})
export class SgpPublicSharedModule { }
