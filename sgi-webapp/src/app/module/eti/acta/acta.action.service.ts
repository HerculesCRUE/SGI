import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { IActa } from '@core/models/eti/acta';
import { IConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { TIPO_COMENTARIO } from '@core/models/eti/tipo-comentario';
import { ActionService } from '@core/services/action-service';
import { ActaService } from '@core/services/eti/acta.service';
import { AsistenteService } from '@core/services/eti/asistente.service';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SgiAuthService } from '@sgi/framework/auth';
import {
  ActaAsistentesFragment
} from './acta-formulario/acta-asistentes/acta-asistentes-listado/acta-asistentes-listado.fragment';
import { ActaComentariosFragment } from './acta-formulario/acta-comentarios/acta-comentarios.fragment';
import { ActaDatosGeneralesFragment } from './acta-formulario/acta-datos-generales/acta-datos-generales.fragment';
import { ActaMemoriasFragment } from './acta-formulario/acta-memorias/acta-memorias.fragment';

export enum Rol { EVALUADOR = TIPO_COMENTARIO.EVALUADOR, GESTOR = TIPO_COMENTARIO.GESTOR }

@Injectable()
export class ActaActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    ASISTENTES: 'asistentes',
    MEMORIAS: 'memorias',
    COMENTARIOS: 'comentarios'
  };

  public readonly = false;

  private acta: IActa;
  private datosGenerales: ActaDatosGeneralesFragment;
  private asistentes: ActaAsistentesFragment;
  private memorias: ActaMemoriasFragment;
  private comentarios: ActaComentariosFragment;
  private rol: Rol;

  constructor(
    fb: FormBuilder,
    route: ActivatedRoute,
    service: ActaService,
    evaluacionService: EvaluacionService,
    convocatoriaReunionService: ConvocatoriaReunionService,
    personaService: PersonaService,
    asistenteService: AsistenteService,
    authService: SgiAuthService,
  ) {
    super();
    this.acta = {} as IActa;
    if (route.snapshot.data.acta) {
      this.acta = route.snapshot.data.acta;
      this.enableEdit();
      this.readonly = this.acta.estadoActual.id === 2 ? true : false;
    }

    this.rol = authService.hasAuthority('ETI-ACT-V') ? Rol.GESTOR : Rol.EVALUADOR;

    this.datosGenerales = new ActaDatosGeneralesFragment(fb, this.acta?.id, this.rol, service);
    this.memorias = new ActaMemoriasFragment(this.acta?.convocatoriaReunion?.id, convocatoriaReunionService);
    this.asistentes = new ActaAsistentesFragment(
      this.acta?.convocatoriaReunion?.id, convocatoriaReunionService, personaService, asistenteService);
    this.comentarios = new ActaComentariosFragment(this.acta?.id, evaluacionService, convocatoriaReunionService, personaService, authService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.MEMORIAS, this.memorias);
    this.addFragment(this.FRAGMENT.ASISTENTES, this.asistentes);
    this.addFragment(this.FRAGMENT.COMENTARIOS, this.comentarios);
  }

  setIdConvocatoria(id: number) {
    this.asistentes.loadAsistentes(id);
    this.memorias.loadMemorias(id);
    this.comentarios.loadEvaluaciones(id);
  }

  protected onKeyChange(key: number): void {
    this.datosGenerales.setKey(key);
  }

  getRol(): Rol {
    return this.rol;
  }

  getConvocatoriaReunion(): IConvocatoriaReunion {
    return this.acta?.convocatoriaReunion;
  }
}
