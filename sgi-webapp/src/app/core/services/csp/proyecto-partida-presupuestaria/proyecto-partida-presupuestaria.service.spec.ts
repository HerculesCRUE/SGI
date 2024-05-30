import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ProyectoPartidaPresupuestariaService } from './proyecto-partida-presupuestaria.service';

describe('ProyectoPartidaPresupuestariaService', () => {
  let service: ProyectoPartidaPresupuestariaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoPartidaPresupuestariaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
