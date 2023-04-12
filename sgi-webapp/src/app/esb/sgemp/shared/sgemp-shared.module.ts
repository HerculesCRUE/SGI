import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgempFormlyFormsModule } from '../formly-forms/sgemp-formly-forms.module';
import { SgempNotFoundErrorDirective } from './directives/sgemp-not-found-error.directive';
import { SearchEmpresaModalComponent } from './select-empresa/dialog/search-empresa.component';
import { SelectEmpresaComponent } from './select-empresa/select-empresa.component';

@NgModule({
  declarations: [
    SearchEmpresaModalComponent,
    SelectEmpresaComponent,
    SgempNotFoundErrorDirective
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SgempFormlyFormsModule,
    SgiAuthModule
  ],
  exports: [
    SelectEmpresaComponent,
    SgempNotFoundErrorDirective
  ]
})
export class SgempSharedModule { }
