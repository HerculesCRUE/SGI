import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { LineaInvestigacionService } from './linea-investigacion.service';

describe('LineaInvestigacionService', () => {
  let service: LineaInvestigacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(LineaInvestigacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
