import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { NotificacionPresupuestoSgeListadoComponent } from './notificacion-presupuesto-sge-listado/notificacion-presupuesto-sge-listado.component';
import { NotificacionPresupuestoSgeRoutingModule } from './notificacion-presupuesto-sge-routing.module';

@NgModule({
  declarations: [NotificacionPresupuestoSgeListadoComponent],
  imports: [
    CommonModule,
    SharedModule,
    NotificacionPresupuestoSgeRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ]
})
export class NotificacionPresupuestoSgeModule { }
