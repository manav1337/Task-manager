// components/TaskForm.js
import React, { useState } from 'react';
import { taskAPI } from '../services/api';

const TaskForm = ({ onTaskCreated, onTaskUpdated, onCancel, initialData, isEditing }) => {
  const [formData, setFormData] = useState({
    title: initialData?.title || '',
    description: initialData?.description || ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log('🟢 Task Form Submitted!');
    console.log('📦 Task Data:', formData);
    console.log('🔧 Mode:', isEditing ? 'EDITING' : 'CREATING');
    console.log('🎯 Task ID:', initialData?.id);
    
    if (!formData.title.trim()) {
      setError('Title is required');
      return;
    }

    setLoading(true);
    setError('');

    try {
      let response;
      
      if (isEditing && initialData?.id) {
        console.log('🔄 Calling updateTask API...');
        response = await taskAPI.updateTask(initialData.id, formData);
        console.log('✅ Task updated successfully:', response);
        onTaskUpdated(response.data);
      } else {
        console.log('🆕 Calling createTask API...');
        response = await taskAPI.createTask(formData);
        console.log('✅ Task created successfully:', response);
        onTaskCreated(response.data);
      }
    } catch (err) {
      console.log('❌ Operation failed:', err);
      console.log('❌ Error details:', err.response);
      setError(err.response?.data?.message || `Failed to ${isEditing ? 'update' : 'create'} task`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(0,0,0,0.5)',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      zIndex: 1000
    }}>
      <div style={{
        backgroundColor: 'white',
        padding: '20px',
        borderRadius: '8px',
        width: '90%',
        maxWidth: '500px'
      }}>
        <h3>{isEditing ? 'Edit Task' : 'Create New Task'}</h3>
        
        {error && (
          <div style={{ color: 'red', marginBottom: '15px' }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: '15px' }}>
            <label style={{ display: 'block', marginBottom: '5px' }}>
              Title *
            </label>
            <input
              type="text"
              name="title"
              value={formData.title}
              onChange={handleChange}
              required
              style={{
                width: '100%',
                padding: '8px',
                border: '1px solid #ddd',
                borderRadius: '4px'
              }}
            />
          </div>

          <div style={{ marginBottom: '20px' }}>
            <label style={{ display: 'block', marginBottom: '5px' }}>
              Description
            </label>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleChange}
              rows="4"
              style={{
                width: '100%',
                padding: '8px',
                border: '1px solid #ddd',
                borderRadius: '4px',
                resize: 'vertical'
              }}
            />
          </div>

          <div style={{ display: 'flex', gap: '10px' }}>
            <button 
              type="submit" 
              disabled={loading}
              style={{
                padding: '10px 20px',
                backgroundColor: '#007bff',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: loading ? 'not-allowed' : 'pointer'
              }}
            >
              {loading ? 'Saving...' : (isEditing ? 'Update Task' : 'Save Task')}
            </button>
            
            <button 
              type="button"
              onClick={onCancel}
              style={{
                padding: '10px 20px',
                backgroundColor: '#6c757d',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer'
              }}
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default TaskForm;