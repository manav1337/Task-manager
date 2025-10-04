// components/admin/TaskManagement.js
import React, { useState, useEffect } from 'react';
import { adminAPI } from '../../services/api';

const TaskManagement = () => {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAllTasks();
  }, []);

  const fetchAllTasks = async () => {
    try {
      const response = await adminAPI.getAllTasks();
      setTasks(response.data);
    } catch (error) {
      console.error('Error fetching tasks:', error);
      alert('Failed to fetch tasks');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteTask = async (taskId) => {
    if (window.confirm('Are you sure you want to delete this task?')) {
      try {
        await adminAPI.deleteTask(taskId);
        setTasks(tasks.filter(task => task.id !== taskId));
        alert('Task deleted successfully');
      } catch (error) {
        console.error('Error deleting task:', error);
        alert('Failed to delete task');
      }
    }
  };

  if (loading) {
    return <div>Loading tasks...</div>;
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h2>Task Management</h2>
        <span>Total Tasks: {tasks.length}</span>
      </div>

      <div style={{ overflowX: 'auto' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr style={{ backgroundColor: '#f8f9fa' }}>
              <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #ddd' }}>ID</th>
              <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #ddd' }}>Title</th>
              <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #ddd' }}>Owner</th>
              <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #ddd' }}>Created</th>
              <th style={{ padding: '12px', textAlign: 'left', borderBottom: '1px solid #ddd' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {tasks.map(task => (
              <tr key={task.id}>
                <td style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>{task.id}</td>
                <td style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>{task.title}</td>
                <td style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>
                  {task.user?.username || 'Unknown'}
                </td>
                <td style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>
                  {new Date(task.createdAt).toLocaleDateString()}
                </td>
                <td style={{ padding: '12px', borderBottom: '1px solid #ddd' }}>
                  <button 
                    onClick={() => handleDeleteTask(task.id)}
                    style={{
                      padding: '5px 10px',
                      backgroundColor: '#dc3545',
                      color: 'white',
                      border: 'none',
                      borderRadius: '4px',
                      cursor: 'pointer'
                    }}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default TaskManagement;