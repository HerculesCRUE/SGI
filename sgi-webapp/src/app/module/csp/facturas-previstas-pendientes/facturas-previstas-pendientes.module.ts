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
import { FacturasPrevistasPendientesListadoExportModalComponent } from './modals/facturas-previstas-pendientes-listado-export-modal/facturas-previstas-pendientes-listado-export-modal.component';

@NgModule({
  declarations: [
    FacturasPrevistasPendientesListadoComponent,
    FacturasPrevistasPendientesListadoExportModalComponent
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
