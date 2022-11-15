import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
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
import { SolicitudPublicDataResolver, SOLICITUD_PUBLIC_DATA_KEY } from './solicitud-public-data.resolver';
import { SOLICITUD_PUBLIC_ROUTE_NAMES } from './solicitud-public-route-names';
import { SOLICITUD_PUBLIC_ROUTE_PARAMS } from './solicitud-public-route-params';

const SOLICITUD_KEY = marker('csp.solicitud');
const MSG_NEW_TITLE = marker('title.new.entity');
const MSG_SOLICITUD_TITLE = marker('pub.solicitud.consultar.titulo');

const routes: SgiRoutes = [
  {
    path: '',
    component: SolicitudConsultarComponent,
    data: {
      title: MSG_SOLICITUD_TITLE
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: SolicitudPublicCrearComponent,
    canActivate: [SolicitudPublicCrearGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: SOLICITUD_KEY, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      }
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: SOLICITUD_PUBLIC_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: SOLICITUD_PUBLIC_ROUTE_NAMES.DATOS_GENERALES,
        component: SolicitudDatosGeneralesPublicComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
  {
    path: `:${SOLICITUD_PUBLIC_ROUTE_PARAMS.ID}`,
    component: SolicitudPublicEditarComponent,
    canDeactivate: [ActionGuard],
    resolve: {
      [SOLICITUD_PUBLIC_DATA_KEY]: SolicitudPublicDataResolver
    },
    data: {
      title: SOLICITUD_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: SOLICITUD_PUBLIC_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: SOLICITUD_PUBLIC_ROUTE_NAMES.DATOS_GENERALES,
        component: SolicitudDatosGeneralesPublicComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_PUBLIC_ROUTE_NAMES.DOCUMENTOS,
        component: SolicitudDocumentosPublicComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_PUBLIC_ROUTE_NAMES.HISTORICO_ESTADOS,
        component: SolicitudHistoricoEstadosPublicComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_PUBLIC_ROUTE_NAMES.TUTOR,
        component: SolicitudRrhhTutorPublicComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_PUBLIC_ROUTE_NAMES.REQUISITOS_CONVOCATORIA,
        component: SolicitudRrhhRequisitosConvocatoriaPublicComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_PUBLIC_ROUTE_NAMES.MEMORIA,
        component: SolicitudRrhhMemoriaPublicComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SolicitudRoutingPublicModule {
}
