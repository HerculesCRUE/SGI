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
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CONVOCATORIA_CONCEPTO_GASTO_DATA_KEY } from '../../convocatoria-concepto-gasto-public-data.resolver';
import { ConvocatoriaConceptoGastoPublicActionService, IConvocatoriaConceptoGastoPublicData } from '../../convocatoria-concepto-gasto-public.action.service';
import { ConvocatoriaConceptoGastoCodigoEcPublicComponent } from './convocatoria-concepto-gasto-codigo-ec-public.component';

describe('ConvocatoriaConceptoGastoCodigoEcPublicComponent', () => {
  let component: ConvocatoriaConceptoGastoCodigoEcPublicComponent;
  let fixture: ComponentFixture<ConvocatoriaConceptoGastoCodigoEcPublicComponent>;
  const routeData: Data = {
    [CONVOCATORIA_CONCEPTO_GASTO_DATA_KEY]: {
      convocatoria: {
        id: 1
      },
      permitido: true
    } as IConvocatoriaConceptoGastoPublicData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ConvocatoriaConceptoGastoCodigoEcPublicComponent],
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
        ConvocatoriaConceptoGastoPublicActionService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaConceptoGastoCodigoEcPublicComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
