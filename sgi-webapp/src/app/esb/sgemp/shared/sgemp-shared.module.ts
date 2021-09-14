import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { SgempFormlyFormsModule } from '../formly-forms/sgemp-formly-forms.module';
import { SearchEmpresaModalComponent } from './select-empresa/dialog/search-empresa.component';
import { SelectEmpresaComponent } from './select-empresa/select-empresa.component';

@NgModule({
  declarations: [
    SearchEmpresaModalComponent,
    SelectEmpresaComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SgempFormlyFormsModule
  ],
  exports: [
    SelectEmpresaComponent
  ]
})
export class SgempSharedModule { }
