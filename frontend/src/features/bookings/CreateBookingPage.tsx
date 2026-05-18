import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation, useQuery } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import { z } from 'zod';
import { Button } from '../../shared/components/Button';
import { Input } from '../../shared/components/Input';
import { Select } from '../../shared/components/Select';
import { formatCurrency } from '../../shared/utils/format';
import { listPets } from '../pets/petsApi';
import { createBooking } from './bookingsApi';
import { listServices } from './servicesApi';

const schema = z.object({
  petId: z.number({ required_error: 'Please select a pet' }).min(1, 'Please select a pet'),
  serviceId: z.number({ required_error: 'Please select a service' }).min(1, 'Please select a service'),
  scheduledAt: z.string().min(1),
  address: z.string().min(5),
  notes: z.string().optional(),
});

type FormValues = z.infer<typeof schema>;

export function CreateBookingPage() {
  const navigate = useNavigate();
  const { data: pets = [], isLoading: isLoadingPets } = useQuery({ queryKey: ['pets'], queryFn: listPets });
  const { data: services = [] } = useQuery({ queryKey: ['services'], queryFn: listServices });
  const form = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      scheduledAt: '',
      address: '',
      notes: '',
    },
  });
  const mutation = useMutation({
    mutationFn: (values: FormValues) => createBooking({
      ...values,
      scheduledAt: new Date(values.scheduledAt).toISOString(),
    }),
    onSuccess: (booking) => navigate(`/bookings/${booking.id}`),
  });

  return (
    <section className="max-w-2xl">
      <h2 className="mb-4 text-xl font-semibold text-slate-900">Create Booking</h2>
      {!isLoadingPets && pets.length === 0 ? (
        <div className="mb-4 rounded-md border border-amber-200 bg-amber-50 p-4 text-sm text-amber-900">
          <p className="font-medium">Add a pet before creating a booking.</p>
          <Link className="mt-2 inline-flex font-medium text-brand-700 hover:text-brand-800" to="/pets">
            Go to My Pets
          </Link>
        </div>
      ) : null}
      <form className="grid gap-4 rounded-md border border-slate-200 bg-white p-5" onSubmit={form.handleSubmit((values) => mutation.mutate(values))}>
        <Select
          label="Pet"
          defaultValue=""
          error={form.formState.errors.petId?.message}
          {...form.register('petId', { setValueAs: (value) => value === '' ? undefined : Number(value) })}
        >
          <option value="">Select pet</option>
          {pets.map((pet) => <option key={pet.id} value={pet.id}>{pet.name} · {pet.species}</option>)}
        </Select>
        <Select
          label="Service"
          defaultValue=""
          error={form.formState.errors.serviceId?.message}
          {...form.register('serviceId', { setValueAs: (value) => value === '' ? undefined : Number(value) })}
        >
          <option value="">Select service</option>
          {services.map((service) => (
            <option key={service.id} value={service.id}>
              {service.name} · {formatCurrency(service.price)} · {service.durationMinutes} min
            </option>
          ))}
        </Select>
        <Input label="Scheduled at" type="datetime-local" error={form.formState.errors.scheduledAt?.message} {...form.register('scheduledAt')} />
        <Input label="Address" error={form.formState.errors.address?.message} {...form.register('address')} />
        <Input label="Notes" error={form.formState.errors.notes?.message} {...form.register('notes')} />
        {mutation.error ? <p className="text-sm text-rose-600">Could not create booking.</p> : null}
        <Button className="w-fit" type="submit" disabled={mutation.isPending || pets.length === 0}>Create booking</Button>
      </form>
    </section>
  );
}
