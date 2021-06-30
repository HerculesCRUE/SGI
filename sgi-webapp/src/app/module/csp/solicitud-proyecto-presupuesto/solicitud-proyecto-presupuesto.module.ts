import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SolicitudProyectoPresupuestoDataResolver } from './solicitud-proyecto-presupuesto-data.resolver';
import { SolicitudProyectoPresupuestoEditarComponent } from './solicitud-proyecto-presupuesto-editar/solicitud-proyecto-presupuesto-editar.component';
import { SolicitudProyectoPresupuestoDatosGeneralesComponent } from './solicitud-proyecto-presupuesto-formulario/solicitud-proyecto-presupuesto-datos-generales/solicitud-proyecto-presupuesto-datos-generales.component';
import { SolicitudProyectoPresupuestoPartidasGastoComponent } from './solicitud-proyecto-presupuesto-formulario/solicitud-proyecto-presupuesto-partidas-gasto/solicitud-proyecto-presupuesto-partidas-gasto.component';
import { SolicitudProyectoPresupuestoRoutingModule } from './solicitud-proyecto-presupuesto-routing.module';

@NgModule({
  declarations: [
    SolicitudProyectoPresupuestoEditarComponent,
    SolicitudProyectoPresupuestoDatosGeneralesComponent,
    SolicitudProyectoPresupuestoPartidasGastoComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    SolicitudProyectoPresupuestoRoutingModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    SolicitudProyectoPresupuestoDataResolver
  ]
})
export class SolicitudProyectoPresupuestoModule { }
