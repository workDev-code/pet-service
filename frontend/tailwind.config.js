/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#eef8f5',
          100: '#d7efe8',
          600: '#14866d',
          700: '#0f6b59'
        }
      }
    }
  },
  plugins: [],
};
