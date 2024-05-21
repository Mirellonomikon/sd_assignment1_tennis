import { Navigate, useLocation } from 'react-router-dom';
import { jwtDecode } from "jwt-decode";

function ProtectedRoute({ element, allowedRoles }) {
  const location = useLocation();

  const token = localStorage.getItem('token');
  const decodedToken = token ? jwtDecode(token) : null;
  const userRole = decodedToken ? decodedToken.userType : null;

  if (!userRole || !allowedRoles.includes(userRole)) {
    return <Navigate to="/" state={{ from: location }} />;
  }

  return element;
}

export default ProtectedRoute;
