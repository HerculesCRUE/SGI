import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { ConfigSelectMultipleCspComponent } from './config-select-multiple-csp.component';

describe('ConfigSelectMultipleCspComponent', () => {
  let component: ConfigSelectMultipleCspComponent;
  let fixture: ComponentFixture<ConfigSelectMultipleCspComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        MaterialDesignModule,
        TestUtils.getIdiomas(),
        SharedModule
      ],
      declarations: [ConfigSelectMultipleCspComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigSelectMultipleCspComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
