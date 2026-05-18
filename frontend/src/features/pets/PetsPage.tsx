import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation, useQuery } from '@tanstack/react-query';
import { AxiosError } from 'axios';
import { Edit2, Trash2, X } from 'lucide-react';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { queryClient } from '../../app/queryClient';
import { Button } from '../../shared/components/Button';
import { DataTable } from '../../shared/components/DataTable';
import { Input } from '../../shared/components/Input';
import { PetResponse } from '../../shared/types/domain';
import { createPet, deletePet, getPetPhotoUrl, listPets, updatePet, uploadPetPhoto } from './petsApi';

const schema = z.object({
  name: z.string().min(1, 'Pet name is required').max(80, 'Pet name is too long'),
  species: z.string().min(1, 'Species is required').max(60, 'Species is too long'),
  breed: z.string().max(80, 'Breed is too long').optional(),
  weightKg: z.coerce.number({ invalid_type_error: 'Weight is required' }).min(0.1, 'Weight must be at least 0.1 kg'),
  notes: z.string().max(1000, 'Notes are too long').optional(),
});

type FormValues = z.infer<typeof schema>;

const MAX_PHOTO_SIZE_BYTES = 20 * 1024 * 1024;
const SUPPORTED_PHOTO_TYPES = new Set(['image/jpeg', 'image/png', 'image/webp']);

export function PetsPage() {
  const [editingPet, setEditingPet] = useState<PetResponse | null>(null);
  const [selectedPhoto, setSelectedPhoto] = useState<File | null>(null);
  const [photoError, setPhotoError] = useState<string | null>(null);
  const { data: pets = [], isLoading } = useQuery({ queryKey: ['pets'], queryFn: listPets });
  const form = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      name: '',
      species: '',
      breed: '',
      weightKg: undefined,
      notes: '',
    },
  });
  const createMutation = useMutation({
    mutationFn: createPet,
    onSuccess: () => {
      clearForm();
      queryClient.invalidateQueries({ queryKey: ['pets'] });
    },
  });
  const updateMutation = useMutation({
    mutationFn: ({ id, values }: { id: number; values: FormValues }) => updatePet(id, values),
  });
  const deleteMutation = useMutation({
    mutationFn: deletePet,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['pets'] }),
  });
  const photoMutation = useMutation({
    mutationFn: ({ id, file }: { id: number; file: File }) => uploadPetPhoto(id, file),
  });

  const isSaving = createMutation.isPending || updateMutation.isPending || photoMutation.isPending;
  const saveError = createMutation.error || updateMutation.error || photoMutation.error;

  function clearForm() {
    setEditingPet(null);
    setSelectedPhoto(null);
    setPhotoError(null);
    form.reset({ name: '', species: '', breed: '', weightKg: undefined, notes: '' });
  }

  function startEdit(pet: PetResponse) {
    setEditingPet(pet);
    setSelectedPhoto(null);
    setPhotoError(null);
    form.reset({
      name: pet.name,
      species: pet.species,
      breed: pet.breed ?? '',
      weightKg: pet.weightKg,
      notes: pet.notes ?? '',
    });
  }

  function handleDelete(pet: PetResponse) {
    if (window.confirm(`Delete ${pet.name}?`)) {
      deleteMutation.mutate(pet.id);
    }
  }

  function handlePhotoChange(event: React.ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0] ?? null;
    setSelectedPhoto(null);
    setPhotoError(null);

    if (!file) {
      return;
    }

    if (!SUPPORTED_PHOTO_TYPES.has(file.type)) {
      setPhotoError('Only JPG, PNG, and WebP pet photos are supported.');
      event.target.value = '';
      return;
    }

    if (file.size > MAX_PHOTO_SIZE_BYTES) {
      setPhotoError('Pet photo must be 20MB or smaller.');
      event.target.value = '';
      return;
    }

    setSelectedPhoto(file);
  }

  async function handleSubmit(values: FormValues) {
    if (photoError) {
      return;
    }

    if (editingPet) {
      const pet = await updateMutation.mutateAsync({ id: editingPet.id, values });
      if (selectedPhoto) {
        await photoMutation.mutateAsync({ id: pet.id, file: selectedPhoto });
      }
      clearForm();
      queryClient.invalidateQueries({ queryKey: ['pets'] });
      return;
    }
    createMutation.mutate(values);
  }

  return (
    <section className="grid gap-5">
      <div>
        <h2 className="text-xl font-semibold text-slate-900">My Pets</h2>
        <p className="text-sm text-slate-500">Manage pets you can use when creating bookings.</p>
      </div>

      <form
        className="grid gap-4 rounded-md border border-slate-200 bg-white p-5 md:grid-cols-2"
        onSubmit={form.handleSubmit(handleSubmit)}
      >
        <div className="md:col-span-2">
          <h3 className="text-base font-semibold text-slate-900">{editingPet ? 'Edit pet' : 'Add pet'}</h3>
        </div>
        <Input label="Pet name" error={form.formState.errors.name} {...form.register('name')} />
        <Input label="Species" error={form.formState.errors.species} {...form.register('species')} />
        <Input label="Breed" error={form.formState.errors.breed} {...form.register('breed')} />
        <Input
          label="Weight (kg)"
          type="number"
          step="0.01"
          min="0.1"
          error={form.formState.errors.weightKg}
          {...form.register('weightKg')}
        />
        <Input className="md:col-span-2" label="Notes" error={form.formState.errors.notes} {...form.register('notes')} />
        {editingPet ? (
          <div className="grid gap-3 md:col-span-2">
            <div className="flex items-center gap-3">
              {editingPet.photoUrl ? (
                <img
                  className="h-16 w-16 rounded-md border border-slate-200 object-cover"
                  src={getPetPhotoUrl(editingPet.photoUrl)}
                  alt={editingPet.name}
                />
              ) : (
                <div className="flex h-16 w-16 items-center justify-center rounded-md border border-dashed border-slate-300 text-xs text-slate-500">
                  No photo
                </div>
              )}
              <label className="grid gap-1.5 text-sm font-medium text-slate-700">
                Change photo
                <input
                  className="text-sm text-slate-600 file:mr-3 file:h-9 file:rounded-md file:border-0 file:bg-slate-100 file:px-3 file:text-sm file:font-medium file:text-slate-700 hover:file:bg-slate-200"
                  type="file"
                  accept="image/jpeg,image/png,image/webp"
                  onChange={handlePhotoChange}
                />
              </label>
            </div>
            {selectedPhoto ? <p className="text-sm text-slate-500">Selected: {selectedPhoto.name}</p> : null}
            {photoError ? <p className="text-sm text-rose-600">{photoError}</p> : null}
          </div>
        ) : null}
        {saveError ? <p className="text-sm text-rose-600 md:col-span-2">{getErrorMessage(saveError)}</p> : null}
        <div className="flex flex-wrap gap-2 md:col-span-2">
          <Button className="w-fit" type="submit" disabled={isSaving}>
            {isSaving ? 'Saving...' : editingPet ? 'Save changes' : 'Add pet'}
          </Button>
          {editingPet ? (
            <Button className="w-fit" type="button" variant="secondary" onClick={clearForm}>
              <X className="mr-2 h-4 w-4" />
              Cancel
            </Button>
          ) : null}
        </div>
      </form>

      {isLoading ? (
        <p className="text-sm text-slate-500">Loading...</p>
      ) : (
        <PetsTable
          pets={pets}
          deletingPetId={deleteMutation.isPending ? deleteMutation.variables : undefined}
          onDelete={handleDelete}
          onEdit={startEdit}
        />
      )}
    </section>
  );
}

function getErrorMessage(error: unknown) {
  if (error instanceof AxiosError) {
    const message = (error.response?.data as { message?: string } | undefined)?.message;
    if (message) {
      return message;
    }
  }
  return 'Could not save pet changes.';
}

function PetsTable({
  pets,
  deletingPetId,
  onDelete,
  onEdit,
}: {
  pets: PetResponse[];
  deletingPetId?: number;
  onDelete: (pet: PetResponse) => void;
  onEdit: (pet: PetResponse) => void;
}) {
  return (
    <DataTable
      data={pets}
      emptyText="No pets yet. Add your first pet above."
      columns={[
        { key: 'name', header: 'Name', render: (row) => row.name },
        {
          key: 'photo',
          header: 'Photo',
          render: (row) => row.photoUrl ? (
            <img className="h-12 w-12 rounded-md object-cover" src={getPetPhotoUrl(row.photoUrl)} alt={row.name} />
          ) : (
            <span className="text-slate-500">No photo</span>
          ),
        },
        { key: 'species', header: 'Species', render: (row) => row.species },
        { key: 'breed', header: 'Breed', render: (row) => row.breed || 'Not provided' },
        { key: 'weight', header: 'Weight', render: (row) => `${row.weightKg} kg` },
        { key: 'notes', header: 'Notes', render: (row) => row.notes || 'None' },
        {
          key: 'actions',
          header: 'Actions',
          render: (row) => (
            <div className="flex gap-2">
              <Button
                type="button"
                variant="secondary"
                className="h-9 px-3"
                title={`Edit ${row.name}`}
                aria-label={`Edit ${row.name}`}
                onClick={() => onEdit(row)}
              >
                <Edit2 className="h-4 w-4" />
              </Button>
              <Button
                type="button"
                variant="danger"
                className="h-9 px-3"
                disabled={deletingPetId === row.id}
                title={`Delete ${row.name}`}
                aria-label={`Delete ${row.name}`}
                onClick={() => onDelete(row)}
              >
                <Trash2 className="h-4 w-4" />
              </Button>
            </div>
          ),
        },
      ]}
    />
  );
}
