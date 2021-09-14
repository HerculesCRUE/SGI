import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ProyectoContextoModalComponent, ProyectoContextoModalData } from './proyecto-contexto-modal.component';

describe('ProyectoContextoModalComponent', () => {
  let component: ProyectoContextoModalComponent;
  let fixture: ComponentFixture<ProyectoContextoModalComponent>;

  const newData = {} as ProyectoContextoModalData;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ProyectoContextoModalComponent],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        ReactiveFormsModule,
        SgiAuthModule
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
    fixture = TestBed.createComponent(ProyectoContextoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});