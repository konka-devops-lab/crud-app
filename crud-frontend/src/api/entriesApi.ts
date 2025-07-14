import { Entry } from '../types';

const API_URL = import.meta.env.VITE_API_URL || '/api/entries';

export const fetchEntries = async (): Promise<Entry[]> => {
  const response = await fetch(API_URL);
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
  return await response.json();
};

export const addEntry = async (amount: number, description: string): Promise<void> => {
  const response = await fetch(API_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ amount, description })
  });

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
};

export const deleteEntry = async (id: number): Promise<void> => {
  const response = await fetch(`${API_URL}/${id}`, {
    method: 'DELETE'
  });

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
};