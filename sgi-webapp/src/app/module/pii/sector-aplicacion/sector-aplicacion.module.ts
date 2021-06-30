import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SectorAplicacionRoutingModule } from './sector-aplicacion-routing.module';
import { SectorAplicacionListadoComponent } from './sector-aplicacion-listado/sector-aplicacion-listado.component';
import { SectorAplicacionModalComponent } from './sector-aplicacion-modal/sector-aplicacion-modal.component';

@NgModule({
  declarations: [SectorAplicacionListadoComponent, SectorAplicacionModalComponent],
  imports: [
    CommonModule,
    SharedModule,
    SectorAplicacionRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ]
})
export class SectorAplicacionModule { }
