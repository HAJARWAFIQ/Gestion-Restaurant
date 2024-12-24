import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AcceuilAdminDashboardComponent } from './acceuil-admin-dashboard.component';

describe('AcceuilAdminDashboardComponent', () => {
  let component: AcceuilAdminDashboardComponent;
  let fixture: ComponentFixture<AcceuilAdminDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AcceuilAdminDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AcceuilAdminDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
