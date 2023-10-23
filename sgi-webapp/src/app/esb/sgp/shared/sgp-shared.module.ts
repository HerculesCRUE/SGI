import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgpFormlyFormsModule } from '../formly-forms/sgp-formly-forms.module';
import { SgpNotFoundErrorDirective } from './directives/sgp-not-found-error.directive';
import { IconViewPersonaDetailComponent } from './icon-view-persona-detail/icon-view-persona-detail.component';
import { PersonaEmailPipe } from './pipes/persona-email.pipe';
import { PersonaEntidadPipe } from './pipes/persona-entidad.pipe';
import { PersonaNombreCompletoPipe } from './pipes/persona-nombre-completo.pipe';
import { SearchPersonaModalComponent } from './select-persona/dialog/search-persona.component';
import { SelectPersonaComponent } from './select-persona/select-persona.component';
import { SelectSexoComponent } from './select-sexo/select-sexo.component';
import { SelectTipoDocumentoComponent } from './select-tipo-documento/select-tipo-documento.component';

@NgModule({
  declarations: [
    IconViewPersonaDetailComponent,
    PersonaEmailPipe,
    PersonaEntidadPipe,
    PersonaNombreCompletoPipe,
    SearchPersonaModalComponent,
    SelectPersonaComponent,
    SelectSexoComponent,
    SelectTipoDocumentoComponent,
    SgpNotFoundErrorDirective
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SgpFormlyFormsModule,
    SgiAuthModule
  ],
  exports: [
    IconViewPersonaDetailComponent,
    PersonaEmailPipe,
    PersonaEntidadPipe,
    PersonaNombreCompletoPipe,
    SelectPersonaComponent,
    SelectSexoComponent,
    SelectTipoDocumentoComponent,
    SgpNotFoundErrorDirective
  ]
})
export class SgpSharedModule { }
