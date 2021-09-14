import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { TipoProteccionSubtipoModalComponent } from './modals/tipo-proteccion-subtipo-modal/tipo-proteccion-subtipo-modal.component';
import { TipoProteccionCrearComponent } from './tipo-proteccion-crear/tipo-proteccion-crear.component';
import { TipoProteccionEditarComponent } from './tipo-proteccion-editar/tipo-proteccion-editar.component';
import { TipoProteccionDatosGeneralesComponent } from './tipo-proteccion-formulario/tipo-proteccion-datos-generales/tipo-proteccion-datos-generales.component';
import { TipoProteccionSubtiposComponent } from './tipo-proteccion-formulario/tipo-proteccion-subtipos/tipo-proteccion-subtipos.component';
import { TipoProteccionListadoComponent } from './tipo-proteccion-listado/tipo-proteccion-listado.component';
import { TipoProteccionRoutingModule } from './tipo-proteccion-routing.module';
import { TipoProteccionResolver } from './tipo-proteccion.resolver';

@NgModule({
  declarations: [
    TipoProteccionListadoComponent,
    TipoProteccionCrearComponent,
    TipoProteccionEditarComponent,
    TipoProteccionDatosGeneralesComponent,
    TipoProteccionSubtiposComponent,
    TipoProteccionSubtipoModalComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    TipoProteccionRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ],
  providers: [
    TipoProteccionResolver
  ]
})
export class TipoProteccionModule { }
