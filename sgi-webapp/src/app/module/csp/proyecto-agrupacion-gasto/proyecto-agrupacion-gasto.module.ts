import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { ProyectoAgrupacionGastoCrearComponent } from './proyecto-agrupacion-gasto-crear/proyecto-agrupacion-gasto-crear.component';
import { ProyectoAgrupacionGastoDataResolver } from './proyecto-agrupacion-gasto-data.resolver';
import { ProyectoAgrupacionGastoEditarComponent } from './proyecto-agrupacion-gasto-editar/proyecto-agrupacion-gasto-editar.component';
import { AgrupacionGastoConceptoComponent } from './proyecto-agrupacion-gasto-formulario/agrupacion-gasto-concepto/agrupacion-gasto-concepto.component';
import { ProyectoAgrupacionGastoDatosGeneralesComponent } from './proyecto-agrupacion-gasto-formulario/proyecto-agrupacion-gasto-datos-generales/proyecto-agrupacion-gasto-datos-generales.component';
import { ProyectoAgrupacionGastoRouting } from './proyecto-agrupacion-gasto-routing.module';
import { AgrupacionGastoConceptoModalComponent } from './modals/agrupacion-gasto-concepto-modal/agrupacion-gasto-concepto-modal.component';

@NgModule({
  declarations: [
    ProyectoAgrupacionGastoCrearComponent,
    ProyectoAgrupacionGastoEditarComponent,
    ProyectoAgrupacionGastoDatosGeneralesComponent,
    AgrupacionGastoConceptoComponent,
    AgrupacionGastoConceptoModalComponent,
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    ProyectoAgrupacionGastoRouting,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    ProyectoAgrupacionGastoDataResolver
  ]
})
export class ProyectoAgrupacionGastoModule { }
