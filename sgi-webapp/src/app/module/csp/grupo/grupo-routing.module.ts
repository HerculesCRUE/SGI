import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { GRUPO_ROUTE_PARAMS } from './grupo-route-params';
import { GrupoCrearComponent } from './grupo-crear/grupo-crear.component';
import { GrupoDataResolver, GRUPO_DATA_KEY } from './grupo-data.resolver';
import { GrupoEditarComponent } from './grupo-editar/grupo-editar.component';
import { GrupoDatosGeneralesComponent } from './grupo-formulario/grupo-datos-generales/grupo-datos-generales.component';
import { GrupoEnlaceComponent } from './grupo-formulario/grupo-enlace/grupo-enlace.component';
import { GrupoEquipoInstrumentalComponent } from './grupo-formulario/grupo-equipo-instrumental/grupo-equipo-instrumental.component';
import { GrupoEquipoInvestigacionComponent } from './grupo-formulario/grupo-equipo-investigacion/grupo-equipo-investigacion.component';
import { GrupoLineaInvestigacionComponent } from './grupo-formulario/grupo-linea-investigacion-listado/grupo-linea-investigacion.component';
import { GrupoPersonaAutorizadaComponent } from './grupo-formulario/grupo-persona-autorizada/grupo-persona-autorizada.component';
import { GrupoResponsableEconomicoComponent } from './grupo-formulario/grupo-responsable-economico/grupo-responsable-economico.component';
import { GrupoListadoComponent } from './grupo-listado/grupo-listado.component';
import { GRUPO_ROUTE_NAMES } from './grupo-route-names';

const GRUPO_TITLE_KEY = marker('csp.grupo');
const GRUPO_LINEA_INVESTIGACION_KEY = marker('csp.grupo-linea-investigacion');
const MSG_NEW_TITLE = marker('title.new.entity');

const routes: SgiRoutes = [
  {
    path: '',
    component: GrupoListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: GRUPO_TITLE_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthorityForAnyUO: ['CSP-GIN-E', 'CSP-GIN-B', 'CSP-GIN-R', 'CSP-GIN-V']
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: GrupoCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: GRUPO_TITLE_KEY, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR,
      },
      hasAuthorityForAnyUO: 'CSP-GIN-C'
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: GRUPO_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: GRUPO_ROUTE_NAMES.DATOS_GENERALES,
        component: GrupoDatosGeneralesComponent,
        canDeactivate: [FragmentGuard],
      },
    ]
  },
  {
    path: `:${GRUPO_ROUTE_PARAMS.ID}`,
    component: GrupoEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      [GRUPO_DATA_KEY]: GrupoDataResolver
    },
    data: {
      title: GRUPO_TITLE_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAnyAuthorityForAnyUO: ['CSP-GIN-E', 'CSP-GIN-V']
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: GRUPO_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: GRUPO_ROUTE_NAMES.DATOS_GENERALES,
        component: GrupoDatosGeneralesComponent,
        canDeactivate: [FragmentGuard],
      },
      {
        path: GRUPO_ROUTE_NAMES.EQUIPO_INVESTIGACION,
        component: GrupoEquipoInvestigacionComponent,
        canDeactivate: [FragmentGuard],
      },
      {
        path: GRUPO_ROUTE_NAMES.RESPONSABLE_ECONOMICO,
        component: GrupoResponsableEconomicoComponent,
        canDeactivate: [FragmentGuard],
      },
      {
        path: GRUPO_ROUTE_NAMES.EQUIPO_INSTRUMENTAL,
        component: GrupoEquipoInstrumentalComponent,
        canDeactivate: [FragmentGuard],
      },
      {
        path: GRUPO_ROUTE_NAMES.ENLACE,
        component: GrupoEnlaceComponent,
        canDeactivate: [FragmentGuard],
      },
      {
        path: GRUPO_ROUTE_NAMES.PERSONA_AUTORIZADA,
        component: GrupoPersonaAutorizadaComponent,
        canDeactivate: [FragmentGuard],
      },
      {
        path: GRUPO_ROUTE_NAMES.LINEA_INVESTIGACION,
        component: GrupoLineaInvestigacionComponent,
        canDeactivate: [FragmentGuard],
      },
    ]
  },
  {
    path: `:${GRUPO_ROUTE_PARAMS.ID}`,
    canActivate: [SgiAuthGuard],
    data: {
      title: GRUPO_TITLE_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAnyAuthorityForAnyUO: ['CSP-GIN-E', 'CSP-GIN-V']
    },
    resolve: {
      [GRUPO_DATA_KEY]: GrupoDataResolver
    },
    children: [
      {
        path: GRUPO_ROUTE_NAMES.LINEA_INVESTIGACION,
        loadChildren: () =>
          import('../grupo-linea-investigacion/grupo-linea-investigacion.module').then(
            (m) => m.GrupoLineaInvestigacionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: GRUPO_LINEA_INVESTIGACION_KEY,
          hasAnyAuthorityForAnyUO: ['CSP-GIN-E', 'CSP-GIN-V']
        }
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class GrupoRoutingModule { }
