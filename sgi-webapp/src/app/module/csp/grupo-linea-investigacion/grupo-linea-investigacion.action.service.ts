import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoLineaInvestigacion } from '@core/models/csp/grupo-linea-investigacion';
import { ActionService } from '@core/services/action-service';
import { GrupoEquipoInstrumentalService } from '@core/services/csp/grupo-equipo-instrumental/grupo-equipo-instrumental.service';
import { GrupoLineaClasificacionService } from '@core/services/csp/grupo-linea-clasificacion/grupo-linea-clasificacion.service';
import { GrupoLineaEquipoInstrumentalService } from '@core/services/csp/grupo-linea-equipo-instrumental/grupo-linea-equipo-instrumental.service';
import { GrupoLineaInvestigacionService } from '@core/services/csp/grupo-linea-investigacion/grupo-linea-investigacion.service';
import { GrupoLineaInvestigadorService } from '@core/services/csp/grupo-linea-investigador/grupo-linea-investigador.service';
import { ClasificacionService } from '@core/services/sgo/clasificacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { NGXLogger } from 'ngx-logger';
import { GrupoLineaClasificacionesFragment } from './grupo-linea-investigacion-formulario/grupo-linea-clasificaciones/grupo-linea-clasificaciones.fragment';
import { GrupoLineaEquipoInstrumentalFragment } from './grupo-linea-investigacion-formulario/grupo-linea-equipo-instrumental/grupo-linea-equipo-instrumental.fragment';
import { GrupoLineaInvestigacionDatosGeneralesFragment } from './grupo-linea-investigacion-formulario/grupo-linea-investigacion-datos-generales/grupo-linea-investigacion-datos-generales.fragment';
import { GrupoLineaInvestigadorFragment } from './grupo-linea-investigacion-formulario/grupo-linea-investigacion-linea-investigador/grupo-linea-investigador.fragment';

export interface IGrupoLineaInvestigacionData {
  id: number;
  grupo: IGrupo;
  gruposLineasInvestigacion: IGrupoLineaInvestigacion[];
  isInvestigador: boolean;
  readonly: boolean;
}

@Injectable()
export class GrupoLineaInvestigacionActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    LINEA_INVESTIGADOR: 'lineaInvestigador',
    CLASIFICACIONES: 'clasificaciones',
    EQUIPO_INSTRUMENTAL: 'lineaEquipoInstrumental'
  };

  public readonly grupoLineaInvestigacion: IGrupoLineaInvestigacion;
  public readonly: boolean;

  private datosGenerales: GrupoLineaInvestigacionDatosGeneralesFragment;
  private lineasInvestigadores: GrupoLineaInvestigadorFragment;
  private clasificaciones: GrupoLineaClasificacionesFragment;
  private lineasEquiposInstrumentales: GrupoLineaEquipoInstrumentalFragment;

  get grupoListadoInvestigacion(): IGrupoLineaInvestigacion {
    return this.datosGenerales.getValue();
  }

  constructor(
    logger: NGXLogger,
    fb: FormBuilder,
    route: ActivatedRoute,
    personaService: PersonaService,
    service: GrupoLineaInvestigacionService,
    grupoLineaInvestigadorService: GrupoLineaInvestigadorService,
    grupoLineaClasificacionService: GrupoLineaClasificacionService,
    clasificacionService: ClasificacionService,
    grupoLineaEquipoInstrumentalService: GrupoLineaEquipoInstrumentalService,
    grupoEquipoInstrumentalService: GrupoEquipoInstrumentalService,
  ) {
    super();
    this.grupoLineaInvestigacion = {} as IGrupoLineaInvestigacion;
    if (route.snapshot.data.grupoLineaInvestigacionData) {
      this.grupoLineaInvestigacion = route.snapshot.data.grupoLineaInvestigacionData;
      this.enableEdit();
      this.readonly = route.snapshot.data.grupoLineaInvestigacionData.readonly;
    }

    this.datosGenerales = new GrupoLineaInvestigacionDatosGeneralesFragment(
      fb,
      this.readonly,
      this.grupoLineaInvestigacion?.id,
      this.grupoLineaInvestigacion.grupo,
      service
    );

    this.lineasInvestigadores = new GrupoLineaInvestigadorFragment(
      logger,
      this.grupoLineaInvestigacion?.id,
      this.grupoLineaInvestigacion?.grupo?.id,
      service,
      grupoLineaInvestigadorService,
      personaService,
      this.readonly
    );

    this.clasificaciones = new GrupoLineaClasificacionesFragment(
      this.grupoLineaInvestigacion?.id,
      grupoLineaClasificacionService,
      service,
      clasificacionService,
      this.readonly
    );

    this.lineasEquiposInstrumentales = new GrupoLineaEquipoInstrumentalFragment(
      logger,
      this.grupoLineaInvestigacion?.id,
      this.grupoLineaInvestigacion?.grupo?.id,
      service, grupoLineaEquipoInstrumentalService,
      grupoEquipoInstrumentalService,
      this.readonly
    );

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.LINEA_INVESTIGADOR, this.lineasInvestigadores);
    this.addFragment(this.FRAGMENT.CLASIFICACIONES, this.clasificaciones);
    this.addFragment(this.FRAGMENT.EQUIPO_INSTRUMENTAL, this.lineasEquiposInstrumentales);

    this.datosGenerales.initialize();
  }

}
