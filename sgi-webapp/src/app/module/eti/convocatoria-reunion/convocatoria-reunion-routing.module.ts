import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ConvocatoriaReunionCrearComponent } from './convocatoria-reunion-crear/convocatoria-reunion-crear.component';
import { ConvocatoriaReunionDataResolver, CONVOCATORIA_REUNION_DATA_KEY } from './convocatoria-reunion-data.resolver';
import { ConvocatoriaReunionEditarComponent } from './convocatoria-reunion-editar/convocatoria-reunion-editar.component';
import { ConvocatoriaReunionAsignacionMemoriasListadoComponent } from './convocatoria-reunion-formulario/convocatoria-reunion-asignacion-memorias/convocatoria-reunion-asignacion-memorias-listado/convocatoria-reunion-asignacion-memorias-listado.component';
import { ConvocatoriaReunionDatosGeneralesComponent } from './convocatoria-reunion-formulario/convocatoria-reunion-datos-generales/convocatoria-reunion-datos-generales.component';
import { ConvocatoriaReunionListadoComponent } from './convocatoria-reunion-listado/convocatoria-reunion-listado.component';
import { CONVOCATORIA_REUNION_ROUTE_NAMES } from './convocatoria-reunion-route-names';
import { CONVOCATORIA_REUNION_ROUTE_PARAMS } from './convocatoria-reunion-route-params';

const CONVOCATORIA_REUNION_KEY = marker('eti.convocatoria-reunion');
const MSG_NEW_TITLE = marker('title.new.entity');

const routes: SgiRoutes = [
  {
    path: '',
    component: ConvocatoriaReunionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: CONVOCATORIA_REUNION_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAuthorityForAnyUO: 'ETI-CNV-V'
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: ConvocatoriaReunionCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: CONVOCATORIA_REUNION_KEY, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAuthorityForAnyUO: 'ETI-CNV-C'
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: CONVOCATORIA_REUNION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: CONVOCATORIA_REUNION_ROUTE_NAMES.DATOS_GENERALES,
        component: ConvocatoriaReunionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_REUNION_ROUTE_NAMES.ASIGNACION_MEMORIAS,
        component: ConvocatoriaReunionAsignacionMemoriasListadoComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
  {
    path: `:${CONVOCATORIA_REUNION_ROUTE_PARAMS.ID}`,
    component: ConvocatoriaReunionEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      [CONVOCATORIA_REUNION_DATA_KEY]: ConvocatoriaReunionDataResolver
    },
    data: {
      title: CONVOCATORIA_REUNION_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAuthorityForAnyUO: 'ETI-CNV-E'
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: CONVOCATORIA_REUNION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: CONVOCATORIA_REUNION_ROUTE_NAMES.DATOS_GENERALES,
        component: ConvocatoriaReunionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_REUNION_ROUTE_NAMES.ASIGNACION_MEMORIAS,
        component: ConvocatoriaReunionAsignacionMemoriasListadoComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ConvocatoriaReunionRoutingModule {
}
