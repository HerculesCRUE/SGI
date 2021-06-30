import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IConvocatoriaAreaTematica } from '@core/models/csp/convocatoria-area-tematica';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CspSharedModule } from '../../../shared/csp-shared.module';
import { AreaTematicaData } from '../../convocatoria-formulario/convocatoria-datos-generales/convocatoria-datos-generales.fragment';
import { ConvocatoriaAreaTematicaModalComponent } from './convocatoria-area-tematica-modal.component';

describe('ConvocatoriaAreaTematicaModalComponent', () => {
  let component: ConvocatoriaAreaTematicaModalComponent;
  let fixture: ComponentFixture<ConvocatoriaAreaTematicaModalComponent>;

  const convocatoriaAreaTematica = {} as IConvocatoriaAreaTematica;
  const newData: AreaTematicaData = {
    padre: undefined,
    observaciones: '',
    convocatoriaAreaTematica: new StatusWrapper<IConvocatoriaAreaTematica>(convocatoriaAreaTematica)
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ConvocatoriaAreaTematicaModalComponent
      ],
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
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: newData },
        { provide: MAT_DIALOG_DATA, useValue: newData },
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaAreaTematicaModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    const formGroup = component.formGroup;
    expect(formGroup).toBeTruthy();
    expect(Object.keys(formGroup.controls).length).toBe(2);
    expect(formGroup.controls.padre).toBeTruthy();
    expect(formGroup.controls.observaciones).toBeTruthy();
  });
});
