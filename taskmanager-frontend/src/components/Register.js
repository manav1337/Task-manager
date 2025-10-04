import React, { useState } from 'react';
import { authAPI } from '../services/api';

const Register = ({ switchToLogin }) => {
  console.log('🔍 authAPI object:', authAPI);
  console.log('🔍 register function:', authAPI?.register);
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: ''
  });
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    console.log('🟢 Form submitted!');
    console.log('📦 Form data:', formData);

    setLoading(true);
    setError('');
    setMessage('');

    try {
      console.log('🚀 Making API call to register...');
      const response = await authAPI.register(formData);
      console.log('✅ Registration successful:', response);
      setMessage(response.data.message);
      // Clear form on success
      setFormData({ username: '', email: '', password: '' });
    } catch (err) {
      console.log('❌ Registration failed:', err);
      setError(err.response?.data?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <h2>Create Account</h2>
      {error && <div className="error-message">{error}</div>}
      {message && <div className="success-message">{message}</div>}
      
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Username:</label>
          <input
            type="text"
            name="username"
            value={formData.username}
            onChange={handleChange}
            required
          />
        </div>
        
        <div className="form-group">
          <label>Email:</label>
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
          />
        </div>
        
        <div className="form-group">
          <label>Password:</label>
          <input
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            required
          />
        </div>
        
        <button type="submit" disabled={loading}>
          {loading ? 'Creating Account...' : 'Register'}
        </button>
      </form>
      
      <p>
        Already have an account? 
        <button onClick={switchToLogin} className="link-button">
          Login here
        </button>
      </p>
    </div>
  );
};

export default Register;