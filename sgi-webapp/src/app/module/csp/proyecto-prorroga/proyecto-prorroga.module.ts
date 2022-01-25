import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { ProyectoProrrogaCrearComponent } from './proyecto-prorroga-crear/proyecto-prorroga-crear.component';
import { ProyectoProrrogaDataResolver } from './proyecto-prorroga-data.resolver';
import { ProyectoProrrogaEditarComponent } from './proyecto-prorroga-editar/proyecto-prorroga-editar.component';
import { ProyectoProrrogaDatosGeneralesComponent } from './proyecto-prorroga-formulario/proyecto-prorroga-datos-generales/proyecto-prorroga-datos-generales.component';
import { ProyectoProrrogaDocumentosComponent } from './proyecto-prorroga-formulario/proyecto-prorroga-documentos/proyecto-prorroga-documentos.component';
import { ProyectoProrrogaRouting } from './proyecto-prorroga-routing.module';

@NgModule({
  declarations: [
    ProyectoProrrogaCrearComponent,
    ProyectoProrrogaEditarComponent,
    ProyectoProrrogaDatosGeneralesComponent,
    ProyectoProrrogaDocumentosComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    ProyectoProrrogaRouting,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
    CspSharedModule
  ],
  providers: [
    ProyectoProrrogaDataResolver
  ]
})
export class ProyectoProrrogaModule { }
