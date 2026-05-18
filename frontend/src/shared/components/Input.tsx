import { forwardRef, InputHTMLAttributes } from 'react';
import type { FieldError } from 'react-hook-form';
import { clsx } from 'clsx';

type InputError = string | FieldError | undefined;

export type InputProps = InputHTMLAttributes<HTMLInputElement> & {
  label: string;
  error?: InputError;
};

function getErrorMessage(error: InputError) {
  if (!error) {
    return undefined;
  }

  if (typeof error === 'string') {
    return error;
  }

  return error.message;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(function Input(
  { label, error, className, ...props },
  ref,
) {
  const errorMessage = getErrorMessage(error);

  return (
    <label className="grid gap-1.5 text-sm font-medium text-slate-700">
      {label}
      <input
        className={clsx(
          'h-10 rounded-md border border-slate-300 bg-white px-3 text-sm outline-none focus:border-brand-600 focus:ring-2 focus:ring-brand-100',
          className,
        )}
        ref={ref}
        {...props}
      />
      {errorMessage ? <span className="text-xs font-normal text-rose-600">{errorMessage}</span> : null}
    </label>
  );
});

Input.displayName = 'Input';
