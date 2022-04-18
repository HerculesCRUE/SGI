import { CommonModule, DecimalPipe } from '@angular/common';
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
import { SolicitudGrupoModalComponent } from './modals/solicitud-grupo-modal/solicitud-grupo-modal.component';
import { SolicitudHitosModalComponent } from './modals/solicitud-hitos-modal/solicitud-hitos-modal.component';
import { SolicitudListadoExportModalComponent } from './modals/solicitud-listado-export-modal/solicitud-listado-export-modal.component';
import { SolicitudModalidadEntidadConvocanteModalComponent } from './modals/solicitud-modalidad-entidad-convocante-modal/solicitud-modalidad-entidad-convocante-modal.component';
import { SolicitudProyectoPresupuestoListadoExportModalComponent } from './modals/solicitud-proyecto-presupuesto-listado-export-modal/solicitud-proyecto-presupuesto-listado-export-modal.component';
import { SolicitudProyectoResponsableEconomicoModalComponent } from './modals/solicitud-proyecto-responsable-economico-modal/solicitud-proyecto-responsable-economico-modal.component';
import { SolicitudCrearComponent } from './solicitud-crear/solicitud-crear.component';
import { SolicitudCrearGuard } from './solicitud-crear/solicitud-crear.guard';
import { SolicitudDataResolver } from './solicitud-data.resolver';
import { SolicitudEditarComponent } from './solicitud-editar/solicitud-editar.component';
import { SolicitudEntidadConvocanteListadoExportService } from './solicitud-entidad-convocante-listado-export.service';
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
import { SolicitudGeneralListadoExportService } from './solicitud-general-listado-export.service';
import { SolicitudListadoExportService } from './solicitud-listado-export.service';
import { SolicitudListadoComponent } from './solicitud-listado/solicitud-listado.component';
import { SolicitudProyectoAreaConocimientoListadoExportService } from './solicitud-proyecto-area-conocimiento-listado-export.service';
import { SolicitudProyectoClasificacionListadoExportService } from './solicitud-proyecto-clasificacion-listado-export.service';
import { SolicitudProyectoEntidadFinanciadoraListadoExportService } from './solicitud-proyecto-entidad-financiadora-listado-export.service';
import { SolicitudProyectoEquipoListadoExportService } from './solicitud-proyecto-equipo-listado-export.service';
import { SolicitudProyectoFichaGeneralListadoExportService } from './solicitud-proyecto-ficha-general-listado-export.service';
import { SolicitudProyectoPresupuestoListadoExportService } from './solicitud-proyecto-presupuesto-listado-export.service';
import { SolicitudProyectoResponsableEconomicoListadoExportService } from './solicitud-proyecto-responsable-economico-listado-export.service';
import { SolicitudProyectoSocioListadoExportService } from './solicitud-proyecto-socio-listado-export.service';
import { SolicitudRoutingModule } from './solicitud-routing.module';

@NgModule({
  declarations: [
    SolicitudCrearComponent,
    SolicitudEditarComponent,
    SolicitudListadoComponent,
    SolicitudListadoExportModalComponent,
    SolicitudDatosGeneralesComponent,
    SolicitudModalidadEntidadConvocanteModalComponent,
    SolicitudHistoricoEstadosComponent,
    SolicitudDocumentosComponent,
    SolicitudHitosModalComponent,
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
    SolicitudAutoevaluacionComponent,
    SolicitudProyectoPresupuestoListadoExportModalComponent,
    SolicitudGrupoModalComponent,
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
    SolicitudCrearGuard,
    DecimalPipe,
    SolicitudListadoExportService,
    SolicitudGeneralListadoExportService,
    SolicitudEntidadConvocanteListadoExportService,
    SolicitudProyectoFichaGeneralListadoExportService,
    SolicitudProyectoAreaConocimientoListadoExportService,
    SolicitudProyectoClasificacionListadoExportService,
    SolicitudProyectoEquipoListadoExportService,
    SolicitudProyectoResponsableEconomicoListadoExportService,
    SolicitudProyectoSocioListadoExportService,
    SolicitudProyectoEntidadFinanciadoraListadoExportService,
    SolicitudProyectoPresupuestoListadoExportService
  ]
})
export class SolicitudModule { }
