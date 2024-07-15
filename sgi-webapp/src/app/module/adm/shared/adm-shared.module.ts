import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { ConfigInputEmailsComponent } from './config-input-emails /config-input-emails.component';
import { ConfigInputFileComponent } from './config-input-file /config-input-file.component';
import { ConfigInputTextCnfComponent } from './config-input-text-cnf/config-input-text-cnf.component';
import { ConfigInputTextCspComponent } from './config-input-text-csp/config-input-text-csp.component';
import { ConfigSelectCnfComponent } from './config-select-cnf/config-select-cnf.component';
import { ConfigSelectCspComponent } from './config-select-csp/config-select-csp.component';
import { ConfigSelectMultipleCnfComponent } from './config-select-multiple-cnf/config-select-multiple-cnf.component';
import { ConfigSelectMultipleCspComponent } from './config-select-multiple-csp/config-select-multiple-csp.component';
import { ResourceUploadComponent } from './resource-upload/resource-upload.component';

@NgModule({
  declarations: [
    ConfigInputEmailsComponent,
    ConfigInputFileComponent,
    ConfigInputTextCnfComponent,
    ConfigInputTextCspComponent,
    ConfigSelectCnfComponent,
    ConfigSelectCspComponent,
    ConfigSelectMultipleCnfComponent,
    ConfigSelectMultipleCspComponent,
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
    ConfigInputTextCnfComponent,
    ConfigInputTextCspComponent,
    ConfigSelectCnfComponent,
    ConfigSelectCspComponent,
    ConfigSelectMultipleCnfComponent,
    ConfigSelectMultipleCspComponent,
    ResourceUploadComponent
  ]
})
export class AdmSharedModule { }
