import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DATA_KEY } from '../../proyecto-socio-periodo-justificacion-data.resolver';
import { IProyectoSocioPeriodoJustificacionData, ProyectoSocioPeriodoJustificacionActionService } from '../../proyecto-socio-periodo-justificacion.action.service';
import { ProyectoSocioPeriodoJustificacionDocumentosComponent } from './proyecto-socio-periodo-justificacion-documentos.component';

describe('ProyectoSocioPeriodoJustificacionDocumentosComponent', () => {
  let component: ProyectoSocioPeriodoJustificacionDocumentosComponent;
  let fixture: ComponentFixture<ProyectoSocioPeriodoJustificacionDocumentosComponent>;
  const routeData: Data = {
    [PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DATA_KEY]: {
      proyectoSocio: {
        id: 1
      },
      proyecto: {
        estado: {}
      },
      proyectoSocioPeriodosJustificacion: []
    } as IProyectoSocioPeriodoJustificacionData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ProyectoSocioPeriodoJustificacionDocumentosComponent],
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
        SharedModule
      ],
      providers: [
        ProyectoSocioPeriodoJustificacionActionService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProyectoSocioPeriodoJustificacionDocumentosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
