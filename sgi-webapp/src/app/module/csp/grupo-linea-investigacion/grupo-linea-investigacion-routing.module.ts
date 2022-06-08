import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { GrupoLineaInvestigacionCrearComponent } from './grupo-linea-investigacion-crear/grupo-linea-investigacion-crear.component';
import { GrupoLineaInvestigacionDataResolver, GRUPO_LINEA_INVESTIGACION_DATA_KEY } from './grupo-linea-investigacion-data.resolver';
import { GrupoLineaInvestigacionEditarComponent } from './grupo-linea-investigacion-editar/grupo-linea-investigacion-editar.component';
import { GrupoLineaClasificacionesComponent } from './grupo-linea-investigacion-formulario/grupo-linea-clasificaciones/grupo-linea-clasificaciones.component';
import { GrupoLineaEquipoInstrumentalComponent } from './grupo-linea-investigacion-formulario/grupo-linea-equipo-instrumental/grupo-linea-equipo-instrumental.component';
import { GrupoLineaInvestigacionDatosGeneralesComponent } from './grupo-linea-investigacion-formulario/grupo-linea-investigacion-datos-generales/grupo-linea-investigacion-datos-generales.component';
import { GrupoLineaInvestigadorComponent } from './grupo-linea-investigacion-formulario/grupo-linea-investigacion-linea-investigador/grupo-linea-investigador.component';
import { GRUPO_LINEA_INVESTIGACION_ROUTE_NAMES } from './grupo-linea-investigacion-route-names';
import { GRUPO_LINEA_INVESTIGACION_ROUTE_PARAMS } from './grupo-linea-investigacion-route-params';

const MSG_NEW_TITLE = marker('title.new.entity');
const MSG_EDIT_TITLE = marker('csp.grupo-linea-investigacion');

const routes: SgiRoutes = [
  {
    path: `${ROUTE_NAMES.NEW}`,
    component: GrupoLineaInvestigacionCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: MSG_EDIT_TITLE, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR,
        hasAuthorityForAnyUO: 'CSP-GIN-E'
      }
    },
    resolve: {
      [GRUPO_LINEA_INVESTIGACION_DATA_KEY]: GrupoLineaInvestigacionDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: GRUPO_LINEA_INVESTIGACION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: GRUPO_LINEA_INVESTIGACION_ROUTE_NAMES.DATOS_GENERALES,
        component: GrupoLineaInvestigacionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  },
  {
    path: `:${GRUPO_LINEA_INVESTIGACION_ROUTE_PARAMS.ID}`,
    component: GrupoLineaInvestigacionEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_EDIT_TITLE,
      hasAnyAuthorityForAnyUO: ['CSP-GIN-E', 'CSP-GIN-V', 'CSP-GIN-INV-VR']
    },
    resolve: {
      [GRUPO_LINEA_INVESTIGACION_DATA_KEY]: GrupoLineaInvestigacionDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: GRUPO_LINEA_INVESTIGACION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: GRUPO_LINEA_INVESTIGACION_ROUTE_NAMES.DATOS_GENERALES,
        component: GrupoLineaInvestigacionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: GRUPO_LINEA_INVESTIGACION_ROUTE_NAMES.LINEA_INVESTIGADOR,
        component: GrupoLineaInvestigadorComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: GRUPO_LINEA_INVESTIGACION_ROUTE_NAMES.CLASIFICACIONES,
        component: GrupoLineaClasificacionesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: GRUPO_LINEA_INVESTIGACION_ROUTE_NAMES.EQUIPO_INSTRUMENTAL,
        component: GrupoLineaEquipoInstrumentalComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class GrupoLineaInvestigacionRouting {
}
