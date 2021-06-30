import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { AreaTematicaRoutingModule } from './area-tematica-routing.module';
import { AreaTematicaResolver } from './area-tematica.resolver';
import { AreaTematicaCrearComponent } from './area-tematica-crear/area-tematica-crear.component';
import { AreaTematicaEditarComponent } from './area-tematica-editar/area-tematica-editar.component';
import { AreaTematicaListadoComponent } from './area-tematica-listado/area-tematica-listado.component';
import { AreaTematicaDatosGeneralesComponent } from './area-tematica-formulario/area-tematica-datos-generales/area-tematica-datos-generales.component';
import { SgiAuthModule } from '@sgi/framework/auth';
import { AreaTematicaArbolComponent } from './area-tematica-formulario/area-tematica-arbol/area-tematica-arbol.component';

@NgModule({
  declarations: [AreaTematicaCrearComponent, AreaTematicaEditarComponent,
    AreaTematicaListadoComponent, AreaTematicaDatosGeneralesComponent, AreaTematicaArbolComponent],
  imports: [
    CommonModule,
    SharedModule,
    AreaTematicaRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ],
  providers: [
    AreaTematicaResolver
  ]
})
export class AreaTematicaModule { }
