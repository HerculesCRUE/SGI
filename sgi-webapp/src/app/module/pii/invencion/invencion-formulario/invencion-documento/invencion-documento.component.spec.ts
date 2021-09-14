import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { InvencionDocumentoService } from '@core/services/pii/invencion/invencion-documento/invencion-documento.service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { IInvencionData, InvencionActionService } from '../../invencion.action.service';
import { INVENCION_DATA_KEY } from '../../invencion.resolver';

import { InvencionDocumentoComponent } from './invencion-documento.component';

describe('InvencionDocumentoComponent', () => {
  let component: InvencionDocumentoComponent;
  let fixture: ComponentFixture<InvencionDocumentoComponent>;

  const routeData: Data = {
    [INVENCION_DATA_KEY]: {} as IInvencionData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        InvencionDocumentoComponent
      ],
      imports: [
        RouterTestingModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        BrowserAnimationsModule,
        TestUtils.getIdiomas(),
        FlexModule,
        FormsModule,
        ReactiveFormsModule,
        SharedModule,
        SgiAuthModule,
        RouterTestingModule
      ],
      providers: [
        SgiAuthService,
        InvencionActionService,
        InvencionService,
        InvencionDocumentoService,
        DocumentoService,
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: ActivatedRoute, useValue: routeMock }
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InvencionDocumentoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
