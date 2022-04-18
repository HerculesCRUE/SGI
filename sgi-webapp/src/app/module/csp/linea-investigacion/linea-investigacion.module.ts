import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LineaInvestigacionRoutingModule } from './linea-investigacion-routing.module';
import { LineaInvestigacionListadoComponent } from './linea-investigacion-listado/linea-investigacion-listado.component';
import { LineaInvestigacionModalComponent } from './linea-investigacion-modal/linea-investigacion-modal.component';

@NgModule({
  declarations: [
    LineaInvestigacionListadoComponent,
    LineaInvestigacionModalComponent],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    LineaInvestigacionRoutingModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
  ]
})
export class LineaInvestigacionModule { }
