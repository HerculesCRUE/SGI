import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { SectorLicenciadoService } from './sector-licenciado.service';

describe('SectorLicenciadoService', () => {
  let service: SectorLicenciadoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SectorLicenciadoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
