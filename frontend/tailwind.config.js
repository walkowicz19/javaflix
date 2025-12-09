/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        netflix: {
          yellow: '#E50914', // Wait, user said Yellow based. Netflix is Red.
          // User said "colors based in yellow".
          // So I should use Yellow instead of Red.
          primary: '#FFD700', // Gold/Yellow
          black: '#141414',
          dark: '#181818',
          gray: '#2F2F2F',
          light: '#E5E5E5'
        }
      },
      fontFamily: {
        sans: ['Inter', 'sans-serif'],
      }
    },
  },
  plugins: [],
}
