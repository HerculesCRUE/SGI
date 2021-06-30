import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { HeaderComponent } from '@block/header/header.component';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ClasificacionDataModal, ClasificacionModalComponent } from './clasificacion-modal.component';

describe('ClasificacionModalComponent', () => {
  let component: ClasificacionModalComponent;
  let fixture: ComponentFixture<ClasificacionModalComponent>;

  beforeEach(waitForAsync(() => {
    const mockDialogRef = {
      close: jasmine.createSpy('close'),
    };

    // Mock MAT_DIALOG
    const matDialogData: ClasificacionDataModal = {
      selectedClasificaciones: []
    };

    TestBed.configureTestingModule({
      imports: [
        SharedModule,
        BrowserAnimationsModule,
        RouterTestingModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        MatDialogModule,
        TestUtils.getIdiomas(),
        FormsModule,
        ReactiveFormsModule,
        SgiAuthModule
      ],
      providers: [
        {
          provide: MatDialogRef,
          useValue: mockDialogRef,
        },
        { provide: MAT_DIALOG_DATA, useValue: matDialogData },
      ],
      declarations: [ClasificacionModalComponent, HeaderComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClasificacionModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
