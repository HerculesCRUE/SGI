import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { INotificacionProyectoExternoCVN } from '@core/models/csp/notificacion-proyecto-externo-cvn';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { ActionFooterComponent } from '@shared/action-footer/action-footer.component';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { NOTIFICACION_DATA_KEY } from '../notificacion-cvn-data.resolver';
import { NotificacionCvnActionService } from '../notificacion-cvn.action.service';

import { NotificacionCvnEditarComponent } from './notificacion-cvn-editar.component';

describe('NotificacionCvnEditarComponent', () => {
  let component: NotificacionCvnEditarComponent;
  let fixture: ComponentFixture<NotificacionCvnEditarComponent>;

  const routeData: Data = {
    [NOTIFICACION_DATA_KEY]: {
    } as INotificacionProyectoExternoCVN
  }

  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);
  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        NotificacionCvnEditarComponent,
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
        NotificacionCvnActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificacionCvnEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
