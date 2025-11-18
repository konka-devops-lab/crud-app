import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import App from '../App';
import { fetchEntries } from '../api/entriesApi';

vi.mock('../api/entriesApi', () => ({
  fetchEntries: vi.fn(),
}));

describe('App Component', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders header and footer', () => {
    render(<App />);
    expect(screen.getByText('CRUD Application')).toBeInTheDocument();
    expect(screen.getByText(/All rights reserved/)).toBeInTheDocument();
  });

  it('displays loading state while fetching entries', () => {
    (fetchEntries as any).mockImplementation(() => new Promise(() => {})); // Never resolves
    render(<App />);
    expect(screen.getByText('Loading...', { selector: '.absolute' })).toBeInTheDocument();
  });

  it('displays entries after loading', async () => {
    const mockEntries = [
      { id: 1, amount: 100, description: 'Test Entry', date: '2023-01-01' },
    ];
    
    (fetchEntries as any).mockResolvedValueOnce(mockEntries);
    
    render(<App />);
    
    await waitFor(() => {
      expect(screen.getByText('Test Entry')).toBeInTheDocument();
      expect(screen.getByText('100')).toBeInTheDocument();
    });
  });
});