import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SelectProyectoComponent } from './select-proyecto.component';

describe('SelectProyectoComponent', () => {
  let component: SelectProyectoComponent;
  let fixture: ComponentFixture<SelectProyectoComponent>;

  beforeEach(waitForAsync(() => {
    const mockDialogRef = {
      close: jasmine.createSpy('close'),
    };

    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        RouterTestingModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        MatDialogModule,
        TestUtils.getIdiomas(),
        FormsModule,
        ReactiveFormsModule
      ],
      providers: [
        {
          provide: MatDialogRef,
          useValue: mockDialogRef,
        }
      ],
      declarations: [SelectProyectoComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectProyectoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
