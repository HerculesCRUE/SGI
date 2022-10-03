import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { ConvocatoriaPublicEditarComponent } from './convocatoria-editar/convocatoria-public-editar.component';
import { ConvocatoriaConceptoGastoPublicComponent } from './convocatoria-formulario/convocatoria-concepto-gasto-public/convocatoria-concepto-gasto-public.component';
import { ConvocatoriaDatosGeneralesPublicComponent } from './convocatoria-formulario/convocatoria-datos-generales-public/convocatoria-datos-generales-public.component';
import { ConvocatoriaDocumentosPublicComponent } from './convocatoria-formulario/convocatoria-documentos-public/convocatoria-documentos-public.component';
import { ConvocatoriaEnlacePublicComponent } from './convocatoria-formulario/convocatoria-enlace-public/convocatoria-enlace-public.component';
import { ConvocatoriaEntidadesConvocantesPublicComponent } from './convocatoria-formulario/convocatoria-entidades-convocantes-public/convocatoria-entidades-convocantes-public.component';
import { ConvocatoriaEntidadesFinanciadorasPublicComponent } from './convocatoria-formulario/convocatoria-entidades-financiadoras-public/convocatoria-entidades-financiadoras-public.component';
import { ConvocatoriaHitosPublicComponent } from './convocatoria-formulario/convocatoria-hitos-public/convocatoria-hitos-public.component';
import { ConvocatoriaPartidaPresupuestariaPublicComponent } from './convocatoria-formulario/convocatoria-partidas-presupuestarias-public/convocatoria-partidas-presupuestarias-public.component';
import { ConvocatoriaPeriodosJustificacionPublicComponent } from './convocatoria-formulario/convocatoria-periodos-justificacion-public/convocatoria-periodos-justificacion-public.component';
import { ConvocatoriaPlazosFasesPublicComponent } from './convocatoria-formulario/convocatoria-plazos-fases-public/convocatoria-plazos-fases-public.component';
import { ConvocatoriaRequisitosEquipoPublicComponent } from './convocatoria-formulario/convocatoria-requisitos-equipo-public/convocatoria-requisitos-equipo-public.component';
import { ConvocatoriaRequisitosIPPublicComponent } from './convocatoria-formulario/convocatoria-requisitos-ip-public/convocatoria-requisitos-ip-public.component';
import { ConvocatoriaSeguimientoCientificoPublicComponent } from './convocatoria-formulario/convocatoria-seguimiento-cientifico-public/convocatoria-seguimiento-cientifico-public.component';
import { ConvocatoriaPublicDataResolver } from './convocatoria-public-data.resolver';
import { ConvocatoriaPublicListadoComponent } from './convocatoria-public-listado/convocatoria-public-listado.component';
import { ConvocatoriaPublicRoutingModule } from './convocatoria-public-routing.module';

@NgModule({
  declarations: [
    ConvocatoriaPublicListadoComponent,
    ConvocatoriaPublicEditarComponent,
    ConvocatoriaDatosGeneralesPublicComponent,
    ConvocatoriaSeguimientoCientificoPublicComponent,
    ConvocatoriaRequisitosIPPublicComponent,
    ConvocatoriaRequisitosEquipoPublicComponent,
    ConvocatoriaPlazosFasesPublicComponent,
    ConvocatoriaPeriodosJustificacionPublicComponent,
    ConvocatoriaPartidaPresupuestariaPublicComponent,
    ConvocatoriaHitosPublicComponent,
    ConvocatoriaEntidadesFinanciadorasPublicComponent,
    ConvocatoriaEntidadesConvocantesPublicComponent,
    ConvocatoriaEnlacePublicComponent,
    ConvocatoriaDocumentosPublicComponent,
    ConvocatoriaConceptoGastoPublicComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    ConvocatoriaPublicRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgempSharedModule
  ],
  providers: [
    ConvocatoriaPublicDataResolver
  ]
})
export class ConvocatoriaPublicModule { }
