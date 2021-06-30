import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { IActa } from '@core/models/eti/acta';
import { ActionService } from '@core/services/action-service';
import { ActaService } from '@core/services/eti/acta.service';
import { AsistenteService } from '@core/services/eti/asistente.service';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { PersonaService } from '@core/services/sgp/persona.service';

import {
  ActaAsistentesFragment,
} from './acta-formulario/acta-asistentes/acta-asistentes-listado/acta-asistentes-listado.fragment';
import { ActaDatosGeneralesFragment } from './acta-formulario/acta-datos-generales/acta-datos-generales.fragment';
import { ActaMemoriasFragment } from './acta-formulario/acta-memorias/acta-memorias.fragment';

@Injectable()
export class ActaActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    ASISTENTES: 'asistentes',
    MEMORIAS: 'memorias'
  };

  public readonly = false;

  private acta: IActa;
  private datosGenerales: ActaDatosGeneralesFragment;
  private asistentes: ActaAsistentesFragment;
  private memorias: ActaMemoriasFragment;

  constructor(
    fb: FormBuilder,
    route: ActivatedRoute,
    service: ActaService,
    convocatoriaReunionService: ConvocatoriaReunionService,
    personaService: PersonaService,
    asistenteService: AsistenteService
  ) {
    super();
    this.acta = {} as IActa;
    if (route.snapshot.data.acta) {
      this.acta = route.snapshot.data.acta;
      this.enableEdit();
      this.readonly = this.acta.estadoActual.id === 2 ? true : false;
    }
    this.datosGenerales = new ActaDatosGeneralesFragment(fb, this.acta?.id, service);
    this.memorias = new ActaMemoriasFragment(this.acta?.convocatoriaReunion?.id, convocatoriaReunionService);
    this.asistentes = new ActaAsistentesFragment(
      this.acta?.convocatoriaReunion?.id, convocatoriaReunionService, personaService, asistenteService);



    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.MEMORIAS, this.memorias);
    this.addFragment(this.FRAGMENT.ASISTENTES, this.asistentes);
  }

  setIdConvocatoria(id: number) {
    this.asistentes.loadAsistentes(id);
    this.memorias.loadMemorias(id);
  }

  protected onKeyChange(key: number): void {
    this.datosGenerales.setKey(key);
  }
}
