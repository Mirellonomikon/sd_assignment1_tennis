import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    primary: {
      main: '#8bc34a',
    },
    error: {
        main: '#ff9800',
    },
    background: {
      default: '#f1f8e9',
    },
  },
  typography: {
    fontFamily: 'Montserrat, Arial',
    button: {
      textTransform: 'none',
    }
  }
});

export default theme;
