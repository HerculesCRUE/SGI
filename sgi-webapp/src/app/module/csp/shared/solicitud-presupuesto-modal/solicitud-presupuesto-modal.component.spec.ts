import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitiudPresupuestoModalComponent, SolicitudPresupuestoModalData } from './solicitud-presupuesto-modal.component';

describe('SolicitudPresupuestoModalComponent', () => {
  let component: SolicitiudPresupuestoModalComponent;
  let fixture: ComponentFixture<SolicitiudPresupuestoModalComponent>;

  beforeEach(waitForAsync(() => {
    const mockDialogRef = {
      close: jasmine.createSpy('close'),
    };
    // Mock MAT_DIALOG
    const matDialogData = {} as SolicitudPresupuestoModalData;

    TestBed.configureTestingModule({
      declarations: [
        SolicitiudPresupuestoModalComponent,
      ],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas()
      ],
      providers: [
        { provide: MatDialogRef, useValue: mockDialogRef },
        { provide: MAT_DIALOG_DATA, useValue: matDialogData },
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitiudPresupuestoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
