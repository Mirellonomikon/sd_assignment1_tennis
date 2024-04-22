import { Navigate, useLocation } from 'react-router-dom';

function ProtectedRoute({ element, allowedRoles }) {
  const location = useLocation();
  const storedUser = localStorage.getItem('user');
  const user = storedUser ? JSON.parse(storedUser) : null;
  const userRole = user ? user.userType : null;

  if (!userRole || !allowedRoles.includes(userRole)) {
    return <Navigate to="/login" state={{ from: location }} />;
  }

  return element;
}

export default ProtectedRoute;
