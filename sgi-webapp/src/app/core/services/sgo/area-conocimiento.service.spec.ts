import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { AreaConocimientoService } from './area-conocimiento.service';

describe('AreaConocimientoService', () => {
  let service: AreaConocimientoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule,
        SgiAuthModule
      ],
      providers: [
        SgiAuthService
      ],
    });
    service = TestBed.inject(AreaConocimientoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
