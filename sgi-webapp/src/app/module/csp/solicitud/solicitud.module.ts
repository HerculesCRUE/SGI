import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FormlyFormsModule } from '@formly-forms/formly-forms.module';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { SgoSharedModule } from 'src/app/esb/sgo/shared/sgo-shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { CambioEstadoModalComponent } from './modals/cambio-estado-modal/cambio-estado-modal.component';
import { SolicitudAreaTematicaModalComponent } from './modals/solicitud-area-tematica-modal/solicitud-area-tematica-modal.component';
import { SolicitudCrearProyectoModalComponent } from './modals/solicitud-crear-proyecto-modal/solicitud-crear-proyecto-modal.component';
import { SolicitiudHitosModalComponent } from './modals/solicitud-hitos-modal/solicitud-hitos-modal.component';
import { SolicitudModalidadEntidadConvocanteModalComponent } from './modals/solicitud-modalidad-entidad-convocante-modal/solicitud-modalidad-entidad-convocante-modal.component';
import { SolicitudProyectoResponsableEconomicoModalComponent } from './modals/solicitud-proyecto-responsable-economico-modal/solicitud-proyecto-responsable-economico-modal.component';
import { SolicitudCrearComponent } from './solicitud-crear/solicitud-crear.component';
import { SolicitudCrearGuard } from './solicitud-crear/solicitud-crear.guard';
import { SolicitudDataResolver } from './solicitud-data.resolver';
import { SolicitudEditarComponent } from './solicitud-editar/solicitud-editar.component';
import { SolicitudAutoevaluacionComponent } from './solicitud-formulario/solicitud-autoevaluacion/solicitud-autoevaluacion.component';
import { SolicitudDatosGeneralesComponent } from './solicitud-formulario/solicitud-datos-generales/solicitud-datos-generales.component';
import { SolicitudDocumentosComponent } from './solicitud-formulario/solicitud-documentos/solicitud-documentos.component';
import { SolicitudEquipoProyectoComponent } from './solicitud-formulario/solicitud-equipo-proyecto/solicitud-equipo-proyecto.component';
import { SolicitudHistoricoEstadosComponent } from './solicitud-formulario/solicitud-historico-estados/solicitud-historico-estados.component';
import { SolicitudHitosComponent } from './solicitud-formulario/solicitud-hitos/solicitud-hitos.component';
import { SolicitudProyectoAreaConocimientoComponent } from './solicitud-formulario/solicitud-proyecto-area-conocimiento/solicitud-proyecto-area-conocimiento.component';
import { SolicitudProyectoClasificacionesComponent } from './solicitud-formulario/solicitud-proyecto-clasificaciones/solicitud-proyecto-clasificaciones.component';
import { SolicitudProyectoEntidadesFinanciadorasComponent } from './solicitud-formulario/solicitud-proyecto-entidades-financiadoras/solicitud-proyecto-entidades-financiadoras.component';
import { SolicitudProyectoFichaGeneralComponent } from './solicitud-formulario/solicitud-proyecto-ficha-general/solicitud-proyecto-ficha-general.component';
import { SolicitudProyectoPresupuestoEntidadesComponent } from './solicitud-formulario/solicitud-proyecto-presupuesto-entidades/solicitud-proyecto-presupuesto-entidades.component';
import { SolicitudProyectoPresupuestoGlobalComponent } from './solicitud-formulario/solicitud-proyecto-presupuesto-global/solicitud-proyecto-presupuesto-global.component';
import { SolicitudProyectoResponsableEconomicoComponent } from './solicitud-formulario/solicitud-proyecto-responsable-economico/solicitud-proyecto-responsable-economico.component';
import { SolicitudProyectoSocioComponent } from './solicitud-formulario/solicitud-proyecto-socio/solicitud-proyecto-socio.component';
import { SolicitudListadoComponent } from './solicitud-listado/solicitud-listado.component';
import { SolicitudRoutingModule } from './solicitud-routing.module';

@NgModule({
  declarations: [
    SolicitudCrearComponent,
    SolicitudEditarComponent,
    SolicitudListadoComponent,
    SolicitudDatosGeneralesComponent,
    SolicitudModalidadEntidadConvocanteModalComponent,
    SolicitudHistoricoEstadosComponent,
    SolicitudDocumentosComponent,
    SolicitiudHitosModalComponent,
    SolicitudHitosComponent,
    SolicitudProyectoFichaGeneralComponent,
    SolicitudAreaTematicaModalComponent,
    SolicitudEquipoProyectoComponent,
    SolicitudProyectoSocioComponent,
    SolicitudProyectoEntidadesFinanciadorasComponent,
    SolicitudProyectoPresupuestoGlobalComponent,
    SolicitudProyectoPresupuestoEntidadesComponent,
    SolicitudCrearProyectoModalComponent,
    CambioEstadoModalComponent,
    SolicitudProyectoClasificacionesComponent,
    SolicitudProyectoAreaConocimientoComponent,
    SolicitudProyectoResponsableEconomicoComponent,
    SolicitudProyectoResponsableEconomicoModalComponent,
    SolicitudAutoevaluacionComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    SolicitudRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    CspSharedModule,
    SgoSharedModule,
    SgpSharedModule,
    SgempSharedModule,
    FormlyFormsModule
  ],
  providers: [
    SolicitudDataResolver,
    SolicitudCrearGuard
  ]
})
export class SolicitudModule { }
