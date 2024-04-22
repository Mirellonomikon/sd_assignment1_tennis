import React from "react";
import { useNavigate } from "react-router-dom";
import { Container, Box, Button, Typography, CssBaseline } from "@mui/material";

const DefaultPage = () => {
  const navigate = useNavigate();

  return (
    <Container component="main" maxWidth="s">
      <CssBaseline />
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          height: '100vh',
          alignItems: 'center',
        }}
      >
        <Typography component="h1" variant="h4" gutterBottom style={{ marginTop: 150, marginBottom: -100, fontWeight: 400, textAlign: 'center' }}>
          Tennis Tournament Schedule
        </Typography>
        <Box
          sx={{
            width: '100%',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            marginTop: 'auto',
            marginBottom: 'auto',
          }}
        >
          <Button
            variant="contained"
            color="primary"
            sx={{ margin: 1, width: 'calc(20%)' }}
            onClick={() => navigate("/login")}
          >
            Login
          </Button>
          <Button
            variant="outlined"
            color="primary"
            sx={{ margin: 1, width: 'calc(15%)' }}
            onClick={() => navigate("/signup")}
          >
            Sign Up
          </Button>
        </Box>
      </Box>
    </Container>
  );
};

export default DefaultPage;
