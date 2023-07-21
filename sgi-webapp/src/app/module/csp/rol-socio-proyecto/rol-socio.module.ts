import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { RolSocioListadoComponent } from './rol-socio-listado/rol-socio-listado.component';
import { RolSocioModalComponent } from './rol-socio-modal/rol-socio-modal.component';
import { RolSocioRoutingModule } from './rol-socio-routing.module';


@NgModule({
  declarations: [RolSocioListadoComponent, RolSocioModalComponent],
  imports: [
    CommonModule,
    RolSocioRoutingModule,
    SharedModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ]
})
export class RolSocioModule { }
