import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';

import { BaremoPesoCuantiaComponent } from './baremo-peso-cuantia.component';

describe('BaremoPesoCuantiaComponent', () => {
  let component: BaremoPesoCuantiaComponent;
  let fixture: ComponentFixture<BaremoPesoCuantiaComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        MaterialDesignModule,
        SharedModule,
        TestUtils.getIdiomas(),
      ],
      providers: [
      ],
      declarations: [BaremoPesoCuantiaComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BaremoPesoCuantiaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
