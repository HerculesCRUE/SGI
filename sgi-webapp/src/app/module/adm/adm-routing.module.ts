import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRoutes } from '@core/route';
import { AdmInicioComponent } from './adm-inicio/adm-inicio.component';
import { AdmRootComponent } from './adm-root/adm-root.component';
import { ADM_ROUTE_NAMES } from './adm-route-names';
import { ConfigCspComponent } from './config-csp/config-csp.component';
import { ConfigEtiComponent } from './config-eti/config-eti.component';
import { ConfigGlobalComponent } from './config-global/config-global.component';
import { ConfigPiiComponent } from './config-pii/config-pii.component';
import { ConfigPrcComponent } from './config-prc/config-prc.component';

const MSG_ROOT_TITLE = marker('adm.root.title');
const MSG_CONFIG_GLOBAL_TITLE = marker('adm.config.global');
const MSG_CONFIG_CSP_TITLE = marker('adm.config.csp');
const MSG_CONFIG_ETI_TITLE = marker('adm.config.eti');
const MSG_CONFIG_PII_TITLE = marker('adm.config.pii');
const MSG_CONFIG_PRC_TITLE = marker('adm.config.prc');

const routes: SgiRoutes = [
  {
    path: '',
    component: AdmRootComponent,
    data: {
      title: MSG_ROOT_TITLE
    },
    children: [
      {
        path: '',
        component: AdmInicioComponent,
        pathMatch: 'full',
        data: {
          title: MSG_ROOT_TITLE,
          hasAuthority: 'ADM-CNF-E'
        }
      },
      {
        path: ADM_ROUTE_NAMES.CONFIG_GLOBAL,
        component: ConfigGlobalComponent,
        data: {
          title: MSG_CONFIG_GLOBAL_TITLE,
          hasAuthority: 'ADM-CNF-E'
        }
      },
      {
        path: ADM_ROUTE_NAMES.CONFIG_CSP,
        component: ConfigCspComponent,
        data: {
          title: MSG_CONFIG_CSP_TITLE,
          hasAuthority: 'ADM-CNF-E'
        }
      },
      {
        path: ADM_ROUTE_NAMES.CONFIG_ETI,
        component: ConfigEtiComponent,
        data: {
          title: MSG_CONFIG_ETI_TITLE,
          hasAuthority: 'ADM-CNF-E'
        }
      },
      {
        path: ADM_ROUTE_NAMES.CONFIG_PII,
        component: ConfigPiiComponent,
        data: {
          title: MSG_CONFIG_PII_TITLE,
          hasAuthority: 'ADM-CNF-E'
        }
      },
      {
        path: ADM_ROUTE_NAMES.CONFIG_PRC,
        component: ConfigPrcComponent,
        data: {
          title: MSG_CONFIG_PRC_TITLE,
          hasAuthority: 'ADM-CNF-E'
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
export class AdmRoutingModule { }
