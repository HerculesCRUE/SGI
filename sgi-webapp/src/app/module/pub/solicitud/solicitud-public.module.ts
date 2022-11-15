import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedPublicModule } from '@shared/shared-public.module';
import { SharedModule } from '@shared/shared.module';
import { SgoPublicSharedModule } from 'src/app/esb/sgo/shared/sgo-public-shared.module';
import { SgpPublicSharedModule } from 'src/app/esb/sgp/shared/sgp-public-shared.module';
import { CambioEstadoPublicModalComponent } from './modals/cambio-estado-public-modal/cambio-estado-public-modal.component';
import { SolicitudModalidadEntidadConvocantePublicModalComponent } from './modals/solicitud-modalidad-entidad-convocante-public-modal/solicitud-modalidad-entidad-convocante-public-modal.component';
import { SolicitudRrhhAcreditarCategoriaProfesionalPublicModalComponent } from './modals/solicitud-rrhh-acreditar-categoria-profesional-public/solicitud-rrhh-acreditar-categoria-profesional-public-modal.component';
import { SolicitudRrhhAcreditarNivelAcademicoPublicModalComponent } from './modals/solicitud-rrhh-acreditar-nivel-academico-public/solicitud-rrhh-acreditar-nivel-academico-public-modal.component';
import { SolicitudConsultarComponent } from './solicitud-consultar/solicitud-consultar.component';
import { SolicitudPublicCrearComponent } from './solicitud-crear/solicitud-public-crear.component';
import { SolicitudPublicCrearGuard } from './solicitud-crear/solicitud-public-crear.guard';
import { SolicitudPublicEditarComponent } from './solicitud-editar/solicitud-public-editar.component';
import { SolicitudDatosGeneralesPublicComponent } from './solicitud-formulario/solicitud-datos-generales-public/solicitud-datos-generales-public.component';
import { SolicitudDocumentosPublicComponent } from './solicitud-formulario/solicitud-documentos-public/solicitud-documentos-public.component';
import { SolicitudHistoricoEstadosPublicComponent } from './solicitud-formulario/solicitud-historico-estados-public/solicitud-historico-estados-public.component';
import { SolicitudRrhhMemoriaPublicComponent } from './solicitud-formulario/solicitud-rrhh-memoria-public/solicitud-rrhh-memoria-public.component';
import { SolicitudRrhhRequisitosConvocatoriaPublicComponent } from './solicitud-formulario/solicitud-rrhh-requisitos-convocatoria-public/solicitud-rrhh-requisitos-convocatoria-public.component';
import { SolicitudRrhhTutorPublicComponent } from './solicitud-formulario/solicitud-rrhh-tutor-public/solicitud-rrhh-tutor-public.component';
import { SolicitudPublicDataResolver } from './solicitud-public-data.resolver';
import { SolicitudRoutingPublicModule } from './solicitud-public-routing.module';

@NgModule({
  declarations: [
    CambioEstadoPublicModalComponent,
    SolicitudDatosGeneralesPublicComponent,
    SolicitudConsultarComponent,
    SolicitudDocumentosPublicComponent,
    SolicitudHistoricoEstadosPublicComponent,
    SolicitudModalidadEntidadConvocantePublicModalComponent,
    SolicitudPublicCrearComponent,
    SolicitudPublicEditarComponent,
    SolicitudRrhhAcreditarCategoriaProfesionalPublicModalComponent,
    SolicitudRrhhAcreditarNivelAcademicoPublicModalComponent,
    SolicitudRrhhMemoriaPublicComponent,
    SolicitudRrhhRequisitosConvocatoriaPublicComponent,
    SolicitudRrhhTutorPublicComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    SolicitudRoutingPublicModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgoPublicSharedModule,
    SharedPublicModule,
    SgpPublicSharedModule
  ],
  providers: [
    SolicitudPublicDataResolver,
    SolicitudPublicCrearGuard
  ]
})
export class SolicitudPublicModule { }
