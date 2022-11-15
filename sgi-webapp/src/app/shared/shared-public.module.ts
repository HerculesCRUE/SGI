import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { FormlyFormsModule } from '@formly-forms/formly-forms.module';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiFileUploadPublicComponent } from './file-upload-public/file-upload-public.component';

@NgModule({
  declarations: [
    SgiFileUploadPublicComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    MaterialDesignModule,
    TranslateModule,
    FormsModule,
    ReactiveFormsModule,
    FormlyFormsModule
  ],
  exports: [
    SgiFileUploadPublicComponent
  ]
})
export class SharedPublicModule { }
