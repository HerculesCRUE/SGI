import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { CspInicioComponent } from './csp-inicio/csp-inicio.component';
import { CspRootComponent } from './csp-root/csp-root.component';
import { CspRoutingModule } from './csp-routing.module';

@NgModule({
  declarations: [
    CspRootComponent,
    CspInicioComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    CspRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    SgiAuthModule,
    ReactiveFormsModule,
    FormsModule
  ],
  providers: []
})
export class CspModule { }
