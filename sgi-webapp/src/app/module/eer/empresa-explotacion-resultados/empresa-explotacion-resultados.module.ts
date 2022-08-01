import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../../csp/shared/csp-shared.module';
import { EerSharedModule } from '../shared/eer-shared.module';
import { EmpresaExplotacionResultadosCrearComponent } from './empresa-explotacion-resultados-crear/empresa-explotacion-resultados-crear.component';
import { EmpresaExplotacionResultadosDataResolver } from './empresa-explotacion-resultados-data.resolver';
import { EmpresaExplotacionResultadosEditarComponent } from './empresa-explotacion-resultados-editar/empresa-explotacion-resultados-editar.component';
import { EmpresaAdministracionSociedadComponent } from './empresa-explotacion-resultados-formulario/empresa-administracion-sociedad/empresa-administracion-sociedad.component';
import { EmpresaComposicionSociedadComponent } from './empresa-explotacion-resultados-formulario/empresa-composicion-sociedad/empresa-composicion-sociedad.component';
import { EmpresaEquipoEmprendedorComponent } from './empresa-explotacion-resultados-formulario/empresa-equipo-emprendedor/empresa-equipo-emprendedor.component';
import { EmpresaExplotacionResultadosDatosGeneralesComponent } from './empresa-explotacion-resultados-formulario/empresa-explotacion-resultados-datos-generales/empresa-explotacion-resultados-datos-generales.component';
import { EmpresaExplotacionResultadosDocumentosComponent } from './empresa-explotacion-resultados-formulario/empresa-explotacion-resultados-documentos/empresa-explotacion-resultados-documentos.component';
import { EmpresaExplotacionResultadosListadoComponent } from './empresa-explotacion-resultados-listado/empresa-explotacion-resultados-listado.component';
import { EmpresaExplotacionResultadosRoutingModule } from './empresa-explotacion-resultados-routing.module';
import { EmpresaAdministracionSociedadModalComponent } from './modals/empresa-administracion-sociedad-modal/empresa-administracion-sociedad-modal.component';
import { EmpresaComposicionSociedadModalComponent } from './modals/empresa-composicion-sociedad-modal/empresa-composicion-sociedad-modal.component';
import { EmpresaEquipoEmprendedorModalComponent } from './modals/empresa-equipo-emprendedor-modal/empresa-equipo-emprendedor-modal.component';

@NgModule({
  declarations: [
    EmpresaExplotacionResultadosListadoComponent,
    EmpresaExplotacionResultadosCrearComponent,
    EmpresaExplotacionResultadosEditarComponent,
    EmpresaExplotacionResultadosDatosGeneralesComponent,
    EmpresaEquipoEmprendedorComponent,
    EmpresaEquipoEmprendedorModalComponent,
    EmpresaExplotacionResultadosDocumentosComponent,
    EmpresaComposicionSociedadComponent,
    EmpresaComposicionSociedadModalComponent,
    EmpresaAdministracionSociedadComponent,
    EmpresaAdministracionSociedadModalComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    EmpresaExplotacionResultadosRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    SgpSharedModule,
    SgempSharedModule,
    CspSharedModule,
    EerSharedModule,
  ],
  providers: [
    EmpresaExplotacionResultadosDataResolver,
  ]
})
export class EmpresaExplotacionResultadosModule { }
