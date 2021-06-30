import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ModeloEjecucionCrearComponent } from './modelo-ejecucion-crear/modelo-ejecucion-crear.component';
import { ModeloEjecucionEditarComponent } from './modelo-ejecucion-editar/modelo-ejecucion-editar.component';
import { ModeloEjecucionDatosGeneralesComponent } from './modelo-ejecucion-formulario/modelo-ejecucion-datos-generales/modelo-ejecucion-datos-generales.component';
import { ModeloEjecucionTipoDocumentoComponent } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-documento/modelo-ejecucion-tipo-documento.component';
import { ModeloEjecucionTipoEnlaceComponent } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-enlace/modelo-ejecucion-tipo-enlace.component';
import { ModeloEjecucionTipoFaseComponent } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-fase/modelo-ejecucion-tipo-fase.component';
import { ModeloEjecucionTipoFinalidadComponent } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-finalidad/modelo-ejecucion-tipo-finalidad.component';
import { ModeloEjecucionTipoHitoComponent } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-hito/modelo-ejecucion-tipo-hito.component';
import { ModeloEjecucionTipoUnidadGestionComponent } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-unidad-gestion/modelo-ejecucion-tipo-unidad-gestion.component';
import { ModeloEjecucionListadoComponent } from './modelo-ejecucion-listado/modelo-ejecucion-listado.component';
import { MODELO_EJECUCION_ROUTE_NAMES } from './modelo-ejecucion-route-names';
import { ModeloEjecucionResolver } from './modelo-ejecucion.resolver';

const MSG_NEW_TITLE = marker('title.new.entity');
const MODELO_EJECUCION_KEY = marker('csp.modelo-ejecucion');

const routes: SgiRoutes = [
  {
    path: '',
    component: ModeloEjecucionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MODELO_EJECUCION_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthorityForAnyUO: ['CSP-ME-V', 'CSP-ME-C', 'CSP-ME-E', 'CSP-ME-B', 'CSP-ME-R']
    },
  },
  {
    path: ROUTE_NAMES.NEW,
    component: ModeloEjecucionCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: MODELO_EJECUCION_KEY, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAuthorityForAnyUO: 'CSP-ME-C'
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: MODELO_EJECUCION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: MODELO_EJECUCION_ROUTE_NAMES.DATOS_GENERALES,
        component: ModeloEjecucionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: MODELO_EJECUCION_ROUTE_NAMES.TIPO_ENLACES,
        component: ModeloEjecucionTipoEnlaceComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: MODELO_EJECUCION_ROUTE_NAMES.TIPO_FINALIDADES,
        component: ModeloEjecucionTipoFinalidadComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: MODELO_EJECUCION_ROUTE_NAMES.TIPO_FASES,
        component: ModeloEjecucionTipoFaseComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: MODELO_EJECUCION_ROUTE_NAMES.TIPO_DOCUMENTOS,
        component: ModeloEjecucionTipoDocumentoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: MODELO_EJECUCION_ROUTE_NAMES.TIPO_HITOS,
        component: ModeloEjecucionTipoHitoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: MODELO_EJECUCION_ROUTE_NAMES.UNIDAD_GESTION,
        component: ModeloEjecucionTipoUnidadGestionComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  },
  {
    path: `:id`,
    component: ModeloEjecucionEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      modeloEjecucion: ModeloEjecucionResolver
    },
    data: {
      title: MODELO_EJECUCION_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAuthorityForAnyUO: 'CSP-ME-E'
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: MODELO_EJECUCION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: MODELO_EJECUCION_ROUTE_NAMES.DATOS_GENERALES,
        component: ModeloEjecucionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: MODELO_EJECUCION_ROUTE_NAMES.TIPO_ENLACES,
        component: ModeloEjecucionTipoEnlaceComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: MODELO_EJECUCION_ROUTE_NAMES.TIPO_FINALIDADES,
        component: ModeloEjecucionTipoFinalidadComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: MODELO_EJECUCION_ROUTE_NAMES.TIPO_FASES,
        component: ModeloEjecucionTipoFaseComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: MODELO_EJECUCION_ROUTE_NAMES.TIPO_DOCUMENTOS,
        component: ModeloEjecucionTipoDocumentoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: MODELO_EJECUCION_ROUTE_NAMES.TIPO_HITOS,
        component: ModeloEjecucionTipoHitoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: MODELO_EJECUCION_ROUTE_NAMES.UNIDAD_GESTION,
        component: ModeloEjecucionTipoUnidadGestionComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  }
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ModeloEjecuccionRoutingModule {
}
