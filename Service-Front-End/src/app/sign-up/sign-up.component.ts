import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import { UserService } from '../services/user.service'; // <-- Importer le service UserService
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-sign-up',
  standalone: true,
  imports: [CommonModule,ReactiveFormsModule], // Assurez-vous que ReactiveFormsModule est importé ici
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.css']
})
export class SignUpComponent {
  signupForm: FormGroup;

  constructor(private fb: FormBuilder, private userService: UserService) {
    this.signupForm = this.fb.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }
  isLoading = false;

  onSubmit() {
    if (this.signupForm.valid) {
      this.isLoading = true; // Activer le spinner
      const user = this.signupForm.value;
      this.userService.createUser(user).subscribe({
        next: (response) => {
          this.isLoading = false; // Activer le spinner
          alert('User created successfully!');
          this.signupForm.reset(); // Réinitialiser le formulaire
        },
        error: (err) => {
          this.isLoading = false; // Désactiver le spinner
          if (err.status === 400 && err.error.message === 'Email already exists') {
            alert('This email is already registered.');
          } else {
            alert('An error occurred. Please try again.');
          }
        },
      });
    } else {
      alert('Please fill out all fields correctly.');
    }
  }

}
