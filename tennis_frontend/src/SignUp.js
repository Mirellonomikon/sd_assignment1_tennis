import React, { useState } from "react";
import { TextField, Button, Typography, Container, CssBaseline, Box, Alert } from "@mui/material";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const SignUp = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [userTypeCode, setUserTypeCode] = useState("");
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const handleSignUp = async () => {
    try {
      const user = {
        username,
        password,
        email,
        name,
        userTypeCode,
      };

      const response = await axios.post("http://localhost:8081/api/user/register", user);
      console.log(response.data);

      setUsername("");
      setPassword("");
      setName("");
      setEmail("");
      setUserTypeCode("player");

      navigate("/login");
    } catch (error) {
      if (error.response && error.response.data) {
        setError(error.response.data);
      } else {
        setError("An unexpected error occurred. Please try again.");
      }
    }
  };

  return (
    <Container component="main" maxWidth="xs">
      <CssBaseline />
      <Box
        sx={{
          marginTop: 8,
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
        }}
      >

        <Typography component="h1" variant="h5" marginBottom="10px">
          Sign Up
        </Typography>

        {error && (
          <Alert severity="error" sx={{ width: "100%", marginBottom: 2 }}>
            {error}
          </Alert>
        )}

        <TextField
          variant="outlined"
          margin="normal"
          required
          fullWidth
          label="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />

        <TextField
          variant="outlined"
          margin="normal"
          required
          fullWidth
          label="Name"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />

        <TextField
          variant="outlined"
          margin="normal"
          required
          fullWidth
          label="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <TextField
          variant="outlined"
          type="password"
          margin="normal"
          required
          fullWidth
          label="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <TextField
          variant="outlined"
          margin="normal"
          fullWidth
          label="User Type Code"
          value={userTypeCode}
          onChange={(e) => setUserTypeCode(e.target.value)}
        />

        <Button
          variant="contained"
          color="primary"
          sx={{ width: 'calc(75%)', marginTop: 2 }}
          onClick={handleSignUp}
        >
          Sign Up
        </Button>

        <Button
          variant="text"
          sx={{ marginTop: 2, width: 'calc(75%)' }}
          onClick={() => navigate("/login")}
        >
          Already have an account? Login
        </Button>
      </Box>
    </Container>
  );
};

export default SignUp;
