import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { PROYECTO_CONCEPTO_GASTO_DATA_KEY } from '../../proyecto-concepto-gasto-data.resolver';
import { ProyectoConceptoGastoActionService, IProyectoConceptoGastoData } from '../../proyecto-concepto-gasto.action.service';
import { ProyectoConceptoGastoCodigoEcComponent } from './proyecto-concepto-gasto-codigo-ec.component';

describe('ProyectoConceptoGastoCodigoEcComponent', () => {
  let component: ProyectoConceptoGastoCodigoEcComponent;
  let fixture: ComponentFixture<ProyectoConceptoGastoCodigoEcComponent>;
  const routeData: Data = {
    [PROYECTO_CONCEPTO_GASTO_DATA_KEY]: {
      proyecto: {
        id: 1
      },
      selectedProyectoConceptosGasto: [],
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
      declarations: [ProyectoConceptoGastoCodigoEcComponent],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        FlexModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        LoggerTestingModule,
        SharedModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        ProyectoConceptoGastoActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    spyOnProperty(history, 'state', 'get').and.returnValue(state);
    fixture = TestBed.createComponent(ProyectoConceptoGastoCodigoEcComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
