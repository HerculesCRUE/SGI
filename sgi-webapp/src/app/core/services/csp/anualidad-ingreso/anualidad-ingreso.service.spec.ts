import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { AnualidadIngresoService } from './anualidad-ingreso.service';


describe('AnualidadIngresoService', () => {
  let service: AnualidadIngresoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(AnualidadIngresoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
