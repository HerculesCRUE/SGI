import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { ActionFooterComponent } from '@shared/action-footer/action-footer.component';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CONVOCATORIA_CONCEPTO_GASTO_DATA_KEY } from '../convocatoria-concepto-gasto-public-data.resolver';
import { ConvocatoriaConceptoGastoPublicActionService, IConvocatoriaConceptoGastoPublicData } from '../convocatoria-concepto-gasto-public.action.service';
import { ConvocatoriaConceptoGastoPublicEditarComponent } from './convocatoria-concepto-gasto-public-editar.component';

describe('ConvocatoriaConceptoGastoPublicEditarComponent', () => {
  let component: ConvocatoriaConceptoGastoPublicEditarComponent;
  let fixture: ComponentFixture<ConvocatoriaConceptoGastoPublicEditarComponent>;
  const routeData: Data = {
    [CONVOCATORIA_CONCEPTO_GASTO_DATA_KEY]: {
      convocatoria: {
        id: 1
      },
      permitido: true,
    } as IConvocatoriaConceptoGastoPublicData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ConvocatoriaConceptoGastoPublicEditarComponent,
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
        ConvocatoriaConceptoGastoPublicActionService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaConceptoGastoPublicEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
