import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { PeticionEvaluacionCrearComponent } from './peticion-evaluacion-crear/peticion-evaluacion-crear.component';
import { PeticionEvaluacionEditarComponent } from './peticion-evaluacion-editar/peticion-evaluacion-editar.component';
import { EquipoInvestigadorListadoComponent } from './peticion-evaluacion-formulario/equipo-investigador/equipo-investigador-listado/equipo-investigador-listado.component';
import { MemoriasListadoComponent } from './peticion-evaluacion-formulario/memorias-listado/memorias-listado.component';
import { PeticionEvaluacionDatosGeneralesComponent } from './peticion-evaluacion-formulario/peticion-evaluacion-datos-generales/peticion-evaluacion-datos-generales.component';
import { PeticionEvaluacionTareasListadoComponent } from './peticion-evaluacion-formulario/peticion-evaluacion-tareas/peticion-evaluacion-tareas-listado/peticion-evaluacion-tareas-listado.component';
import { PeticionEvaluacionListadoInvComponent } from './peticion-evaluacion-listado-inv/peticion-evaluacion-listado-inv.component';
import { PETICION_EVALUACION_ROUTE_NAMES } from './peticion-evaluacion-route-names';
import { PeticionEvaluacionResolver } from './peticion-evaluacion.resolver';

const PETICION_EVALUACION_KEY = marker('eti.peticion-evaluacion');
const MSG_NEW_TITLE = marker('title.new.entity');

const routes: SgiRoutes = [
  {
    path: '',
    component: PeticionEvaluacionListadoInvComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: PETICION_EVALUACION_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAuthorityForAnyUO: 'ETI-PEV-INV-VR'
    },

  },
  {
    path: ROUTE_NAMES.NEW,
    component: PeticionEvaluacionCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: PETICION_EVALUACION_KEY, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAnyAuthorityForAnyUO: ['ETI-PEV-INV-C']
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PETICION_EVALUACION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: PETICION_EVALUACION_ROUTE_NAMES.DATOS_GENERALES,
        component: PeticionEvaluacionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PETICION_EVALUACION_ROUTE_NAMES.EQUIPO_INVESTIGADOR,
        component: EquipoInvestigadorListadoComponent
      }
    ]
  },
  {
    path: `:id`,
    component: PeticionEvaluacionEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      peticionEvaluacion: PeticionEvaluacionResolver
    },
    data: {
      title: PETICION_EVALUACION_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAuthorityForAnyUO: 'ETI-PEV-INV-ER'
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PETICION_EVALUACION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: PETICION_EVALUACION_ROUTE_NAMES.DATOS_GENERALES,
        component: PeticionEvaluacionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PETICION_EVALUACION_ROUTE_NAMES.EQUIPO_INVESTIGADOR,
        component: EquipoInvestigadorListadoComponent
      },
      {
        path: PETICION_EVALUACION_ROUTE_NAMES.TAREAS,
        component: PeticionEvaluacionTareasListadoComponent
      },
      {
        path: PETICION_EVALUACION_ROUTE_NAMES.MEMORIAS,
        component: MemoriasListadoComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PeticionEvaluacionInvRoutingModule {
}
