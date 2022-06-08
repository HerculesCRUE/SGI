import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ReportPrcService } from './report-prc.service';

describe('ReportPrcService', () => {
  let service: ReportPrcService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ReportPrcService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
