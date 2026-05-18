export type Role = 'CUSTOMER' | 'STAFF' | 'ADMIN';
export type BookingStatus = 'PENDING' | 'ASSIGNED' | 'COMPLETED' | 'CANCELLED';

export interface UserResponse {
  id: number;
  fullName: string;
  email: string;
  role: Role;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  user: UserResponse;
}

export interface PetResponse {
  id: number;
  ownerId: number;
  name: string;
  species: string;
  breed?: string | null;
  weightKg: number;
  notes?: string | null;
  photoUrl?: string | null;
}

export interface ServiceCatalogResponse {
  id: number;
  name: string;
  description?: string | null;
  price: number;
  durationMinutes: number;
  active: boolean;
}

export interface StaffResponse {
  id: number;
  fullName: string;
  email: string;
}

export interface BookingResponse {
  id: number;
  customer: { id: number; fullName: string; email: string };
  pet: { id: number; name: string; species: string; breed?: string | null };
  service: { id: number; name: string; price: number; durationMinutes: number };
  assignedStaff?: { id: number; fullName: string; email: string } | null;
  scheduledAt: string;
  address: string;
  notes?: string | null;
  status: BookingStatus;
  createdAt: string;
  updatedAt: string;
}
