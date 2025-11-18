import { Entry } from '../types';
import { apiCallCounter, httpRequestDurationMicroseconds } from '../monitoring/metrics';

const API_URL = import.meta.env.VITE_API_URL;

export const fetchEntries = async (): Promise<Entry[]> => {
  const startTime = Date.now();
  try {
    const response = await fetch(API_URL, {
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      mode: 'cors'
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const data = await response.json();
    
    // Record metrics
    apiCallCounter.inc({ method: 'GET', endpoint: '/entries' });
    httpRequestDurationMicroseconds.observe(
      { method: 'GET', route: '/entries', status_code: response.status },
      (Date.now() - startTime) / 1000
    );

    return data;
  } catch (error) {
    console.error('Error fetching entries:', error);
    throw error;
  }
};

export const addEntry = async (amount: number, description: string, date: string): Promise<void> => {
  const startTime = Date.now();
  try {
    const response = await fetch(API_URL, {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      mode: 'cors',
      body: JSON.stringify({ amount, description, date })
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    // Record metrics
    apiCallCounter.inc({ method: 'POST', endpoint: '/entries' });
    httpRequestDurationMicroseconds.observe(
      { method: 'POST', route: '/entries', status_code: response.status },
      (Date.now() - startTime) / 1000
    );

  } catch (error) {
    console.error('Error adding entry:', error);
    throw error;
  }
};

export const updateEntry = async (id: number, amount: number, description: string, date: string): Promise<void> => {
  const startTime = Date.now();
  try {
    const response = await fetch(`${API_URL}/${id}`, {
      method: 'PUT',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      mode: 'cors',
      credentials: 'omit',
      body: JSON.stringify({ amount, description, date })
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    // Record metrics
    if (apiCallCounter && httpRequestDurationMicroseconds) {
      apiCallCounter.inc({ method: 'PUT', endpoint: '/entries' });
      httpRequestDurationMicroseconds.observe(
        { method: 'PUT', route: '/entries', status_code: response.status },
        (Date.now() - startTime) / 1000
      );
    }

  } catch (error) {
    console.error('Error updating entry:', error);
    throw error;
  }
};

export const deleteEntry = async (id: number): Promise<void> => {
  const startTime = Date.now();
  try {
    const response = await fetch(`${API_URL}/${id}`, {
      method: 'DELETE',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      mode: 'cors'
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    // Record metrics
    apiCallCounter.inc({ method: 'DELETE', endpoint: '/entries' });
    httpRequestDurationMicroseconds.observe(
      { method: 'DELETE', route: '/entries', status_code: response.status },
      (Date.now() - startTime) / 1000
    );

  } catch (error) {
    console.error('Error deleting entry:', error);
    throw error;
  }
};