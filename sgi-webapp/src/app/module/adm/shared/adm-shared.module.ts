import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { ConfigInputEmailsComponent } from './config-input-emails /config-input-emails.component';
import { ConfigInputFileComponent } from './config-input-file /config-input-file.component';
import { ConfigInputTextComponent } from './config-input-text/config-input-text.component';
import { ConfigSelectComponent } from './config-select/config-select.component';
import { ResourceUploadComponent } from './resource-upload/resource-upload.component';

@NgModule({
  declarations: [
    ConfigInputEmailsComponent,
    ConfigInputFileComponent,
    ConfigInputTextComponent,
    ConfigSelectComponent,
    ResourceUploadComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  exports: [
    ConfigInputEmailsComponent,
    ConfigInputFileComponent,
    ConfigInputTextComponent,
    ConfigSelectComponent,
    ResourceUploadComponent
  ]
})
export class AdmSharedModule { }
