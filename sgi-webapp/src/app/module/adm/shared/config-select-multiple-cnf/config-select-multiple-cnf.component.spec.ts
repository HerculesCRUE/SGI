import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { ConfigSelectMultipleCnfComponent } from './config-select-multiple-cnf.component';

describe('ConfigSelectMultipleCnfComponent', () => {
  let component: ConfigSelectMultipleCnfComponent;
  let fixture: ComponentFixture<ConfigSelectMultipleCnfComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        MaterialDesignModule,
        TestUtils.getIdiomas(),
        SharedModule
      ],
      declarations: [ConfigSelectMultipleCnfComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigSelectMultipleCnfComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
