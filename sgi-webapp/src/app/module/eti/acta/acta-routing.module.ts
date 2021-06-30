import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ActaCrearComponent } from './acta-crear/acta-crear.component';
import { ActaEditarComponent } from './acta-editar/acta-editar.component';
import {
  ActaAsistentesListadoComponent
} from './acta-formulario/acta-asistentes/acta-asistentes-listado/acta-asistentes-listado.component';
import { ActaDatosGeneralesComponent } from './acta-formulario/acta-datos-generales/acta-datos-generales.component';
import { ActaMemoriasComponent } from './acta-formulario/acta-memorias/acta-memorias.component';
import { ActaListadoComponent } from './acta-listado/acta-listado.component';
import { ACTA_ROUTE_NAMES } from './acta-route-names';
import { ActaResolver } from './acta.resolver';

const MSG_NEW_TITLE = marker('title.new.entity');
const ACTA_KEY = marker('eti.acta');

const routes: SgiRoutes = [
  {
    path: '',
    component: ActaListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: ACTA_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAuthorityForAnyUO: 'ETI-ACT-V'
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: ActaCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: ACTA_KEY, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAuthorityForAnyUO: 'ETI-ACT-C'
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: ACTA_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: ACTA_ROUTE_NAMES.DATOS_GENERALES,
        component: ActaDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: ACTA_ROUTE_NAMES.MEMORIAS,
        component: ActaMemoriasComponent
      },
      {
        path: ACTA_ROUTE_NAMES.ASISTENTES,
        component: ActaAsistentesListadoComponent
      }
    ]
  },
  {
    path: `:id`,
    component: ActaEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      acta: ActaResolver
    },
    data: {
      title: ACTA_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAuthorityForAnyUO: 'ETI-ACT-E'
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: ACTA_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: ACTA_ROUTE_NAMES.DATOS_GENERALES,
        component: ActaDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: ACTA_ROUTE_NAMES.MEMORIAS,
        component: ActaMemoriasComponent
      },
      {
        path: ACTA_ROUTE_NAMES.ASISTENTES,
        component: ActaAsistentesListadoComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ActaRoutingModule {
}
