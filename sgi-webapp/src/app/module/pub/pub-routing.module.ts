import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRoutes } from '@core/route';
import { PubInicioComponent } from './pub-inicio/pub-inicio.component';
import { PubRootComponent } from './pub-root/pub-root.component';
import { PUB_ROUTE_NAMES } from './pub-route-names';

const MSG_ROOT_TITLE = marker('inv.root.title');
const MSG_CONVOCATORIAS_TITLE = marker('menu.principal.inv.convocatorias');
const MSG_SOLICITUDES_TITLE = marker('menu.principal.inv.solicitudes');

const routes: SgiRoutes = [
  {
    path: '',
    component: PubRootComponent,
    data: {
      title: MSG_ROOT_TITLE
    },
    children: [
      {
        path: '',
        component: PubInicioComponent,
        pathMatch: 'full',
        data: {
          title: MSG_ROOT_TITLE
        }
      },
      {
        path: PUB_ROUTE_NAMES.CONVOCATORIAS,
        loadChildren: () =>
          import('../pub/convocatoria/convocatoria-public.module').then(
            (m) => m.ConvocatoriaPublicModule
          ),
        data: {
          title: MSG_CONVOCATORIAS_TITLE
        }
      },
      {
        path: PUB_ROUTE_NAMES.SOLICITUDES,
        loadChildren: () =>
          import('../pub/solicitud/solicitud-public.module').then(
            (m) => m.SolicitudPublicModule
          ),
        data: {
          title: MSG_SOLICITUDES_TITLE
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
export class PubRoutingModule {
}
