import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { FuenteFinanciacionRoutingModule } from './fuente-financiacion-routing.module';
import { FuenteFinanciacionListadoComponent } from './fuente-financiacion-listado/fuente-financiacion-listado.component';
import { FuenteFinanciacionModalComponent } from './fuente-financiacion-modal/fuente-financiacion-modal.component';

@NgModule({
  declarations: [
    FuenteFinanciacionListadoComponent,
    FuenteFinanciacionModalComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FuenteFinanciacionRoutingModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
  ]
})
export class FuenteFinanciacionModule { }
