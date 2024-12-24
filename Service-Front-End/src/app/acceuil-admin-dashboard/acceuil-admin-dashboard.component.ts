import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Chart, registerables } from 'chart.js';
import { Router } from '@angular/router';

@Component({
  selector: 'app-acceuil-admin-dashboard',
  standalone: true,
  imports: [],
  templateUrl: './acceuil-admin-dashboard.component.html',
  styleUrl: './acceuil-admin-dashboard.component.css'
})
export class AcceuilAdminDashboardComponent {
  isUserLoggedIn: boolean = false;
  username: string | null = null;
  constructor(private authService: AuthService, private router: Router) {
    Chart.register(...registerables);
  }

    ngOnInit() {
      setTimeout(() => {
        this.createRevenueChart();
        this.createPerformanceChart();
      }, 0);
    }

  logout() {
    this.authService.logout();
    this.isUserLoggedIn = false;
    this.username = null;
    this.router.navigate(['/home']);
  }

  createRevenueChart() {
    const ctx = document.getElementById('revenueChart') as HTMLCanvasElement | null;

    if (!ctx) {
      console.error('Revenue chart canvas element not found!');
      return;
    }

    new Chart(ctx, {
      type: 'line',
      data: {
        labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
        datasets: [
          {
            label: 'Revenue',
            data: [10, 20, 30, 40, 50, 60],
            borderColor: 'orange',
            borderWidth: 2,
            fill: false,
          },
        ],
      },
    });
  }

  createPerformanceChart() {
    const ctx = document.getElementById('performanceChart') as HTMLCanvasElement | null;

    if (!ctx) {
      console.error('Performance chart canvas element not found!');
      return;
    }

    new Chart(ctx, {
      type: 'bar',
      data: {
        labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
        datasets: [
          {
            label: 'Performance',
            data: [15, 25, 35, 45, 55, 65],
            backgroundColor: 'orange',
          },
        ],
      },
    });
  }

}
