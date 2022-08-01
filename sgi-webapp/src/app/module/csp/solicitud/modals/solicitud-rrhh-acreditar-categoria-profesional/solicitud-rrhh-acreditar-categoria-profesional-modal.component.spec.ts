import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { DialogComponent } from '@block/dialog/dialog.component';
import { HeaderComponent } from '@block/header/header.component';
import { ICategoriaProfesional } from '@core/models/sgp/categoria-profesional';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CspSharedModule } from '../../../shared/csp-shared.module';
import { SolicitudRrhhAcreditarCategoriaProfesionalModalComponent, SolicitudRrhhAcreditarCategoriaProfesionalModalData } from './solicitud-rrhh-acreditar-categoria-profesional-modal.component';

describe('SolicitudRrhhAcreditarCategoriaProfesionalModalComponent', () => {
  let component: SolicitudRrhhAcreditarCategoriaProfesionalModalComponent;
  let fixture: ComponentFixture<SolicitudRrhhAcreditarCategoriaProfesionalModalComponent>;

  beforeEach(waitForAsync(() => {
    // Mock MAT_DIALOG
    const matDialogData = {
      categoriaProfesional: {} as ICategoriaProfesional
    } as SolicitudRrhhAcreditarCategoriaProfesionalModalData;

    TestBed.configureTestingModule({
      declarations: [
        SolicitudRrhhAcreditarCategoriaProfesionalModalComponent,
        DialogComponent,
        HeaderComponent],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        ReactiveFormsModule,
        SgiAuthModule,
        SharedModule,
        CspSharedModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: matDialogData },
        SgiAuthService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitudRrhhAcreditarCategoriaProfesionalModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
