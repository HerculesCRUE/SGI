import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { AdmInicioComponent } from './adm-inicio/adm-inicio.component';
import { AdmRootComponent } from './adm-root/adm-root.component';
import { AdmRoutingModule } from './adm-routing.module';
import { ConfigCspComponent } from './config-csp/config-csp.component';
import { ConfigEtiComponent } from './config-eti/config-eti.component';
import { ConfigGlobalComponent } from './config-global/config-global.component';
import { ConfigPiiComponent } from './config-pii/config-pii.component';
import { ConfigPrcComponent } from './config-prc/config-prc.component';
import { AdmSharedModule } from './shared/adm-shared.module';

@NgModule({
  declarations: [
    AdmRootComponent,
    AdmInicioComponent,
    ConfigGlobalComponent,
    ConfigCspComponent,
    ConfigEtiComponent,
    ConfigPiiComponent,
    ConfigPrcComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    AdmRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    SgiAuthModule,
    ReactiveFormsModule,
    FormsModule,
    AdmSharedModule
  ]
})
export class CnfModule { }
