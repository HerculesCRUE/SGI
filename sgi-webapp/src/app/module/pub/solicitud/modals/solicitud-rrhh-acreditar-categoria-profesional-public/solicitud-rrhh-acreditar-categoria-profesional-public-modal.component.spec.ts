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
import { SharedPublicModule } from '@shared/shared-public.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudRrhhAcreditarCategoriaProfesionalPublicModalComponent, SolicitudRrhhAcreditarCategoriaProfesionalPublicModalData } from './solicitud-rrhh-acreditar-categoria-profesional-public-modal.component';

describe('SolicitudRrhhAcreditarCategoriaProfesionalPublicModalComponent', () => {
  let component: SolicitudRrhhAcreditarCategoriaProfesionalPublicModalComponent;
  let fixture: ComponentFixture<SolicitudRrhhAcreditarCategoriaProfesionalPublicModalComponent>;

  beforeEach(waitForAsync(() => {
    // Mock MAT_DIALOG
    const matDialogData = {
      categoriaProfesional: {} as ICategoriaProfesional
    } as SolicitudRrhhAcreditarCategoriaProfesionalPublicModalData;

    TestBed.configureTestingModule({
      declarations: [
        SolicitudRrhhAcreditarCategoriaProfesionalPublicModalComponent,
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
        SharedModule,
        SharedPublicModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: matDialogData }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitudRrhhAcreditarCategoriaProfesionalPublicModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
