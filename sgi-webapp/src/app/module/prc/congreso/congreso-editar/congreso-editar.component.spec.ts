import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { ActionEmptyFooterComponent } from '@shared/action-empty-footer/action-empty-footer.component';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ProduccionCientificaInitializerService } from '../../shared/produccion-cientifica-initializer.service';
import { PRODUCCION_CIENTIFICA_DATA_KEY } from '../../shared/produccion-cientifica-route-params';
import { IProduccionCientificaData } from '../../shared/produccion-cientifica.resolver';
import { CongresoActionService } from '../congreso.action.service';

import { CongresoEditarComponent } from './congreso-editar.component';

describe('CongresoEditarComponent', () => {
  let component: CongresoEditarComponent;
  let fixture: ComponentFixture<CongresoEditarComponent>;
  const routeData: Data = {
    [PRODUCCION_CIENTIFICA_DATA_KEY]: {
      produccionCientifica: {
        id: 1
      }
    } as IProduccionCientificaData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        CongresoEditarComponent,
        ActionEmptyFooterComponent
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
        CongresoActionService,
        ProduccionCientificaInitializerService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CongresoEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
