import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { IProyecto } from '@core/models/csp/proyecto';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { ActionFooterComponent } from '@shared/action-footer/action-footer.component';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { PROYECTO_CONCEPTO_GASTO_DATA_KEY } from '../proyecto-concepto-gasto-data.resolver';
import { ProyectoConceptoGastoActionService, IProyectoConceptoGastoData } from '../proyecto-concepto-gasto.action.service';
import { ProyectoConceptoGastoCrearComponent } from './proyecto-concepto-gasto-crear.component';

describe('ProyectoConceptoGastoCrearComponent', () => {
  let component: ProyectoConceptoGastoCrearComponent;
  let fixture: ComponentFixture<ProyectoConceptoGastoCrearComponent>;
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
  const routeMock = TestUtils.buildActivatedRouteMock(undefined, routeData);

  const state = {
    convocatoriaConceptoGastoId: 1
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ProyectoConceptoGastoCrearComponent,
        ActionFooterComponent
      ],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        FlexModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        FlexLayoutModule,
        SharedModule
      ],
      providers: [
        ProyectoConceptoGastoActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    spyOnProperty(history, 'state', 'get').and.returnValue(state);
    fixture = TestBed.createComponent(ProyectoConceptoGastoCrearComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
