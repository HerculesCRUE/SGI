import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { SgpFormlyFormsModule } from '../formly-forms/sgp-formly-forms.module';
import { PersonaEmailPipe } from './pipes/persona-email.pipe';
import { PersonaEntidadPipe } from './pipes/persona-entidad.pipe';
import { SearchPersonaModalComponent } from './select-persona/dialog/search-persona.component';
import { SelectPersonaComponent } from './select-persona/select-persona.component';

@NgModule({
  declarations: [
    SearchPersonaModalComponent,
    SelectPersonaComponent,
    PersonaEntidadPipe,
    PersonaEmailPipe
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SgpFormlyFormsModule
  ],
  exports: [
    SelectPersonaComponent,
    PersonaEntidadPipe,
    PersonaEmailPipe
  ]
})
export class SgpSharedModule { }
