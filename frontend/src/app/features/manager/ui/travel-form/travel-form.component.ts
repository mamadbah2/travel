import { Component, input, output, signal, effect } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  ActivityRequest,
  CreateTravelRequest,
  DestinationRequest,
  TravelResponse,
  AccommodationType,
  TransportationType,
} from '../../../../shared/models/api.models';

@Component({
  selector: 'app-travel-form',
  imports: [FormsModule],
  templateUrl: './travel-form.component.html',
  styleUrl: './travel-form.component.css',
})
export class TravelFormComponent {
  readonly loading = input(false);
  readonly submitLabel = input('Create travel');
  readonly initialData = input<TravelResponse | null>(null);
  readonly submitTravel = output<CreateTravelRequest>();

  title = '';
  description = '';
  startDate = '';
  endDate = '';
  price = 0;
  maxCapacity = 1;
  accommodationType: AccommodationType | null = null;
  accommodationName = '';
  transportationType: TransportationType | null = null;
  transportationDetails = '';
  destinations: DestinationRequest[] = [];
  activities: ActivityRequest[] = [];

  readonly accommodationTypes: AccommodationType[] = [
    'HOTEL', 'HOSTEL', 'RESORT', 'APARTMENT', 'CAMPING', 'GUESTHOUSE', 'OTHER',
  ];
  readonly transportationTypes: TransportationType[] = [
    'FLIGHT', 'BUS', 'TRAIN', 'BOAT', 'CAR', 'MINIBUS', 'OTHER',
  ];

  constructor() {
    effect(() => {
      const data = this.initialData();
      if (data) {
        this.title = data.title;
        this.description = data.description ?? '';
        this.startDate = data.startDate;
        this.endDate = data.endDate;
        this.price = data.price;
        this.maxCapacity = data.maxCapacity;
        this.accommodationType = data.accommodationType;
        this.accommodationName = data.accommodationName ?? '';
        this.transportationType = data.transportationType;
        this.transportationDetails = data.transportationDetails ?? '';
        this.destinations = data.destinations.map((d) => ({
          name: d.name,
          country: d.country,
          city: d.city,
          description: d.description,
          displayOrder: d.displayOrder,
        }));
        this.activities = data.activities.map((a) => ({
          name: a.name,
          description: a.description,
          location: a.location,
          displayOrder: a.displayOrder,
        }));
      }
    });
  }

  addDestination(): void {
    this.destinations = [
      ...this.destinations,
      { name: '', country: '', city: null, description: null, displayOrder: this.destinations.length },
    ];
  }

  removeDestination(index: number): void {
    this.destinations = this.destinations.filter((_, i) => i !== index);
  }

  addActivity(): void {
    this.activities = [
      ...this.activities,
      { name: '', description: null, location: null, displayOrder: this.activities.length },
    ];
  }

  removeActivity(index: number): void {
    this.activities = this.activities.filter((_, i) => i !== index);
  }

  onSubmit(): void {
    if (!this.title || !this.startDate || !this.endDate || this.price <= 0) return;

    this.submitTravel.emit({
      title: this.title,
      description: this.description || null,
      startDate: this.startDate,
      endDate: this.endDate,
      price: this.price,
      maxCapacity: this.maxCapacity,
      accommodationType: this.accommodationType,
      accommodationName: this.accommodationName || null,
      transportationType: this.transportationType,
      transportationDetails: this.transportationDetails || null,
      destinations: this.destinations.filter((d) => d.name && d.country),
      activities: this.activities.filter((a) => a.name),
    });
  }
}
