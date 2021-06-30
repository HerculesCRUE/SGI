import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { ResultadoInformePatentabilidadRoutingModule } from './resultado-informe-patentabilidad-routing.module';
import { ResultadoInformePatentabilidadListadoComponent } from './resultado-informe-patentabilidad-listado/resultado-informe-patentabilidad-listado.component';

@NgModule({
  declarations: [ResultadoInformePatentabilidadListadoComponent],
  imports: [
    CommonModule,
    SharedModule,
    ResultadoInformePatentabilidadRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ],
})
export class ResultadoInformePatentabilidadModule { }
