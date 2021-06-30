import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IPrograma } from '@core/models/csp/programa';
import { ActionService } from '@core/services/action-service';
import { ProgramaService } from '@core/services/csp/programa.service';
import { NGXLogger } from 'ngx-logger';
import { PlanInvestigacionDatosGeneralesFragment } from './plan-investigacion-formulario/plan-investigacion-datos-generales/plan-investigacion-datos-generales.fragment';
import { PlanInvestigacionProgramaFragment } from './plan-investigacion-formulario/plan-investigacion-programas/plan-investigacion-programas.fragment';




@Injectable()
export class PlanInvestigacionActionService extends ActionService {
  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    PROGRAMAS: 'programas'
  };

  private programa: IPrograma;
  private planInvestigacion: PlanInvestigacionDatosGeneralesFragment;
  private programas: PlanInvestigacionProgramaFragment;

  constructor(
    private readonly logger: NGXLogger,
    route: ActivatedRoute,
    programaService: ProgramaService
  ) {
    super();
    this.programa = {} as IPrograma;
    if (route.snapshot.data.plan) {
      this.programa = route.snapshot.data.plan;
      this.enableEdit();
    }

    this.planInvestigacion = new PlanInvestigacionDatosGeneralesFragment(logger, this.programa?.id,
      programaService);
    this.programas = new PlanInvestigacionProgramaFragment(logger, this.programa?.id,
      programaService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.planInvestigacion);
    this.addFragment(this.FRAGMENT.PROGRAMAS, this.programas);
  }

  get getPrograma() {
    return this.programa;
  }
}
