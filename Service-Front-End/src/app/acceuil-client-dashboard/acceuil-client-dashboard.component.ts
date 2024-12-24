import { Component } from '@angular/core';
import { Chart, registerables } from 'chart.js';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-acceuil-client-dashboard',
  standalone: true,
  imports: [],
  templateUrl: './acceuil-client-dashboard.component.html',
  styleUrl: './acceuil-client-dashboard.component.css'
})
export class AcceuilClientDashboardComponent {
  constructor(private authService: AuthService, private router: Router) {
    Chart.register(...registerables);
  }

  ngOnInit() {
    setTimeout(() => {
      this.createMonthlyExpenditureChart();
      this.createOrderCategoriesChart();
      this.createOrderStatusChart();
    }, 0);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/home']);
  }

  createMonthlyExpenditureChart() {
    const ctx = document.getElementById('monthlyExpenditureChart') as HTMLCanvasElement | null;

    if (!ctx) {
      console.error('Monthly Expenditure chart canvas element not found!');
      return;
    }

    new Chart(ctx, {
      type: 'line',
      data: {
        labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
        datasets: [
          {
            label: 'Expenditure',
            data: [200, 450, 300, 500, 400, 600],
            borderColor: 'orange',
            borderWidth: 2,
            fill: false,
          },
        ],
      },
    });
  }

  createOrderCategoriesChart() {
    const ctx = document.getElementById('orderCategoriesChart') as HTMLCanvasElement | null;

    if (!ctx) {
      console.error('Order Categories chart canvas element not found!');
      return;
    }

    new Chart(ctx, {
      type: 'pie',
      data: {
        labels: ['Burger', 'Pizza', 'Salade', 'Drink'],
        datasets: [
          {
            data: [40, 25, 20, 15],
            backgroundColor: ['#D6E7BB', '#E2BC35', '#58d68d', '#8e44ad'],
                    },
        ],
      },
    });
  }

  createOrderStatusChart() {
    const ctx = document.getElementById('orderStatusChart') as HTMLCanvasElement | null;

    if (!ctx) {
      console.error('Order Status chart canvas element not found!');
      return;
    }

    new Chart(ctx, {
      type: 'bar',
      data: {
        labels: ['Delivered', 'Pending', 'Cancelled'],
        datasets: [
          {
            label: 'Orders',
            data: [10, 5, 2],
            backgroundColor: ['green', 'yellow', 'red'],
          },
        ],
      },
    });
  }
}
