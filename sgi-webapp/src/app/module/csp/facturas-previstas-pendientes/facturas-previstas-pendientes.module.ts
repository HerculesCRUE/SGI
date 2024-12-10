import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { FacturasPrevistasPendientesListadoExportService } from './facturas-previstas-pendientes-listado-export.service';
import { FacturasPrevistasPendientesListadoComponent } from './facturas-previstas-pendientes-listado/facturas-previstas-pendientes-listado.component';
import { FacturasPrevistasPendientesRoutingModule } from './facturas-previstas-pendientes-routing.module';

@NgModule({
  declarations: [
    FacturasPrevistasPendientesListadoComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FacturasPrevistasPendientesRoutingModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
    CspSharedModule
  ],
  providers: [
    FacturasPrevistasPendientesListadoExportService
  ]
})
export class FacturasPrevistasPendientesModule { }
