import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CspSharedModule } from '../../../shared/csp-shared.module';
import { AreaTematicaModalData, SolicitudAreaTematicaModalComponent } from './solicitud-area-tematica-modal.component';

describe('SolcitudAreaTematicaComponent', () => {
  let component: SolicitudAreaTematicaModalComponent;
  let fixture: ComponentFixture<SolicitudAreaTematicaModalComponent>;

  const areaTematicaConvocatoria: IAreaTematica = {
  } as IAreaTematica;

  const newData: AreaTematicaModalData = {
    padre: areaTematicaConvocatoria,
    areasTematicasConvocatoria: [areaTematicaConvocatoria],
    areaTematicaSolicitud: areaTematicaConvocatoria,
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [SolicitudAreaTematicaModalComponent],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        FormsModule,
        ReactiveFormsModule,
        CspSharedModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: newData },
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitudAreaTematicaModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
