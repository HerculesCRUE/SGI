import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { InvencionCrearComponent } from './invencion-crear/invencion-crear.component';
import { InvencionEditarComponent } from './invencion-editar/invencion-editar.component';
import { InvencionContratosComponent } from './invencion-formulario/invencion-contratos/invencion-contratos.component';
import { InvencionDatosGeneralesComponent } from './invencion-formulario/invencion-datos-generales/invencion-datos-generales.component';
import { InvencionDocumentoComponent } from './invencion-formulario/invencion-documento/invencion-documento.component';
import { InvencionGastosComponent } from './invencion-formulario/invencion-gastos/invencion-gastos.component';
import { InvencionInformesPatentabilidadComponent } from './invencion-formulario/invencion-informes-patentabilidad/invencion-informes-patentabilidad.component';
import { InvencionIngresosComponent } from './invencion-formulario/invencion-ingresos/invencion-ingresos.component';
import { InvencionInventorComponent } from './invencion-formulario/invencion-inventor/invencion-inventor.component';
import { InvencionRepartosComponent } from './invencion-formulario/invencion-repartos/invencion-repartos.component';
import { PeriodoTitularidadComponent } from './invencion-formulario/periodo-titularidad/periodo-titularidad.component';
import { SolicitudProteccionComponent } from './invencion-formulario/solicitud-proteccion/solicitud-proteccion.component';
import { InvencionListadoComponent } from './invencion-listado/invencion-listado.component';
import { INVENCION_ROUTE_NAMES } from './invencion-route-names';
import { INVENCION_ROUTE_PARAMS } from './invencion-route-params';
import { InvencionResolver, INVENCION_DATA_KEY } from './invencion.resolver';

const MSG_LISTADO_TITLE = marker('menu.pii.invenciones');
const MSG_NEW_TITLE = marker('title.new.entity');
const INVENCION_KEY = marker('pii.invencion');
const SOLICITUD_PROTECCION_KEY = marker('pii.solicitud-proteccion');
const REPARTO_KEY = marker('pii.reparto');

const routes: SgiRoutes = [
  {
    path: '',
    component: InvencionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAnyAuthority: ['PII-INV-V', 'PII-INV-C', 'PII-INV-E', 'PII-INV-B', 'PII-INV-R']
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: InvencionCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: INVENCION_KEY, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAuthority: 'PII-INV-C'
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: INVENCION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: INVENCION_ROUTE_NAMES.DATOS_GENERALES,
        component: InvencionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: INVENCION_ROUTE_NAMES.EQUIPO_INVENTOR,
        component: InvencionInventorComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
  {
    path: `:${INVENCION_ROUTE_PARAMS.ID}`,
    component: InvencionEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      [INVENCION_DATA_KEY]: InvencionResolver
    },
    data: {
      title: INVENCION_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAnyAuthority: ['PII-INV-V', 'PII-INV-E']
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: INVENCION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: INVENCION_ROUTE_NAMES.DATOS_GENERALES,
        component: InvencionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: INVENCION_ROUTE_NAMES.DOCUMENTOS,
        component: InvencionDocumentoComponent,
      },
      {
        path: INVENCION_ROUTE_NAMES.INFORME_PATENTABILIDAD,
        component: InvencionInformesPatentabilidadComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: INVENCION_ROUTE_NAMES.EQUIPO_INVENTOR,
        component: InvencionInventorComponent,
      },
      {
        path: INVENCION_ROUTE_NAMES.SOLICITUDES_PROTECCION,
        component: SolicitudProteccionComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: INVENCION_ROUTE_NAMES.GASTOS,
        component: InvencionGastosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: INVENCION_ROUTE_NAMES.INGRESOS,
        component: InvencionIngresosComponent,
        canDeactivate: [FragmentGuard],
      },
      {
        path: INVENCION_ROUTE_NAMES.TITULARIDAD,
        component: PeriodoTitularidadComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: INVENCION_ROUTE_NAMES.CONTRATOS,
        component: InvencionContratosComponent,
        canDeactivate: [FragmentGuard],
      },
      {
        path: INVENCION_ROUTE_NAMES.REPARTOS,
        component: InvencionRepartosComponent,
        canDeactivate: [FragmentGuard],
      }
    ]
  },
  {
    path: `:${INVENCION_ROUTE_PARAMS.ID}`,
    canActivate: [SgiAuthGuard],
    data: {
      title: INVENCION_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAnyAuthorityForAnyUO: ['PII-INV-E', 'PII-INV-V']
    },
    resolve: {
      [INVENCION_DATA_KEY]: InvencionResolver
    },
    children: [
      {
        path: INVENCION_ROUTE_NAMES.SOLICITUDES_PROTECCION,
        loadChildren: () =>
          import('../solicitud-proteccion/solicitud-proteccion.module').then(
            (m) => m.SolicitudProteccionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: SOLICITUD_PROTECCION_KEY
        }
      },
      {
        path: INVENCION_ROUTE_NAMES.REPARTOS,
        loadChildren: () =>
          import('../invencion-reparto/invencion-reparto.module').then(
            m => m.InvencionRepartoModule),
        canActivate: [SgiAuthGuard],
        data: {
          title: REPARTO_KEY
        }
      },
      {
        path: '**',
        redirectTo: INVENCION_ROUTE_NAMES.DATOS_GENERALES
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class InvencionRoutingModule {
}
