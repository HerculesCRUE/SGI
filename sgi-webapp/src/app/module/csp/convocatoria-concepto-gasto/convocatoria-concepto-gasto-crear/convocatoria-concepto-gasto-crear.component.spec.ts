import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { ActionFooterComponent } from '@shared/action-footer/action-footer.component';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CONVOCATORIA_CONCEPTO_GASTO_DATA_KEY } from '../convocatoria-concepto-gasto-data.resolver';
import { ConvocatoriaConceptoGastoActionService, IConvocatoriaConceptoGastoData } from '../convocatoria-concepto-gasto.action.service';
import { ConvocatoriaConceptoGastoCrearComponent } from './convocatoria-concepto-gasto-crear.component';

describe('ConvocatoriaConceptoGastoCrearComponent', () => {
  let component: ConvocatoriaConceptoGastoCrearComponent;
  let fixture: ComponentFixture<ConvocatoriaConceptoGastoCrearComponent>;
  const routeData: Data = {
    [CONVOCATORIA_CONCEPTO_GASTO_DATA_KEY]: {
      convocatoria: {
        id: 1
      } as IConvocatoria,
      selectedConvocatoriaConceptoGastoCodigosEc: [],
      selectedConvocatoriaConceptoGastosNoPermitidos: [],
      selectedConvocatoriaConceptoGastosPermitidos: [],
      selectedConvocatoriaConceptoGastos: [],
      convocatoriaConceptoGasto: null,
      convocatoriaConceptoGastoCodigoEcsConvocatoria: [],
      permitido: true,
      canEdit: true,
      readonly: false
    } as IConvocatoriaConceptoGastoData
  };
  const routeMock = TestUtils.buildActivatedRouteMock(undefined, routeData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ConvocatoriaConceptoGastoCrearComponent,
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
        ConvocatoriaConceptoGastoActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaConceptoGastoCrearComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
