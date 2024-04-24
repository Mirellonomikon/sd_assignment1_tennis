import React from "react";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import ProtectedRoute from './ProtectedRoute';
import Login from './Login';
import DefaultPage from "./DefaultPage";
import SignUp from './SignUp';
import RefereeSchedule from './RefereeSchedule';
import PlayerSchedule from './PlayerSchedule';
import AdminSchedule from './AdminSchedule';
import AdminUsersView from "./AdminUsersView";
import theme from './theme';
import { ThemeProvider } from "@mui/material/styles";

const App = () => {

  return (
    <ThemeProvider theme={theme}>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<DefaultPage />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<SignUp />} />
        
        <Route
          path="/referee-schedule"
          element={
            <ProtectedRoute
              element={<RefereeSchedule />}
              allowedRoles={['referee']}
            />
          }
        />

        <Route
          path="/player-schedule"
          element={
            <ProtectedRoute
              element={<PlayerSchedule />}
              allowedRoles={['player']}
            />
          }
        />

        <Route
          path="/admin-schedule"
          element={
            <ProtectedRoute
              element={<AdminSchedule />}
              allowedRoles={['administrator']}
            />
          }
        />

        <Route
          path="/admin-users-view"
          element={
            <ProtectedRoute
              element={<AdminUsersView />}
              allowedRoles={['administrator']}
            />
          }
        />
      </Routes>
    </BrowserRouter>
    </ThemeProvider>
  );
};

export default App;
