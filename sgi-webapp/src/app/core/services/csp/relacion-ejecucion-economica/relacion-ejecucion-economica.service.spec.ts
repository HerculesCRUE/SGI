import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { RelacionEjecucionEconomicaService } from './relacion-ejecucion-economica.service';

describe('RelacionEjecucionEconomicaService', () => {
  let service: RelacionEjecucionEconomicaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(RelacionEjecucionEconomicaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
