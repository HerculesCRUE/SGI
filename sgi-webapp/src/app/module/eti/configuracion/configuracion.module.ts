import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@sgi/framework/auth';
import { ConfiguracionRoutingModule } from './configuracion-routing.module';
import { ConfiguracionFormularioComponent } from './configuracion-formulario/configuracion-formulario.component';

@NgModule({
  declarations: [
    ConfiguracionFormularioComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    ConfiguracionRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SgiAuthModule
  ]
})
export class ConfiguracionModule { }
