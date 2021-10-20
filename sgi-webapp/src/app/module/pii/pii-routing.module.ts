import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { PiiInicioComponent } from './pii-inicio/pii-inicio.component';
import { PiiRootComponent } from './pii-root/pii-root.component';
import { PII_ROUTE_NAMES } from './pii-route-names';

const MSG_ROOT_TITLE = marker('pii.root.title');
const MSG_INVENCION_TITLE = marker('pii.invencion');
const MSG_TIPO_PROTECCION_TITLE = marker('pii.tipo-proteccion');
const MSG_SECTOR_APLICACION_TITLE = marker('pii.sector-aplicacion');
const MSG_RESULTADO_INFORME_PATENTABILIDAD_TITLE = marker('pii.resultado-informe-patentabilidad');
const MSG_TIPO_PROCEDIMIENTO_TITLE = marker('pii.tipo-procedimiento');
const MSG_VIA_PROTECCION_TITLE = marker('pii.via-proteccion');
const MSG_TRAMO_REPARTO_TITLE = marker('pii.tramo-reparto');

const routes: SgiRoutes = [
  {
    path: '',
    component: PiiRootComponent,
    data: {
      title: MSG_ROOT_TITLE
    },
    children: [
      {
        path: '',
        component: PiiInicioComponent,
        pathMatch: 'full',
        data: {
          title: MSG_ROOT_TITLE
        }
      },
      {
        path: PII_ROUTE_NAMES.INVENCION,
        loadChildren: () =>
          import('./invencion/invencion.module').then(
            (m) => m.InvencionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_INVENCION_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthority: ['PII-INV-V', 'PII-INV-C', 'PII-INV-E', 'PII-INV-B', 'PII-INV-R']
        }
      },
      {
        path: PII_ROUTE_NAMES.TIPO_PROTECCION,
        loadChildren: () =>
          import('./tipo-proteccion/tipo-proteccion.module').then(
            (m) => m.TipoProteccionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_TIPO_PROTECCION_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthority: ['PII-TPR-V', 'PII-TPR-C', 'PII-TPR-E', 'PII-TPR-B', 'PII-TPR-R']
        }
      },
      {
        path: PII_ROUTE_NAMES.SECTOR_APLICACION,
        loadChildren: () =>
          import('./sector-aplicacion/sector-aplicacion.module').then(
            (m) => m.SectorAplicacionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_SECTOR_APLICACION_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthority: ['PII-SEA-V', 'PII-SEA-C', 'PII-SEA-E', 'PII-SEA-B', 'PII-SEA-R']
        }
      },
      {
        path: PII_ROUTE_NAMES.RESULTADO_INFORME_PATENTABILIDAD,
        loadChildren: () =>
          import('./resultado-informe-patentabilidad/resultado-informe-patentabilidad.module').then(
            (m) => m.ResultadoInformePatentabilidadModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_RESULTADO_INFORME_PATENTABILIDAD_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthorityForAnyUO: ['PII-RIP-V', 'PII-RIP-C', 'PII-RIP-E', 'PII-RIP-B', 'PII-RIP-R']
        }
      },
      {
        path: PII_ROUTE_NAMES.TIPO_PROCEDIMIENTO,
        loadChildren: () =>
          import('./tipo-procedimiento/tipo-procedimiento.module').then(
            (m) => m.TipoProcedimientoModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_TIPO_PROCEDIMIENTO_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthorityForAnyUO: ['PII-RIP-V', 'PII-RIP-C', 'PII-RIP-E', 'PII-RIP-B', 'PII-RIP-R']
        }
      },
      {
        path: PII_ROUTE_NAMES.VIA_PROTECCION,
        loadChildren: () =>
          import('./via-proteccion/via-proteccion.module').then(
            (m) => m.ViaProteccionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_VIA_PROTECCION_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthorityForAnyUO: ['PII-VPR-V', 'PII-VPR-C', 'PII-VPR-E', 'PII-VPR-B', 'PII-VPR-R']
        }
      },
      {
        path: PII_ROUTE_NAMES.TRAMO_REPARTO,
        loadChildren: () =>
          import('./tramo-reparto/tramo-reparto.module').then(
            (m) => m.TramoRepartoModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_TRAMO_REPARTO_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthority: ['PII-TRE-V', 'PII-TRE-C', 'PII-TRE-E', 'PII-TRE-B', 'PII-TRE-R']
        }
      },
      { path: '**', component: null }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PiiRoutingModule { }
