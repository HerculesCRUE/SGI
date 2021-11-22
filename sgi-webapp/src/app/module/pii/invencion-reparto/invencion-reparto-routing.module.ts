import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { InvencionRepartoCrearComponent } from './invencion-reparto-crear/invencion-reparto-crear.component';
import { InvencionRepartoDataResolver, INVENCION_REPARTO_DATA_KEY } from './invencion-reparto-data.resolver';
import { InvencionRepartoEditarComponent } from './invencion-reparto-editar/invencion-reparto-editar.component';
import { InvencionRepartoDatosGeneralesComponent } from './invencion-reparto-formulario/invencion-reparto-datos-generales/invencion-reparto-datos-generales.component';
import { InvencionRepartoEquipoInventorComponent } from './invencion-reparto-formulario/invencion-reparto-equipo-inventor/invencion-reparto-equipo-inventor.component';
import { INVENCION_REPARTO_ROUTE_NAMES } from './invencion-reparto-route-names';
import { INVENCION_REPARTO_ROUTE_PARAMS } from './invencion-reparto-route-params';

const MSG_NEW_TITLE = marker('title.new.entity');
const REPARTO_KEY = marker('pii.reparto');

const routes: SgiRoutes = [
  {
    path: `${ROUTE_NAMES.NEW}`,
    component: InvencionRepartoCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: REPARTO_KEY, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAnyAuthority: ['PII-INV-E', 'PII-INV-V']
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: INVENCION_REPARTO_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: INVENCION_REPARTO_ROUTE_NAMES.DATOS_GENERALES,
        component: InvencionRepartoDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
  {
    path: `:${INVENCION_REPARTO_ROUTE_PARAMS.ID}`,
    component: InvencionRepartoEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: REPARTO_KEY,
      hasAnyAuthority: ['PII-INV-E', 'PII-INV-V']
    },
    resolve: {
      [INVENCION_REPARTO_DATA_KEY]: InvencionRepartoDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: INVENCION_REPARTO_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: INVENCION_REPARTO_ROUTE_NAMES.DATOS_GENERALES,
        component: InvencionRepartoEquipoInventorComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class InvencionRepartoRoutingModule { }
