import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { SharedModule } from '@shared/shared.module';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { SgoSharedModule } from 'src/app/esb/sgo/shared/sgo-shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../../csp/shared/csp-shared.module';
import { PiiSharedModule } from '../shared/pii-shared.module';
import { InvencionCrearComponent } from './invencion-crear/invencion-crear.component';
import { InvencionEditarComponent } from './invencion-editar/invencion-editar.component';
import { InvencionEquipoInventorListadoExportService } from './invencion-equipo-inventor-listado-export.service';
import { InvencionContratosComponent } from './invencion-formulario/invencion-contratos/invencion-contratos.component';
import { InvencionDatosGeneralesComponent } from './invencion-formulario/invencion-datos-generales/invencion-datos-generales.component';
import { InvencionDocumentoComponent } from './invencion-formulario/invencion-documento/invencion-documento.component';
import { InvencionGastosComponent } from './invencion-formulario/invencion-gastos/invencion-gastos.component';
import { InvencionInformesPatentabilidadComponent } from './invencion-formulario/invencion-informes-patentabilidad/invencion-informes-patentabilidad.component';
import { InvencionIngresosComponent } from './invencion-formulario/invencion-ingresos/invencion-ingresos.component';
import { InvencionInventorComponent } from './invencion-formulario/invencion-inventor/invencion-inventor.component';
import { InvencionRepartosComponent } from './invencion-formulario/invencion-repartos/invencion-repartos.component';
import { PeriodoTitularidadTitularesComponent } from './invencion-formulario/periodo-titularidad/periodo-titularidad-titulares/periodo-titularidad-titulares.component';
import { PeriodoTitularidadTitularidadesComponent } from './invencion-formulario/periodo-titularidad/periodo-titularidad-titularidades/periodo-titularidad-titularidades.component';
import { PeriodoTitularidadComponent } from './invencion-formulario/periodo-titularidad/periodo-titularidad.component';
import { SolicitudProteccionComponent } from './invencion-formulario/solicitud-proteccion/solicitud-proteccion.component';
import { InvencionGeneralListadoExportService } from './invencion-general-listado-export.service';
import { InvencionListadoExportService } from './invencion-listado-export.service';
import { InvencionListadoComponent } from './invencion-listado/invencion-listado.component';
import { InvencionRoutingModule } from './invencion-routing.module';
import { InvencionSolicitudesProteccionListadoExportService } from './invencion-solicitudes-proteccion-listado-export.service';
import { InvencionResolver } from './invencion.resolver';
import { InformePatentabilidadModalComponent } from './modals/informe-patentabilidad-modal/informe-patentabilidad-modal.component';
import { InvencionDocumentoModalComponent } from './modals/invencion-documento-modal/invencion-documento-modal.component';
import { InvencionGastoModalComponent } from './modals/invencion-gasto-modal/invencion-gasto-modal.component';
import { InvencionInventorModalComponent } from './modals/invencion-inventor-modal/invencion-inventor-modal.component';
import { InvencionListadoExportModalComponent } from './modals/invencion-listado-export-modal/invencion-listado-export-modal.component';
import { PeriodoTitularidadModalComponent } from './modals/periodo-titularidad-modal/periodo-titularidad-modal.component';
import { PeriodoTitularidadTitularModalComponent } from './modals/periodo-titularidad-titular-modal/periodo-titularidad-titular-modal.component';
import { SectorAplicacionModalComponent } from './modals/sector-aplicacion-modal/sector-aplicacion-modal.component';
import { SectorLicenciadoModalComponent } from './modals/sector-licenciado-modal/sector-licenciado-modal.component';
import { SolicitudProteccionListadoExportModalComponent } from './modals/solicitud-proteccion-listado-export-modal/solicitud-proteccion-listado-export-modal.component';
import { SolicitudProteccionGeneralListadoExportService } from './solicitud-proteccion-general-listado-export.service';
import { SolicitudProteccionListadoExportService } from './solicitud-proteccion-listado-export.service';

@NgModule({
  declarations: [
    InvencionListadoComponent,
    InvencionCrearComponent,
    InvencionDatosGeneralesComponent,
    InvencionEditarComponent,
    SectorAplicacionModalComponent,
    InvencionDocumentoComponent,
    InvencionDocumentoModalComponent,
    InvencionInformesPatentabilidadComponent,
    InformePatentabilidadModalComponent,
    InvencionInventorComponent,
    InvencionInventorModalComponent,
    SolicitudProteccionComponent,
    InvencionGastosComponent,
    InvencionGastoModalComponent,
    InvencionIngresosComponent,
    PeriodoTitularidadComponent,
    PeriodoTitularidadTitularidadesComponent,
    PeriodoTitularidadTitularesComponent,
    PeriodoTitularidadModalComponent,
    PeriodoTitularidadTitularModalComponent,
    InvencionContratosComponent,
    SectorLicenciadoModalComponent,
    InvencionRepartosComponent,
    InvencionListadoExportModalComponent,
    SolicitudProteccionListadoExportModalComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    InvencionRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    CspSharedModule,
    SgpSharedModule,
    SgoSharedModule,
    SgempSharedModule,
    PiiSharedModule
  ],
  providers: [
    InvencionResolver,
    InvencionListadoExportService,
    InvencionGeneralListadoExportService,
    InvencionEquipoInventorListadoExportService,
    InvencionSolicitudesProteccionListadoExportService,
    LuxonDatePipe,
    SolicitudProteccionListadoExportService,
    SolicitudProteccionGeneralListadoExportService
  ]
})
export class InvencionModule { }
