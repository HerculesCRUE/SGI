import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { MiembrosGruposInvestigacionListadoExportService } from './miembros-grupos-investigacion-listado-export.service';
import { MiembrosGruposInvestigacionListadoComponent } from './miembros-grupos-investigacion-listado/miembros-grupos-investigacion-listado.component';
import { MiembrosGruposInvestigacionRoutingModule } from './miembros-grupos-investigacion-routing.module';

@NgModule({
  declarations: [
    MiembrosGruposInvestigacionListadoComponent
  ],
  imports: [
    CommonModule,
    CspSharedModule,
    FormsModule,
    MaterialDesignModule,
    MiembrosGruposInvestigacionRoutingModule,
    ReactiveFormsModule,
    SgiAuthModule,
    SgpSharedModule,
    SharedModule,
    TranslateModule
  ],
  providers: [
    MiembrosGruposInvestigacionListadoExportService
  ]
})
export class MiembrosGruposInvestigacionModule { }
