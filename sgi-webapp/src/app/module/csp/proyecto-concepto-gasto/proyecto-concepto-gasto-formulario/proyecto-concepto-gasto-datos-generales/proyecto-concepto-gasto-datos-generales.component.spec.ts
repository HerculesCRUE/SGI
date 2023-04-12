import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { IProyecto } from '@core/models/csp/proyecto';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CspSharedModule } from '../../../shared/csp-shared.module';
import { PROYECTO_CONCEPTO_GASTO_DATA_KEY } from '../../proyecto-concepto-gasto-data.resolver';
import { ProyectoConceptoGastoActionService, IProyectoConceptoGastoData } from '../../proyecto-concepto-gasto.action.service';
import { ProyectoConceptoGastoDatosGeneralesComponent } from './proyecto-concepto-gasto-datos-generales.component';

describe('ProyectoConceptoGastoDatosGeneralesComponent', () => {
  let component: ProyectoConceptoGastoDatosGeneralesComponent;
  let fixture: ComponentFixture<ProyectoConceptoGastoDatosGeneralesComponent>;
  const routeData: Data = {
    [PROYECTO_CONCEPTO_GASTO_DATA_KEY]: {
      proyecto: {
        id: 1
      } as IProyecto,
      selectedProyectoConceptosGasto: null,
      selectedProyectoConceptosGastoCodigosEc: [],
      convocatoriaConceptoGastoId: null,
      selectedProyectoConceptosGastoNoPermitidos: [],
      selectedProyectoConceptosGastoPermitidos: [],
      permitido: true,
      readonly: false
    } as IProyectoConceptoGastoData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  const state = {
    convocatoriaConceptoGastoId: 1
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ProyectoConceptoGastoDatosGeneralesComponent
      ],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        FormsModule,
        ReactiveFormsModule,
        SharedModule,
        CspSharedModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        ProyectoConceptoGastoActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    spyOnProperty(history, 'state', 'get').and.returnValue(state);
    fixture = TestBed.createComponent(ProyectoConceptoGastoDatosGeneralesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
