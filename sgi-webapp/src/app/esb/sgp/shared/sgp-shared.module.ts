import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { SgpFormlyFormsModule } from '../formly-forms/sgp-formly-forms.module';
import { SearchPersonaModalComponent } from './select-persona/dialog/search-persona.component';
import { SelectPersonaComponent } from './select-persona/select-persona.component';

@NgModule({
  declarations: [
    SearchPersonaModalComponent,
    SelectPersonaComponent
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
    SelectPersonaComponent
  ]
})
export class SgpSharedModule { }
