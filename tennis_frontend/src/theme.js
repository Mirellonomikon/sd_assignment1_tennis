import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    primary: {
      main: '#8bc34a',
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
  },
});

export default theme;
